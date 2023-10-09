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

package raw.compiler.rql2

import com.typesafe.scalalogging.StrictLogging
import org.bitbucket.inkytonik.kiama.==>
import org.bitbucket.inkytonik.kiama.rewriting.Rewriter._
import org.bitbucket.inkytonik.kiama.util.Entity
import raw.compiler.api.{CompilerServiceProvider, EvalRuntimeFailure, EvalSuccess, EvalValidationFailure}
import raw.compiler.base._
import raw.compiler.base.errors._
import raw.compiler.base.source._
import raw.compiler.common
import raw.compiler.common.ProgramParamEntity
import raw.compiler.common.source._
import raw.compiler.rql2.api.{
  Arg,
  EntryExtension,
  ExpArg,
  ExpParam,
  PackageExtensionProvider,
  TypeArg,
  TypeParam,
  ValueArg,
  ValueParam
}
import raw.compiler.rql2.builtin.TypePackageBuilder
import raw.compiler.rql2.errors.{
  CannotDetermineTypeOfParameter,
  ExpectedTypeButGotExpression,
  FailedToEvaluate,
  FunctionOrMethodExpected,
  InvalidType,
  MandatoryArgumentAfterOptionalArgument,
  MandatoryArgumentsMissing,
  NamedParameterAfterOptionalParameter,
  NoOptionalArgumentsExpected,
  OutputTypeRequiredForRecursiveFunction,
  PackageNotFound,
  RepeatedFieldNames,
  RepeatedOptionalArguments,
  UnexpectedArguments,
  UnexpectedOptionalArgument
}
import raw.compiler.rql2.source._
import raw.runtime.ProgramEnvironment
import raw.runtime.interpreter._

import scala.collection.mutable
import scala.util.control.NonFatal

case class FunAppPackageEntryArguments(
    mandatoryArgs: Seq[Arg],
    optionalArgs: Vector[(String, Arg)],
    varArgs: Seq[Arg],
    extraProps: Set[Rql2TypeProperty]
)

final private class MergeTypeException extends Exception

/* 't' is the *clean* target type, 'effective' is the actualType, fixed through regular inference (compatible with 't' but
possibly too much try/null inside */
final case class CompatibilityReport(t: Type, effective: Type) {

  // These are the properties that are brought up by any unexpected property nested (or not) in the actual type.
  // Iterables are treated in a special way: one doesn't recurse in them because their items aren't processed in
  // the moment the argument is passed. An unexpected `null` in the middle of an iterable won't be hit in the moment
  // of the call, therefore its presence doesn't imply the call should be protected. The `null` is going to be
  // hit when consuming the iterable. This is then up to the consumer to protect itself from the error. An iterable consumer
  // should always handle a possible unexpected null/error because it's the one consuming. It can type as an try itself
  // in order to propagate the error, or handle it in some way.
  def extraProps: Set[Rql2TypeProperty] = {

    def recurse(target: Type, effective: Type): Set[Rql2TypeProperty] = (target, effective) match {
      case (Rql2ListType(inner1, props1), Rql2ListType(inner2, props2)) => recurse(inner1, inner2) ++ (props2 &~ props1)
      case (Rql2IterableType(inner1, props1), Rql2IterableType(inner2, props2)) => (props2 &~ props1)
      case (Rql2RecordType(atts1, props1), Rql2RecordType(atts2, props2)) =>
        val tipes1 = atts1.map(_.tipe)
        val tipes2 = atts2.map(_.tipe)
        assert(tipes1.size == tipes2.size)
        tipes1.zip(tipes2).flatMap { case (t1, t2) => recurse(t1, t2) }.toSet ++ (props2 &~ props1)
      case (t1: Rql2TypeWithProperties, t2: Rql2TypeWithProperties) => t2.props &~ t1.props
      case (_: PackageEntryType, _: PackageEntryType) => Set.empty
      case (_: PackageType, _: PackageType) => Set.empty
      case _ => Set.empty
    }
    recurse(t, effective)
  }

  // castNeeded is pretty much a way to specify some extra property is to be expected (see extraProps). But it's recursing
  // through iterables because an iterable has to be cast to its expected type if its items bring extra properties.
  // The logic for casting is:
  // If castNeeded is false, don't cast, don't handle possible errors.
  // If castNeeded is true _and_ no extraProps are found, use the regular `cast`. That will cast iterables without failing.
  // If castNeeded is true _and_ extraProps are found, use ProtectCast since unexpected null/try are possible in the moment
  // of calling.
  // TODO (bgaidioz) Consider moving that logic in Propagation? It's only used because of that feature.
  def castNeeded: Boolean = {

    def recurse(target: Type, effective: Type): Boolean = (target, effective) match {
      case (Rql2ListType(inner1, props1), Rql2ListType(inner2, props2)) =>
        recurse(inner1, inner2) || (props2 &~ props1).nonEmpty
      case (Rql2IterableType(inner1, props1), Rql2IterableType(inner2, props2)) =>
        recurse(inner1, inner2) || (props2 &~ props1).nonEmpty
      case (Rql2RecordType(atts1, props1), Rql2RecordType(atts2, props2)) =>
        val tipes1 = atts1.map(_.tipe)
        val tipes2 = atts2.map(_.tipe)
        assert(tipes1.size == tipes2.size)
        tipes1.zip(tipes2).exists { case (t1, t2) => recurse(t1, t2) } || (props2 &~ props1).nonEmpty
      case (t1: Rql2TypeWithProperties, t2: Rql2TypeWithProperties) => (t2.props &~ t1.props).nonEmpty
      case (_: PackageEntryType, _: PackageEntryType) => false
      case (_: PackageType, _: PackageType) => false
      case _ => false
    }

    recurse(t, effective)
  }

}

// Helper to merge types.
// Does not handle PackageType or TypeAlias, since these are never exposed to Package extensions.
class TypesMerger extends Rql2TypeUtils with StrictLogging {

  final def isCompatible(actual: Type, expected: Type): Boolean = {
    getCompatibleType(actual, expected).isDefined
  }

  final def mergeType(ts: Type*): Option[Type] = {

    assert(ts.forall(t => !hasTypeConstraint(t)))

    def merge(t1: Type, t2: Type): Option[Type] = {
      // For complex types (records, collections), get the inner types as merged and
      // wrap back, merging the properties
      (t1, t2) match {
        case (Rql2RecordType(atts1, props1), Rql2RecordType(atts2, props2)) =>
          if (atts1.size != atts2.size) None
          else {
            val mergedAttrs =
              atts1.zip(atts2).foldLeft(Some(Vector.empty[Rql2AttrType]): Option[Vector[Rql2AttrType]]) {
                case (Some(atts), (Rql2AttrType(f1, i1), Rql2AttrType(f2, i2))) =>
                  if (f1 == f2) {
                    merge(i1, i2).map(t => atts :+ Rql2AttrType(f1, t))
                  } else None
                case _ => None
              }
            mergedAttrs.map(atts => Rql2RecordType(atts, props1 ++ props2))
          }
        case (Rql2ListType(i1, props1), Rql2ListType(i2, props2)) =>
          merge(i1, i2).map(t => Rql2ListType(t, props1 ++ props2))
        case (Rql2IterableType(i1, props1), Rql2IterableType(i2, props2)) =>
          merge(i1, i2).map(t => Rql2IterableType(t, props1 ++ props2))
        case _ =>
          // The merged type of two primitive types:
          // * Call getCompatibleType after having removed properties, _both ways_
          // * Whichever successful call returns the merged type, without the properties.
          // * Add the properties (merge both)
          getCompatibleType(resetProps(t1, Set.empty), resetProps(t2, Set.empty))
            .orElse(
              getCompatibleType(resetProps(t2, Set.empty), resetProps(t1, Set.empty))
            )
            .map(t => resetProps(t, getProps(t1) ++ getProps(t2)))
      }
    }

    ts.tail.foldLeft(ts.headOption) { case (maybeType, t2) => maybeType.flatMap(t1 => merge(t1, t2)) }
  }

  final def getCompatibleType(actual: Type, expected: Type): Option[Type] = {
    assert(!hasTypeConstraint(actual), s"Type constraint found where actual type expected: $actual")
    try {
      val t = recurse(actual, expected)
      Some(t)
    } catch {
      case _: MergeTypeException => None
    }
  }

  // This function is comparing types in a strict manner (types have to be the same), but is relatively flexible regarding
  // attributes: if `actual` has all attributes of `expected`, then it's OK.
  def propertyCompatible(actual: Type, expected: Type): Boolean = {
    assert(!isTypeConstraint(expected))
    (actual, expected) match {
      case (Rql2ListType(actualInner, actualProps), Rql2ListType(expectedInner, expectedProps)) =>
        expectedProps.subsetOf(actualProps) && propertyCompatible(actualInner, expectedInner)
      case (Rql2IterableType(actualInner, actualProps), Rql2IterableType(expectedInner, expectedProps)) =>
        expectedProps.subsetOf(actualProps) && propertyCompatible(actualInner, expectedInner)
      case (Rql2RecordType(actualAtts, actualProps), Rql2RecordType(expectedAtts, expectedProps)) =>
        // same field names, compatible properties and matching field types
        actualAtts.map(_.idn) == expectedAtts.map(_.idn) && (actualProps.intersect(expectedProps) == expectedProps) &&
          actualAtts.map(_.tipe).zip(expectedAtts.map(_.tipe)).forall {
            case (aField, eField) => propertyCompatible(aField, eField)
          }
      case _ =>
        val actualProps = getProps(actual)
        val expectedProps = getProps(expected)
        // compatible properties and, should be the same type
        expectedProps
          .subsetOf(actualProps) && resetProps(actual, expectedProps) == expected
    }
  }

