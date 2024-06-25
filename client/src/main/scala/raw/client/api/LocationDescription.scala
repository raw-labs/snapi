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

final case class LocationSettingKey(key: String)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[IntOptionValue], name = "int"),
    new JsonType(value = classOf[StringOptionValue], name = "string"),
    new JsonType(value = classOf[BinaryOptionValue], name = "binary"),
    new JsonType(value = classOf[BooleanOptionValue], name = "boolean"),
    new JsonType(value = classOf[DurationOptionValue], name = "duration"),
    new JsonType(value = classOf[MapOptionValue], name = "map"),
    new JsonType(value = classOf[AraryOptionValue], name = "array")
  )
) sealed trait OptionValue
final case class IntOptionValue(value: Int) extends OptionValue
final case class StringOptionValue(value: String) extends OptionValue
final case class BinaryOptionValue(value: Array[Byte]) extends OptionValue
final case class BooleanOptionValue(value: Boolean) extends OptionValue
final case class DurationOptionValue(value: Duration) extends OptionValue
final case class MapOptionValue(map: Seq[(String, OptionValue)]) extends OptionValue
final case class AraryOptionValue(value: Array[OptionValue]) extends OptionValue

sealed trait OptionType
case object IntOptionType extends OptionType
case object StringOptionType extends OptionType
case object BinaryOptionType extends OptionType
case object BooleanOptionType extends OptionType
case object DurationOptionType extends OptionType
final case class MapOptionType(k: OptionType, v: OptionType) extends OptionType
case object ArrayOptionType extends OptionType

final case class LocationDescription(
    url: String,
    options: Map[String, OptionValue] = Map.empty
) {

  def getIntSetting(key: String): Option[Int] = {
    settings.get(LocationSettingKey(key)) match {
      case Some(LocationIntSetting(value)) => Some(value)
      case _ => None
    }
  }

  def getStringSetting(key: String): Option[String] = {
    settings.get(LocationSettingKey(key)) match {
      case Some(LocationStringSetting(value)) => Some(value)
      case _ => None
    }
  }

  def getBinarySetting(key: String): Option[Array[Byte]] = {
    settings.get(LocationSettingKey(key)) match {
      case Some(LocationBinarySetting(value)) => Some(value.toArray)
      case _ => None
    }
  }

  def getBooleanSetting(key: String): Option[Boolean] = {
    settings.get(LocationSettingKey(key)) match {
      case Some(LocationBooleanSetting(value)) => Some(value)
      case _ => None
    }
  }

  def getDurationSettings(key: String): Option[Duration] = {
    settings.get(LocationSettingKey(key)) match {
      case Some(LocationDurationSetting(value)) => Some(value)
      case _ => None
    }
  }

  def getKVSetting(key: String): Option[Array[(String, String)]] = {
    settings.get(LocationSettingKey(key)) match {
      case Some(LocationKVSetting(value)) => Some(value.toArray)
      case _ => None
    }
  }

  def getIntArraySetting(key: String): Option[Array[Int]] = {
    settings.get(LocationSettingKey(key)) match {
      case Some(LocationIntArraySetting(value)) => Some(value)
      case _ => None
    }
  }

}
