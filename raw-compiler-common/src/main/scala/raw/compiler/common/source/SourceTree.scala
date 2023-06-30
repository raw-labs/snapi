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

package raw.compiler.common.source

import org.bitbucket.inkytonik.kiama.output.PrettyExpression
import org.bitbucket.inkytonik.kiama.rewriting.Strategy
import org.bitbucket.inkytonik.kiama.rewriting.Rewriter._
import raw.compiler.base.Counter
import raw.compiler.base.source._

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
//             Qual is more general than Stmt: a Gen is a Qual but not a Stmt while Bind is both??
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
sealed trait CommonNode extends SourceNode

/**
 * Parent of all "common language" types.
 */
sealed trait CommonType extends Type with CommonNode

/**
 * This type is used when the tree has errors.
 */
final case class ErrorType() extends CommonType

/**
 * Parent of all "common language" expressions.
 */
sealed trait CommonExp extends Exp with CommonNode

///////////////////////////////////////////////////////////////////////////
// Types
///////////////////////////////////////////////////////////////////////////

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
 * Location Type.
 * Used to represent a location and its properties. Refer to LocationDescription.
 */
final case class LocationType() extends CommonType

/**
 * InputStream Type.
 * Used for input readers.
 */
final case class InputStreamType() extends CommonType

/**
 * OutputStream Type.
 * Used for output writers, which directly write to an output stream.
 */
final case class OutputStreamType() extends CommonType

/** Void Type */
final case class VoidType() extends CommonType

/**
 * Primitive types
 */
sealed trait PrimitiveType extends CommonType

final case class BoolType() extends PrimitiveType

final case class StringType() extends PrimitiveType

final case class BinaryType() extends PrimitiveType

/**
 * Number types
 *
 * These are also primitive types.
 */
sealed trait NumberType extends PrimitiveType

final case class ByteType() extends NumberType

final case class ShortType() extends NumberType

final case class IntType() extends NumberType

final case class LongType() extends NumberType

final case class FloatType() extends NumberType

final case class DoubleType() extends NumberType

final case class DecimalType() extends NumberType

/**
 * Temporal types
 *
 * These are also primitive types.
 */
sealed trait TemporalType extends PrimitiveType

final case class DateType() extends TemporalType

final case class TimeType() extends TemporalType

final case class TimestampType() extends TemporalType

final case class IntervalType() extends TemporalType

/**
 * Record Type
 */
final case class RecordType(atts: Vector[AttrType]) extends CommonType
final case class AttrType(idn: String, tipe: Type) extends CommonNode

/**
 * Tuple Type
 *
 * This is a RecordType.
 */
object TupleType {
  def apply(ts: Type*): RecordType = {
    RecordType(ts.zipWithIndex.map { case (t, i) => AttrType(s"_${i + 1}", t) }.toVector)
  }

  def unapply(t: Type): Option[Vector[Type]] = t match {
    case RecordType(atts) if atts.zipWithIndex.forall { case (att, idx) => att.idn == s"_${idx + 1}" } =>
      Some(atts.map(_.tipe))
    case _ => None
  }
}

/**
 * Generator Type
 *
 * Generators are iterators.
 */
final case class GeneratorType(innerType: Type) extends CommonType

/**
 * Iterable Type
 */
final case class IterableType(innerType: Type) extends CommonType

/**
 * List Type
 *
 * Inherits from IterableType but provides index operations.
 */
final case class ListType(innerType: Type) extends CommonType

/**
 * Option Type
 */
final case class OptionType(innerType: Type) extends CommonType

/**
 * Try Type
 */
final case class TryType(innerType: Type) extends CommonType

/**
 * RDD Type
 */
final case class RDDType(inner: Type) extends CommonType

/**
 * Regex Type.
 *
 * @param n Number of groups
 */
final case class RegexType(n: Int) extends PrimitiveType

/**
 * Or Type.
 */
final case class OrType(tipes: Vector[Type]) extends CommonType

object OrType {
  def apply(tipes: Type*): OrType = {
    OrType(tipes.toVector)
  }
}

///////////////////////////////////////////////////////////////////////////
// Type Constraints
///////////////////////////////////////////////////////////////////////////

sealed trait CommonTypeConstraint extends CommonType

/**
 * One-of Type constraint.
 */
final case class OneOfType(tipes: Vector[Type]) extends CommonTypeConstraint

object OneOfType {
  def apply(tipes: Type*): OneOfType = {
    OneOfType(tipes.toVector)
  }
}

final case class ExpectedRegexType() extends CommonTypeConstraint

final case class ExpectedRecordType(idns: Set[String]) extends CommonTypeConstraint

final case class ExpectedFunType() extends CommonTypeConstraint

final case class CastableToType(t: Type) extends CommonTypeConstraint

///////////////////////////////////////////////////////////////////////////
// Identifiers
///////////////////////////////////////////////////////////////////////////

sealed abstract class CommonIdnNode extends BaseIdnNode with CommonNode

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

final case class Eval(program: RawBridge[BaseProgram]) extends CommonExp

final case class ErrorExp() extends CommonExp