  def funParamTypeCompatibility(actual: Type, expected: Type): Option[CompatibilityReport] = {
    assert(!hasTypeConstraint(actual), s"Type constraint found where actual type expected: $actual")
    if (isTypeConstraint(expected)) {
      (actual, expected) match {
        case (_, IsTryable()) =>
          // if we expected the type to be tryable (of anything), the target is the actual type as tryable (possibly nullable)
          Some(
            CompatibilityReport(
              addProp(actual, Rql2IsTryableTypeProperty()),
              actual
            )
          )
        case (_, IsNullable()) =>
          // if we expected the type to be nullable (of anything), the target is the actual type as nullable, and _not tryable_
          Some(
            CompatibilityReport(
              resetProps(actual, Set(Rql2IsNullableTypeProperty())),
              actual
            )
          )
        case (_, HasTypeProperties(props)) =>
          // Whichever extra property will be eventually added by
          // wrapping the object in Some/Success.
          Some(CompatibilityReport(resetProps(actual, props), actual))
        case (_, DoesNotHaveTypeProperties(props)) if props.intersect(getProps(actual)).isEmpty =>
          Some(CompatibilityReport(actual, actual))
        case (_, OneOfType(expectedTypes)) =>
          expectedTypes.flatMap(t => funParamTypeCompatibility(actual, t)).headOption
        case (_, MergeableType(target)) => mergeType(actual, target).map(m => CompatibilityReport(m, m)) // m
        case (Rql2RecordType(as, _), ExpectedRecordType(idns)) if idns.subsetOf(as.map(_.idn).toSet) =>
          Some(CompatibilityReport(resetProps(actual, Set.empty), actual))
        case (r: Rql2RecordType, e: ExpectedProjType) if r.atts.exists(att => att.idn == e.i) =>
          Some(CompatibilityReport(actual, actual))
        case (_: Rql2RegexType, ExpectedRegexType()) => Some(CompatibilityReport(actual, actual))
        case (_, _: AnythingType) => Some(CompatibilityReport(actual, actual))
        case _ => None
      }
    } else {
      try {
        (actual, expected) match {
          case (Rql2ListType(actualItemType, actualProps), Rql2ListType(expectedItemType, expectedProps)) =>
            funParamTypeCompatibility(actualItemType, expectedItemType).map { report =>
              CompatibilityReport(
                Rql2ListType(report.t, expectedProps),
                Rql2ListType(report.effective, actualProps)
              )
            }
          case (Rql2IterableType(actualItemType, actualProps), Rql2IterableType(expectedItemType, expectedProps)) =>
            val r = funParamTypeCompatibility(actualItemType, expectedItemType)
            r.map { report =>
              CompatibilityReport(
                Rql2IterableType(report.t, expectedProps),
                Rql2IterableType(report.effective, actualProps)
              )
            }
          case (Rql2RecordType(actualAtts, actualProps), Rql2RecordType(expectedAtts, expectedProps)) =>
            if (actualAtts.size != expectedAtts.size || actualAtts.map(_.idn) != expectedAtts.map(_.idn)) None
            else {
              val actualTypes = actualAtts.map(_.tipe)
              val expectedTypes = expectedAtts.map(_.tipe)
              val fieldChecks =
                actualTypes.zip(expectedTypes).map { case (t1, t2) => funParamTypeCompatibility(t1, t2) }
              if (fieldChecks.forall(_.isDefined)) {
                val okChecks = fieldChecks.flatten
                val effectiveFields = actualAtts.zip(okChecks).map { case (a, c) => Rql2AttrType(a.idn, c.effective) }
                val targetFields = actualAtts.zip(okChecks).map { case (a, c) => Rql2AttrType(a.idn, c.t) }
                Some(
                  CompatibilityReport(
                    Rql2RecordType(targetFields, expectedProps),
                    Rql2RecordType(effectiveFields, actualProps)
                  )
                )
              } else None
            }
          case (a: FunType, e: FunType) if a.ms.size == e.ms.size && a.os.isEmpty =>
            // We're typechecking a parameter of a package extension _that is a function_ (e.g. f in List.Transform(l, f)).
            // We need some flexibility regarding parameters of that function. If the expected parameter type
            // of the function isn't tryable/nullable (e.g. a plain list(int) where both the list and int
            // aren't null/try, then the actual parameter type of the actual function passed as a parameter, can be
            // a nullable list of nullable int (all combinations of flags), because later that function can always be
            // applied to a list(int) value (casting the value to null/try is simple).
            val params = a.ms.zip(e.ms).map {
              case (a, e) =>
                if (!isTypeConstraint(e)) {
                  // if 'e' is a type, it has to be equal to 'a' modulo the properties
                  if (propertyCompatible(a, e)) e
                  else { return None }
                } else {
                  // `e` is a constraint. We can use funParamTypeCompatibility which deals with
                  // extra properties and merges `a` with `e` in case it's a contraint
                  funParamTypeCompatibility(a, e) match {
                    case Some(report) => report.t
                    case _ => return None
                  }
                }
            }
            val t = FunType(
              params,
              Vector.empty,
              recurse(a.r, e.r),
              e.props
            )
            Some(CompatibilityReport(t, actual))
          case _ =>
            val extraProps = getProps(actual) &~ getProps(expected)
            val simplified = removeProps(actual, extraProps)
            val t = recurse(simplified, expected)
            Some(CompatibilityReport(t, addProps(t, extraProps)))
        }
      } catch {
        case _: MergeTypeException =>
          // would be weird
          None
      }
    }
  }

  @throws[MergeTypeException]
  protected def recurse(actual: Type, expected: Type): Type = (actual, expected) match {
    // Upcast to short
    case (a: Rql2ByteType, e: Rql2ShortType) if a.props.subsetOf(e.props) => Rql2ShortType(e.props)
    // Update to int
    case (a: Rql2ByteType, e: Rql2IntType) if a.props.subsetOf(e.props) => Rql2IntType(e.props)
    case (a: Rql2ShortType, e: Rql2IntType) if a.props.subsetOf(e.props) => Rql2IntType(e.props)
    // Upcast to long
    case (a: Rql2ByteType, e: Rql2LongType) if a.props.subsetOf(e.props) => Rql2LongType(e.props)
    case (a: Rql2ShortType, e: Rql2LongType) if a.props.subsetOf(e.props) => Rql2LongType(e.props)
    case (a: Rql2IntType, e: Rql2LongType) if a.props.subsetOf(e.props) => Rql2LongType(e.props)
    // Upcast to float
    case (a: Rql2ByteType, e: Rql2FloatType) if a.props.subsetOf(e.props) => Rql2FloatType(e.props)
    case (a: Rql2ShortType, e: Rql2FloatType) if a.props.subsetOf(e.props) => Rql2FloatType(e.props)
    case (a: Rql2IntType, e: Rql2FloatType) if a.props.subsetOf(e.props) => Rql2FloatType(e.props)
    case (a: Rql2LongType, e: Rql2FloatType) if a.props.subsetOf(e.props) => Rql2FloatType(e.props)
    // Upcast to double
    case (a: Rql2ByteType, e: Rql2DoubleType) if a.props.subsetOf(e.props) => Rql2DoubleType(e.props)
    case (a: Rql2ShortType, e: Rql2DoubleType) if a.props.subsetOf(e.props) => Rql2DoubleType(e.props)
    case (a: Rql2IntType, e: Rql2DoubleType) if a.props.subsetOf(e.props) => Rql2DoubleType(e.props)
    case (a: Rql2LongType, e: Rql2DoubleType) if a.props.subsetOf(e.props) => Rql2DoubleType(e.props)
    case (a: Rql2FloatType, e: Rql2DoubleType) if a.props.subsetOf(e.props) => Rql2DoubleType(e.props)
    // Upcast do decimal
    case (a: Rql2ByteType, e: Rql2DecimalType) if a.props.subsetOf(e.props) => Rql2DecimalType(e.props)
    case (a: Rql2ShortType, e: Rql2DecimalType) if a.props.subsetOf(e.props) => Rql2DecimalType(e.props)
    case (a: Rql2IntType, e: Rql2DecimalType) if a.props.subsetOf(e.props) => Rql2DecimalType(e.props)
    case (a: Rql2LongType, e: Rql2DecimalType) if a.props.subsetOf(e.props) => Rql2DecimalType(e.props)
    case (a: Rql2FloatType, e: Rql2DecimalType) if a.props.subsetOf(e.props) => Rql2DecimalType(e.props)
    case (a: Rql2DoubleType, e: Rql2DecimalType) if a.props.subsetOf(e.props) => Rql2DecimalType(e.props)
    // Upcast to timestamp
    case (a: Rql2DateType, e: Rql2TimestampType) if a.props.subsetOf(e.props) => Rql2TimestampType(e.props)
    // Upcast to location: a string can be used as a location type (without any properties)
    case (a: Rql2StringType, e: Rql2LocationType) if a.props.subsetOf(e.props) => Rql2LocationType(e.props)
    // Bool type
    case (a: Rql2BoolType, e: Rql2BoolType) if a.props.subsetOf(e.props) => Rql2BoolType(e.props)
    // String type
    case (a: Rql2StringType, e: Rql2StringType) if a.props.subsetOf(e.props) => Rql2StringType(e.props)
    // Location type
    case (a: Rql2LocationType, e: Rql2LocationType) if a.props.subsetOf(e.props) => Rql2LocationType(e.props)
    // Binary type
    case (a: Rql2BinaryType, e: Rql2BinaryType) if a.props.subsetOf(e.props) => Rql2BinaryType(e.props)
    // Byte type
    case (a: Rql2ByteType, e: Rql2ByteType) if a.props.subsetOf(e.props) => Rql2ByteType(e.props)
    // Short type
    case (a: Rql2ShortType, e: Rql2ShortType) if a.props.subsetOf(e.props) => Rql2ShortType(e.props)
    // Int type
    case (a: Rql2IntType, e: Rql2IntType) if a.props.subsetOf(e.props) => Rql2IntType(e.props)
    // Long type
    case (a: Rql2LongType, e: Rql2LongType) if a.props.subsetOf(e.props) => Rql2LongType(e.props)
    // Float type
    case (a: Rql2FloatType, e: Rql2FloatType) if a.props.subsetOf(e.props) => Rql2FloatType(e.props)
    // Double type
    case (a: Rql2DoubleType, e: Rql2DoubleType) if a.props.subsetOf(e.props) => Rql2DoubleType(e.props)
    // Decimal type
    case (a: Rql2DecimalType, e: Rql2DecimalType) if a.props.subsetOf(e.props) => Rql2DecimalType(e.props)
    // Date type
    case (a: Rql2DateType, e: Rql2DateType) if a.props.subsetOf(e.props) => Rql2DateType(e.props)
    // Time type
    case (a: Rql2TimeType, e: Rql2TimeType) if a.props.subsetOf(e.props) => Rql2TimeType(e.props)
    // Timestamp type
    case (a: Rql2TimestampType, e: Rql2TimestampType) if a.props.subsetOf(e.props) => Rql2TimestampType(e.props)
    // Interval type
    case (a: Rql2IntervalType, e: Rql2IntervalType) if a.props.subsetOf(e.props) => Rql2IntervalType(e.props)
    // Regex type
    case (a: Rql2RegexType, e: Rql2RegexType) if a.n == e.n && a.props.subsetOf(e.props) => Rql2RegexType(a.n, e.props)
    // Record Type
    case (a: Rql2RecordType, e: Rql2RecordType)
        if a.atts.map(_.idn) == e.atts.map(_.idn) && a.props.subsetOf(e.props) =>
      Rql2RecordType(
        a.atts.zip(e.atts).map { case (a, e) => Rql2AttrType(a.idn, recurse(a.tipe, e.tipe)) },
        e.props
      )
    // Iterable Type
    case (a: Rql2IterableType, e: Rql2IterableType) if a.props.subsetOf(e.props) =>
      Rql2IterableType(recurse(a.innerType, e.innerType), e.props)
    // List Type
    case (a: Rql2ListType, e: Rql2ListType) if a.props.subsetOf(e.props) =>
      Rql2ListType(recurse(a.innerType, e.innerType), e.props)
    // FunType
    // We're strict with FunType. Function type arguments are handled with a relaxed semantic when they are arguments
    // of package extensions (accepting nullables/tryables, even for their parameter types). The case here is to process
    // function type arguments of user-defined functions.
    case (a: FunType, e: FunType) if a.ms == e.ms && a.os == e.os => FunType(e.ms, e.os, recurse(a.r, e.r), e.props)
    // Or Type
    case (Rql2OrType(as, _), Rql2OrType(es, _)) =>
      if (as.forall(a => es.exists(e => getCompatibleType(a, e).isDefined))) actual
      else throw new MergeTypeException
    case (a: Rql2UndefinedType, e) if !hasTypeConstraint(e) && a.props.subsetOf(getProps(e)) =>
      // Hit here
      e
    // ExpType
    case (a: ExpType, e: ExpType) => ExpType(recurse(a.t, e.t))
    //
    // Type Constraints
    //
    case (a: Rql2TypeWithProperties, IsTryable()) => addProp(a, Rql2IsTryableTypeProperty())
    case (a: Rql2TypeWithProperties, IsNullable()) => addProp(a, Rql2IsNullableTypeProperty())
    case (a: Rql2TypeWithProperties, HasTypeProperties(props)) if a.props.subsetOf(props) => addProps(actual, props)
    case (a: Rql2TypeWithProperties, DoesNotHaveTypeProperties(props)) if props.intersect(a.props).isEmpty => actual
    case (_, OneOfType(expectedTypes)) =>
//      assert(expectedTypes.forall(t => !isTypeConstraint(t)), s"Type constraint found in OneOfType: $expectedTypes")
      // We need to merge it (using getCompatibleType) with the actual type to get the target type.
      // For example is expected type is OneOfType(StringType, FloatType()) but actual is IntType(),
      // then our final type  must be FloatType(), which is the merge of IntType() with FloatType().
      // Note that we take the first one that works (that's done by headOption).
      expectedTypes.flatMap(t => getCompatibleType(actual, t)).headOption.getOrElse(throw new MergeTypeException)
    case (_, MergeableType(t)) => mergeType(actual, t).getOrElse(throw new MergeTypeException)
    // Type Constraints are different because they never "merge". They just return the actual type unchanged.
    case (Rql2RecordType(as, _), ExpectedRecordType(idns)) if idns.subsetOf(as.map(_.idn).toSet) => actual
    case (r: Rql2RecordType, e: ExpectedProjType) if r.atts.exists(att => att.idn == e.i) => actual
    // Projection on lists or collections: collection.name same as Collection.Transform(collection, x -> x.name)
    case (Rql2IterableType(r: Rql2RecordType, _), e: ExpectedProjType) if r.atts.exists(att => att.idn == e.i) => actual
    case (Rql2ListType(r: Rql2RecordType, _), e: ExpectedProjType) if r.atts.exists(att => att.idn == e.i) => actual
    case (_: Rql2RegexType, ExpectedRegexType()) => actual
    case (_, _: AnythingType) => actual
    //
    // Base Types
    //
    case (_: NothingType, _) =>
      // This is required so that e.g. List.Build(List.Build(1), List.Build()) works.
      // This means ListType(NothingType()) merges with List(IntType()) but returns ListType(NothingType()) though.
      // Note that at this point, type constraints (above) should have been resolved, so expected should be a real type.
      expected
    case (_, _: AnyType) => actual
    case (_: ErrorType, _) =>
      // By making ErrorType compatible with all types, we make the type checker silent and not report a cascade of
      // subsequent errors.
      actual
    case _ => throw new MergeTypeException
  }

}

