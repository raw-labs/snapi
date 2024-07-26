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

import raw.sources.api.LocationConfig

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[RawNull], name = "null"),
    new JsonType(value = classOf[RawError], name = "error"),
    new JsonType(value = classOf[RawByte], name = "byte"),
    new JsonType(value = classOf[RawShort], name = "short"),
    new JsonType(value = classOf[RawInt], name = "int"),
    new JsonType(value = classOf[RawLong], name = "long"),
    new JsonType(value = classOf[RawFloat], name = "float"),
    new JsonType(value = classOf[RawDouble], name = "double"),
    new JsonType(value = classOf[RawDecimal], name = "decimal"),
    new JsonType(value = classOf[RawBool], name = "bool"),
    new JsonType(value = classOf[RawString], name = "string"),
    new JsonType(value = classOf[RawBinary], name = "binary"),
    new JsonType(value = classOf[RawLocation], name = "location"),
    new JsonType(value = classOf[RawDate], name = "date"),
    new JsonType(value = classOf[RawTime], name = "time"),
    new JsonType(value = classOf[RawTimestamp], name = "timestamp"),
    new JsonType(value = classOf[RawInterval], name = "interval"),
    new JsonType(value = classOf[RawRecord], name = "record"),
    new JsonType(value = classOf[RawList], name = "list"),
    new JsonType(value = classOf[RawIterable], name = "iterable"),
    new JsonType(value = classOf[RawOr], name = "or")
  )
)
sealed trait RawValue
final case class RawAny(v: Any) extends RawValue
final case class RawNull() extends RawValue
final case class RawError(v: String) extends RawValue
final case class RawByte(v: java.lang.Byte) extends RawValue
final case class RawShort(v: java.lang.Short) extends RawValue
final case class RawInt(v: java.lang.Integer) extends RawValue
final case class RawLong(v: java.lang.Long) extends RawValue
final case class RawFloat(v: java.lang.Float) extends RawValue
final case class RawDouble(v: java.lang.Double) extends RawValue
final case class RawDecimal(v: java.math.BigDecimal) extends RawValue
final case class RawBool(v: java.lang.Boolean) extends RawValue
final case class RawString(v: java.lang.String) extends RawValue
final case class RawBinary(v: Array[Byte]) extends RawValue
final case class RawLocation(v: LocationConfig) extends RawValue
final case class RawDate(v: java.time.LocalDate) extends RawValue
final case class RawTime(v: java.time.LocalTime) extends RawValue
final case class RawTimestamp(v: java.time.LocalDateTime) extends RawValue
final case class RawInterval(
    years: Int,
    months: Int,
    weeks: Int,
    days: Int,
    hours: Int,
    minutes: Int,
    seconds: Int,
    millis: Int
) extends RawValue
final case class RawRecord(v: Seq[RawValue]) extends RawValue
final case class RawList(v: Seq[RawValue]) extends RawValue
final case class RawIterable(v: Seq[RawValue]) extends RawValue // Data has been ready is now materialized.
final case class RawOr(vs: Seq[RawValue]) extends RawValue
