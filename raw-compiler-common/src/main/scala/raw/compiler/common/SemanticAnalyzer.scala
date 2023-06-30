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

package raw.compiler.common

import org.bitbucket.inkytonik.kiama.==>
import org.bitbucket.inkytonik.kiama.rewriting.Rewriter._
import org.bitbucket.inkytonik.kiama.util.Entity
import raw.compiler.base.{ExpectedType, MultipleEntity, ProgramContext, TreeDescription, UnknownEntity}
import raw.compiler.base
import raw.compiler.base.source._
import raw.compiler.base.errors._
import raw.compiler.common.source._

import scala.collection.mutable

trait ExpectedTypes extends base.ExpectedTypes {

  // Primitive number types
  val byte = ByteType()
  val short = ShortType()
  val int = IntType()
  val long = LongType()
  val float = FloatType()
  val double = DoubleType()
  val decimal = DecimalType()

  // Primitive temporal types
  val date = DateType()
  val time = TimeType()
  val interval = IntervalType()
  val timestamp = TimestampType()

  // Primitive types
  val bool = BoolType()
  val string = StringType()
  val binary = BinaryType()
  val location = LocationType()
  val inputStream = InputStreamType()
  val outputStream = OutputStreamType()

  // Collection types
  val options = OptionType(anything)
  val generators = GeneratorType(anything)
  val iterables = IterableType(anything)
  val lists = ListType(anything)
  val tries = TryType(anything)

  // Type constraints
  val regexes = ExpectedRegexType()

  // Number types constraints
  val integers = OneOfType(byte, short, int, long)
  val numerics = OneOfType(integers.tipes ++ Vector(float, double, decimal))

  // Temporal types constraints
  val temporals = OneOfType(time, interval, date, timestamp)

  // Other helpers
  val numbersAndString = OneOfType(numerics.tipes :+ string)
  val optionString = OptionType(string)
  val numbersAndInterval = OneOfType(numerics.tipes :+ interval)
  val timeAndInterval = OneOfType(time, interval)
  val dateTimestampInterval = OneOfType(date, timestamp, interval)
  val numericsTemporalsString = OneOfType(numerics.tipes ++ temporals.tipes :+ string)
  val locationOptionValue = OrType(
    int,
    string,
    binary,
    bool,
    interval,
    ListType(TupleType(OptionType(string), OptionType(string))),
    ListType(IntType())
  )
  val locationOptions = ListType(TupleType(string, locationOptionValue))
  val stringOrLocation = OneOfType(string, location)
  val stringOrOutputStream = OneOfType(string, outputStream)
  val intOrTimestamp = OneOfType(int, timestamp)

}

