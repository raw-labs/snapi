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

package raw.inferrer.local

import org.apache.commons.lang3.math.NumberUtils
import raw.inferrer._

import scala.collection.mutable

case class CleanedTemporalFormats(
    cleanedType: SourceType,
    timeFormat: Option[String],
    dateFormat: Option[String],
    timestampFormat: Option[String]
)

private[inferrer] trait TextTypeInferrer extends DatetimeFormatFinder {

  def getType(value: String, currentType: SourceType): SourceType = {
    (currentType: @unchecked) match {
      case _: SourceStringType => currentType
      case SourceBoolType(n) =>
        if (tryAsBoolean(value)) currentType
        else SourceStringType(n)
      case _: SourceNothingType =>
        if (isNumber(value)) {
          findNumber(value, false)
        } else if (tryAsBoolean(value)) SourceBoolType(false)
        else {
          findTemporal(value, false)
            .getOrElse(SourceStringType(false))
        }
      case _: SourceNullType =>
        if (isNumber(value)) {
          findNumber(value, true)
        } else if (tryAsBoolean(value)) SourceBoolType(true)
        else {
          findTemporal(value, true)
            .getOrElse(SourceStringType(true))
        }
      case number: SourceNumberType =>
        if (isNumber(value)) {
          (currentType: @unchecked) match {
            case SourceIntType(n) => findNumber(value, n)
            case SourceLongType(n) =>
              if (tryAsLong(value)) SourceLongType(n)
              else if (tryAsDouble(value)) SourceDoubleType(n)
              else SourceDecimalType(n)
            case SourceDoubleType(n) =>
              if (tryAsDouble(value)) SourceDoubleType(n)
              else SourceDecimalType(n)
            case SourceDecimalType(n) => SourceDecimalType(n)
          }
        } else {
          SourceStringType(number.nullable)
        }
      case SourceTimeType(Some(fmt), n) =>
        if (tryTimeFormat(value, fmt)) SourceTimeType(Some(fmt), n)
        else SourceStringType(n)
      case SourceDateType(Some(fmt), n) =>
        if (tryDateFormat(value, fmt)) SourceDateType(Some(fmt), n)
        else SourceStringType(n)
      case SourceTimestampType(Some(fmt), n) =>
        if (tryTimestampFormat(value, fmt)) SourceTimestampType(Some(fmt), n)
        else SourceStringType(n)
      case c: SourceCollectionType => SourceOrType(Set(SourceStringType(false), c))
      case or: SourceOrType => addElementToOrType(or, value)
    }
  }

  def addElementToOrType(or: SourceOrType, value: String): SourceOrType = {
    // will try to merge the new value with a primitive type, if it exists
    val primitive = or.ors.find(_.isInstanceOf[SourcePrimitiveType])
    val mutableSet: mutable.HashSet[SourceNullableType] = mutable.HashSet(or.ors.toSeq: _*)
    primitive.foreach(toRemove => mutableSet.remove(toRemove))
    val toAdd = getType(value, primitive.getOrElse(SourceNothingType()))
    toAdd match {
      case newType: SourceNullableType => mutableSet.add(newType)
      case _ => throw new InferrerException(s"Could not add type ${toAdd.getClass.getName} to or-type")
    }
    SourceOrType(mutableSet.toSet)
  }

  private def findNumber(value: String, nullable: Boolean): SourceType = {
    if (tryAsInt(value)) SourceIntType(nullable)
    else if (tryAsLong(value)) SourceLongType(nullable)
    else if (tryAsDouble(value)) SourceDoubleType(nullable)
    else SourceDecimalType(nullable)
  }

  private def findTemporal(value: String, nullable: Boolean): Option[SourceType] = {
    // Tries times, dates and them timestamps in a nested set of map.getOrElse
    val maybeTemporal: Option[SourceType] = getTime(value)
      .map(t => Some(SourceTimeType(Some(t._1), nullable)))
      .getOrElse(
        getDate(value)
          .map(d => Some(SourceDateType(Some(d._1), nullable)))
          .getOrElse(getTimestamp(value).map(d => SourceTimestampType(Some(d._1), nullable)))
      )
    maybeTemporal
  }

  private def tryAsBoolean(s: String): Boolean = {
    s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false")
  }

  final protected def isNumber(v: String) = {
    // We do not accept hexadecimal numbers like the NumberUtils.isCreatable function
    if (!v.startsWith("0x") && !v.startsWith("0X")) {
      NumberUtils.isCreatable(v)
    } else {
      false
    }
  }

  private def tryAsInt(v: String): Boolean = {
    try {
      Integer.parseInt(v)
      true
    } catch {
      case _: NumberFormatException => false
    }
  }

  private def tryAsLong(v: String): Boolean = {
    try {
      java.lang.Long.parseLong(v)
      true
    } catch {
      case _: NumberFormatException => false
    }
  }

  final protected def tryAsDouble(v: String): Boolean =
    try {
      java.lang.Double.parseDouble(v)
      true
    } catch {
      case _: NumberFormatException => false
    }

  def collectTemporalFormats(t: SourceType): (Set[String], Set[String], Set[String]) = {
    val tFormats: mutable.HashSet[String] = mutable.HashSet[String]()
    val dFormats: mutable.HashSet[String] = mutable.HashSet[String]()
    val tsFormats: mutable.HashSet[String] = mutable.HashSet[String]()

    def recurse(t: SourceType): Unit = t match {
      case SourceCollectionType(innerType, _) => recurse(innerType)
      case SourceRecordType(atts, _) => atts.foreach(att => recurse(att.tipe))
      case SourceOrType(inner) => inner.foreach(x => recurse(x))
      case x: SourceTimeType => x.format.foreach(f => tFormats += f)
      case x: SourceDateType => x.format.foreach(f => dFormats += f)
      case x: SourceTimestampType => x.format.foreach(f => tsFormats += f)
      case _ =>
    }
    recurse(t)

    (tFormats.toSet, dFormats.toSet, tsFormats.toSet)
  }

  def temporalsAsStrings(t: SourceType, times: Boolean, dates: Boolean, timestamps: Boolean): SourceType = t match {
    case SourceCollectionType(innerType, n) =>
      SourceCollectionType(temporalsAsStrings(innerType, times, dates, timestamps), n)
    case SourceRecordType(atts, n) => SourceRecordType(
        atts.map(att => SourceAttrType(att.idn, temporalsAsStrings(att.tipe, times, dates, timestamps))),
        n
      )
    case x: SourceTimestampType if timestamps => SourceStringType(x.nullable)
    case x: SourceTimeType if times => SourceStringType(x.nullable)
    case x: SourceDateType if dates => SourceStringType(x.nullable)
    case _ => t
  }

  def uniquifyTemporalFormats(t: SourceType) = {
    val (tFormats, dFormats, tsFormats) = collectTemporalFormats(t)

    // if there isn't exactly one date/time/timestamp format then it will make then strings
    val maybeTimeFormat = if (tFormats.size == 1) Some(tFormats.head) else None
    val maybeDateFormat = if (dFormats.size == 1) Some(dFormats.head) else None
    val maybeTimestampFormat = if (tsFormats.size == 1) Some(tsFormats.head) else None

    val finalType = temporalsAsStrings(
      t,
      maybeTimeFormat.isEmpty,
      maybeDateFormat.isEmpty,
      maybeTimestampFormat.isEmpty
    )

    CleanedTemporalFormats(finalType, maybeTimeFormat, maybeDateFormat, maybeTimestampFormat)
  }
}
