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

package raw.compiler.rql2.source

import org.bitbucket.inkytonik.kiama.output._
import raw.compiler.base.source.Type
import raw.compiler.common.source._

///////////////////////////////////////////////////////////////////////////
// RQL2
///////////////////////////////////////////////////////////////////////////

sealed trait Rql2Node extends SourceNode

///////////////////////////////////////////////////////////////////////////
// Program
///////////////////////////////////////////////////////////////////////////

final case class Rql2Program(methods: Vector[Rql2Method], me: Option[Exp]) extends Rql2Node with SourceProgram {
  override val params: Vector[SourceProgramParam] = Vector.empty
}

object Rql2Program {
  def apply(e: Exp): Rql2Program = Rql2Program(Vector.empty, Some(e))
}

final case class Rql2Method(p: FunProto, i: IdnDef) extends Rql2Node

///////////////////////////////////////////////////////////////////////////
// Types
///////////////////////////////////////////////////////////////////////////

sealed trait Rql2Type extends Type with Rql2Node

sealed trait Rql2TypeProperty extends Rql2Node
final case class Rql2IsNullableTypeProperty() extends Rql2TypeProperty
final case class Rql2IsTryableTypeProperty() extends Rql2TypeProperty

/**
 * A subset of RQL2 types have type properties.
 */
sealed trait Rql2TypeWithProperties extends Rql2Type {
  def props: Set[Rql2TypeProperty]
  def cloneAndAddProp(p: Rql2TypeProperty): Type
  def cloneAndRemoveProp(p: Rql2TypeProperty): Type
}

