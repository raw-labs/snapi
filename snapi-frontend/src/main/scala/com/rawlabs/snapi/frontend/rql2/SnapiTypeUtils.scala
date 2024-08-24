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

package com.rawlabs.snapi.frontend.rql2

import com.rawlabs.compiler.{
  RawAttrType,
  RawBinaryType,
  RawBoolType,
  RawByteType,
  RawDateType,
  RawDecimalType,
  RawDoubleType,
  RawFloatType,
  RawIntType,
  RawIntervalType,
  RawIterableType,
  RawListType,
  RawLongType,
  RawOrType,
  RawRecordType,
  RawShortType,
  RawStringType,
  RawTimeType,
  RawTimestampType,
  RawType,
  RawUndefinedType
}
import org.bitbucket.inkytonik.kiama.rewriting.Rewriter.{everywhere, query}
import com.rawlabs.snapi.frontend.base.source.{AnythingType, Type}
import com.rawlabs.snapi.frontend.rql2.source._
import com.rawlabs.snapi.frontend.rql2.extensions.builtin.IntervalPackageBuilder
import com.rawlabs.snapi.frontend.inferrer.api._
import com.rawlabs.snapi.frontend.rql2.extensions.builtin.{
  DatePackageBuilder,
  ListPackageBuilder,
  NullablePackageBuilder,
  RecordPackageBuilder,
  TimePackageBuilder,
  TimestampPackageBuilder
}

trait SnapiTypeUtils {

  final def hasTypeConstraint(t: Type): Boolean = {
    everywhere(query[Any] { case _: CommonTypeConstraint | _: SnapiTypeConstraint | _: AnythingType => return true })(t)
    false
  }

  final def isTypeConstraint(t: Type): Boolean = {
    t match {
      case _: CommonTypeConstraint | _: SnapiTypeConstraint | _: AnythingType => true
      case _ => false
    }
  }

  final def getProps(t: Type): Set[SnapiTypeProperty] = t match {
    case t: SnapiTypeWithProperties => t.props
    case _ => Set.empty
  }

  final def addProp(t: Type, p: SnapiTypeProperty): Type = t match {
    case t: SnapiTypeWithProperties => t.cloneAndAddProp(p)
    case _ => t
  }

  final def addProps(t: Type, props: Set[SnapiTypeProperty]): Type = {
    props.foldLeft(t) { case (t, p) => addProp(t, p) }
  }

  final def removeProp(t: Type, p: SnapiTypeProperty): Type = t match {
    case t: SnapiTypeWithProperties => t.cloneAndRemoveProp(p)
    case _ => t
  }

  final def removeProps(t: Type, props: Set[SnapiTypeProperty]): Type = {
    props.foldLeft(t) { case (t, p) => removeProp(t, p) }
  }

  final def resetProps(t: Type, props: Set[SnapiTypeProperty]): Type = {
    val clean = removeProps(t, getProps(t))
    addProps(clean, props)
  }

