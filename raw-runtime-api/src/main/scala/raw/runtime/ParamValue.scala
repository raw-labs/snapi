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

package raw.runtime

import com.fasterxml.jackson.annotation.JsonSubTypes.{Type => JsonType}
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[ParamNull], name = "null"),
    new JsonType(value = classOf[ParamByte], name = "byte"),
    new JsonType(value = classOf[ParamShort], name = "short"),
    new JsonType(value = classOf[ParamInt], name = "int"),
    new JsonType(value = classOf[ParamLong], name = "long"),
    new JsonType(value = classOf[ParamFloat], name = "float"),
    new JsonType(value = classOf[ParamDouble], name = "double"),
    new JsonType(value = classOf[ParamDecimal], name = "decimal"),
    new JsonType(value = classOf[ParamBool], name = "bool"),
    new JsonType(value = classOf[ParamString], name = "string"),
    new JsonType(value = classOf[ParamDate], name = "date"),
    new JsonType(value = classOf[ParamTime], name = "time"),
    new JsonType(value = classOf[ParamTimestamp], name = "timestamp"),
    new JsonType(value = classOf[ParamInterval], name = "interval")
  )
)
trait ParamValue
final case class ParamNull() extends ParamValue
final case class ParamByte(v: java.lang.Byte) extends ParamValue
final case class ParamShort(v: java.lang.Short) extends ParamValue
final case class ParamInt(v: java.lang.Integer) extends ParamValue
final case class ParamLong(v: java.lang.Long) extends ParamValue
final case class ParamFloat(v: java.lang.Float) extends ParamValue
final case class ParamDouble(v: java.lang.Double) extends ParamValue
final case class ParamDecimal(v: BigDecimal) extends ParamValue
final case class ParamBool(v: java.lang.Boolean) extends ParamValue
final case class ParamString(v: java.lang.String) extends ParamValue
final case class ParamDate(v: java.time.LocalDate) extends ParamValue
final case class ParamTime(v: java.time.LocalTime) extends ParamValue
final case class ParamTimestamp(v: java.time.LocalDateTime) extends ParamValue
final case class ParamInterval(v: java.time.Duration) extends ParamValue
