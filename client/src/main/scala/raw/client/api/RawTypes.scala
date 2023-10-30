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

package raw.client.api

import com.fasterxml.jackson.annotation.JsonSubTypes.{Type => JsonType}
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[RawNullType], name = "null"),
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
    new JsonType(value = classOf[RawLocationType], name = "location"),
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
  def cloneNotNullable: RawType
  def cloneNotTriable: RawType
}

final case class RawNullType() extends RawType {
  val nullable = true
  val triable = false

  override def cloneNotNullable: RawType = throw new AssertionError("cannot clone RawNull as not nullable")
  override def cloneNotTriable: RawType = throw new AssertionError("cannot clone RawNull as not triable")
}

sealed abstract class RawPrimitiveType extends RawType

sealed abstract class RawNumberType extends RawPrimitiveType

final case class RawByteType(nullable: Boolean, triable: Boolean) extends RawNumberType {
  override def cloneNotNullable: RawType = RawByteType(nullable = false, triable = triable)

  override def cloneNotTriable: RawType = RawByteType(nullable = nullable, triable = false)
}
final case class RawShortType(nullable: Boolean, triable: Boolean) extends RawNumberType {
  override def cloneNotNullable: RawType = RawShortType(nullable = false, triable = triable)

  override def cloneNotTriable: RawType = RawShortType(nullable = nullable, triable = false)
}
final case class RawIntType(nullable: Boolean, triable: Boolean) extends RawNumberType {
  override def cloneNotNullable: RawType = RawIntType(nullable = false, triable = triable)

  override def cloneNotTriable: RawType = RawIntType(nullable = nullable, triable = false)
}
final case class RawLongType(nullable: Boolean, triable: Boolean) extends RawNumberType {
  override def cloneNotNullable: RawType = RawLongType(nullable = false, triable = triable)

  override def cloneNotTriable: RawType = RawLongType(nullable = nullable, triable = false)
}
final case class RawFloatType(nullable: Boolean, triable: Boolean) extends RawNumberType {
  override def cloneNotNullable: RawType = RawFloatType(nullable = false, triable = triable)

  override def cloneNotTriable: RawType = RawFloatType(nullable = nullable, triable = false)
}
final case class RawDoubleType(nullable: Boolean, triable: Boolean) extends RawNumberType {
  override def cloneNotNullable: RawType = RawDoubleType(nullable = false, triable = triable)

  override def cloneNotTriable: RawType = RawDoubleType(nullable = nullable, triable = false)
}
final case class RawDecimalType(nullable: Boolean, triable: Boolean) extends RawNumberType {
  override def cloneNotNullable: RawType = RawDecimalType(nullable = false, triable = triable)

  override def cloneNotTriable: RawType = RawDecimalType(nullable = nullable, triable = false)
}

final case class RawBoolType(nullable: Boolean, triable: Boolean) extends RawPrimitiveType {
  override def cloneNotNullable: RawType = RawBoolType(nullable = false, triable = triable)

  override def cloneNotTriable: RawType = RawBoolType(nullable = nullable, triable = false)
}
final case class RawStringType(nullable: Boolean, triable: Boolean) extends RawPrimitiveType {
  override def cloneNotNullable: RawType = RawStringType(nullable = false, triable = triable)

  override def cloneNotTriable: RawType = RawStringType(nullable = nullable, triable = false)
}
final case class RawBinaryType(nullable: Boolean, triable: Boolean) extends RawPrimitiveType {
  override def cloneNotNullable: RawType = RawBinaryType(nullable = false, triable = triable)

  override def cloneNotTriable: RawType = RawBinaryType(nullable = nullable, triable = false)
}
final case class RawLocationType(nullable: Boolean, triable: Boolean) extends RawPrimitiveType {
  override def cloneNotNullable: RawType = RawLocationType(nullable = false, triable = triable)

  override def cloneNotTriable: RawType = RawLocationType(nullable = nullable, triable = false)
}

final case class RawDateType(nullable: Boolean, triable: Boolean) extends RawPrimitiveType {
  override def cloneNotNullable: RawType = RawDateType(nullable = false, triable = triable)

  override def cloneNotTriable: RawType = RawDateType(nullable = nullable, triable = false)
}
final case class RawTimeType(nullable: Boolean, triable: Boolean) extends RawPrimitiveType {
  override def cloneNotNullable: RawType = RawTimeType(nullable = false, triable = triable)

  override def cloneNotTriable: RawType = RawTimeType(nullable = nullable, triable = false)
}
final case class RawTimestampType(nullable: Boolean, triable: Boolean) extends RawPrimitiveType {
  override def cloneNotNullable: RawType = RawTimestampType(nullable = false, triable = triable)

  override def cloneNotTriable: RawType = RawTimestampType(nullable = nullable, triable = false)
}
final case class RawIntervalType(nullable: Boolean, triable: Boolean) extends RawPrimitiveType {
  override def cloneNotNullable: RawType = RawIntervalType(nullable = false, triable = triable)

  override def cloneNotTriable: RawType = RawIntervalType(nullable = nullable, triable = false)
}

final case class RawAttrType(idn: String, tipe: RawType)
final case class RawRecordType(atts: Vector[RawAttrType], nullable: Boolean, triable: Boolean) extends RawType {
  override def cloneNotNullable: RawType = RawRecordType(atts, nullable = false, triable = triable)

  override def cloneNotTriable: RawType = RawRecordType(atts, nullable = nullable, triable = false)
}

final case class RawListType(innerType: RawType, nullable: Boolean, triable: Boolean) extends RawType {
  override def cloneNotNullable: RawType = RawListType(innerType, nullable = false, triable = triable)

  override def cloneNotTriable: RawType = RawListType(innerType, nullable = nullable, triable = false)
}

final case class RawIterableType(innerType: RawType, nullable: Boolean, triable: Boolean) extends RawType {
  override def cloneNotNullable: RawType = RawIterableType(innerType, nullable = false, triable = triable)

  override def cloneNotTriable: RawType = RawIterableType(innerType, nullable = nullable, triable = false)
}

final case class RawOrType(ors: Seq[RawType], nullable: Boolean, triable: Boolean) extends RawType {
  override def cloneNotNullable: RawType = RawOrType(ors, nullable = false, triable = triable)

  override def cloneNotTriable: RawType = RawOrType(ors, nullable = nullable, triable = false)
}
