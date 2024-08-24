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

package com.rawlabs.snapi.frontend.rql2.source

import org.bitbucket.inkytonik.kiama.output._
import org.bitbucket.inkytonik.kiama.output.PrettyExpression
import com.rawlabs.snapi.frontend.base.source.Type
import com.rawlabs.snapi.frontend.base.Counter
import com.rawlabs.snapi.frontend.base.source._

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

final case class ErrorExp() extends CommonExp

///////////////////////////////////////////////////////////////////////////
// RQL2
///////////////////////////////////////////////////////////////////////////

sealed trait SnapiNode extends SourceNode

///////////////////////////////////////////////////////////////////////////
// Program
///////////////////////////////////////////////////////////////////////////

final case class SnapiProgram(methods: Vector[SnapiMethod], me: Option[Exp]) extends SnapiNode with SourceProgram {
  override val params: Vector[SourceProgramParam] = Vector.empty
}

object SnapiProgram {
  def apply(e: Exp): SnapiProgram = SnapiProgram(Vector.empty, Some(e))
}

final case class SnapiMethod(p: FunProto, i: IdnDef) extends SnapiNode

///////////////////////////////////////////////////////////////////////////
// Types
///////////////////////////////////////////////////////////////////////////

sealed trait SnapiType extends Type with SnapiNode

sealed trait SnapiTypeProperty extends SnapiNode
final case class SnapiIsNullableTypeProperty() extends SnapiTypeProperty
final case class SnapiIsTryableTypeProperty() extends SnapiTypeProperty

/**
 * A subset of RQL2 types have type properties.
 */
sealed trait SnapiTypeWithProperties extends SnapiType {
  def props: Set[SnapiTypeProperty]
  def cloneAndAddProp(p: SnapiTypeProperty): Type
  def cloneAndRemoveProp(p: SnapiTypeProperty): Type
}

