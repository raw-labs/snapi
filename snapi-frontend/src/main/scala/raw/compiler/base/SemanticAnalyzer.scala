/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.compiler.base

import com.typesafe.scalalogging.StrictLogging
import org.bitbucket.inkytonik.kiama.==>
import org.bitbucket.inkytonik.kiama.attribution.Decorators
import org.bitbucket.inkytonik.kiama.util.{Entity, Environments}
import raw.compiler.base.errors._
import raw.compiler.base.source._
import raw.compiler.utils.{Attribution, ExtraRewriters}

/** Used by some methods as a helper to avoid returning Either. */
class UnsupportedTypeException(val t: Type) extends Throwable {
  override def getMessage: String = s"UnsupportedTypeException: $t"
}

final case class ExpectedType(t: Type, hint: Option[String] = None, suggestions: Seq[String] = Seq.empty)

trait ExpectedTypes {
  val anything = AnythingType()
  val ignore = anything
}

abstract class SemanticAnalyzer[N <: BaseNode: Manifest, P <: N: Manifest, E <: N: Manifest](
    tree: org.bitbucket.inkytonik.kiama.relation.Tree[N, P]
) extends Attribution
    with Environments[Entity]
    with ExpectedTypes
    with ExtraRewriters
    with StrictLogging {

  import scala.language.implicitConversions

  implicit def typeToExpectedType(t: Type): ExpectedType = ExpectedType(t, hint = None, suggestions = Seq.empty)

  /** Decorators on the tree. */
  final protected lazy val decorators = new Decorators(tree)

  import decorators.{chain, Chain}

  private def collectErrors(pf: Any ==> Seq[CompilationMessage]): Seq[CompilationMessage] = {
    val c = collectNodes[N, Seq, Seq[CompilationMessage]](pf)
    c(tree.root).flatten
  }

  protected def errorDef: N ==> Seq[CompilationMessage] = PartialFunction.empty[N, Seq[CompilationMessage]]

  lazy val errors: Seq[CompilationMessage] = collectErrors {
    case n: N if errorDef.isDefinedAt(n) =>
      // If an error was triggered by errorDef, this is reported. In addition, however, we also report an additional
      // error if our actual/expected types are incompatible. One example why this is important:
      // Say we are a FunApp. We trigger an error in errorDef because one of our arguments is wrong.
      // But we are also being used in a context where we should not be a function in the first place.
      // The check on typeCompatible will also report that error; therefore, we get two errors, which is correct.
      // (There may be cases where we end up reporting "an error too much"; if that ever happens, we should consider
      // whether the error check should be done with actual/expected types or with specific cases on errorDef.)
      val errs = errorDef(n)
      n match {
        case e: E if errs.isEmpty && !typeCompatible(e) =>
          val ExpectedType(expected, hints, suggestions) = expectedType(e)
          errs :+ UnexpectedType(e, actualType(e), expected, hints, suggestions)
        case _ => errs
      }
    case e: E if !typeCompatible(e) =>
      val ExpectedType(expected, hints, suggestions) = expectedType(e)
      Seq(UnexpectedType(e, actualType(e), expected, hints, suggestions))
  }

  final def tipe(e: E): Type = actualType(e)

  private def typeCompatible(e: E): Boolean = {
    val expected = expectedType(e).t
    val actual = actualType(e)
    isCompatible(actual, expected)
  }

  protected def isCompatible(actual: Type, expected: Type): Boolean = {
    (actual, expected) match {
      case (_, _: AnythingType) => true
      case _ => actual == expected
    }
  }

  lazy val actualType: E => Type = attr(actualTypeDef _)

  protected def actualTypeDef(n: E): Type

  final protected lazy val expectedType: E => ExpectedType = attr(expectedTypeDef)

  protected def expectedTypeDef(e: E): ExpectedType = e match {
    case n: N if tree.isRoot(n) =>
      // No constraints at the root
      anything
    case _ =>
      // TODO: Why wasn't this needed before? See typeCompatible check above!!
      anything
  }

  def getEntityGoingIn(n: N, idn: String): Entity = lookup(env.in(n), idn, UnknownEntity())

  def getEntityGoingOut(n: N, idn: String): Entity = lookup(env.out(n), idn, UnknownEntity())

  // TODO (msb): Use of BaseIdnNode is weird. Shouldn't it be some variant of N?
  final lazy val entity: BaseIdnNode => Entity = attr(entityDef)

  // TODO (msb): Is this safe?
  def defineIfNew(env: Environment, i: String, e: => Entity): Environment = {
    lookup(env, i, UnknownEntity(), local = true) match {
      case UnknownEntity() => define(env, i, e)
      case m: MultipleEntity => define(env, i, new MultipleEntity(m.entities :+ e))
      case prev => define(env, i, new MultipleEntity(Vector(prev, e)))
    }
  }

  /** Default implementation of the entity of an identifier. */
  protected def entityDef(n: BaseIdnNode): Entity

  lazy val entityType: Entity => Type = attr(entityTypeDef)

  protected def entityTypeDef(e: Entity): Type
  protected lazy val defenv: Environment = rootenv()

  /** Chain for looking up identifiers. */

  final protected lazy val env: Chain[Environment] = chain(envin, envout)

  protected def envin(in: N => Environment): N ==> Environment = {
    case n: N if tree.isRoot(n) => defenv
  }

  protected def envout(out: N => Environment): N ==> Environment = {
    PartialFunction.empty
  }

  /** Define only if i is not yet defined. */
  protected def defineIfUndefined(env: Environment, i: String, e: Entity): Environment = {
    if (isDefinedInScope(env, i)) env
    else define(env, i, e)
  }

  /** Lookup in outer scope. */
  protected def lookupOuter(env: Environment, i: String, e: => Entity): Entity = env match {
    case _ :: t => lookup(t, i, e)
    case _ => e
  }

  def rootType: Option[Type]

  protected def descriptionDef: TreeDescription

  final lazy val description: TreeDescription = descriptionDef

}
