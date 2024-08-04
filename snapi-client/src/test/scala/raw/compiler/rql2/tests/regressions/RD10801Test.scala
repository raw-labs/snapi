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

package raw.compiler.rql2.tests.regressions

import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import raw.compiler.utils.SnapiInterpolator
import raw.testing.tags.TruffleTests

@TruffleTests class RD10801Test extends Rql2TruffleCompilerTestContext {

  private val qqq = "\"\"\""

  // some types are already checked, like 'function':
  test(s"""Collection.Count(Json.Parse($qqq[]$qqq, type collection(record(a: (int) -> float, b: int, c: int))))""")(
    _ should runErrorAs("unsupported type")
  )

  private val jsonContent = """[
    |  { "f1": "", "f2": "", "f3": "", "f4": "", "f5": "", "f6": "", "f7": "" },
    |  { "f8": "", "f9": "", "dup": "", "f10": "", "f11": "", "f12": "", "f13": "", "f14": "", "f15": "", "dup": "" }
    |] """.stripMargin
  private val rd10801Json = tempFile(jsonContent, "json")

  test(s"""Json.InferAndParse($qqq$jsonContent$qqq)""".stripMargin)(_ should runErrorAs("unsupported type"))
  test(
    s"""Json.Parse($qqq$jsonContent$qqq, type record(f1: string, f2: string, dup: string, f4: string, dup: string)"""
  )(_ should runErrorAs("unsupported type"))

  test(snapi"""Json.InferAndRead("$rd10801Json")""")(_ should runErrorAs("unsupported type"))
  test(snapi"""Json.Read("$rd10801Json", type record(f1: string, f2: string, dup: string, f4: string, dup: string)""")(
    _ should runErrorAs("unsupported type")
  )

  private val csvContent = """f1,f2,f3,dup,f5,dup
    |v1,v2,v3,v4,v5,v6
    |v1,v2,v3,v4,v5,v6""".stripMargin
  private val rd10801Csv = tempFile(csvContent, "csv")
  test(s"""Csv.InferAndParse($qqq$csvContent$qqq)""".stripMargin)(_ should evaluateTo("""[
    |  {f1: "v1", f2: "v2", f3: "v3", dup: "v4", f5: "v5", dup: "v6"},
    |  {f1: "v1", f2: "v2", f3: "v3", dup: "v4", f5: "v5", dup: "v6"}
    |]""".stripMargin))
  test(
    s"""Csv.Parse($qqq$csvContent$qqq, type collection(record(f1: string, f2: string, f3: string, dup: string, f5: string, dup: string)),
      |     delimiter = ",", skip = 1)""".stripMargin
  )(_ should evaluateTo("""[
    |  {f1: "v1", f2: "v2", f3: "v3", dup: "v4", f5: "v5", dup: "v6"},
    |  {f1: "v1", f2: "v2", f3: "v3", dup: "v4", f5: "v5", dup: "v6"}
    |]""".stripMargin))

  test(snapi"""Csv.InferAndRead($qqq$rd10801Csv$qqq)""".stripMargin)(_ should evaluateTo("""[
    |  {f1: "v1", f2: "v2", f3: "v3", dup: "v4", f5: "v5", dup: "v6"},
    |  {f1: "v1", f2: "v2", f3: "v3", dup: "v4", f5: "v5", dup: "v6"}
    |]""".stripMargin))
  test(
    snapi"""Csv.Read($qqq$rd10801Csv$qqq,
      |type collection(record(f1: string, f2: string, f3: string, dup: string, f5: string, dup: string)),
      |delimiter = ",", skip = 1)""".stripMargin
  )(_ should evaluateTo("""[
    |  {f1: "v1", f2: "v2", f3: "v3", dup: "v4", f5: "v5", dup: "v6"},
    |  {f1: "v1", f2: "v2", f3: "v3", dup: "v4", f5: "v5", dup: "v6"}
    |]""".stripMargin))

  test(
    s"""Xml.Parse($qqq<?xml version="1.0"?><nothing/>$qqq, type record(nothing: collection(record(a: (int) -> float, b: int, c: int))))"""
  )(
    _ should runErrorAs("unsupported type")
  )

  // duplicate args is a mistake in XML, it's not valid XML.
  private val xmlWithDuplicateAttributes = tempFile("""<?xml version="1.0"?><nothing a="12" b="13" a="14"/>""", "xml")
  private val xmlWithDuplicateTags = tempFile("""<?xml version="1.0"?><nothing><a>12</a><b>13</b><a>14</a></nothing>""")

  test(
    snapi"""Xml.InferAndRead("$xmlWithDuplicateAttributes")"""
  )(
    _ should runErrorAs("inference error") // duplicate field 'a' leads to an inferrer failure
  )

  test(
    snapi"""Xml.InferAndRead("$xmlWithDuplicateTags")"""
  ) { it =>
    it should typeAs("record(b: int, a: collection(int))") // duplicate tags are inferred as a collection
    it should run
  }

  test(
    snapi"""Xml.Read("$xmlWithDuplicateAttributes", type record(`@a`: int, `@b`: int, `@a`: int))"""
  )(
    _ should runErrorAs("unsupported type; duplicate field: @a") // duplicate attribute is forbidden in XML
  )

  test(
    snapi"""Xml.Read("$xmlWithDuplicateAttributes", type record(`@a`: int, `@b`: int))"""
  )(
    _ should runErrorAs("Duplicate attribute") // Jackson error at runtime
  )

  test(
    snapi"""Xml.Read("$xmlWithDuplicateTags", type record(a: int, b: int, a: int))"""
  )(
    _ should runErrorAs("unsupported type; duplicate field: a") // duplicate fields are forbidden in snapi XML reading
  )

  test(
    s"""Xml.Parse($qqq<?xml version="1.0"?><nothing a="12" b="13" a="14"></nothing>$qqq, type record(nothing: collection(record(`@a`: int, `@b`: int, `@a`: string, `#text`: string))))"""
  )(
    _ should runErrorAs("unsupported type; duplicate field: @a")
  )

  test(
    s"""Xml.Parse($qqq<?xml version="1.0"?><nothing><a>12</a><b>13</b><a>14</a></nothing>$qqq, type record(nothing: collection(record(a: int, b: int, a: string))))"""
  )(
    _ should runErrorAs("unsupported type; duplicate field: a")
  )

}
