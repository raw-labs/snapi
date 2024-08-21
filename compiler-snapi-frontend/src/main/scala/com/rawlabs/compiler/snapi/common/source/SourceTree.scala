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

package com.rawlabs.compiler.snapi.common.source

import org.bitbucket.inkytonik.kiama.output._
import com.rawlabs.compiler.snapi.base.source.Type
import org.bitbucket.inkytonik.kiama.output.PrettyExpression
import org.bitbucket.inkytonik.kiama.rewriting.Strategy
import org.bitbucket.inkytonik.kiama.rewriting.Rewriter._
import com.rawlabs.compiler.snapi.base.Counter
import com.rawlabs.compiler.snapi.base.source._

trait SourceNode extends BaseNode

object SourceTree {
  import org.bitbucket.inkytonik.kiama.relation.Tree

  type SourceTree = Tree[SourceNode, SourceProgram]
}

/**
 * Top-level source node.
 */
trait SourceProgram extends SourceNode with BaseProgram {
  def params: Vector[SourceProgramParam]
  def comment: Option[String] = None
}

final case class SourceProgramParam(idn: CommonIdnNode, t: Type) extends CommonNode

/**
 * Qualifiers
 */
// TODO (msb): These are best called "Statements" because that's (also?) what they are
//             Qual is more general than Stmt: a Gen is a Qual but not a Stmt while Bind is
trait Qual extends SourceNode

/**
 * Declarations
 */
trait Decl extends SourceNode with Qual

/**
 * Expressions
 */
trait Exp extends SourceNode with Qual with PrettyExpression

/**
 * * From now on are "common language"-related nodes. **
 */

/**
 * Parent of all "common language" nodes.
 */
trait CommonNode extends SourceNode

/**
 * Parent of all "common language" types.
 */
trait CommonType extends Type with CommonNode

/**
 * Any Type
 * The top type.
 */
final case class AnyType() extends CommonType

/**
 * Nothing Type
 * The bottom type.
 */
final case class NothingType() extends CommonType

/**
 * This type is used when the tree has errors.
 */
final case class ErrorType() extends CommonType

/**
 * Parent of all "common language" expressions.
 */
trait CommonExp extends Exp with CommonNode

///////////////////////////////////////////////////////////////////////////
// Type Constraints
///////////////////////////////////////////////////////////////////////////

trait CommonTypeConstraint extends CommonType

/**
 * One-of Type constraint.
 */
final case class OneOfType(tipes: Vector[Type]) extends CommonTypeConstraint

object OneOfType {
  def apply(tipes: Type*): OneOfType = {
    OneOfType(tipes.toVector)
  }
}

final case class ExpectedRecordType(idns: Set[String]) extends CommonTypeConstraint

///////////////////////////////////////////////////////////////////////////
// Identifiers
///////////////////////////////////////////////////////////////////////////

abstract class CommonIdnNode extends BaseIdnNode with CommonNode

/**
 * Defining occurrence of an identifier
 */
final case class IdnDef(idn: String) extends CommonIdnNode

object IdnDef {
  def apply(): IdnDef = IdnDef(Counter.next("common"))
}

/**
 * Use of an identifier
 */
final case class IdnUse(idn: String) extends CommonIdnNode

object IdnUse {
  def apply(i: IdnDef): IdnUse = IdnUse(i.idn)
}

/**
 * Identifier expression
 */
final case class IdnExp(idn: IdnUse) extends CommonExp

object IdnExp {
  def apply(i: IdnDef): IdnExp = IdnExp(IdnUse(i.idn))
  def apply(i: String): IdnExp = IdnExp(IdnUse(i))
  def apply(p: SourceProgramParam): IdnExp = IdnExp(p.idn.idn)
  def apply(p: BaseIdnNode): IdnExp = IdnExp(p.idn)
}

///////////////////////////////////////////////////////////////////////////
// Expressions
///////////////////////////////////////////////////////////////////////////

/**
 * Bind
 */
final case class Bind(e: Exp, idn: IdnDef) extends CommonNode with Decl

object Bind {
  def apply(e: Strategy, idn: Strategy): Strategy = {
    rulefs[Bind] { case _ => congruence(e, idn) }
  }

  def apply(idn: IdnDef, e: Exp): Bind = Bind(e, idn)
}

final case class ErrorExp() extends CommonExp
