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

package com.rawlabs.snapi.frontend.inferrer.api

sealed abstract class SourceType

final case class SourceNothingType() extends SourceType

final case class SourceAnyType() extends SourceType

final case class SourceOrType(ors: Set[SourceNullableType]) extends SourceType

sealed abstract class SourceNullableType extends SourceType {
  def nullable: Boolean
  def notNullable: Boolean = !nullable
}

final case class SourceNullType() extends SourceNullableType {
  val nullable = true
}

sealed abstract class SourcePrimitiveType extends SourceNullableType
sealed abstract class SourceNumberType extends SourcePrimitiveType

final case class SourceByteType(nullable: Boolean) extends SourceNumberType
final case class SourceShortType(nullable: Boolean) extends SourceNumberType
final case class SourceIntType(nullable: Boolean) extends SourceNumberType
final case class SourceLongType(nullable: Boolean) extends SourceNumberType
final case class SourceFloatType(nullable: Boolean) extends SourceNumberType
final case class SourceDoubleType(nullable: Boolean) extends SourceNumberType
final case class SourceDecimalType(nullable: Boolean) extends SourceNumberType

final case class SourceBoolType(nullable: Boolean) extends SourcePrimitiveType
final case class SourceStringType(nullable: Boolean) extends SourcePrimitiveType
final case class SourceBinaryType(nullable: Boolean) extends SourcePrimitiveType

final case class SourceDateType(format: Option[String], nullable: Boolean) extends SourcePrimitiveType
final case class SourceTimeType(format: Option[String], nullable: Boolean) extends SourcePrimitiveType
final case class SourceTimestampType(format: Option[String], nullable: Boolean) extends SourcePrimitiveType
final case class SourceIntervalType(nullable: Boolean) extends SourcePrimitiveType

final case class SourceAttrType(idn: String, tipe: SourceType)
final case class SourceRecordType(atts: Vector[SourceAttrType], nullable: Boolean) extends SourceNullableType

final case class SourceCollectionType(innerType: SourceType, nullable: Boolean) extends SourceNullableType

object SourceNullableType {
  def setNullableShallowClone(t: SourceNullableType, nullable: Boolean): SourceNullableType = t match {
    case SourceByteType(_) => SourceByteType(nullable)
    case SourceShortType(_) => SourceShortType(nullable)
    case SourceIntType(_) => SourceIntType(nullable)
    case SourceLongType(_) => SourceLongType(nullable)
    case SourceFloatType(_) => SourceFloatType(nullable)
    case SourceDoubleType(_) => SourceDoubleType(nullable)
    case SourceDecimalType(_) => SourceDecimalType(nullable)
    case SourceBoolType(_) => SourceBoolType(nullable)
    case SourceStringType(_) => SourceStringType(nullable)
    case SourceDateType(fmt, _) => SourceDateType(fmt, nullable)
    case SourceTimeType(fmt, _) => SourceTimeType(fmt, nullable)
    case SourceTimestampType(fmt, _) => SourceTimestampType(fmt, nullable)
    case SourceIntervalType(_) => SourceIntervalType(nullable)
    case SourceRecordType(atts, _) => SourceRecordType(atts, nullable)
    case SourceCollectionType(innerType, _) => SourceCollectionType(innerType, nullable)
    case SourceBinaryType(_) => SourceBinaryType(nullable)
    case SourceNullType() =>
      assert(nullable, "Requesting non-nullable version of SqlNullType")
      t
  }

  def setNullableShallowClone(t: SourceNullableType): SourceNullableType = setNullableShallowClone(t, nullable = true)

  def setNotNullableShallowClone(t: SourceNullableType): SourceNullableType =
    setNullableShallowClone(t, nullable = false)

  def setNullableDeepClone(t: SourceType): SourceType = t match {
    case SourceCollectionType(innerType, _) => SourceCollectionType(setNullableDeepClone(innerType), true)
    case SourceRecordType(atts, _) =>
      SourceRecordType(atts.map(att => SourceAttrType(att.idn, setNullableDeepClone(att.tipe))), true)
    case x: SourceNullableType => setNullableShallowClone(x)
    case SourceOrType(inner) => SourceOrType(inner.map(x => setNullableDeepClone(x).asInstanceOf[SourceNullableType]))
    case _ => t
  }

}
