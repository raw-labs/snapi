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
import raw.compiler.rql2.source._
import raw.inferrer.api._

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
        case _ => throw new IllegalArgumentException()
      }
    }

    try {
      Some(convert(t))
    } catch {
      case _: IllegalArgumentException => None
    }
  }

}

object Rql2TypeUtils extends Rql2TypeUtils
