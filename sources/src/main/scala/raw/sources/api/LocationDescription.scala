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

import java.time.Duration

sealed trait OptionValue
final case class IntOptionValue(value: Int) extends OptionValue
final case class StringOptionValue(value: String) extends OptionValue
final case class BinaryOptionValue(value: Array[Byte]) extends OptionValue
final case class BooleanOptionValue(value: Boolean) extends OptionValue
final case class DurationOptionValue(value: Duration) extends OptionValue
final case class MapOptionValue(map: Map[OptionValue, OptionValue]) extends OptionValue
final case class ArrayOptionValue(value: Array[OptionValue]) extends OptionValue
final case class Tuple2OptionValue(v1: OptionValue, v2: OptionValue) extends OptionValue

sealed trait OptionType
case object IntOptionType extends OptionType
case object StringOptionType extends OptionType
case object BinaryOptionType extends OptionType
case object BooleanOptionType extends OptionType
case object DurationOptionType extends OptionType
final case class MapOptionType(k: OptionType, v: OptionType) extends OptionType
final case class ArrayOptionType(t: OptionType) extends OptionType
final case class Tuple2OptionType(t1: OptionType, t2: OptionType) extends OptionType

final case class LocationDescription(url: String, options: Map[String, OptionValue]) {

  def getBinaryOpt(key: String): Option[Array[Byte]] = {
    options.get(key) match {
      case Some(BinaryOptionValue(value)) => Some(value)
      case Some(_) => throw new LocationException(s"$key must be a string")
      case None => None
    }
  }

  def getBinary(key: String): Array[Byte] = {
    getBinaryOpt(key).getOrElse(throw new LocationException(s"$key is required"))
  }

  def getStringOpt(key: String): Option[String] = {
    options.get(key) match {
      case Some(StringOptionValue(value)) => Some(value)
      case Some(_) => throw new LocationException(s"$key must be a string")
      case None => None
    }
  }

  def getString(key: String): String = {
    getStringOpt(key).getOrElse(throw new LocationException(s"$key is required"))
  }

  def getMapStringStringOpt(key: String): Option[Map[String, String]] = {
    options.get(key) match {
      case Some(MapOptionValue(map)) => Some(map.map {
          case (StringOptionValue(k), v) =>
            val nv = v match {
              case StringOptionValue(v1) => v1
              case _ => throw new LocationException(s"$key must be a map of string to string")
            }
            k -> nv
        })
      case Some(_) => throw new LocationException(s"$key must be a map of string to string")
      case None => None
    }
  }

  def getMapStringString(key: String): Map[String, String] = {
    getMapStringStringOpt(key).getOrElse(throw new LocationException(s"$key is required"))
  }

  def getArrayIntOpt(key: String): Option[Array[Int]] = {
    options.get(key) match {
      case Some(ArrayOptionValue(value)) => Some(value.map {
          case IntOptionValue(v) => v
          case _ => throw new LocationException(s"$key must be an array of integers")
        })
      case Some(_) => throw new LocationException(s"$key must be an array of integers")
      case None => None
    }
  }

  def getArrayInt(key: String): Array[Int] = {
    getArrayIntOpt(key).getOrElse(throw new LocationException(s"$key is required"))
  }

  def getArrayTuple2StringStringOpt(key: String): Option[Array[(String, String)]] = {
    options.get(key) match {
      case Some(ArrayOptionValue(value)) => Some(value.map {
          case Tuple2OptionValue(StringOptionValue(v1), StringOptionValue(v2)) => (v1, v2)
          case _ => throw new LocationException(s"$key must be an array of tuples of strings")
        })
      case Some(_) => throw new LocationException(s"$key must be an array of tuples of strings")
      case None => None
    }
  }

}
