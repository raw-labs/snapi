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
import raw.compiler.base.source.{AnythingType, Type}
import raw.compiler.common.source._
import raw.compiler.rql2.source._
import raw.inferrer._

trait Rql2TypeUtils {

  final def hasTypeConstraint(t: Type): Boolean = {
    // TODO (msb): Make use of TypeConstraint trait over base, common and rql2!
    everywhere(query[Type] {
      case _: OneOfType => return true
      case _: ExpectedRegexType => return true
      case _: ExpectedRecordType => return true
      case _: ExpectedFunType => return true
      case _: CastableToType => return true
      case _: ExpectedProjType => return true
      case _: MergeableType => return true
      case _: HasTypeProperties => return true
      case _: DoesNotHaveTypeProperties => return true
      case _: AnythingType => return true
    })(t)
    false
  }

  final def isTypeConstraint(t: Type): Boolean = {
    // TODO (msb): Make use of TypeConstraint trait over base, common and rql2!
    (query[Type] {
      case _: OneOfType => return true
      case _: ExpectedRegexType => return true
      case _: ExpectedRecordType => return true
      case _: ExpectedFunType => return true
      case _: CastableToType => return true
      case _: ExpectedProjType => return true
      case _: MergeableType => return true
      case _: HasTypeProperties => return true
      case _: IsTryable => return true
      case _: IsNullable => return true
      case _: DoesNotHaveTypeProperties => return true
      case _: AnythingType => return true
    })(t)
    false
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

  def rql2ToCommonType(t: Type): Type = {

    t match {
      case Rql2StringType(props) => handleProps(props, StringType())
      case Rql2BoolType(props) => handleProps(props, BoolType())
      case Rql2BinaryType(props) => handleProps(props, BinaryType())
      case Rql2LocationType(props) => handleProps(props, LocationType())
      case Rql2ByteType(props) => handleProps(props, ByteType())
      case Rql2ShortType(props) => handleProps(props, ShortType())
      case Rql2IntType(props) => handleProps(props, IntType())
      case Rql2LongType(props) => handleProps(props, LongType())
      case Rql2FloatType(props) => handleProps(props, FloatType())
      case Rql2DoubleType(props) => handleProps(props, DoubleType())
      case Rql2DecimalType(props) => handleProps(props, DecimalType())
      case Rql2DateType(props) => handleProps(props, DateType())
      case Rql2TimeType(props) => handleProps(props, TimeType())
      case Rql2TimestampType(props) => handleProps(props, TimestampType())
      case Rql2IntervalType(props) => handleProps(props, IntervalType())
      case Rql2RegexType(n, props) => handleProps(props, RegexType(n))
      case Rql2UndefinedType(props) => handleProps(props, NothingType())
      case Rql2ListType(innerType, props) => handleProps(props, ListType(rql2ToCommonType(innerType)))
      case Rql2IterableType(innerType, props) => handleProps(props, IterableType(rql2ToCommonType(innerType)))
      case Rql2RecordType(atts, props) =>
        handleProps(props, RecordType(atts.map(att => AttrType(att.idn, rql2ToCommonType(att.tipe)))))
      case Rql2OrType(types, props) => handleProps(props, OrType(types.map(rql2ToCommonType)))
      case ExpType(t) => rql2ToCommonType(t)
      case PackageType(_) => VoidType()
      case PackageEntryType(_, _) => VoidType()
    }
  }

  protected def handleProps(props: Set[Rql2TypeProperty], t: Type): Type = {
    var r = t
    if (props.contains(Rql2IsNullableTypeProperty())) {
      r = OptionType(r)
    }
    // TryType is handled last to make sure TryType is at the top.
    if (props.contains(Rql2IsTryableTypeProperty())) {
      r = TryType(r)
    }
    r
  }
}

object Rql2TypeUtils extends Rql2TypeUtils