///////////////////////////////////////////////////////////////////////////
// Intrinsics
///////////////////////////////////////////////////////////////////////////

sealed abstract class Intrinsic extends CommonNode

///////////////////////////////////////////////////////////////////////////
// Strings
///////////////////////////////////////////////////////////////////////////

final case class Trim() extends Intrinsic
final case class LTrim() extends Intrinsic
final case class RTrim() extends Intrinsic
final case class ReplaceStr() extends Intrinsic
final case class ReplaceRegex() extends Intrinsic
final case class Upper() extends Intrinsic
final case class Lower() extends Intrinsic
final case class Split() extends Intrinsic
final case class Reverse() extends Intrinsic
final case class Replicate() extends Intrinsic
final case class StartsWith() extends Intrinsic
final case class StrEmpty() extends Intrinsic
final case class Substr() extends Intrinsic
final case class Len() extends Intrinsic
final case class CountSubstr() extends Intrinsic
final case class LevenshteinDistance() extends Intrinsic
final case class DecodeString() extends Intrinsic
final case class EncodeString() extends Intrinsic
final case class Base64Encode() extends Intrinsic

///////////////////////////////////////////////////////////////////////////
// Temporals
///////////////////////////////////////////////////////////////////////////

final case class DateNow() extends Intrinsic
final case class DateYear() extends Intrinsic
final case class DateMonth() extends Intrinsic
final case class DateDay() extends Intrinsic
final case class LongToDate() extends Intrinsic
final case class StringToDate() extends Intrinsic
final case class DateTrunc() extends Intrinsic
final case class TryToDate() extends Intrinsic

final case class ToTime() extends Intrinsic
final case class TryToTime() extends Intrinsic
final case class THour() extends Intrinsic
final case class TMinute() extends Intrinsic
final case class TSecond() extends Intrinsic
final case class TMillis() extends Intrinsic
final case class TimeNow() extends Intrinsic

final case class IToMillis() extends Intrinsic
final case class MillisToI() extends Intrinsic

final case class ToTimestamp() extends Intrinsic
final case class TryToTimestamp() extends Intrinsic
final case class TsYear() extends Intrinsic
final case class TsMonth() extends Intrinsic
final case class TsDay() extends Intrinsic
final case class TsHour() extends Intrinsic
final case class TsMinute() extends Intrinsic
final case class TsSecond() extends Intrinsic
final case class TsMillis() extends Intrinsic
final case class CurrentTimestamp() extends Intrinsic

final case class ToInterval() extends Intrinsic

final case class TimeBucket() extends Intrinsic

final case class FromUnixTime() extends Intrinsic
final case class UnixTimestamp() extends Intrinsic

///////////////////////////////////////////////////////////////////////////
// Math
///////////////////////////////////////////////////////////////////////////

final case class Abs() extends Intrinsic
final case class Acos() extends Intrinsic
final case class Asin() extends Intrinsic
final case class Atan() extends Intrinsic
final case class Atn2() extends Intrinsic
final case class Ceiling() extends Intrinsic
final case class Cos() extends Intrinsic
final case class Cot() extends Intrinsic
final case class Degrees() extends Intrinsic
final case class Exponential() extends Intrinsic
final case class Floor() extends Intrinsic
final case class Log() extends Intrinsic
final case class Log10() extends Intrinsic
final case class Pi() extends Intrinsic
final case class Power() extends Intrinsic
final case class Radians() extends Intrinsic
final case class Rand() extends Intrinsic
final case class Round() extends Intrinsic
final case class Sign() extends Intrinsic
final case class Sin() extends Intrinsic
final case class Sqrt() extends Intrinsic
final case class Square() extends Intrinsic
final case class Tan() extends Intrinsic
final case class TestAccess() extends Intrinsic

///////////////////////////////////////////////////////////////////////////
// System Properties
///////////////////////////////////////////////////////////////////////////

final case class ReadIntProperty() extends Intrinsic
final case class ReadBoolProperty() extends Intrinsic
final case class ReadIntervalProperty() extends Intrinsic
final case class TryReadIntervalProperty() extends Intrinsic

///////////////////////////////////////////////////////////////////////////
// I/O
///////////////////////////////////////////////////////////////////////////

final case class HttpRequest() extends Intrinsic
final case class DescribeInternal() extends Intrinsic

///////////////////////////////////////////////////////////////////////////
// Environment
///////////////////////////////////////////////////////////////////////////

final case class ReadByteParam() extends Intrinsic
final case class ReadShortParam() extends Intrinsic
final case class ReadIntParam() extends Intrinsic
final case class ReadLongParam() extends Intrinsic
final case class ReadFloatParam() extends Intrinsic
final case class ReadDoubleParam() extends Intrinsic
final case class ReadDecimalParam() extends Intrinsic
final case class ReadBoolParam() extends Intrinsic
final case class ReadStringParam() extends Intrinsic
final case class ReadDateParam() extends Intrinsic
final case class ReadTimeParam() extends Intrinsic
final case class ReadTimestampParam() extends Intrinsic
final case class ReadIntervalParam() extends Intrinsic
final case class Scopes() extends Intrinsic
final case class Secret() extends Intrinsic
