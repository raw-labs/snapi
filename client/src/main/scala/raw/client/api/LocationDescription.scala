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

import java.time.Duration

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[IntOptionValue], name = "int"),
    new JsonType(value = classOf[StringOptionValue], name = "string"),
    new JsonType(value = classOf[BinaryOptionValue], name = "binary"),
    new JsonType(value = classOf[BooleanOptionValue], name = "boolean"),
    new JsonType(value = classOf[DurationOptionValue], name = "duration"),
    new JsonType(value = classOf[MapOptionValue], name = "map"),
    new JsonType(value = classOf[ArrayOptionValue], name = "array")
  )
) sealed trait OptionValue
final case class IntOptionValue(value: Int) extends OptionValue
final case class StringOptionValue(value: String) extends OptionValue
final case class BinaryOptionValue(value: Array[Byte]) extends OptionValue
final case class BooleanOptionValue(value: Boolean) extends OptionValue
final case class DurationOptionValue(value: Duration) extends OptionValue
final case class MapOptionValue(map: Map[OptionValue, OptionValue]) extends OptionValue
final case class ArrayOptionValue(value: Array[OptionValue]) extends OptionValue

sealed trait OptionType
case object IntOptionType extends OptionType
case object StringOptionType extends OptionType
case object BinaryOptionType extends OptionType
case object BooleanOptionType extends OptionType
case object DurationOptionType extends OptionType
final case class MapOptionType(k: OptionType, v: OptionType) extends OptionType
final case class ArrayOptionType(t: OptionType) extends OptionType

final case class LocationDescription(url: String, options: Map[String, OptionValue])
