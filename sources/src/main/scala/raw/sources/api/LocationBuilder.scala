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

import raw.client.api.{OptionType, OptionValue}

import scala.util.matching.Regex

trait LocationBuilder {

  def schemes: Seq[String]

  def regex: Regex

  def validOptions: Map[String, OptionType]

  def build(groups: List[String], options: Map[String, OptionValue])(implicit sourceContext: SourceContext): Location

}