abstract class SemanticAnalyzer(tree: SourceTree.SourceTree)(implicit protected val programContext: ProgramContext)
    extends base.SemanticAnalyzer[SourceNode, SourceProgram, Exp](tree)
    with ExpectedTypes {

  override protected def errorDef: SourceNode ==> Seq[BaseError] = {
    super.errorDef orElse {
      case i: IdnUse if entity(i) == UnknownEntity() => Seq(UnknownDecl(i))
      case i: IdnDef if entity(i).isInstanceOf[MultipleEntity] => Seq(MultipleDecl(i))
      case Eval(r) if tipeProgram(r).isLeft => tipeProgram(r).left.get
    }
  }

  override protected def isCompatible(actual: Type, expected: Type): Boolean = {
    // expected == ErrorType() is often used when e1 failed to type, and the expected type e2 is set to type of e1.
    // In that case, the expected type of e2 would be set to error, which we should then keep silent about.
    actual == NothingType() || expected == AnyType() ||
    expected == ErrorType() ||
    ((actual, expected) match {
      case (OrType(actualTypes), OrType(expectedTypes)) =>
        actualTypes.forall(a => expectedTypes.exists(e => isCompatible(a, e)))
      case (_, OrType(expectedTypes)) => expectedTypes.exists(isCompatible(actual, _))
      case (_, OneOfType(expectedTypes)) => expectedTypes.exists(isCompatible(actual, _))
      case (ListType(i1), ListType(i2)) => isCompatible(i1, i2)
      case (IterableType(i1), IterableType(i2)) => isCompatible(i1, i2)
      case (GeneratorType(i1), GeneratorType(i2)) => isCompatible(i1, i2)
      case (OptionType(i1), OptionType(i2)) => isCompatible(i1, i2)
      case (TryType(i1), TryType(i2)) => isCompatible(i1, i2)
      case (RecordType(as1), RecordType(as2)) => as1.map(_.idn) == as2.map(_.idn) &&
          as1.map(_.tipe).zip(as2.map(_.tipe)).forall { case (x, y) => isCompatible(x, y) }
      case (BinaryType(), BinaryType()) => true
      case (RegexType(n1), RegexType(n2)) => n1 == n2
      case (RecordType(atts), ExpectedRecordType(idns)) if idns.subsetOf(atts.map(_.idn).toSet) => true
      case (RDDType(inner1), RDDType(inner2)) => isCompatible(inner1, inner2)
      case (RegexType(_), ExpectedRegexType()) => true
      case (_: ErrorType, _) => true
      case _ => super.isCompatible(actual, expected)
    })
  }

  override protected lazy val defenv: Environment = rootenv(intrinsics.toSeq: _*)

  override protected def envin(in: SourceNode => Environment): SourceNode ==> Environment =
    envinDef(in) orElse super.envin(in)

  override protected def envout(out: SourceNode => Environment): SourceNode ==> Environment =
    envoutDef(out) orElse super.envout(out)

  private def envinDef(in: SourceNode => Environment): SourceNode ==> Environment = { case _: SourceProgram => defenv }

  private def envoutDef(out: SourceNode => Environment): SourceNode ==> Environment = {
    case e: Exp =>
      // Exp nodes cannot define identifiers, so their `out` environment is always the same as their `in`.
      env.in(e)
    case t: Type =>
      // Skip walking through types since nothing is added to scope
      env.in(t)
    case i: IdnDef => defineIfNew(env.in(i), i.idn, defentity(i))
  }

  protected def defentity(i: IdnDef): Entity = i match {
    case tree.parent(b: Bind) => new BindEntity(b)
    case tree.parent(p: SourceProgramParam) => new ProgramParamEntity(p)
    case _ => throw new AssertionError(s"Unhandled node in defentity: ${tree.parent(i).toString}")
  }

  override protected def actualTypeDef(n: Exp): Type = n match {
    case e: CommonExp => actualTypeCommon(e)
    case _ => ErrorType()
  }

  private def actualTypeCommon(n: CommonExp): Type = n match {
    case IdnExp(idn) => entityType(entity(idn))
    case Eval(r) => tipeProgram(r) match {
        case Right(t) => t
        case Left(_) => ErrorType()
      }
    case ErrorExp() => ErrorType()
  }

  override protected def entityTypeDef(e: Entity): Type = e match {
    case i: BindEntity => actualType(i.b.e)
    case i: IntrinsicEntity => i.t
    case p: ProgramParamEntity => p.p.t
    case UnknownEntity() | _: MultipleEntity => ErrorType()
  }

  // TODO (msb): Not entirely safe to make this public as type may not be resolved.
  def idnType(idn: CommonIdnNode): Type = {
    entityType(entity(idn))
  }

  /** Default implementation of the entity of an identifier. */
  override protected def entityDef(n: BaseIdnNode): Entity = n match {
    case n: IdnDef => lookup(env(n), n.idn, UnknownEntity())
    case n: IdnUse => lookup(env(n), n.idn, UnknownEntity())
  }

  protected def intrinsics: Map[String, IntrinsicEntity] = Map.empty

  override protected def expectedTypeDef(n: Exp): ExpectedType = n match {
    case tree.parent.pair(e: Exp, parent: CommonNode) => expectedTypeCommon(e, parent)
    case _ => super.expectedTypeDef(n)
  }

  private def expectedTypeCommon(e: Exp, parent: CommonNode): Type = (parent: @unchecked) match {
    case Bind(_, _) => anything
  }

  protected def envInOfNode(n: SourceNode): Environment = env.in(n)

  /** Return the set of all identifiers in scope, going into the node. */
  final def identifiersInScope(node: SourceNode): Set[String] = {
    envInOfNode(node).flatten.map(_._1).toSet
  }

  /** Return the set of all entities in scope, going into the node. */
  final def scope(node: SourceNode): Set[Entity] = {
    // Remove entities that are not user-defined binds
    // TODO: Re-evaluate this.
    envInOfNode(node).flatMap(_.values).filterNot(_.isInstanceOf[IntrinsicEntity]).toSet
  }

  /** Return the set of all entities in scope, going into the node along with their identifier visible string identifier. */
  final def scopeInWithIdentifier(node: SourceNode): Map[String, Entity] = {
    // Remove entities that are not user-defined binds
    val m = mutable.HashMap[String, Entity]()
    envInOfNode(node).flatten
      .filterNot { case (_, e) => e.isInstanceOf[IntrinsicEntity] }
      .foreach {
        case (i, e) => if (!m.contains(i)) {
            m.put(i, e)
          }
      }
    m.toMap
  }

  /** Return the set of entities in the current scope, going into the node. */
  final def currentScope(node: SourceNode): Set[Entity] = {
    envInOfNode(node).head.values.toSet
  }

  /** Return the set of entities used inside by the node. */
  final def uses(node: SourceNode): Set[Entity] = {
    val uses = mutable.HashSet[Entity]()
    everywhere(query[Any] { case idnUse: IdnUse => uses += entity(idnUse) })(node)
    uses.to
  }

  /** Return the set of "free entities" inside the node. */
  final def freeVars(node: SourceNode): Set[Entity] = {
    val in = scope(node)
    val used = uses(node)
    in.intersect(used)
  }

  /** Version of freeVars that also returns the identifier. */
  final def freeVarsWithIdentifier(node: SourceNode, useNodes: Seq[SourceNode]): Map[String, Entity] = {
    val toIntersect = if (useNodes.isEmpty) Seq(node) else useNodes
    val in = scopeInWithIdentifier(node)
    in.filter { case (_, e) => toIntersect.exists(n => uses(n).contains(e)) }
  }

  final def tipeProgram(r: RawBridge[BaseProgram]): Either[Seq[BaseError], Type] = {
    tipeProgramAttr(r)
  }

  // Wrap in an attribute to cache.
  private lazy val tipeProgramAttr: RawBridge[BaseProgram] => Either[Seq[BaseError], Type] =
    attr { case r => tipeProgramDef(r) }

  protected def tipeProgramDef(r: RawBridge[BaseProgram]): Either[Seq[BaseError], Type] = {
    r match {
      case RawBridgeImpl(language, program) => programContext
          .validateProgram(language, program)
          .right
          .map {
            case Some(t) =>
              assert(t != ErrorType())
              t
            case None => NotValueType()
          }
      case RawBridgeRef(t, _) => Right(t)
    }
  }

  final def isIntrinsic(idn: CommonIdnNode): Boolean = entity(idn) match {
    case _: IntrinsicEntity => true
    case _ => false
  }

  override protected def descriptionDef: TreeDescription = {
    TreeDescription(Map.empty, rootType, None)
  }

}
