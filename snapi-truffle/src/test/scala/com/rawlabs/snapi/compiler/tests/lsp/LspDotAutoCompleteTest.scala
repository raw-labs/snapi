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

package com.rawlabs.snapi.compiler.tests.lsp

import com.rawlabs.snapi.frontend.api.{AutoCompleteResponse, FieldCompletion, PackageEntryCompletion, Pos}
import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class LspDotAutoCompleteTest extends SnapiTestContext {

  private def dotAutoCompleteTest(code: String, line: Int, col: Int, expectedFields: Seq[(String, String)]): Unit = {
    val AutoCompleteResponse(entries) = dotAutoComplete(code, Pos(line, col))
    assert(entries.toSeq == expectedFields.map(ef => FieldCompletion(ef._1, ef._2)))
  }

  test("simple auto-complete record test") { _ =>
    val code = """
      |let x = Record.Build(a = 1, B = "2")
      |in
      |x
      |""".stripMargin
    dotAutoCompleteTest(code, 4, 1, Seq(("a", "int"), ("B", "string")))
  }

  test("harder auto-complete record test") { _ =>
    val code = """
      |let f() = Record.Build(a = Record.Build(g = 5, k = 11), B = "2", c = 3.1)
      |in
      |f()
      |""".stripMargin
    dotAutoCompleteTest(
      code,
      4,
      3,
      Seq(("a", "record(g: int, k: int)"), ("B", "string"), ("c", "double"))
    )
  }

  test("package autocomplete for string package test") { _ =>
    val code = """String""".stripMargin
    val AutoCompleteResponse(entries) = dotAutoComplete(code, Pos(1, 6))
    assert(
      entries.forall {
        case PackageEntryCompletion(_, _) => true
        case _ => false
      }
    )
    assert(entries.length > 0)
    assert(
      entries.exists {
        case PackageEntryCompletion(name, docs) if name == "Lower" =>
          assert(docs.summary.contains("Convert a string to lowercase."))
          true
        case _ => false
      }
    )
  }

  test("package autocomplete for non existing package test") { _ =>
    val code = """Stringz""".stripMargin
    val AutoCompleteResponse(entries) = dotAutoComplete(code, Pos(1, 7))
    assert(entries.length == 0)
  }

  test("RD-10698 crash fix") { _ =>
    val code = """main(`1 + 2`: int) = let
      |    a = 1,
      |    a = 2
      |in a""".stripMargin
    val AutoCompleteResponse(entries) = dotAutoComplete(code, Pos(4, 5))
    assert(entries.length == 0)
  }

}
