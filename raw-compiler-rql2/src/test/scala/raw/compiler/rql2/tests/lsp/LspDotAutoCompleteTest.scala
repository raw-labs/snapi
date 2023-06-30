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

package raw.compiler.rql2.tests.lsp

import raw.compiler.{
  AutoCompleteLSPResponse,
  DotAutoCompleteLSPRequest,
  FieldLSPAutoCompleteResponse,
  PackageEntryLSPAutoCompleteResponse,
  Pos,
  ProgramEnvironment
}
import raw.compiler.rql2.tests.CompilerTestContext

trait LspDotAutoCompleteTest extends CompilerTestContext {

  val queryEnvironment: ProgramEnvironment = ProgramEnvironment(Some("snapi"), Set.empty, Map.empty)

  private def dotAutoCompleteTest(code: String, line: Int, col: Int, expectedFields: Seq[(String, String)]): Unit = {
    val response = doLsp(DotAutoCompleteLSPRequest(code, queryEnvironment, Pos(line, col)))
    response match {
      case AutoCompleteLSPResponse(entries, _) =>
        assert(entries.toSeq == expectedFields.map(ef => FieldLSPAutoCompleteResponse(ef._1, ef._2)))
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
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
    dotAutoCompleteTest(code, 4, 3, Seq(("a", "record(g: int, k: int)"), ("B", "string"), ("c", "double")))
  }

  test("package autocomplete for string package test") { _ =>
    val code = """String""".stripMargin
    val response = doLsp(DotAutoCompleteLSPRequest(code, queryEnvironment, Pos(1, 6)))
    response match {
      case AutoCompleteLSPResponse(entries, _) =>
        assert(
          entries.forall {
            case PackageEntryLSPAutoCompleteResponse(_, _) => true
            case _ => false
          }
        )
        assert(entries.length > 0)
        assert(
          entries.exists {
            case PackageEntryLSPAutoCompleteResponse(name, docs) if name == "Lower" =>
              assert(docs.summary.contains("Convert a string to lowercase."))
              true
            case _ => false
          }
        )
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("package autocomplete for non existing package test") { _ =>
    val code = """Stringz""".stripMargin
    val response = doLsp(DotAutoCompleteLSPRequest(code, queryEnvironment, Pos(1, 7)))
    response match {
      case AutoCompleteLSPResponse(entries, _) => assert(entries.length == 0)
      case r => throw new AssertionError(s"Should have returned empty response")
    }
  }

}
