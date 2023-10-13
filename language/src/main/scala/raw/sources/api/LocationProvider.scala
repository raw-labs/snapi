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

package raw.sources.api

import com.fasterxml.jackson.annotation.JsonSubTypes.{Type => JsonType}
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}

import java.time.Duration

final case class LocationSettingKey(key: String)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[LocationIntSetting], name = "int"),
    new JsonType(value = classOf[LocationStringSetting], name = "string"),
    new JsonType(value = classOf[LocationBinarySetting], name = "binary"),
    new JsonType(value = classOf[LocationBooleanSetting], name = "boolean"),
    new JsonType(value = classOf[LocationDurationSetting], name = "duration"),
    new JsonType(value = classOf[LocationKVSetting], name = "kv"),
    new JsonType(value = classOf[LocationIntArraySetting], name = "array-int")
  )
) sealed trait LocationSettingValue
final case class LocationIntSetting(value: Int) extends LocationSettingValue
final case class LocationStringSetting(value: String) extends LocationSettingValue
final case class LocationBinarySetting(value: Seq[Byte]) extends LocationSettingValue
final case class LocationBooleanSetting(value: Boolean) extends LocationSettingValue
final case class LocationDurationSetting(value: Duration) extends LocationSettingValue
final case class LocationKVSetting(map: Seq[(String, String)]) extends LocationSettingValue
final case class LocationIntArraySetting(value: Array[Int]) extends LocationSettingValue

final case class LocationDescription(
    url: String,
    settings: Map[LocationSettingKey, LocationSettingValue] = Map.empty
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

trait LocationProvider {

  def build(location: LocationDescription)(implicit sourceContext: SourceContext): Location

  protected def getScheme(url: String): Option[String] = {
    val i = url.indexOf(':')
    if (i == -1) None
    else Some(url.take(i))
  }

}
