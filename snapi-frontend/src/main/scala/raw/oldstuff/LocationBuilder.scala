/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

///*
// * Copyright 2023 RAW Labs S.A.
// *
// * Use of this software is governed by the Business Source License
// * included in the file licenses/BSL.txt.
// *
// * As of the Change Date specified in that file, in accordance with
// * the Business Source License, use of this software will be governed
// * by the Apache License, Version 2.0, included in the file
// * licenses/APL.txt.
// */
//
//package raw.sources.api
//
//import scala.util.matching.Regex
//
//final case class OptionDefinition(
//    name: String,
//    optionType: OptionType,
//    mandatory: Boolean
//)
//
//trait LocationBuilder {
//
//  def schemes: Seq[String]
//
//  def validOptions: Seq[OptionDefinition]
//
//  def build(desc: LocationDescription)(implicit sourceContext: SourceContext): Location
//
//  protected def getRegexMatchingGroups(url: String, regex: Regex): List[String] = {
//    regex.findFirstMatchIn(url).map(_.subgroups).getOrElse(throw new LocationException(s"invalid URL for source: $url"))
//  }
//
//}
