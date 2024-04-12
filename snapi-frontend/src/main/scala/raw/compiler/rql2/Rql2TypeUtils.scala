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

import org.bitbucket.inkytonik.kiama.rewriting.Rewriter.{everywhere, query}
import raw.client.api._
import raw.compiler.base.source.{AnythingType, Type}
import raw.compiler.common.source._
import raw.compiler.rql2.api._
import raw.compiler.rql2.source._
import raw.inferrer.api._

import scala.collection.mutable

trait Rql2TypeUtils {

  final def hasTypeConstraint(t: Type): Boolean = {
    everywhere(query[Any] { case _: CommonTypeConstraint | _: Rql2TypeConstraint | _: AnythingType => return true })(t)
    false
  }

  final def isTypeConstraint(t: Type): Boolean = {
    t match {
      case _: CommonTypeConstraint | _: Rql2TypeConstraint | _: AnythingType => true
      case _ => false
    }
  }

  final def getProps(t: Type): Set[Rql2TypeProperty] = t match {
    case t: Rql2TypeWithProperties => t.props
    case _ => Set.empty
  }

  final def addProp(t: Type, p: Rql2TypeProperty): Type = t match {
    case t: Rql2TypeWithProperties => t.cloneAndAddProp(p)
    case _ => t
  }

  final def addProps(t: Type, props: Set[Rql2TypeProperty]): Type = {
    props.foldLeft(t) { case (t, p) => addProp(t, p) }
  }

  final def removeProp(t: Type, p: Rql2TypeProperty): Type = t match {
    case t: Rql2TypeWithProperties => t.cloneAndRemoveProp(p)
    case _ => t
  }

  final def removeProps(t: Type, props: Set[Rql2TypeProperty]): Type = {
    props.foldLeft(t) { case (t, p) => removeProp(t, p) }
  }

  final def resetProps(t: Type, props: Set[Rql2TypeProperty]): Type = {
    val clean = removeProps(t, getProps(t))
    addProps(clean, props)
  }

  final def inferTypeToRql2Type(t: SourceType, makeNullable: Boolean, makeTryable: Boolean): Type = {
    def wrapProps(t: Type, nullable: Boolean, tryable: Boolean): Type = {
      val nullType = if (nullable) addProp(t, Rql2IsNullableTypeProperty()) else t
      if (tryable) addProp(nullType, Rql2IsTryableTypeProperty()) else nullType
    }

    t match {
      case SourceByteType(n) => wrapProps(Rql2ByteType(), n || makeNullable, makeTryable)
      case SourceShortType(n) => wrapProps(Rql2ShortType(), n || makeNullable, makeTryable)
      case SourceIntType(n) => wrapProps(Rql2IntType(), n || makeNullable, makeTryable)
      case SourceLongType(n) => wrapProps(Rql2LongType(), n || makeNullable, makeTryable)
      case SourceFloatType(n) => wrapProps(Rql2FloatType(), n || makeNullable, makeTryable)
      case SourceDoubleType(n) => wrapProps(Rql2DoubleType(), n || makeNullable, makeTryable)
      case SourceDecimalType(n) => wrapProps(Rql2DecimalType(), n || makeNullable, makeTryable)
      case SourceBoolType(n) => wrapProps(Rql2BoolType(), n || makeNullable, makeTryable)
      case SourceStringType(n) => wrapProps(Rql2StringType(), n || makeNullable, makeTryable)
      case SourceDateType(_, n) => wrapProps(Rql2DateType(), n || makeNullable, makeTryable)
      case SourceTimeType(_, n) => wrapProps(Rql2TimeType(), n || makeNullable, makeTryable)
      case SourceTimestampType(_, n) => wrapProps(Rql2TimestampType(), n || makeNullable, makeTryable)
      case SourceIntervalType(n) => wrapProps(Rql2IntervalType(), n || makeNullable, makeTryable)
      case SourceRecordType(atts, n) => wrapProps(
          Rql2RecordType(
            atts.map(att => Rql2AttrType(att.idn, inferTypeToRql2Type(att.tipe, makeNullable, makeTryable)))
          ),
          n || makeNullable,
          makeTryable
        )
      case SourceCollectionType(inner, n) => wrapProps(
          Rql2IterableType(inferTypeToRql2Type(inner, makeNullable, makeTryable)),
          n || makeNullable,
          makeTryable
        )
      case SourceBinaryType(n) => wrapProps(Rql2BinaryType(), n || makeNullable, makeTryable)
      case _: SourceNothingType => wrapProps(Rql2UndefinedType(), makeNullable, makeTryable)
      case _: SourceNullType => wrapProps(Rql2UndefinedType(), true, makeTryable)
      case _: SourceAnyType => AnyType()
      case SourceOrType(ors) =>
        val options = ors
          .map(x => inferTypeToRql2Type(x, makeNullable, makeTryable)) // recurse with the flags
          .map(t => resetProps(t, Set.empty)) // but clear 'tryable' from or type options + nullable
        val nullable = ors.exists(_.nullable)
        if (nullable) Rql2OrType(options.toVector, Set(Rql2IsNullableTypeProperty()))
        else Rql2OrType(options.toVector)
    }

  }