final case class SnapiUndefinedType(props: Set[SnapiTypeProperty] = Set.empty) extends SnapiTypeWithProperties {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiUndefinedType(props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiUndefinedType(props - p)
}

/**
 * Primitive types
 */
sealed trait SnapiPrimitiveType extends SnapiTypeWithProperties

final case class SnapiBoolType(props: Set[SnapiTypeProperty] = Set.empty) extends SnapiPrimitiveType {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiBoolType(props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiBoolType(props - p)
}

final case class SnapiStringType(props: Set[SnapiTypeProperty] = Set.empty) extends SnapiPrimitiveType {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiStringType(props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiStringType(props - p)
}

final case class SnapiLocationType(props: Set[SnapiTypeProperty] = Set.empty) extends SnapiPrimitiveType {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiLocationType(props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiLocationType(props - p)
}

final case class SnapiBinaryType(props: Set[SnapiTypeProperty] = Set.empty) extends SnapiPrimitiveType {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiBinaryType(props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiBinaryType(props - p)
}

/**
 * Number types
 *
 * These are also primitive types.
 */
sealed trait SnapiNumberType extends SnapiPrimitiveType

sealed trait SnapiIntegralNumberType extends SnapiNumberType

final case class SnapiByteType(props: Set[SnapiTypeProperty] = Set.empty) extends SnapiIntegralNumberType {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiByteType(props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiByteType(props - p)
}

final case class SnapiShortType(props: Set[SnapiTypeProperty] = Set.empty) extends SnapiIntegralNumberType {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiShortType(props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiShortType(props - p)
}

final case class SnapiIntType(props: Set[SnapiTypeProperty] = Set.empty) extends SnapiIntegralNumberType {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiIntType(props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiIntType(props - p)
}

final case class SnapiLongType(props: Set[SnapiTypeProperty] = Set.empty) extends SnapiIntegralNumberType {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiLongType(props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiLongType(props - p)
}

final case class SnapiFloatType(props: Set[SnapiTypeProperty] = Set.empty) extends SnapiNumberType {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiFloatType(props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiFloatType(props - p)
}

final case class SnapiDoubleType(props: Set[SnapiTypeProperty] = Set.empty) extends SnapiNumberType {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiDoubleType(props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiDoubleType(props - p)
}

final case class SnapiDecimalType(props: Set[SnapiTypeProperty] = Set.empty) extends SnapiNumberType {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiDecimalType(props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiDecimalType(props - p)
}

/**
 * Temporal types
 *
 * These are also primitive types.
 */
sealed trait SnapiTemporalType extends SnapiPrimitiveType

final case class SnapiDateType(props: Set[SnapiTypeProperty] = Set.empty) extends SnapiTemporalType {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiDateType(props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiDateType(props - p)
}

final case class SnapiTimeType(props: Set[SnapiTypeProperty] = Set.empty) extends SnapiTemporalType {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiTimeType(props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiTimeType(props - p)
}

final case class SnapiTimestampType(props: Set[SnapiTypeProperty] = Set.empty) extends SnapiTemporalType {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiTimestampType(props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiTimestampType(props - p)
}

final case class SnapiIntervalType(props: Set[SnapiTypeProperty] = Set.empty) extends SnapiTemporalType {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiIntervalType(props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiIntervalType(props - p)
}

/**
 * Record Type
 */
final case class SnapiRecordType(atts: Vector[SnapiAttrType], props: Set[SnapiTypeProperty] = Set.empty)
    extends SnapiTypeWithProperties {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiRecordType(atts, props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiRecordType(atts, props - p)
}
final case class SnapiAttrType(idn: String, tipe: Type) extends SnapiNode

/**
 * Iterable Type
 */
final case class SnapiIterableType(innerType: Type, props: Set[SnapiTypeProperty] = Set.empty)
    extends SnapiTypeWithProperties {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiIterableType(innerType, props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiIterableType(innerType, props - p)
}

/**
 * List Type
 *
 * Inherits from IterableType but provides index operations.
 */
final case class SnapiListType(innerType: Type, props: Set[SnapiTypeProperty] = Set.empty)
    extends SnapiTypeWithProperties {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiListType(innerType, props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiListType(innerType, props - p)
}

/**
 * Function Type.
 */
final case class FunType(
    ms: Vector[Type],
    os: Vector[FunOptTypeParam],
    r: Type,
    props: Set[SnapiTypeProperty] = Set.empty
) extends SnapiTypeWithProperties {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = FunType(ms, os, r, props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = FunType(ms, os, r, props - p)
}
final case class FunOptTypeParam(i: String, t: Type) extends SnapiNode

/**
 * Or Type.
 */
final case class SnapiOrType(tipes: Vector[Type], props: Set[SnapiTypeProperty] = Set.empty)
    extends SnapiTypeWithProperties {
  override def cloneAndAddProp(p: SnapiTypeProperty): Type = SnapiOrType(tipes, props + p)
  override def cloneAndRemoveProp(p: SnapiTypeProperty): Type = SnapiOrType(tipes, props - p)
}

object SnapiOrType {
  def apply(t1: Type, t2: Type, props: Set[SnapiTypeProperty]): SnapiOrType = {
    (t1, t2) match {
      case (SnapiOrType(tipes1, props1), SnapiOrType(tipes2, props2)) if props == props1 && props == props2 =>
        SnapiOrType(tipes1 ++ tipes2, props)
      case (SnapiOrType(tipes1, props1), _) if props == props1 => SnapiOrType(tipes1 :+ t2, props)
      case (_, SnapiOrType(tipes2, props2)) if props == props2 => SnapiOrType(Vector(t1) ++ tipes2, props)
      case _ => SnapiOrType(Vector(t1, t2), props)
    }
  }
}

/**
 * Package Type.
 */
final case class PackageType(name: String) extends SnapiType
final case class PackageEntryType(pkgName: String, entName: String) extends SnapiType

/**
 * Type Alias.
 */
final case class TypeAliasType(idn: IdnUse) extends SnapiType

/**
 * Expression Type.
 *
 * The type of an expression such as `type int`.
 */
final case class ExpType(t: Type) extends SnapiType

///////////////////////////////////////////////////////////////////////////
// Type Constraints
///////////////////////////////////////////////////////////////////////////

sealed trait SnapiTypeConstraint extends SnapiType

final case class ExpectedProjType(i: String) extends SnapiTypeConstraint

final case class MergeableType(t: Type) extends SnapiTypeConstraint

final case class HasTypeProperties(props: Set[SnapiTypeProperty]) extends SnapiTypeConstraint

final case class IsTryable() extends SnapiTypeConstraint

final case class IsNullable() extends SnapiTypeConstraint

final case class DoesNotHaveTypeProperties(props: Set[SnapiTypeProperty]) extends SnapiTypeConstraint

///////////////////////////////////////////////////////////////////////////
// Expressions
///////////////////////////////////////////////////////////////////////////

sealed trait SnapiExp extends Exp with SnapiNode

/**
 * Constants
 */

trait Const extends SnapiExp

final case class NullConst() extends Const

final case class BoolConst(value: Boolean) extends Const

final case class StringConst(value: String) extends Const

final case class TripleQuotedStringConst(value: String) extends Const

final case class BinaryConst(bytes: Array[Byte]) extends Const {
  override def equals(obj: Any): Boolean = {
    obj match {
      case BinaryConst(otherBytes) => bytes.sameElements(otherBytes)
      case _ => false
    }
  }
}

final case class LocationConst(bytes: Array[Byte], publicDescription: String) extends Const

/**
 * Number Constants
 */
trait NumberConst extends Const

final case class ByteConst(value: String) extends NumberConst

final case class ShortConst(value: String) extends NumberConst

final case class IntConst(value: String) extends NumberConst

final case class LongConst(value: String) extends NumberConst

final case class FloatConst(value: String) extends NumberConst

final case class DoubleConst(value: String) extends NumberConst

final case class DecimalConst(value: String) extends NumberConst

/**
 * Type Expression
 */
final case class TypeExp(t: Type) extends SnapiExp

/**
 * Let
 */

final case class Let(decls: Vector[LetDecl], e: Exp) extends SnapiExp

sealed abstract class LetDecl extends SnapiNode
final case class LetBind(e: Exp, i: IdnDef, t: Option[Type]) extends LetDecl
final case class LetFun(p: FunProto, i: IdnDef) extends LetDecl
final case class LetFunRec(i: IdnDef, p: FunProto) extends LetDecl

final case class FunProto(ps: Vector[FunParam], r: Option[Type], b: FunBody) extends SnapiNode

final case class FunParam(i: IdnDef, t: Option[Type], e: Option[Exp]) extends SnapiNode

// Body of function (defined as a separate node to have a separate scope).
final case class FunBody(e: Exp) extends SnapiNode

/**
 * Function Abstraction
 */

final case class FunAbs(p: FunProto) extends SnapiExp

/**
 * Function Application
 */

final case class FunApp(f: Exp, args: Vector[FunAppArg]) extends SnapiExp

final case class FunAppArg(e: Exp, idn: Option[String]) extends SnapiNode

/**
 * Projection
 */

final case class Proj(e: Exp, i: String) extends SnapiExp

/**
 * Unary Operators
 */

final case class UnaryExp(unaryOp: UnaryOp, exp: Exp) extends SnapiExp with PrettyUnaryExpression {
  override def op: String = unaryOp.op
  override def priority: Int = unaryOp.priority
  override def fixity: Fixity = unaryOp.fixity
}

sealed abstract class UnaryOp(val op: String, val priority: Int) extends SnapiNode {
  def fixity = Prefix
}

final case class Not() extends UnaryOp("not", 2)

final case class Neg() extends UnaryOp("-", 2)

/**
 * Binary Operators
 */
final case class BinaryExp(binaryOp: BinaryOp, left: Exp, right: Exp) extends SnapiExp with PrettyBinaryExpression {
  override def op: String = binaryOp.op
  override def priority: Int = binaryOp.priority
  override def fixity: Fixity = binaryOp.fixity
}

sealed abstract class BinaryOp(val op: String, val priority: Int) extends SnapiNode {
  def fixity = Infix(LeftAssoc)
}

final case class Plus() extends BinaryOp("+", 5)

final case class Sub() extends BinaryOp("-", 5)

final case class Mult() extends BinaryOp("*", 4)

final case class Div() extends BinaryOp("/", 4)

final case class Mod() extends BinaryOp("%", 4)

sealed abstract class ComparableOp(op: String, priority: Int) extends BinaryOp(op, priority)

final case class Ge() extends ComparableOp(">=", 7)

final case class Gt() extends ComparableOp(">", 7)

final case class Le() extends ComparableOp("<=", 7)

final case class Lt() extends ComparableOp("<", 7)

final case class Eq() extends ComparableOp("==", 8)

final case class Neq() extends ComparableOp("!=", 8)

sealed abstract class BooleanOp(op: String, priority: Int) extends BinaryOp(op, priority)

final case class And() extends BooleanOp("and", 12)

final case class Or() extends BooleanOp("or", 13)

/**
 * Tertiary Operators
 */

final case class IfThenElse(e1: Exp, e2: Exp, e3: Exp) extends SnapiExp

/**
 * PackageIdnExp
 *
 * Used to refer to built-in package names.
 */
final case class PackageIdnExp(name: String) extends SnapiExp
