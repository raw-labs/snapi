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
  FieldLSPAutoCompleteResponse,
  FunParamLSPAutoCompleteResponse,
  LetBindLSPAutoCompleteResponse,
  LetFunLSPAutoCompleteResponse,
  LetFunRecAutoCompleteResponse,
  PackageEntryLSPAutoCompleteResponse,
  PackageLSPAutoCompleteResponse,
  Pos,
  WordAutoCompleteLSPRequest
}
import raw.compiler.rql2.tests.CompilerTestContext
import raw.runtime.ProgramEnvironment

trait LspWordAutoCompleteTest extends CompilerTestContext {

  val programEnvironment: ProgramEnvironment = ProgramEnvironment(Some("snapi"), Set.empty, Map.empty)

  private def wordAutoCompleteTest(
      code: String,
      line: Int,
      col: Int,
      prefix: String,
      expected: Seq[(String, Option[String])]
  ): Unit = {
    val response = doLsp(WordAutoCompleteLSPRequest(code, programEnvironment, prefix, Pos(line, col)))
    response match {
      case AutoCompleteLSPResponse(entries, _) =>
        val actual = entries.map {
          case FieldLSPAutoCompleteResponse(n, t) => (n, Some(t))
          case LetBindLSPAutoCompleteResponse(n, t) => (n, Some(t))
          case LetFunLSPAutoCompleteResponse(n, t) => (n, Some(t))
          case LetFunRecAutoCompleteResponse(n, t) => (n, Some(t))
          case FunParamLSPAutoCompleteResponse(n, t) => (n, Some(t))
          case PackageLSPAutoCompleteResponse(n, d) => (n, Some(d.description))
          case PackageEntryLSPAutoCompleteResponse(n, d) => (n, Some(d.description))
        }
        // Check that all expected are in actual.
        // actual can have more though - e.g. built-in packages.
        expected.foreach(e => assert(actual.contains(e)))
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("simple word auto-complete test") { _ =>
    val code = """
      |let x = Record.Build(a = 1, B = "2"),
      |    f1(v: int) = 1,
      |    f2(v: string) = v
      |in
      |f
      |""".stripMargin
    wordAutoCompleteTest(code, 6, 2, "f", Seq(("f1", Some("(int) -> int")), ("f2", Some("(string) -> string"))))
  }

  test("simple word auto-complete test 2") { _ =>
    val code = """
      |let x = Record.Build(a = 1, B = "2"),
      |    f1(v: int) = 1,
      |    f2(v: string) = v,
      |    f3 = f
      |""".stripMargin
    wordAutoCompleteTest(code, 5, 10, "f", Seq(("f1", Some("(int) -> int")), ("f2", Some("(string) -> string"))))
  }

  test("simple word auto-complete test same line") { _ =>
    val code = """
      |let x = Record.Build(a = 1, B = "2"),
      |    f1(v: int) = 1,
      |    f2(v: string) = v,
      |    f3 =
      |""".stripMargin
    wordAutoCompleteTest(
      code,
      5,
      9,
      "",
      Seq(("f1", Some("(int) -> int")), ("f2", Some("(string) -> string")), ("x", Some("record(a: int, B: string)")))
    )
  }

  test("simple word auto-complete next line 3") { _ =>
    val code = """
      |let x = Record.Build(a = 1, B = "2"),
      |    f1(v: int) = 1,
      |    f2(v: string) = v,
      |    f3 =
      |""".stripMargin
    wordAutoCompleteTest(code, 6, 1, "", Seq(("f1", Some("(int) -> int")), ("f2", Some("(string) -> string"))))
  }

  test("built-in package autocomplete") { _ =>
    val code = """Env"""
    wordAutoCompleteTest(
      code,
      1,
      3,
      prefix = "Env",
      Seq(("Environment", Some("Library of functions to access environment properties.")))
    )
  }

  test("non existing package autocomplete") { _ =>
    val code = """Envi"""
    wordAutoCompleteTest(
      code,
      1,
      4,
      prefix = "Envi",
      Seq.empty
    )
  }

  test("RD-5989") { _ =>
    val specialChars = Seq(" ", "*", "+", "/", "-", ":", "#", "^", "&", "(", ")", "[", "]")
    specialChars.foreach { c =>
      val idn = s"`my$c`"
      val code = s"""let
        |    myVar = 123,
        |    $idn = 3
        |in
        |m""".stripMargin
      wordAutoCompleteTest(code, 5, 2, "m", Seq((idn, Some("int")), ("myVar", Some("int"))))
    }
  }

}