  def isComparable(t: Type): Boolean = {
    everywhere(query[Type] { case _: FunType => return false })(t)
    true
  }

  // Returns Option[RawType] as not all Rql2 types are representable as Raw types.
  def rql2TypeToRawType(t: Type): Option[RawType] = {
    def convert(t: Type): RawType = {
      // Read nullable and triable properties.
      var nullable = false
      var triable = false
      t match {
        case tp: Rql2TypeWithProperties =>
          if (tp.props.contains(Rql2IsNullableTypeProperty())) {
            nullable = true
          }
          if (tp.props.contains(Rql2IsTryableTypeProperty())) {
            triable = true
          }
        case _ =>
      }
      // Convert type.
      t match {
        case _: Rql2UndefinedType => RawUndefinedType(nullable, triable)
        case _: Rql2ByteType => RawByteType(nullable, triable)
        case _: Rql2ShortType => RawShortType(nullable, triable)
        case _: Rql2IntType => RawIntType(nullable, triable)
        case _: Rql2LongType => RawLongType(nullable, triable)
        case _: Rql2FloatType => RawFloatType(nullable, triable)
        case _: Rql2DoubleType => RawDoubleType(nullable, triable)
        case _: Rql2DecimalType => RawDecimalType(nullable, triable)
        case _: Rql2BoolType => RawBoolType(nullable, triable)
        case _: Rql2StringType => RawStringType(nullable, triable)
        case _: Rql2BinaryType => RawBinaryType(nullable, triable)
        case _: Rql2LocationType => RawLocationType(nullable, triable)
        case _: Rql2DateType => RawDateType(nullable, triable)
        case _: Rql2TimeType => RawTimeType(nullable, triable)
        case _: Rql2TimestampType => RawTimestampType(nullable, triable)
        case _: Rql2IntervalType => RawIntervalType(nullable, triable)
        case Rql2RecordType(atts, _) => RawRecordType(
            atts.map { case Rql2AttrType(idn, t1) => RawAttrType(idn, convert(t1)) },
            nullable,
            triable
          )
        case Rql2ListType(inner, _) => RawListType(convert(inner), nullable, triable)
        case Rql2IterableType(inner, _) => RawIterableType(convert(inner), nullable, triable)
        case Rql2OrType(ors, _) => RawOrType(ors.map(convert), nullable, triable)
        case ExpType(inner) => RawExpType(convert(inner))
        case FunType(ms, os, r, _) =>
          RawFunType(ms.map(convert), os.map(x => RawFunOptParamType(x.i, convert(x.t))), convert(r))
        case _ => throw new IllegalArgumentException()
      }
    }

    try {
      Some(convert(t))
    } catch {
      case _: IllegalArgumentException => None
    }
  }

