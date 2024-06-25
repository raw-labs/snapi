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

import raw.client.api.{MapOptionValue, OptionType, OptionValue, StringOptionValue}

import scala.util.matching.Regex

trait LocationBuilder {

  def schemes: Seq[String]

  def regex: Regex

  def validOptions: Map[String, OptionType]

  def build(groups: List[String], options: Map[String, OptionValue])(implicit sourceContext: SourceContext): Location

  protected def getStringOption(options: Map[String, OptionValue], key: String): String = {
    options.get(key) match {
      case Some(StringOptionValue(value)) => key
      case Some(_) => throw new LocationException(s"$key must be a string")
      case None => throw new LocationException(s"$key is required")
    }
  }

  protected def getMapStringToStringOption(options: Map[String, OptionValue], key: String): Map[String, String] = {
    options.get(key) match {
      case Some(MapOptionValue(map)) => map.map {
          case (k, v) =>
            val nv = v match {
              case StringOptionValue(v1) => v1
              case _ => throw new LocationException(s"$key must be a map of string to string")
            }
            k -> nv
        }.toMap
      case Some(_) => throw new LocationException(s"$key must be a map of string to string")
      case None => throw new LocationException(s"$key is required")
    }
  }

}
