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

import raw.compiler.rql2.tests.CompilerTestContext
import raw.compiler.api._

trait LspWordAutoCompleteTest extends CompilerTestContext {

  private def wordAutoCompleteTest(
      code: String,
      line: Int,
      col: Int,
      prefix: String,
      expected: Seq[(String, Option[String])]
  ): Unit = {
    val AutoCompleteResponse(entries, _) = wordAutoComplete(code, prefix, Pos(line, col))
    val actual = entries.map {
      case FieldCompletion(n, t) => (n, Some(t))
      case LetBindCompletion(n, t) => (n, Some(t))
      case LetFunCompletion(n, t) => (n, Some(t))
      case LetFunRecCompletion(n, t) => (n, Some(t))
      case FunParamCompletion(n, t) => (n, Some(t))
      case PackageCompletion(n, d) => (n, Some(d.description))
      case PackageEntryCompletion(n, d) => (n, Some(d.description))
    }
    // Check that all expected are in actual.
    // actual can have more though - e.g. built-in packages.
    expected.foreach(e => assert(actual.contains(e)))
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