  def rawTypeToRql2Type(t: RawType): Type = {

    def flags(nullable: Boolean, triable: Boolean): Set[Rql2TypeProperty] = {
      val props = mutable.HashSet.empty[Rql2TypeProperty]
      if (nullable) {
        props.add(Rql2IsNullableTypeProperty())
      }
      if (triable) {
        props.add(Rql2IsTryableTypeProperty())
      }
      props.toSet
    }

    t match {
      case RawUndefinedType(nullable, triable) => Rql2UndefinedType(flags(nullable, triable))
      case RawByteType(nullable, triable) => Rql2ByteType(flags(nullable, triable))
      case RawShortType(nullable, triable) => Rql2ShortType(flags(nullable, triable))
      case RawIntType(nullable, triable) => Rql2IntType(flags(nullable, triable))
      case RawLongType(nullable, triable) => Rql2LongType(flags(nullable, triable))
      case RawFloatType(nullable, triable) => Rql2FloatType(flags(nullable, triable))
      case RawDoubleType(nullable, triable) => Rql2DoubleType(flags(nullable, triable))
      case RawDecimalType(nullable, triable) => Rql2DecimalType(flags(nullable, triable))
      case RawBoolType(nullable, triable) => Rql2BoolType(flags(nullable, triable))
      case RawStringType(nullable, triable) => Rql2StringType(flags(nullable, triable))
      case RawBinaryType(nullable, triable) => Rql2BinaryType(flags(nullable, triable))
      case RawLocationType(nullable, triable) => Rql2LocationType(flags(nullable, triable))
      case RawDateType(nullable, triable) => Rql2DateType(flags(nullable, triable))
      case RawTimeType(nullable, triable) => Rql2TimeType(flags(nullable, triable))
      case RawTimestampType(nullable, triable) => Rql2TimestampType(flags(nullable, triable))
      case RawIntervalType(nullable, triable) => Rql2IntervalType(flags(nullable, triable))
      case RawRecordType(atts, nullable, triable) => Rql2RecordType(
          atts.map { case RawAttrType(idn, t) => Rql2AttrType(idn, rawTypeToRql2Type(t)) },
          flags(nullable, triable)
        )
      case RawListType(innerType, nullable, triable) =>
        Rql2ListType(rawTypeToRql2Type(innerType), flags(nullable, triable))
      case RawIterableType(innerType, nullable, triable) =>
        Rql2IterableType(rawTypeToRql2Type(innerType), flags(nullable, triable))
      case RawOrType(ors, nullable, triable) => Rql2OrType(ors.map(rawTypeToRql2Type).to, flags(nullable, triable))
    }
  }

  def rawValueToRql2Value(v: RawValue, t: RawType): Value = {
    def wrap(t: RawType, v: Value): Value = {
      assert(!v.isInstanceOf[TryValue] || !v.isInstanceOf[OptionValue])
      if (t.triable && t.nullable) {
        TryValue(Right(OptionValue(Some(v))))
      } else if (t.triable) {
        TryValue(Right(v))
      } else if (t.nullable) {
        OptionValue(Some(v))
      } else {
        v
      }
    }

    // Convert type.
    v match {
      case RawError(v) => TryValue(Left(v))
      case RawNull() =>
        if (t.triable) {
          TryValue(Right(OptionValue(None)))
        } else {
          OptionValue(None)
        }
      case RawByte(v) => wrap(t, ByteValue(v))
      case RawShort(v) => wrap(t, ShortValue(v))
      case RawInt(v) => wrap(t, IntValue(v))
      case RawLong(v) => wrap(t, LongValue(v))
      case RawFloat(v) => wrap(t, FloatValue(v))
      case RawDouble(v) => wrap(t, DoubleValue(v))
      case RawDecimal(v) => wrap(t, DecimalValue(v))
      case RawBool(v) => wrap(t, BoolValue(v))
      case RawString(v) => wrap(t, StringValue(v))
      case RawBinary(v) => wrap(t, BinaryValue(v))
      case RawLocation(v) => wrap(t, LocationValue(v))
      case RawDate(v) => wrap(t, DateValue(v))
      case RawTime(v) => wrap(t, TimeValue(v))
      case RawTimestamp(v) => wrap(t, TimestampValue(v))
      case RawInterval(years, months, weeks, days, hours, minutes, seconds, millis) =>
        wrap(t, IntervalValue(years, months, weeks, days, hours, minutes, seconds, millis))
      case RawRecord(vs) =>
        val recordType = t.asInstanceOf[RawRecordType]
        val atts = vs.zipWithIndex.map { case (v, idx) => rawValueToRql2Value(v, recordType.atts(idx).tipe) }
        wrap(t, RecordValue(atts))
      case RawList(vs) => wrap(t, ListValue(vs.map(rawValueToRql2Value(_, t.asInstanceOf[RawListType].innerType))))
      case RawIterable(vs) =>
        wrap(t, IterableValue(vs.map(rawValueToRql2Value(_, t.asInstanceOf[RawIterableType].innerType))))
      case RawOr(ors) =>
        val orType = t.asInstanceOf[RawOrType]
        val options = ors.zipWithIndex.map { case (v1, idx) => rawValueToRql2Value(v1, orType.ors(idx)) }
        wrap(t, OrValue(options))
    }
  }

}

object Rql2TypeUtils extends Rql2TypeUtils