final case class Rql2UndefinedType(props: Set[Rql2TypeProperty] = Set.empty) extends Rql2TypeWithProperties {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2UndefinedType(props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2UndefinedType(props - p)
}

final case class Rql2AnyType(
    props: Set[Rql2TypeProperty] = Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty())
) extends Rql2TypeWithProperties {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2AnyType(props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2AnyType(props - p)
}

/**
 * Primitive types
 */
sealed trait Rql2PrimitiveType extends Rql2TypeWithProperties

final case class Rql2BoolType(props: Set[Rql2TypeProperty] = Set.empty) extends Rql2PrimitiveType {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2BoolType(props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2BoolType(props - p)
}

final case class Rql2StringType(props: Set[Rql2TypeProperty] = Set.empty) extends Rql2PrimitiveType {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2StringType(props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2StringType(props - p)
}

final case class Rql2LocationType(props: Set[Rql2TypeProperty] = Set.empty) extends Rql2PrimitiveType {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2LocationType(props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2LocationType(props - p)
}

final case class Rql2BinaryType(props: Set[Rql2TypeProperty] = Set.empty) extends Rql2PrimitiveType {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2BinaryType(props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2BinaryType(props - p)
}

/**
 * Number types
 *
 * These are also primitive types.
 */
sealed trait Rql2NumberType extends Rql2PrimitiveType

sealed trait Rql2IntegralNumberType extends Rql2NumberType

final case class Rql2ByteType(props: Set[Rql2TypeProperty] = Set.empty) extends Rql2IntegralNumberType {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2ByteType(props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2ByteType(props - p)
}

final case class Rql2ShortType(props: Set[Rql2TypeProperty] = Set.empty) extends Rql2IntegralNumberType {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2ShortType(props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2ShortType(props - p)
}

final case class Rql2IntType(props: Set[Rql2TypeProperty] = Set.empty) extends Rql2IntegralNumberType {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2IntType(props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2IntType(props - p)
}

final case class Rql2LongType(props: Set[Rql2TypeProperty] = Set.empty) extends Rql2IntegralNumberType {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2LongType(props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2LongType(props - p)
}

final case class Rql2FloatType(props: Set[Rql2TypeProperty] = Set.empty) extends Rql2NumberType {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2FloatType(props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2FloatType(props - p)
}

final case class Rql2DoubleType(props: Set[Rql2TypeProperty] = Set.empty) extends Rql2NumberType {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2DoubleType(props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2DoubleType(props - p)
}

final case class Rql2DecimalType(props: Set[Rql2TypeProperty] = Set.empty) extends Rql2NumberType {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2DecimalType(props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2DecimalType(props - p)
}

/**
 * Temporal types
 *
 * These are also primitive types.
 */
sealed trait Rql2TemporalType extends Rql2PrimitiveType

final case class Rql2DateType(props: Set[Rql2TypeProperty] = Set.empty) extends Rql2TemporalType {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2DateType(props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2DateType(props - p)
}

final case class Rql2TimeType(props: Set[Rql2TypeProperty] = Set.empty) extends Rql2TemporalType {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2TimeType(props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2TimeType(props - p)
}

final case class Rql2TimestampType(props: Set[Rql2TypeProperty] = Set.empty) extends Rql2TemporalType {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2TimestampType(props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2TimestampType(props - p)
}

final case class Rql2IntervalType(props: Set[Rql2TypeProperty] = Set.empty) extends Rql2TemporalType {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2IntervalType(props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2IntervalType(props - p)
}

/**
 * Record Type
 */
final case class Rql2RecordType(atts: Vector[Rql2AttrType], props: Set[Rql2TypeProperty] = Set.empty)
    extends Rql2TypeWithProperties {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2RecordType(atts, props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2RecordType(atts, props - p)
}
final case class Rql2AttrType(idn: String, tipe: Type) extends Rql2Node

/**
 * Iterable Type
 */
final case class Rql2IterableType(innerType: Type, props: Set[Rql2TypeProperty] = Set.empty)
    extends Rql2TypeWithProperties {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2IterableType(innerType, props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2IterableType(innerType, props - p)
}

/**
 * List Type
 *
 * Inherits from IterableType but provides index operations.
 */
final case class Rql2ListType(innerType: Type, props: Set[Rql2TypeProperty] = Set.empty)
    extends Rql2TypeWithProperties {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2ListType(innerType, props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2ListType(innerType, props - p)
}

/**
 * Function Type.
 */
final case class FunType(
    ms: Vector[Type],
    os: Vector[FunOptTypeParam],
    r: Type,
    props: Set[Rql2TypeProperty] = Set.empty
) extends Rql2TypeWithProperties {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = FunType(ms, os, r, props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = FunType(ms, os, r, props - p)
}
final case class FunOptTypeParam(i: String, t: Type) extends Rql2Node

/**
 * Or Type.
 */
final case class Rql2OrType(tipes: Vector[Type], props: Set[Rql2TypeProperty] = Set.empty)
    extends Rql2TypeWithProperties {
  override def cloneAndAddProp(p: Rql2TypeProperty): Type = Rql2OrType(tipes, props + p)
  override def cloneAndRemoveProp(p: Rql2TypeProperty): Type = Rql2OrType(tipes, props - p)
}

object Rql2OrType {
  def apply(t1: Type, t2: Type, props: Set[Rql2TypeProperty]): Rql2OrType = {
    (t1, t2) match {
      case (Rql2OrType(tipes1, props1), Rql2OrType(tipes2, props2)) if props == props1 && props == props2 =>
        Rql2OrType(tipes1 ++ tipes2, props)
      case (Rql2OrType(tipes1, props1), _) if props == props1 => Rql2OrType(tipes1 :+ t2, props)
      case (_, Rql2OrType(tipes2, props2)) if props == props2 => Rql2OrType(Vector(t1) ++ tipes2, props)
      case _ => Rql2OrType(Vector(t1, t2), props)
    }
  }
}

/**
 * Package Type.
 */
final case class PackageType(name: String) extends Rql2Type
final case class PackageEntryType(pkgName: String, entName: String) extends Rql2Type

/**
 * Type Alias.
 */
final case class TypeAliasType(idn: IdnUse) extends Rql2Type

/**
 * Expression Type.
 *
 * The type of an expression such as `type int`.
 */
final case class ExpType(t: Type) extends Rql2Type

///////////////////////////////////////////////////////////////////////////
// Type Constraints
///////////////////////////////////////////////////////////////////////////

sealed trait Rql2TypeConstraint extends Rql2Type

final case class ExpectedProjType(i: String) extends Rql2TypeConstraint

final case class MergeableType(t: Type) extends Rql2TypeConstraint

final case class HasTypeProperties(props: Set[Rql2TypeProperty]) extends Rql2TypeConstraint

final case class IsTryable() extends Rql2TypeConstraint

final case class IsNullable() extends Rql2TypeConstraint

final case class DoesNotHaveTypeProperties(props: Set[Rql2TypeProperty]) extends Rql2TypeConstraint

///////////////////////////////////////////////////////////////////////////
// Expressions
///////////////////////////////////////////////////////////////////////////

sealed trait Rql2Exp extends Exp with Rql2Node

/**
 * Constants
 */

trait Const extends Rql2Exp

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
final case class TypeExp(t: Type) extends Rql2Exp

/**
 * Let
 */

final case class Let(decls: Vector[LetDecl], e: Exp) extends Rql2Exp

sealed abstract class LetDecl extends Rql2Node
final case class LetBind(e: Exp, i: IdnDef, t: Option[Type]) extends LetDecl
final case class LetFun(p: FunProto, i: IdnDef) extends LetDecl
final case class LetFunRec(i: IdnDef, p: FunProto) extends LetDecl

final case class FunProto(ps: Vector[FunParam], r: Option[Type], b: FunBody) extends Rql2Node

final case class FunParam(i: IdnDef, t: Option[Type], e: Option[Exp]) extends Rql2Node

// Body of function (defined as a separate node to have a separate scope).
final case class FunBody(e: Exp) extends Rql2Node

/**
 * Function Abstraction
 */

final case class FunAbs(p: FunProto) extends Rql2Exp

/**
 * Function Application
 */

final case class FunApp(f: Exp, args: Vector[FunAppArg]) extends Rql2Exp

final case class FunAppArg(e: Exp, idn: Option[String]) extends Rql2Node

/**
 * Projection
 */

final case class Proj(e: Exp, i: String) extends Rql2Exp

/**
 * Unary Operators
 */

final case class UnaryExp(unaryOp: UnaryOp, exp: Exp) extends Rql2Exp with PrettyUnaryExpression {
  override def op: String = unaryOp.op
  override def priority: Int = unaryOp.priority
  override def fixity: Fixity = unaryOp.fixity
}

sealed abstract class UnaryOp(val op: String, val priority: Int) extends Rql2Node {
  def fixity = Prefix
}

final case class Not() extends UnaryOp("not", 2)

final case class Neg() extends UnaryOp("-", 2)

/**
 * Binary Operators
 */
final case class BinaryExp(binaryOp: BinaryOp, left: Exp, right: Exp) extends Rql2Exp with PrettyBinaryExpression {
  override def op: String = binaryOp.op
  override def priority: Int = binaryOp.priority
  override def fixity: Fixity = binaryOp.fixity
}

sealed abstract class BinaryOp(val op: String, val priority: Int) extends Rql2Node {
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

final case class IfThenElse(e1: Exp, e2: Exp, e3: Exp) extends Rql2Exp

/**
 * PackageIdnExp
 *
 * Used to refer to built-in package names.
 */
final case class PackageIdnExp(name: String) extends Rql2Exp
