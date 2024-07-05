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

package raw.compiler.rql2.tests.builtin

import raw.compiler.utils._
import raw.compiler.rql2.tests.Rql2CompilerTestContext
import raw.sources.filesystem.local.LocalLocationsTestContext

import java.nio.file.Path

trait LocationPackageTest extends Rql2CompilerTestContext with LocalLocationsTestContext {

  test(s"""
    |let
    |  desc = Location.Describe("$studentsCsvLocal")
    |in
    |   desc.format
    |""".stripMargin)(it => it should evaluateTo(""" "csv" """.stripMargin))

  test(s"""
    |let
    |  desc = Location.Describe("$studentsCsvLocal")
    |in
    |   desc.properties
    |""".stripMargin)(it => it should evaluateTo(""" [
    |  { name: "escape", value: "\\" },
    |  { name: "has_header", value: "true" },
    |  { name: "multiLine_fields", value: "false" },
    |  { name: "nans", value: "[]" },
    |  { name: "delimiter", value: "," },
    |  { name: "nulls", value: "[\"\"]" },
    |  { name: "quote", value: "\"" },
    |  { name: "skip", value: "1" }
    |] """.stripMargin))

  val csvWithReservedKeyword: Path = tempFile("""type, name, age, category
    |human, john, 29, mammal
    |tree, pine tree, 80, vegetable
    |car, ferrari, 5, machine """.stripMargin)

  // Checking that describe is not adding backticks everywhere
  // Only the 'type' field should have them
  test(snapi"""
    |let
    |  desc = Location.Describe("$csvWithReservedKeyword")
    |in
    |   desc.`type`
    |""".stripMargin)(it =>
    it should evaluateTo(
      s""" "collection(record(`type`: string, name: string, age: int, category: string))" """.stripMargin
    )
  )

  // Error handling
  test(s"""
    |let urls = List.Build("$studentsCsvLocal", "file:/not/found")
    |in List.Transform(urls, u -> (Location.Describe(u)).format)
    |""".stripMargin)(it =>
    it should evaluateTo(
      """List.Build("csv",
        |Error.Build("inference error: file system error: path not found: /not/found"))""".stripMargin
    )
  )

  test(s"""
    |let urls = List.Build("$authorsJsonLocalDirectory", "file:/not/found")
    |in List.Transform(urls, u -> List.Count(Location.Ls(u)))
    |""".stripMargin)(it =>
    it should evaluateTo(
      """List.Build(3L,
        |Error.Build("file system error: path not found: /not/found"))""".stripMargin
    )
  )

  test(s"""
    |let urls = List.Build("$authorsJsonLocalDirectory", "file:/not/found")
    |in List.Transform(urls, u -> List.Count(Location.Ll(u)))
    |""".stripMargin)(it =>
    it should evaluateTo(
      """List.Build(3L,
        |Error.Build("file system error: path not found: /not/found"))""".stripMargin
    )
  )
}
