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

package raw.compiler.rql2.tests.regressions

import raw.compiler.rql2.tests.CompilerTestContext
import raw.compiler.utils.SnapiInterpolator

trait RD10801Test extends CompilerTestContext {

  private val qqq = "\"\"\""

  // some types are already checked, like 'function':
  test(s"""Collection.Count(Json.Parse($qqq[]$qqq, type collection(record(a: (int) -> float, b: int, c: int))))""")(
    _ should runErrorAs("unsupported type")
  )

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

  private val rd10801Json = """[
    |  { "f1": "", "f2": "", "f3": "", "f4": "", "f5": "", "f6": "", "f7": "" },
    |  { "f8": "", "f9": "", "dup": "", "f10": "", "f11": "", "f12": "", "f13": "", "f14": "", "f15": "", "dup": "" }
    |] """.stripMargin

  test(s"""Json.InferAndParse($qqq$rd10801Json$qqq)""".stripMargin)(_ should runErrorAs("unsupported type"))

  private val dataset = tempFile(rd10801Json, "json")

  test(snapi"""Json.InferAndRead("$dataset")""")(_ should runErrorAs("unsupported type"))

}