class SemanticAnalyzer(val tree: SourceTree.SourceTree)(implicit programContext: ProgramContext)
    extends common.SemanticAnalyzer(tree)
    with ExpectedTypes
    with Rql2TypeUtils {

  ///////////////////////////////////////////////////////////////////////////
  // Errors
  ///////////////////////////////////////////////////////////////////////////

  override protected def errorDef: SourceNode ==> Seq[BaseError] = {
    val rql2Errors: PartialFunction[SourceNode, Seq[BaseError]] = {
      case i: IdnUse if entity(i) == UnknownEntity() =>
        i match {
          // Try to see if this UnknownEntity is the user attempting to reference a package name but making a typo.
          // For this we must match the pattern Proj(IdnExp("BadPackageName"), "BadEntryName").
          case tree.parent(idnExp @ IdnExp(IdnUse(badPackageName))) => idnExp match {
              case tree.parent(Proj(_, badEntryName))
                  if badPackageName.nonEmpty && badPackageName.head.isUpper && badEntryName.nonEmpty && badEntryName.head.isUpper =>
                val badName = s"$badPackageName.$badEntryName"
                val names = PackageExtensionProvider.packages(programContext.compilerContext.maybeClassLoader).flatMap {
                  case p => p.p.entries.collect {
                      case e if levenshteinDistance(badName, s"${p.p.name}.$e") < 3 => s"${p.p.name}.$e"
                    }
                }
                if (names.isEmpty) {
                  // No found based on levenshtein distance. Try to see if there is any entry name in another package that
                  // is a perfect match. For instance, if the user does String.IsNull instead of Nullable.IsNull,
                  // or Text.Split instead of String.Split. The criteria is that the entry name must be a perfect match
                  // but defined in a single other package.
                  val packagesWithEntry =
                    PackageExtensionProvider.packages(programContext.compilerContext.maybeClassLoader).flatMap {
                      case p => p.p.entries.collect {
                          case e if badEntryName == e => p.p.name
                        }
                    }
                  if (packagesWithEntry.length == 1)
                    Seq(UnknownDecl(i, hint = Some(s"did you mean ${packagesWithEntry.head}.$badEntryName?")))
                  else Seq(UnknownDecl(i))
                } else Seq(UnknownDecl(i, hint = Some(s"did you mean ${names.mkString(" or ")}?")))
              case _ =>
                // Otherwise, check to see if there is any variable in scope whose name is again close enough.
                val names = envInOfNode(i).flatten
                  .filterNot(_._2.isInstanceOf[PackageEntity]) // Do not auto-suggest package names.
                  .map { case (s, _) => s }
                  .collect {
                    case e: String if levenshteinDistance(i.idn, e) < 3 => e
                  }
                  .toSet
                if (names.isEmpty) Seq(UnknownDecl(i))
                else Seq(UnknownDecl(i, hint = Some(s"did you mean ${names.mkString(" or ")}?")))
            }
          case _ => Seq(UnknownDecl(i))
        }
      case t: TypeAliasType if resolveType(t) == ErrorType() => Seq(InvalidType(t))
      case p @ FunParam(_, None, _) if resolveParamType(p).isLeft => resolveParamType(p).left.get
      case LetFunRec(i, FunProto(_, None, _)) => Seq(OutputTypeRequiredForRecursiveFunction(i))
      case f @ FunProto(ps, _, _) if namedParameterFoundAfterOptionalParameter(ps) =>
        Seq(NamedParameterAfterOptionalParameter(f))
      case f @ FunApp(fa, args) if repeatedOptionalArgument(fa, args) => Seq(RepeatedOptionalArguments(f))
      case f: FunApp if funAppHasError(f).isDefined => funAppHasError(f).get
      case p @ Proj(e, idn) if idnIsAmbiguous(idn, e) => Seq(RepeatedFieldNames(p, idn))
    }
    rql2Errors.orElse(super.errorDef)
  }

  private def idnIsAmbiguous(idn: String, e: Exp): Boolean = {
    actualType(e) match {
      case Rql2RecordType(atts, _) => atts.collect { case att if att.idn == idn => att }.length > 1
      case Rql2IterableType(Rql2RecordType(atts, _), _) => atts.collect { case att if att.idn == idn => att }.length > 1
      case Rql2ListType(Rql2RecordType(atts, _), _) => atts.collect { case att if att.idn == idn => att }.length > 1
      case _ => false
    }
  }

  private def namedParameterFoundAfterOptionalParameter(ps: Vector[FunParam]): Boolean = {
    var optionalStarted = false
    for (p <- ps) {
      if (optionalStarted && p.e.isEmpty) {
        return true
      }
      if (p.e.isDefined) {
        optionalStarted = true
      }
    }
    // All ok.
    false
  }

  private def repeatedOptionalArgument(fa: Exp, as: Vector[FunAppArg]): Boolean = {
    actualType(fa) match {
      case _: FunType =>
        // For calling RQL2 functions, we check if arguments are unique.
        val idns = as.collect { case FunAppArg(_, Some(idn)) => idn }
        idns.toSet.size != idns.length
      case PackageEntryType(pkgName, entName) =>
        // For packages, we check if it's actually allowed.
        programContext.getPackage(pkgName) match {
          case Some(p) =>
            if (p.existsEntry(entName)) {
              val entry = p.getEntry(entName)
              if (entry.allowRepeatedOptionalArguments) {
                // Do not complain on repeated optional arguments.
                false
              } else {
                // Otherwise, check for repeated optional arguments.
                val idns = as.collect { case FunAppArg(_, Some(idn)) => idn }
                idns.toSet.size != idns.length
              }
            } else {
              // Keep quiet on errors
              false
            }
          case _ =>
            // Keep quiet on errors
            false
        }
      case _ =>
        // Keep quiet on errors
        false
    }
  }

  private def namedArgFoundAfterOptionalArg(args: Vector[FunAppArg]): Boolean = {
    var optionalStarted = false
    for (a <- args) {
      if (optionalStarted && a.idn.isEmpty) {
        return true
      }
      if (a.idn.isDefined) {
        optionalStarted = true
      }
    }
    // All ok.
    false
  }

  private def funAppHasError(fa: FunApp): Option[Seq[BaseError]] = {
    val FunApp(f, args) = fa
    // Check that arguments are defined in correct order.
    if (namedArgFoundAfterOptionalArg(args)) {
      return Some(Seq(MandatoryArgumentAfterOptionalArgument(f)))
    }
    // Now type check function.
    actualType(f) match {
      case PackageEntryType(pkgName, entName) => getFunAppPackageEntryType(fa, pkgName, entName).left.toOption
      case f: FunType => getFunAppFunType(fa, f).left.toOption.map(Seq(_))
      case t => t match {
          case _: ErrorType =>
            // Error flagged elsewhere so keep quiet.
            None
          case _ => Some(Seq(FunctionOrMethodExpected(f, t)))
        }
    }
  }

  private def levenshteinDistance(s1: String, s2: String): Int = {
    val dist = Array.tabulate(s2.length + 1, s1.length + 1)((j, i) => if (j == 0) i else if (i == 0) j else 0)

    @inline
    def minimum(i: Int*): Int = i.min

    for {
      j <- dist.indices.tail
      i <- dist(0).indices.tail
    } dist(j)(i) =
      if (s2(j - 1) == s1(i - 1)) dist(j - 1)(i - 1)
      else minimum(dist(j - 1)(i) + 1, dist(j)(i - 1) + 1, dist(j - 1)(i - 1) + 1)

    dist(s2.length)(s1.length)
  }
  ///////////////////////////////////////////////////////////////////////////
  // Scopes for Expressions
  ///////////////////////////////////////////////////////////////////////////

  override protected lazy val defenv: Environment = rootenv(
    PackageExtensionProvider.packages(programContext.compilerContext.maybeClassLoader).map(p => p.p.name -> p): _*
  )

  override protected def envin(in: SourceNode => Environment): SourceNode ==> Environment =
    envinDef(in) orElse super.envin(in)

  override protected def envout(out: SourceNode => Environment): SourceNode ==> Environment =
    envoutDef(out) orElse super.envout(out)

  private def envinDef(in: SourceNode => Environment): SourceNode ==> Environment = {
    case l: Let => enter(in(l))
    case p: FunProto => enter(in(p))
    case b: FunBody => enter(in(b))
    case f: FunAbs => enter(in(f))
//    case tree.parent.pair(_: Exp, f: FunParam) =>
//      // Optional parameters have the scope of the parent.
//      in(f)
  }

  private def envoutDef(out: SourceNode => Environment): SourceNode ==> Environment = {
    case l: Let => leave(env.in(l))
    case p: FunProto => leave(env.in(p))
    case b: FunBody => leave(env.in(b))
    case f: FunAbs => leave(env.in(f))
//    case tree.parent.pair(e: Exp, _: FunParam) => out(e)
  }

  override protected def defentity(i: IdnDef): Entity = i match {
    case tree.parent(b: LetBind) => new LetBindEntity(b)
    case tree.parent(f: LetFun) => new LetFunEntity(f)
    case tree.parent(f: LetFunRec) => new LetFunRecEntity(f)
    case tree.parent(f: FunParam) => new FunParamEntity(f)
    case tree.parent(d: Rql2Method) => new MethodEntity(d)
    case _ => super.defentity(i)
  }

  override protected def entityTypeDef(e: Entity): Type = e match {
    case b: LetBindEntity =>
      // Type of the LetBind expression is the type that is specified by the user; otherwise, it's type of the body
      // expression itself.
      b.b.t.getOrElse(actualType(b.b.e))
    case f: LetFunEntity => getFunType(f.f.p)
    case f: LetFunRecEntity => f.f.p.r match {
        case Some(_) => getFunType(f.f.p)
        case None =>
          // If output type is not defined for a recursive function, return ErrorType().
          // The error message for that case is handled in errorDef.
          ErrorType()
      }
    case f: FunParamEntity => f.f.t.getOrElse(resolveParamType(f.f).getOrElse(ErrorType()))
    case p: PackageEntity => PackageType(p.p.name)
    case d: MethodEntity =>
      // Methods also type - and are generated as - functions.
      getFunType(d.d.p)
    case _ => super.entityTypeDef(e)
  }

  private def getFunType(f: FunProto): Type = {
    val FunProto(ps, r, FunBody(e)) = f

    // Collect mandatory arguments
    val ms = ps.collect {
      case p if p.e.isEmpty =>
        resolveParamType(p) match {
          case Right(t) => t
          case _ => return ErrorType()
        }
    }

    // Collect optional arguments
    val os = ps.collect {
      case p if p.e.isDefined =>
        resolveParamType(p) match {
          case Right(t) => FunOptTypeParam(p.i.idn, t)
          case _ => return ErrorType()
        }
    }

    r match {
      case Some(t) => FunType(ms, os, t)
      case None => actualType(e) match {
          case _: ErrorType => ErrorType()
          case t => FunType(ms, os, t)
        }
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  // Type Checking
  ///////////////////////////////////////////////////////////////////////////

  class FullTypesMerger extends TypesMerger {

    @throws[MergeTypeException]
    override protected def recurse(actual: Type, expected: Type): Type = (actual, expected) match {
      case (a: TypeAliasType, e: TypeAliasType) => recurse(resolveType(a), resolveType(e))
      case (a: TypeAliasType, _) => recurse(resolveType(a), expected)
      case (_, e: TypeAliasType) => recurse(actual, resolveType(e))
      case (a: PackageEntryType, e: PackageEntryType) =>
        if (a.pkgName == e.pkgName && a.entName == e.entName) a else { throw new MergeTypeException }
      case (a: PackageType, e: PackageType) => if (a.name == e.name) a else { throw new MergeTypeException }
      case (a: PackageType, e: ExpectedProjType) => programContext.getPackage(a.name) match {
          case Some(p) if p.existsEntry(e.i) => actual
          case _ => throw new MergeTypeException
        }
      case _ => super.recurse(actual, expected)
    }
  }

  private val fullTypesMerger = new FullTypesMerger

  // Types are compatible if they "merge".
  final override protected def isCompatible(actual: Type, expected: Type): Boolean =
    fullTypesMerger.isCompatible(actual, expected)

  final def getCompatibleType(actual: Type, expected: Type): Option[Type] = {
    fullTypesMerger.getCompatibleType(actual, expected)
  }

  final def funParamTypeCompatibility(actual: Type, expected: Type): Option[CompatibilityReport] =
    fullTypesMerger.funParamTypeCompatibility(actual, expected)

  final def mergeType(t1: Type, t2: Type): Option[Type] = {
    fullTypesMerger.mergeType(t1, t2)
  }

  override protected def actualTypeDef(n: Exp): Type = {
    val t = n match {
      case e: Rql2Exp => actualTypeRql2Exp(e)
      case _ => super.actualTypeDef(n)
    }
    // Resolve the type (remove type alias)
    val rt = resolveType(t)
    // If any ErrorType anywhere nested in the tree, bring ErrorType() to the top.
    val checkForErrorType = everywhere(query[Any]({ case _: ErrorType => return ErrorType() }))
    checkForErrorType(rt)
    rt
  }

  private def resolveType(t: Type): Type = {
    val s = everywheretd(rule[Any] { case TypeAliasType(idn) => resolveTypeAlias(idn) })
    rewrite(s)(t)
  }

  private def resolveTypeAlias(idn: IdnUse): Type = {
    val te = entity(idn)
    // The type alias itself can point to other type aliases.
    val t = resolveType(entityType(te))
    t match {
      case ExpType(t) => t
      case _ => ErrorType()
    }
  }

  private def actualTypeRql2Exp(n: Rql2Exp): Type = n match {
    case c: Const => c match {
        case _: NullConst => Rql2UndefinedType(Set(Rql2IsNullableTypeProperty()))
        case nc: NumberConst => nc match {
            case _: ByteConst => Rql2ByteType()
            case _: ShortConst => Rql2ShortType()
            case _: IntConst => Rql2IntType()
            case _: LongConst => Rql2LongType()
            case _: FloatConst => Rql2FloatType()
            case _: DoubleConst => Rql2DoubleType()
            case _: DecimalConst => Rql2DecimalType()
          }
        case _: StringConst => Rql2StringType()
        case _: TripleQuotedStringConst => Rql2StringType()
        case _: BoolConst => Rql2BoolType()
        case _: BinaryConst => Rql2BinaryType()
      }
    case l: Let => actualType(l.e)
    case TypeExp(t) => ExpType(t)
    case IfThenElse(e1, e2, e3) => mergeType(actualType(e2), actualType(e3)) match {
        case Some(t) => addProps(t, getProps(actualType(e1)))
        case None => ErrorType()
      }
    case UnaryExp(op, e) => op match {
        case _: Not => Rql2BoolType(getProps(actualType(e)))
        case _: Neg => actualType(e)
      }
    case BinaryExp(op, e1, e2) => op match {
        case _: ComparableOp => Rql2BoolType(getProps(actualType(e1)) ++ getProps(actualType(e2)))
        case _: BooleanOp =>
          Rql2BoolType(Set(Rql2IsNullableTypeProperty()) ++ getProps(actualType(e1)) ++ getProps(actualType(e2)))
        case _: Div => mergeType(actualType(e1), actualType(e2)) match {
            case Some(t) => addProp(t, Rql2IsTryableTypeProperty()) // Div can fail on divide by zero.
            case None => ErrorType()
          }
        case _ => mergeType(actualType(e1), actualType(e2)) match {
            case Some(t) => t
            case None => ErrorType()
          }
      }
    case FunAbs(p) => getFunType(p)
    case fa @ FunApp(f, _) => actualType(f) match {
        case PackageEntryType(pkgName, entName) => getFunAppPackageEntryType(fa, pkgName, entName) match {
            case Right(t) => t
            case Left(err) =>
              // Error is handled by errorDef.
              ErrorType()
          }
        case f: FunType => getFunAppFunType(fa, f) match {
            case Right(t) =>
              // If the function is nullable/tryable, its properties are added to the application type properties.
              addProps(t, f.props)
            case Left(_) =>
              // Error is handled by errorDef.
              ErrorType()
          }
        case _ => ErrorType()
      }
    case Proj(e, i) => actualType(e) match {
        case PackageType(name) =>
          // Only type if package has the entry.
          programContext.getPackage(name) match {
            case Some(p) if p.existsEntry(i) => PackageEntryType(name, i)
            case _ => ErrorType()
          }
        case Rql2RecordType(atts, props) =>
          // Only type if record has the field.
          atts
            .collectFirst {
              case att if att.idn == i =>
                // Include the type properties of the <e> as well.
                addProps(att.tipe, props)
            }
            .getOrElse(ErrorType())
        case Rql2ListType(Rql2RecordType(atts, recordProps), listProps) =>
          // Only type if record has the field.
          atts
            .collectFirst {
              case att if att.idn == i =>
                // Include the type properties of the <e> as well.
                Rql2ListType(addProps(att.tipe, recordProps), listProps)
            }
            .getOrElse(ErrorType())
        case Rql2IterableType(Rql2RecordType(atts, recordProps), listProps) =>
          // Only type if record has the field.
          atts
            .collectFirst {
              case att if att.idn == i =>
                // Include the type properties of the <e> as well.
                Rql2IterableType(addProps(att.tipe, recordProps), listProps)
            }
            .getOrElse(ErrorType())
        case _ => ErrorType()
      }
    case PackageIdnExp(name) =>
      PackageExtensionProvider.getPackage(name, programContext.compilerContext.maybeClassLoader) match {
        case Some(_) => PackageType(name)
        case None => throw new AssertionError(s"Built-in package $name not found")
      }
  }

  def getArgumentsForFunAppPackageEntry(
      fa: FunApp,
      packageEntry: EntryExtension
  ): Either[BaseError, Option[FunAppPackageEntryArguments]] = {
    val args = fa.args

    val prevMandatoryArgs = mutable.ArrayBuffer[Arg]()
    val prevOptionalArgs = mutable.ArrayBuffer[(String, Arg)]()
    val prevVarArgs = mutable.ArrayBuffer[Arg]()
    val outProps = mutable.Set.empty[Rql2TypeProperty]

    // This list includes mandatory and var args.
    val argsList = args.collect { case arg if arg.idn.isEmpty => arg }.to[mutable.ArrayBuffer]

    // Check mandatory arguments, one-by-one.
    var idx = 0
    while (idx < packageEntry.nrMandatoryParams) {
      // If there are none left, it is an error
      if (argsList.isEmpty) {
        val missingArgs = packageEntry.nrMandatoryParams - idx
        val missingParams = packageEntry.docs.params.slice(idx, idx + missingArgs)
        val argsMissing = missingParams.map(p => s"${p.name}: ${p.description}")
        return Left(MandatoryArgumentsMissing(fa, argsMissing = argsMissing))
      }
      val arg = argsList.remove(0)
      val actual = actualType(arg.e)
      if (actual == ErrorType()) return Right(None)
      packageEntry.getMandatoryParam(prevMandatoryArgs, idx) match {
        case Right(param) => param match {
            case ExpParam(expected) =>
              // Check if argument is type compatible.
              funParamTypeCompatibility(actual, expected) match {
                case Some(report) =>
                  // All ok.
                  prevMandatoryArgs += ExpArg(arg.e, report.t)
                  outProps ++= report.extraProps
                case _ => return Left(
                    UnexpectedType(
                      arg.e,
                      actual,
                      expected,
                      packageEntry.getMandatoryParamHint(prevMandatoryArgs, idx, actual, expected),
                      packageEntry.getMandatoryParamSuggestions(prevMandatoryArgs, idx, actual, expected)
                    )
                  )
              }
            case TypeParam(expected) =>
              // Check if argument is a type expression.
              actual match {
                case ExpType(actual) =>
                  // It is a type.
                  // Check if it is is type compatible.
                  if (!isCompatible(actual, expected)) {
                    return Left(
                      UnexpectedType(
                        arg.e,
                        actual,
                        expected,
                        packageEntry.getMandatoryParamHint(prevMandatoryArgs, idx, actual, expected),
                        packageEntry.getMandatoryParamSuggestions(prevMandatoryArgs, idx, actual, expected)
                      )
                    )
                  }
                  // All ok.
                  prevMandatoryArgs += TypeArg(actual)
                case _ => return Left(ExpectedTypeButGotExpression(arg.e))
              }
            case ValueParam(expected) =>
              // Check if argument is type compatible.
              funParamTypeCompatibility(actual, expected) match {
                case Some(report) =>
                  // All ok.
                  // Check if argument and expression match.
                  getValue(report, arg.e).fold(
                    err => return Left(err),
                    v => {
                      prevMandatoryArgs += ValueArg(v, report.t)
//                      outProps ++= report.extraProps
                    }
                  )
                case _ => return Left(
                    UnexpectedType(
                      arg.e,
                      actual,
                      expected,
                      packageEntry.getMandatoryParamHint(prevMandatoryArgs, idx, actual, expected),
                      packageEntry.getMandatoryParamSuggestions(prevMandatoryArgs, idx, actual, expected)
                    )
                  )
              }
          }
        case Left(err) => return Left(InvalidSemantic(arg.e, err))
      }
      idx += 1
    }

    // At this point, argsList should only contain var args.

    // Check optional arguments, one-by-one.
    val optionalArgsList = args.collect { case arg if arg.idn.isDefined => arg }.to[mutable.ArrayBuffer]
    val maybeOptionalParams = packageEntry.optionalParams
    maybeOptionalParams match {
      case Some(optionalParams) =>
        // Process all optional parameters.
        while (optionalArgsList.nonEmpty) {
          val optionalArg = optionalArgsList.remove(0)
          val idn = optionalArg.idn.get
          // Optional argument idn is not known.
          if (optionalParams.nonEmpty && !optionalParams.contains(idn)) {
            return Left(UnexpectedOptionalArgument(optionalArg))
          }
          val actual = actualType(optionalArg.e)
          if (actual == ErrorType()) return Right(None)
          packageEntry.getOptionalParam(prevMandatoryArgs, idn) match {
            case Right(param) => param match {
                case ExpParam(expected) =>
                  // Check if argument is type compatible.
                  funParamTypeCompatibility(actual, expected) match {
                    case Some(report) =>
                      // All ok.
                      prevOptionalArgs.append((idn, ExpArg(optionalArg.e, report.t)))
                      outProps ++= report.extraProps
                    case _ => return Left(
                        UnexpectedType(
                          optionalArg.e,
                          actual,
                          expected,
                          packageEntry.getOptionalParamHint(prevMandatoryArgs, idx, actual, expected),
                          packageEntry.getOptionalParamSuggestions(prevMandatoryArgs, idx, actual, expected)
                        )
                      )
                  }
                case TypeParam(expected) =>
                  // Check if argument is a type expression.
                  actual match {
                    case ExpType(actual) =>
                      // It is a type.
                      // Check if it is is type compatible.
                      if (!isCompatible(actual, expected)) {
                        return Left(
                          UnexpectedType(
                            optionalArg.e,
                            actual,
                            expected,
                            packageEntry.getOptionalParamHint(prevMandatoryArgs, idx, actual, expected),
                            packageEntry.getOptionalParamSuggestions(prevMandatoryArgs, idx, actual, expected)
                          )
                        )
                      }
                      // All ok.
                      prevOptionalArgs.append((idn, TypeArg(actual)))
                    case _ => return Left(ExpectedTypeButGotExpression(optionalArg.e))
                  }
                case ValueParam(expected) =>
                  // Check if argument is type compatible.
                  funParamTypeCompatibility(actual, expected) match {
                    case Some(report) =>
                      // All ok.
                      getValue(report, optionalArg.e).fold(
                        err => return Left(err),
                        v => {
                          prevOptionalArgs.append((idn, ValueArg(v, report.t)))
                          outProps ++= report.extraProps
                        }
                      )
                    case None => return Left(
                        UnexpectedType(
                          optionalArg.e,
                          actual,
                          expected,
                          packageEntry.getOptionalParamHint(prevMandatoryArgs, idx, actual, expected),
                          packageEntry.getOptionalParamSuggestions(prevMandatoryArgs, idx, actual, expected)
                        )
                      )
                  }
              }
            case Left(err) => return Left(InvalidSemantic(optionalArg.e, err))
          }
        }
      case None =>
        // No optional parameters are expected.
        if (optionalArgsList.nonEmpty) {
          return Left(NoOptionalArgumentsExpected(fa))
        }
    }

    // At this point, all optional arguments have been processed.

    // Now it's time to process varargs.

    // We have non-optional arguments left.
    if (argsList.nonEmpty) {
      // If there are no var args, this is an error
      if (!packageEntry.hasVarArgs) {
        return Left(UnexpectedArguments(fa))
      }

      // Otherwise, need to typecheck all var args.
      idx = 0
      while (argsList.nonEmpty) {
        val arg = argsList.remove(0)
        val actual = actualType(arg.e)
        if (actual == ErrorType()) return Right(None)

        packageEntry.getVarParam(prevMandatoryArgs, prevVarArgs, idx) match {
          case Right(param) => param match {
              case ExpParam(expected) =>
                // Check if argument is type compatible.
                funParamTypeCompatibility(actual, expected) match {
                  case Some(report) =>
                    // All ok.
                    prevVarArgs += ExpArg(arg.e, report.t)
                    outProps ++= report.extraProps
                  case _ => return Left(
                      UnexpectedType(
                        arg.e,
                        actual,
                        expected,
                        packageEntry.getVarParamHint(prevMandatoryArgs, prevVarArgs, idx, actual, expected),
                        packageEntry.getVarParamSuggestions(prevMandatoryArgs, prevVarArgs, idx, actual, expected)
                      )
                    )
                }
              case TypeParam(expected) =>
                // Check if argument is a type expression.
                actual match {
                  case ExpType(actual) =>
                    // It is a type.
                    // Check if it is is type compatible.
                    if (!isCompatible(actual, expected)) {
                      return Left(
                        UnexpectedType(
                          arg.e,
                          actual,
                          expected,
                          packageEntry.getVarParamHint(prevMandatoryArgs, prevVarArgs, idx, actual, expected),
                          packageEntry.getVarParamSuggestions(prevMandatoryArgs, prevVarArgs, idx, actual, expected)
                        )
                      )
                    }
                    // All ok.
                    prevVarArgs += TypeArg(actual)
                  case _ => return Left(ExpectedTypeButGotExpression(arg.e))
                }
              case ValueParam(expected) => funParamTypeCompatibility(actual, expected) match {
                  case Some(report) =>
                    // All ok.
                    getValue(report, arg.e).fold(
                      err => return Left(err),
                      v => {
                        prevVarArgs += ValueArg(v, report.t)
                        outProps ++= report.extraProps
                      }
                    )
                  case None => return Left(
                      UnexpectedType(
                        arg.e,
                        actual,
                        expected,
                        packageEntry.getVarParamHint(prevMandatoryArgs, prevVarArgs, idx, actual, expected),
                        packageEntry.getVarParamSuggestions(prevMandatoryArgs, prevVarArgs, idx, actual, expected)
                      )
                    )
                }
            }
          case Left(err) => return Left(InvalidSemantic(arg.e, err))
        }
        idx += 1
      }
    }

    Right(
      Some(
        FunAppPackageEntryArguments(
          prevMandatoryArgs.to,
          prevOptionalArgs.to,
          prevVarArgs.to,
          outProps.toSet
        )
      )
    )
  }

  private def getFunAppPackageEntryType(
      fa: FunApp,
      pkgName: String,
      entName: String
  ): Either[Seq[BaseError], Type] = {
    programContext.getPackage(pkgName) match {
      case Some(p) =>
        val entry = p.getEntry(entName)

        getArgumentsForFunAppPackageEntry(fa, entry) match {
          case Right(Some(FunAppPackageEntryArguments(mandatoryArgs, optionalArgs, varArgs, extraProps))) =>
            // All good, so we can obtain the type.
            entry
              .returnTypeErrorList(fa, mandatoryArgs, optionalArgs, varArgs)
              .right
              .map(addProps(_, extraProps))
          case Right(None) => Right(ErrorType())
          case Left(err) => Left(Seq(err))
        }
      case None => Left(Seq(PackageNotFound(fa)))
    }
  }

  def getFunAppPackageEntryTypePartial(
      fa: FunApp,
      p: PackageEntryType,
      stopArgIdx: Int
  ): Option[Type] = {
    val stopArg = fa.args(stopArgIdx)
    val PackageEntryType(pkgName, entName) = p
    val args = fa.args
    programContext.getPackage(pkgName) match {
      case Some(p) =>
        val packageEntry = p.getEntry(entName)

        val prevMandatoryArgs = mutable.ArrayBuffer[Arg]()
        val prevOptionalArgs = mutable.ArrayBuffer[(String, Arg)]()
        val prevVarArgs = mutable.ArrayBuffer[Arg]()

        // This list includes mandatory and var args.
        val argsList = args.collect { case arg if arg.idn.isEmpty => arg }.to[mutable.ArrayBuffer]

        // Check mandatory arguments, one-by-one.
        var idx = 0
        while (idx < packageEntry.nrMandatoryParams) {
          // If there are none left, it is an error
          if (argsList.isEmpty) {
            return None
          }

          if (argsList.head eq stopArg) {
            // We are done!
            packageEntry.getMandatoryParam(prevMandatoryArgs, idx) match {
              case Right(param) => param match {
                  case ExpParam(expected) => return Some(expected)
                  case TypeParam(expected) => return Some(expected)
                  case ValueParam(expected) => return Some(expected)
                }
              case Left(_) => return None
            }
          } else {
            val arg = argsList.remove(0)
            val actual = actualType(arg.e)
            if (actual == ErrorType()) return Some(ErrorType())
            packageEntry.getMandatoryParam(prevMandatoryArgs, idx) match {
              case Right(param) => param match {
                  case ExpParam(expected) =>
                    // Check if argument is type compatible.
                    funParamTypeCompatibility(actual, expected) match {
                      case Some(report) =>
                        // All ok.
                        prevMandatoryArgs += ExpArg(arg.e, report.t)
                      case _ => return None
                    }
                  case TypeParam(expected) =>
                    // Check if argument is a type expression.
                    actual match {
                      case ExpType(actual) =>
                        // It is a type.
                        // Check if it is is type compatible.
                        if (!isCompatible(actual, expected)) {
                          return None
                        }
                        // All ok.
                        prevMandatoryArgs += TypeArg(actual)
                      case _ => return None
                    }
                  case ValueParam(expected) =>
                    // Check if argument is type compatible.
                    funParamTypeCompatibility(actual, expected) match {
                      case Some(report) =>
                        // All ok.
                        // Check if argument and expression match.
                        getValue(report, arg.e).fold(
                          _ => return None,
                          v => prevMandatoryArgs += ValueArg(v, report.t)
                        )
                      case _ => return None
                    }
                }
              case Left(_) => return None
            }
            idx += 1
          }
        }

        // At this point, argsList should only contain var args.

        // Check optional arguments, one-by-one.
        val optionalArgsList = args.collect { case arg if arg.idn.isDefined => arg }.to[mutable.ArrayBuffer]
        val maybeOptionalParams = packageEntry.optionalParams
        maybeOptionalParams match {
          case Some(optionalParams) =>
            // Process all optional parameters.
            while (optionalArgsList.nonEmpty) {
              val optionalArg = optionalArgsList.remove(0)
              val idn = optionalArg.idn.get
              // Optional argument idn is not known.
              if (optionalParams.nonEmpty && !optionalParams.contains(idn)) {
                return None
              }

              if (stopArg.idn.isDefined && idn == stopArg.idn.get) {
                // We are done!
                packageEntry.getOptionalParam(prevMandatoryArgs, idn) match {
                  case Right(param) => param match {
                      case ExpParam(expected) => return Some(expected)
                      case TypeParam(expected) => return Some(expected)
                      case ValueParam(expected) => return Some(expected)
                    }
                  case Left(_) => return None
                }
              } else {
                val actual = actualType(optionalArg.e)
                if (actual == ErrorType()) return Some(ErrorType())
                packageEntry.getOptionalParam(prevMandatoryArgs, idn) match {
                  case Right(param) => param match {
                      case ExpParam(expected) => funParamTypeCompatibility(actual, expected) match {
                          case Some(report) =>
                            // All ok.
                            prevOptionalArgs.append((idn, ExpArg(optionalArg.e, report.t)))
                          case _ => return None
                        }
                      case TypeParam(expected) =>
                        // Check if argument is a type expression.
                        actual match {
                          case ExpType(actual) =>
                            // It is a type.
                            // Check if it is is type compatible.
                            if (!isCompatible(actual, expected)) {
                              return None
                            }
                            // All ok.
                            prevOptionalArgs.append((idn, TypeArg(actual)))
                          case _ => return None
                        }
                      case ValueParam(expected) => funParamTypeCompatibility(actual, expected) match {
                          case Some(report) =>
                            // All ok.
                            getValue(report, optionalArg.e).fold(
                              _ => return None,
                              v => prevOptionalArgs.append((idn, ValueArg(v, expected)))
                            )
                          case _ => return None
                        }
                    }
                  case Left(_) => return None
                }
              }
            }
          case None =>
            // No optional parameters are expected.
            if (optionalArgsList.nonEmpty) {
              return None
            }
        }

        // At this point, all optional arguments have been processed.

        // Now it's time to process varargs.

        // We have non-optional arguments left.
        if (argsList.nonEmpty) {
          // If there are no var args, this is an error
          if (!packageEntry.hasVarArgs) {
            return None
          }

          // Otherwise, need to typecheck all var args.
          idx = 0
          while (argsList.nonEmpty) {
            if (stopArgIdx == packageEntry.nrMandatoryParams + idx) {
              // We are done!
              packageEntry.getVarParam(prevMandatoryArgs, prevVarArgs, idx) match {
                case Right(param) => param match {
                    case ExpParam(expected) => return Some(expected)
                    case TypeParam(expected) => return Some(expected)
                    case ValueParam(expected) => return Some(expected)
                  }
                case Left(_) => return None
              }
            } else {
              val arg = argsList.remove(0)
              val actual = actualType(arg.e)
              if (actual == ErrorType()) return Some(ErrorType())
              packageEntry.getVarParam(prevMandatoryArgs, prevVarArgs, idx) match {
                case Right(param) => param match {
                    case ExpParam(expected) => funParamTypeCompatibility(actual, expected) match {
                        case Some(report) =>
                          // All ok.
                          prevVarArgs += ExpArg(arg.e, report.t)
                        case _ => return None
                      }
                    case TypeParam(expected) =>
                      // Check if argument is a type expression.
                      actual match {
                        case ExpType(actual) =>
                          // It is a type.
                          // Check if it is is type compatible.
                          if (!isCompatible(actual, expected)) {
                            return None
                          }
                          // All ok.
                          prevVarArgs += TypeArg(actual)
                        case _ => return None
                      }
                    case ValueParam(expected) => funParamTypeCompatibility(actual, expected) match {
                        case Some(report) =>
                          // All ok.
                          // Check if argument and expression match.
                          getValue(report, arg.e).fold(
                            _ => return None,
                            v => prevVarArgs += ValueArg(v, expected)
                          )
                        case _ => return None
                      }
                  }
                case Left(_) => return None
              }
              idx += 1
            }
          }
        }

        // Did not find stopArgIdx?
        None
      case None => None
    }
  }

  // TODO (msb): Make it return "N errors" at once?
  private def getFunAppFunType(fa: FunApp, f: FunType): Either[BaseError, Type] = {
    val FunType(mandatoryParams, optionalParams, r, _) = f
    val args = fa.args

    // Check that we have sufficient mandatory arguments.
    if (args.count(a => a.idn.isEmpty) < mandatoryParams.length) {
      return Left(MandatoryArgumentsMissing(fa, Seq.empty))
    }

    // Check that mandatory argument types are compatible with mandatory parameters.
    mandatoryParams.zipWithIndex.foreach {
      case (p, idx) =>
        val a = args(idx)
        // Check if argument is type compatible.
        if (!isCompatible(actualType(a.e), p.t)) {
          return Left(UnexpectedType(a.e, actualType(a.e), p.t))
        }
    }

    // Now must process optional ones.
    val processed = mutable.HashSet[String]()
    args.drop(mandatoryParams.length).zipWithIndex.foreach {
      case (a, idx) =>
        val p = a.idn match {
          case Some(idn) =>
            // Optional parameter with name.
            if (processed.contains(idn)) {
              return Left(RepeatedOptionalArguments(a))
            }
            optionalParams.collectFirst { case p if p.i == idn => p } match {
              case Some(p) =>
                processed.add(idn)
                p
              case None => return Left(UnexpectedOptionalArgument(a))
            }

          case None =>
            // Optional parameter without name.
            if (optionalParams.length > idx) {
              val p = optionalParams(idx)
              processed.add(p.i)
              p
            } else { return Left(UnexpectedArguments(fa)) }
        }
        // Check if argument is type compatible.
        if (!isCompatible(actualType(a.e), p.t)) {
          return Left(UnexpectedType(a.e, actualType(a.e), p.t))
        }
    }

    // All ok, so return the "return type".
    Right(r)
  }

  final private def getValue(report: CompatibilityReport, e: Exp): Either[BaseError, Value] = {
    // Recurse over all entities in the order of its dependencies.
    // Populate an ordered list of declarations as a side-effect.
    val lets: mutable.ArrayBuffer[LetDecl] = mutable.ArrayBuffer.empty[LetDecl]

    class RecurseEntitiesException(val err: BaseError) extends Exception

    @throws[RecurseEntitiesException]
    def recurseEntities(
        queue: mutable.ArrayBuffer[Entity],
        done: mutable.HashSet[Entity] = mutable.HashSet.empty[Entity]
    ): Unit = {
      // Find next unprocessed entity
      var e: Entity = null
      while (queue.nonEmpty && e == null) {
        e = queue.remove(0)
        if (done.contains(e)) {
          e = null
        }
      }
      if (e != null) {
        // Found one entity to process
        e match {
          case f: FunParamEntity => throw new RecurseEntitiesException(
              FailedToEvaluate(f.f.i, Some("value cannot be determined for function parameter"))
            )
          case p: ProgramParamEntity => throw new RecurseEntitiesException(
              FailedToEvaluate(p.p.idn, Some("value cannot be determined for program parameter"))
            )
          case r: LetFunRecEntity => throw new RecurseEntitiesException(
              FailedToEvaluate(r.f.i, Some("value cannot be determined for recursive definitions"))
            )
          case b: LetBindEntity =>
            // Handle dependencies first.
            queue.prependAll(freeVars(b.b.e))
            recurseEntities(queue, done)
            if (!done.contains(e)) {
              lets += LetBind(b.b.e, b.b.i, b.b.t)
              done += e
            }
          case f: LetFunEntity =>
            queue.prependAll(freeVars(f.f.p))
            recurseEntities(queue, done)
            if (!done.contains(e)) {
              lets += LetFun(f.f.p, f.f.i)
              done += e
            }
          case m: MethodEntity =>
            queue.prependAll(freeVars(m.d.p))
            recurseEntities(queue, done)
            if (!done.contains(e)) {
              lets += LetFun(m.d.p, m.d.i)
              done += e
            }
          case p: PackageEntity =>
            // Nothing to do since these are always available.
            assert(
              PackageExtensionProvider.names(programContext.compilerContext.maybeClassLoader).contains(p.p.name),
              "Non-built-in package found! This must be handled here!!!"
            )
            recurseEntities(queue, done)
            if (!done.contains(e)) {
              done += e
            }
        }
      }
    }

    // Start with <e>'s free variables
    val fv = freeVars(e)
    try {
      recurseEntities(fv.to)
    } catch {
      case ex: RecurseEntitiesException =>
        // Found an entity we cannot resolve. Abort.
        return Left(ex.err)
    }

    // Otherwise, create an RQL2 program with the expressions and declarations it depends on.
    // Wrap it into a Type.Cast so that we ensure implicit casts are already.
    // This allows us to pattern match at L0 more easily, since all types are implicitly casted:
    // for instance if the <e> is a StringType (i.e. StringConst) but expected type as LocationType, the implicit cast phase
    // of Rql2, indirectly applied by calling Type.Cast, makes sure the cast is applied and hence we get back a
    // LocationType (i.e. LocationDescription).
    val expected = addProps(report.t, report.extraProps)
    val program = {
      if (lets.isEmpty) Rql2Program(Vector.empty, Some(TypePackageBuilder.Cast(expected, e)))
      else Rql2Program(Vector.empty, Some(TypePackageBuilder.Cast(expected, Let(lets.to, e))))
    }

    def getProgramContext = {
      // Create a clone of the RuntimeContext overriding the output format setting.
      val runtimeContext = programContext.runtimeContext
      val environment = runtimeContext.environment
      new ProgramContext(
        programContext.runtimeContext.cloneWith(newEnvironment =
          ProgramEnvironment(
            environment.user,
            environment.maybeArguments,
            environment.scopes,
            environment.options + ("output-format" -> ""),
            environment.maybeTraceId
          )
        )
      )(programContext.compilerContext)
    }

    programContext.getOrAddStagedCompilation(
      program, {
        // Perform compilation of expression and its dependencies.
        val prettyPrinterProgram = SourcePrettyPrinter.format(program)
        try {
          CompilerServiceProvider(programContext.compilerContext.maybeClassLoader)(getProgramContext.settings).eval(
            prettyPrinterProgram,
            getProgramContext.runtimeContext.environment
          ) match {
            case EvalSuccess(v) =>
              var stagedCompilerResult = v

              // Remove extraProps
              if (report.extraProps.contains(Rql2IsTryableTypeProperty())) {
                val tryValue = stagedCompilerResult.asInstanceOf[TryValue].v
                if (tryValue.isLeft) {
                  return Left(FailedToEvaluate(e, tryValue.left.toOption))
                }
                stagedCompilerResult = stagedCompilerResult.asInstanceOf[TryValue].v.right.get
              }
              if (report.extraProps.contains(Rql2IsNullableTypeProperty())) {
                val optionValue = stagedCompilerResult.asInstanceOf[OptionValue].v
                if (optionValue.isEmpty) {
                  return Left(FailedToEvaluate(e, Some("unexpected null value found")))
                }
                stagedCompilerResult = stagedCompilerResult.asInstanceOf[OptionValue].v.get
              }
              Right(stagedCompilerResult)
            case EvalValidationFailure(errs) =>
              logger.warn(s"""Staged compilation of expression failed to validate with semantic errors:
                |Expected type: $expected
                |Expression: $e
                |Errors: $errs""".stripMargin)
              Left(FailedToEvaluate(e))
            case EvalRuntimeFailure(err) =>
              logger.warn(s"""Staged compilation of expression failed at runtime with errors:
                |Expected type: $expected
                |Expression: $e
                |Error: $err""".stripMargin)
              Left(FailedToEvaluate(e))
          }
        } catch {
          case NonFatal(t) =>
            logger.warn(
              s"""Staged compilation of expression failed with NonFatal:
                |Expected type: $expected
                |Expression: $e""".stripMargin,
              t
            )
            Left(FailedToEvaluate(e))
        }
      }
    )
  }

  final def resolveParamType(p: FunParam): Either[Seq[BaseError], Type] = {
    val FunParam(i, mt, me) = p
    if (mt.isDefined) Right(mt.map(resolveType).get)
    else {
      // Try to determine the type of the optional parameter from the optional expression.
      me match {
        case Some(e) =>
          // We use the type of the optional expression as the type of the parameter.
          Right(actualType(e))
        case None =>
          // The type of the parameter is not defined, nor there is an optional expression.
          // Let's see if we can determine it from context: more specifically, let's see if this parameter
          // is part of a FunAbs which is being called from the FunApp that refers to a package, in which case
          // we can try to use the 'expected type' from the package entry arguments to determine our type.
          // For this we:
          // 1) Walk up to the parent of the FunParam to find its FunAbs;
          // 2) See if we know the expected type of that FunAbs (from the context described above).
          // 3) If so, we know the FunType, so then we find our own parameter and get its expected type and use that instead.
          // Refer also to additional information on the docs of funAbsExpectedTypeFromFunApp.
          p match {
            case tree.parent(fp: FunProto) => fp match {
                case tree.parent(f: FunAbs) => funAbsExpectedType(f) match {
                    case Some(t: FunType) =>
                      // This is a mandatory parameter.
                      val idx = fp.ps.indexOf(p)
                      if (idx >= t.ms.size) {
                        return Left(
                          Seq(
                            UnexpectedValue(f, t, "function with " + f.p.ps.size + " parameters")
                          )
                        )
                      }
                      val m = t.ms(idx)
                      // Use the expected parameter type as our own type.
                      Right(m.t)
                    case Some(oneOf @ OneOfType(options)) =>
                      val idx = fp.ps.indexOf(p)
                      val ok = options.collect {
                        case t: FunType if t.ms.size == f.p.ps.size =>
                          // Only consider options that have the same number of parameters as our function.
                          val m = t.ms(idx)
                          // Use the expected parameter type as our own type.
                          m.t
                      }
                      assert(ok.size <= 1, "multiple options for parameter type")
                      if (ok.size == 1) {
                        Right(ok.head)
                      } else {
                        Left(
                          Seq(
                            UnexpectedValue(f, oneOf, "function with " + f.p.ps.size + " parameters")
                          )
                        )
                      }
                    case Some(ErrorType()) => Right(ErrorType()) // Propagating errors silently
                    case Some(t) =>
                      val paramTypes = fp.ps.map(x => x.t.getOrElse(Rql2UndefinedType()))
                      val outType = fp.r.getOrElse(Rql2UndefinedType())
                      Left(
                        Seq(
                          UnexpectedType(f, FunType(paramTypes, Vector.empty, outType, Set.empty), t),
                          CannotDetermineTypeOfParameter(p)
                        )
                      )
                    case _ =>
                      // Cannot determine the type from the FunAbs, so bailing out.
                      Left(Seq(CannotDetermineTypeOfParameter(p)))
                  }
                case _ =>
                  // Parent is not a FunAbs (e.g. it's a Let), so bailing out, as we have no support (yet?) for this case.
                  Left(Seq(CannotDetermineTypeOfParameter(p)))
              }
          }
      }
    }
  }

  // If a FunAbs is used as an argument in a FunApp call, try to determine its type.
  private lazy val funAbsExpectedType: FunAbs => Option[Type] = attr {
    case f: FunAbs => f match {
        case tree.parent(letBind: LetBind) =>
          // Covers the case of let f: (int) -> int = v -> v + 1
          letBind.t
        case tree.parent(faa: FunAppArg) => faa match {
            // If a FunAbs is used as an argument in a FunApp call, try to determine its type.
            // For instance,
            //   Collection.Filter(c, s -> s > 10)
            // The FunAbs is 's -> s.age > 10'.
            // The type of 's' is not given by the user, so it must be determined from the "context".
            // In this case, we use the Package extension system, which has "expected types" per argument.
            // So if we know that the expected type of argument 2 of 'Collection.Filter' is a function of int to bool,
            // then we know 's' must be an int.
            // Refer also to additional information on the docs of resolveParamType.
            case tree.parent(fa @ FunApp(p, args)) if args.exists(arg => arg.e eq f) =>
              val argIdx = args.indexWhere(arg => arg.e eq f)
              actualType(p) match {
                case t: PackageEntryType => getFunAppPackageEntryTypePartial(fa, t, argIdx)
                case _ => None
              }
          }
        case tree.parent(app: FunApp) if f eq app.f =>
          // Copes with FunApp of FunAbs case.
          // It figures out the type of the FunAbs parameters from the FunApps, which are just close by.
          val (unnamed, named) = app.args.partition(_.idn.isEmpty)
          val ms: Vector[Type] = unnamed.map(arg => tipe(arg.e))
          val os: Vector[FunOptTypeParam] = named.map(arg => FunOptTypeParam(arg.idn.get, tipe(arg.e)))
          Some(FunType(ms, os, AnythingType()))
        case _ => None
      }
  }

  final override protected def expectedTypeDef(n: Exp): ExpectedType = {
    n match {
      case tree.parent.pair(e: Exp, parent: Rql2Node) => expectedTypeRql2(e, parent)
      case _ => super.expectedTypeDef(n)
    }
  }

  private val rql2byte = Rql2ByteType(Set(Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty()))
  private val rql2short = Rql2ShortType(Set(Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty()))
  private val rql2int = Rql2IntType(Set(Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty()))
  private val rql2long = Rql2LongType(Set(Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty()))
  private val rql2float = Rql2FloatType(Set(Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty()))
  private val rql2double = Rql2DoubleType(Set(Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty()))
  private val rql2decimal = Rql2DecimalType(Set(Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty()))
  private val rql2bool = Rql2BoolType(Set(Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty()))
  private val rql2numerics = OneOfType(rql2byte, rql2short, rql2int, rql2long, rql2float, rql2double, rql2decimal)
  private val rql2string = Rql2StringType(Set(Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty()))
  private val rql2time = Rql2TimeType(Set(Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty()))
  private val rql2interval = Rql2IntervalType(Set(Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty()))
  private val rql2date = Rql2DateType(Set(Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty()))
  private val rql2timestamp = Rql2TimestampType(Set(Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty()))
  private val rql2numbersAndString = OneOfType(rql2numerics.tipes :+ rql2string)
  private val rql2temporals = OneOfType(rql2time, rql2interval, rql2date, rql2timestamp)
  private val rql2numericsTemporalsString = OneOfType(rql2numerics.tipes ++ rql2temporals.tipes :+ rql2string)
  private val rql2numericsTemporalsStringsBools =
    OneOfType(rql2numerics.tipes ++ rql2temporals.tipes ++ Vector(rql2bool, rql2string))

  private def expectedTypeRql2(e: Exp, parent: Rql2Node): ExpectedType = (parent: @unchecked) match {
    case Rql2Program(_, Some(e1)) =>
      assert(e eq e1)
      anything
    case b: LetBind =>
      // The type of the expression must be the type the user specified.
      // If the user didn't specify any type on the bind, it can be anything.
      ExpectedType(b.t.getOrElse(anything))
    case l: Let =>
      assert(e eq l.e)
      // The 'in' part of Let can be anything.
      anything
    case IfThenElse(e1, e2, e3) =>
      if (e eq e1) rql2bool
      else if (e eq e2) anything
      else {
        assert(e eq e3)
        MergeableType(actualType(e2))
      }
    case UnaryExp(op, e1) =>
      assert(e eq e1)
      op match {
        case _: Not => rql2bool
        case _: Neg => rql2numerics
      }
    case BinaryExp(op, e1, e2) =>
      def expected(expectedE1: Type, expectedE2: Option[Type]): Type = {
        if (e eq e1) {
          expectedE1
        } else {
          assert(e eq e2)
          expectedE2.getOrElse {
            val t1 = actualType(e1)
            if (t1.isInstanceOf[Rql2UndefinedType]) expectedE1
            else expectedE2.getOrElse(MergeableType(t1))
          }
        }
      }

      op match {
        case _: Eq | _: Neq => expected(rql2numericsTemporalsStringsBools, None)
        case _: ComparableOp => expected(rql2numericsTemporalsString, None)
        case _: BooleanOp => expected(rql2bool, Some(rql2bool))
        case _: Plus => expected(rql2numbersAndString, None)
        case _ => expected(rql2numerics, Some(rql2numerics))
      }
    case FunParam(_, mt, Some(e1)) =>
      assert(e eq e1)
      mt match {
        case Some(t) => t
        case None => anything
      }
    case FunApp(f, _) =>
      assert(e eq f)
      // Ignored here since we check in errorDef.
      anything
    case FunAppArg(e1, _) =>
      assert(e eq e1)
      // Ignored here since we check in errorDef.
      anything
    case tree.parent.pair(FunBody(e1), FunProto(_, mt, _)) =>
      assert(e eq e1)
      // If output type is defined, body type must match that.
      // Otherwise, it can be anything.
      mt match {
        case Some(t) => t
        case None => anything
      }
    case Proj(e1, i) =>
      assert(e eq e1)
      if (i.nonEmpty) {
        actualType(e) match {
          case PackageType(name) =>
            for (
              p <- PackageExtensionProvider.packages(programContext.compilerContext.maybeClassLoader);
              if p.p.name == name; e <- p.p.entries; if e == i
            ) {
              return ExpectedType(ExpectedProjType(i))
            }
            val actualName = s"$name.$i"
            val names =
              PackageExtensionProvider.packages(programContext.compilerContext.maybeClassLoader).flatMap { p =>
                p.p.entries.collect {
                  case e if levenshteinDistance(actualName, s"${p.p.name}.$e") < 3 => s"${p.p.name}.$e"
                }
              }
            if (names.isEmpty) {
              // No found based on levenshtein distance. Try to see if there is any entry name in another package that
              // is a perfect match. For instance, if the user does String.IsNull instead of Nullable.IsNull,
              // or Text.Split instead of String.Split. The criteria is that the entry name must be a perfect match
              // but defined in a single other package.
              val packagesWithEntry =
                PackageExtensionProvider.packages(programContext.compilerContext.maybeClassLoader).flatMap {
                  case p => p.p.entries.collect {
                      case e if i == e => p.p.name
                    }
                }
              if (packagesWithEntry.length == 1)
                ExpectedType(ExpectedProjType(i), hint = Some(s"did you mean ${packagesWithEntry.head}.$i?"))
              else ExpectedType(ExpectedProjType(i))
            } else ExpectedType(ExpectedProjType(i), hint = Some(s"did you mean ${names.mkString(" or ")}?"))
          case Rql2RecordType(atts, _) =>
            if (atts.exists(_.idn == i)) {
              ExpectedProjType(i)
            } else {
              val names = atts.collect {
                case att if levenshteinDistance(att.idn, i) < 3 => att.idn
              }
              if (names.isEmpty) ExpectedProjType(i)
              else ExpectedType(ExpectedProjType(i), hint = Some(s"did you mean ${names.mkString(" or ")}?"))
            }
          case Rql2ListType(Rql2RecordType(atts, _), _) =>
            if (atts.exists(_.idn == i)) {
              ExpectedProjType(i)
            } else {
              val names = atts.collect {
                case att if levenshteinDistance(att.idn, i) < 3 => att.idn
              }
              if (names.isEmpty) ExpectedProjType(i)
              else ExpectedType(ExpectedProjType(i), hint = Some(s"did you mean ${names.mkString(" or ")}?"))
            }
          case Rql2IterableType(Rql2RecordType(atts, _), _) =>
            if (atts.exists(_.idn == i)) {
              ExpectedProjType(i)
            } else {
              val names = atts.collect {
                case att if levenshteinDistance(att.idn, i) < 3 => att.idn
              }
              if (names.isEmpty) ExpectedProjType(i)
              else ExpectedType(ExpectedProjType(i), hint = Some(s"did you mean ${names.mkString(" or ")}?"))
            }
          case _ => ExpectedProjType(i)
        }
      } else {
        // If projected field is empty (which the LSP special-purpose parser accepts),
        // then keep quiet by accepting anything here.
        anything
      }
  }

  override def idnType(idn: CommonIdnNode): Type = {
    resolveType(super.idnType(idn))
  }

  ///////////////////////////////////////////////////////////////////////////
  // Program Description
  ///////////////////////////////////////////////////////////////////////////

  override lazy val rootType: Option[Type] = {
    val Rql2Program(_, me) = tree.root
    me.map(tipe)
  }

  override protected def descriptionDef: TreeDescription = {
    val Rql2Program(methods, me) = tree.root
    val decls = methods.map {
      case Rql2Method(FunProto(ps, r, FunBody(e)), idn) =>
        val params = ps.map { p =>
          val t = resolveParamType(p) match {
            case Right(t) => t
            case _ => ErrorType()
          }
          TreeParamDescription(p.i.idn, t, p.e.isEmpty)
        }
        val returnType = r match {
          case Some(t) => t
          case None => actualType(e)
        }
        idn.idn -> List(TreeDeclDescription(Some(params), returnType, None))
    }.toMap
    TreeDescription(decls, me.map(tipe), None)
  }

}
