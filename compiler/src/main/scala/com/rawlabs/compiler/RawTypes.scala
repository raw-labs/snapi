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

package com.rawlabs.compiler

import com.fasterxml.jackson.annotation.JsonSubTypes.{Type => JsonType}
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[RawAnyType], name = "any"),
    new JsonType(value = classOf[RawUndefinedType], name = "undefined"),
    new JsonType(value = classOf[RawByteType], name = "byte"),
    new JsonType(value = classOf[RawShortType], name = "short"),
    new JsonType(value = classOf[RawIntType], name = "int"),
    new JsonType(value = classOf[RawLongType], name = "long"),
    new JsonType(value = classOf[RawFloatType], name = "float"),
    new JsonType(value = classOf[RawDoubleType], name = "double"),
    new JsonType(value = classOf[RawDecimalType], name = "decimal"),
    new JsonType(value = classOf[RawBoolType], name = "bool"),
    new JsonType(value = classOf[RawStringType], name = "string"),
    new JsonType(value = classOf[RawBinaryType], name = "binary"),
    new JsonType(value = classOf[RawDateType], name = "date"),
    new JsonType(value = classOf[RawTimeType], name = "time"),
    new JsonType(value = classOf[RawTimestampType], name = "timestamp"),
    new JsonType(value = classOf[RawIntervalType], name = "interval"),
    new JsonType(value = classOf[RawRecordType], name = "record"),
    new JsonType(value = classOf[RawListType], name = "list"),
    new JsonType(value = classOf[RawIterableType], name = "iterable"),
    new JsonType(value = classOf[RawOrType], name = "or")
  )
)
sealed abstract class RawType {
  def nullable: Boolean
  def triable: Boolean
  def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType
  def cloneNullable: RawType = cloneWithFlags(nullable = true, triable = triable)
  def cloneNotNullable: RawType = cloneWithFlags(nullable = false, triable = triable)
  def cloneTriable: RawType = cloneWithFlags(nullable = nullable, triable = true)
  def cloneNotTriable: RawType = cloneWithFlags(nullable = nullable, triable = false)
}

final case class RawAnyType() extends RawType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType =
    throw new AssertionError("unsupported operation")

  override def nullable: Boolean = false

  override def triable: Boolean = false
}

final case class RawUndefinedType(nullable: Boolean, triable: Boolean) extends RawType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType = RawUndefinedType(nullable, triable)
}

sealed abstract class RawPrimitiveType extends RawType

sealed abstract class RawNumberType extends RawPrimitiveType

final case class RawByteType(nullable: Boolean, triable: Boolean) extends RawNumberType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType = RawByteType(nullable, triable)
}
final case class RawShortType(nullable: Boolean, triable: Boolean) extends RawNumberType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType = RawShortType(nullable, triable)
}
final case class RawIntType(nullable: Boolean, triable: Boolean) extends RawNumberType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType = RawIntType(nullable, triable)
}
final case class RawLongType(nullable: Boolean, triable: Boolean) extends RawNumberType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType = RawLongType(nullable, triable)
}
final case class RawFloatType(nullable: Boolean, triable: Boolean) extends RawNumberType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType = RawFloatType(nullable, triable)
}
final case class RawDoubleType(nullable: Boolean, triable: Boolean) extends RawNumberType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType = RawDoubleType(nullable, triable)
}
final case class RawDecimalType(nullable: Boolean, triable: Boolean) extends RawNumberType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType = RawDecimalType(nullable, triable)
}

final case class RawBoolType(nullable: Boolean, triable: Boolean) extends RawPrimitiveType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType = RawBoolType(nullable, triable)
}
final case class RawStringType(nullable: Boolean, triable: Boolean) extends RawPrimitiveType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType = RawStringType(nullable, triable)
}
final case class RawBinaryType(nullable: Boolean, triable: Boolean) extends RawPrimitiveType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType = RawBinaryType(nullable, triable)
}
sealed abstract class RawTemporalType extends RawPrimitiveType

final case class RawDateType(nullable: Boolean, triable: Boolean) extends RawTemporalType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType = RawDateType(nullable, triable)
}
final case class RawTimeType(nullable: Boolean, triable: Boolean) extends RawTemporalType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType = RawTimeType(nullable, triable)
}
final case class RawTimestampType(nullable: Boolean, triable: Boolean) extends RawTemporalType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType = RawTimestampType(nullable, triable)
}
final case class RawIntervalType(nullable: Boolean, triable: Boolean) extends RawTemporalType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType = RawIntervalType(nullable, triable)
}

final case class RawAttrType(idn: String, tipe: RawType)
final case class RawRecordType(atts: Vector[RawAttrType], nullable: Boolean, triable: Boolean) extends RawType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType = RawRecordType(atts, nullable, triable)
}

final case class RawListType(innerType: RawType, nullable: Boolean, triable: Boolean) extends RawType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType = RawListType(innerType, nullable, triable)
}

final case class RawIterableType(innerType: RawType, nullable: Boolean, triable: Boolean) extends RawType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType =
    RawIterableType(innerType, nullable, triable)
}

final case class RawOrType(ors: Vector[RawType], nullable: Boolean, triable: Boolean) extends RawType {
  override def cloneWithFlags(nullable: Boolean, triable: Boolean): RawType = RawOrType(ors, nullable, triable)
}