  final def inferTypeToRql2Type(t: SourceType, makeNullable: Boolean, makeTryable: Boolean): Type = {
    def wrapProps(t: Type, nullable: Boolean, tryable: Boolean): Type = {
      val nullType = if (nullable) addProp(t, SnapiIsNullableTypeProperty()) else t
      if (tryable) addProp(nullType, SnapiIsTryableTypeProperty()) else nullType
    }

    t match {
      case SourceByteType(n) => wrapProps(SnapiByteType(), n || makeNullable, makeTryable)
      case SourceShortType(n) => wrapProps(SnapiShortType(), n || makeNullable, makeTryable)
      case SourceIntType(n) => wrapProps(SnapiIntType(), n || makeNullable, makeTryable)
      case SourceLongType(n) => wrapProps(SnapiLongType(), n || makeNullable, makeTryable)
      case SourceFloatType(n) => wrapProps(SnapiFloatType(), n || makeNullable, makeTryable)
      case SourceDoubleType(n) => wrapProps(SnapiDoubleType(), n || makeNullable, makeTryable)
      case SourceDecimalType(n) => wrapProps(SnapiDecimalType(), n || makeNullable, makeTryable)
      case SourceBoolType(n) => wrapProps(SnapiBoolType(), n || makeNullable, makeTryable)
      case SourceStringType(n) => wrapProps(SnapiStringType(), n || makeNullable, makeTryable)
      case SourceDateType(_, n) => wrapProps(SnapiDateType(), n || makeNullable, makeTryable)
      case SourceTimeType(_, n) => wrapProps(SnapiTimeType(), n || makeNullable, makeTryable)
      case SourceTimestampType(_, n) => wrapProps(SnapiTimestampType(), n || makeNullable, makeTryable)
      case SourceIntervalType(n) => wrapProps(SnapiIntervalType(), n || makeNullable, makeTryable)
      case SourceRecordType(atts, n) => wrapProps(
          SnapiRecordType(
            atts.map(att => SnapiAttrType(att.idn, inferTypeToRql2Type(att.tipe, makeNullable, makeTryable)))
          ),
          n || makeNullable,
          makeTryable
        )
      case SourceCollectionType(inner, n) => wrapProps(
          SnapiIterableType(inferTypeToRql2Type(inner, makeNullable, makeTryable)),
          n || makeNullable,
          makeTryable
        )
      case SourceBinaryType(n) => wrapProps(SnapiBinaryType(), n || makeNullable, makeTryable)
      case _: SourceNothingType => wrapProps(SnapiUndefinedType(), makeNullable, makeTryable)
      case _: SourceNullType => wrapProps(SnapiUndefinedType(), true, makeTryable)
      case _: SourceAnyType => AnyType()
      case SourceOrType(ors) =>
        val options = ors
          .map(x => inferTypeToRql2Type(x, makeNullable, makeTryable)) // recurse with the flags
          .map(t => resetProps(t, Set.empty)) // but clear 'tryable' from or type options + nullable
        val nullable = ors.exists(_.nullable)
        if (nullable) SnapiOrType(options.toVector, Set(SnapiIsNullableTypeProperty()))
        else SnapiOrType(options.toVector)
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
        case tp: SnapiTypeWithProperties =>
          if (tp.props.contains(SnapiIsNullableTypeProperty())) {
            nullable = true
          }
          if (tp.props.contains(SnapiIsTryableTypeProperty())) {
            triable = true
          }
        case _ =>
      }
      // Convert type.
      t match {
        case _: SnapiUndefinedType => RawUndefinedType(nullable, triable)
        case _: SnapiByteType => RawByteType(nullable, triable)
        case _: SnapiShortType => RawShortType(nullable, triable)
        case _: SnapiIntType => RawIntType(nullable, triable)
        case _: SnapiLongType => RawLongType(nullable, triable)
        case _: SnapiFloatType => RawFloatType(nullable, triable)
        case _: SnapiDoubleType => RawDoubleType(nullable, triable)
        case _: SnapiDecimalType => RawDecimalType(nullable, triable)
        case _: SnapiBoolType => RawBoolType(nullable, triable)
        case _: SnapiStringType => RawStringType(nullable, triable)
        case _: SnapiBinaryType => RawBinaryType(nullable, triable)
        case _: SnapiDateType => RawDateType(nullable, triable)
        case _: SnapiTimeType => RawTimeType(nullable, triable)
        case _: SnapiTimestampType => RawTimestampType(nullable, triable)
        case _: SnapiIntervalType => RawIntervalType(nullable, triable)
        case SnapiRecordType(atts, _) => RawRecordType(
            atts.map { case SnapiAttrType(idn, t1) => RawAttrType(idn, convert(t1)) },
            nullable,
            triable
          )
        case SnapiListType(inner, _) => RawListType(convert(inner), nullable, triable)
        case SnapiIterableType(inner, _) => RawIterableType(convert(inner), nullable, triable)
        case SnapiOrType(ors, _) => RawOrType(ors.map(convert), nullable, triable)
        case _ => throw new IllegalArgumentException()
      }
    }

    try {
      Some(convert(t))
    } catch {
      case _: IllegalArgumentException => None
    }
  }

  final def valueToExp(value: SnapiValue, t: Type): Exp = value match {
    case SnapiByteValue(v) => ByteConst(v.toString)
    case SnapiShortValue(v) => ShortConst(v.toString)
    case SnapiIntValue(v) => IntConst(v.toString)
    case SnapiLongValue(v) => LongConst(v.toString)
    case SnapiFloatValue(v) => FloatConst(v.toString)
    case SnapiDoubleValue(v) => DoubleConst(v.toString)
    case SnapiStringValue(v) => StringConst(v)
    case SnapiBoolValue(v) => BoolConst(v)
    case SnapiOptionValue(option) =>
      val innerType = resetProps(t, Set.empty)
      option
        .map(v => valueToExp(v, innerType))
        .map(NullablePackageBuilder.Build(_))
        .getOrElse(NullablePackageBuilder.Empty(innerType))
    case SnapiRecordValue(vs) =>
      val SnapiRecordType(atts, _) = t
      RecordPackageBuilder.Build(
        vs.zip(atts).map { case (SnapiRecordAttr(idn, v1), att) => idn -> valueToExp(v1, att.tipe) }.toVector
      )
    case SnapiListValue(v) =>
      val SnapiListType(innerType, _) = t
      ListPackageBuilder.Build(v.map(x => valueToExp(x, innerType)): _*)
    case SnapiDateValue(v) => DatePackageBuilder.FromLocalDate(v)
    case SnapiTimeValue(v) => TimePackageBuilder.FromLocalTime(v)
    case SnapiTimestampValue(v) => TimestampPackageBuilder.FromLocalDateTime(v)
    case SnapiIntervalValue(
          years,
          month,
          weeks,
          days,
          hours,
          minutes,
          seconds,
          millis
        ) => IntervalPackageBuilder.FromRawInterval(years, month, weeks, days, hours, minutes, seconds, millis)
  }

}

object SnapiTypeUtils extends SnapiTypeUtils
