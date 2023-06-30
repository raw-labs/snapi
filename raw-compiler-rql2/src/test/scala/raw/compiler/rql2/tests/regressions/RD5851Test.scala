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

import raw.compiler.{
  AutoCompleteLSPResponse,
  DotAutoCompleteLSPRequest,
  FieldLSPAutoCompleteResponse,
  FunParamLSPAutoCompleteResponse,
  LSPResponse,
  LetBindLSPAutoCompleteResponse,
  LetFunLSPAutoCompleteResponse,
  LetFunRecAutoCompleteResponse,
  PackageEntryLSPAutoCompleteResponse,
  PackageLSPAutoCompleteResponse,
  Pos,
  ProgramEnvironment,
  WordAutoCompleteLSPRequest
}
import raw.compiler.rql2.tests.CompilerTestContext

trait RD5851Test extends CompilerTestContext {

  val queryEnvironment: ProgramEnvironment = ProgramEnvironment(Some("snapi"), Set.empty, Map.empty)

  def autoCompleteNames(response: LSPResponse): Seq[String] = {
    response match {
      case AutoCompleteLSPResponse(entries, _) => entries.map {
          case FieldLSPAutoCompleteResponse(n, _) => n
          case LetBindLSPAutoCompleteResponse(n, _) => n
          case LetFunLSPAutoCompleteResponse(n, _) => n
          case LetFunRecAutoCompleteResponse(n, _) => n
          case FunParamLSPAutoCompleteResponse(n, _) => n
          case PackageLSPAutoCompleteResponse(n, _) => n
          case PackageEntryLSPAutoCompleteResponse(n, _) => n
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  private def dotAutoCompleteTest(code: String, line: Int, col: Int, expectedFields: Seq[String]): Unit = {
    val response = doLsp(DotAutoCompleteLSPRequest(code, queryEnvironment, Pos(line, col)))
    val actual = autoCompleteNames(response)
    assert(actual == expectedFields)
  }

  private def wordAutoCompleteTest(
      code: String,
      line: Int,
      col: Int,
      prefix: String,
      expected: Seq[String]
  ): Unit = {
    val response = doLsp(WordAutoCompleteLSPRequest(code, queryEnvironment, prefix, Pos(line, col)))
    val actual = autoCompleteNames(response)
    // Check that all expected are in actual.
    // actual can have more though - e.g. built-in packages.
    expected.foreach(e => assert(actual.contains(e)))
  }

  val simpleLib = lib("""
    |a(v: int) = v * 2
    |`function space`() = 1
    |c(v: string = "Hello") = v
    |""".stripMargin)

  test("escape field with spaces") { _ =>
    val code = """
      |let x = Record.Build(`field space` = 1, B = "2")
      |in
      |x
      |""".stripMargin
    dotAutoCompleteTest(code, 4, 1, Seq("`field space`", "B"))
  }

  test("escape field with reserved keyword") { _ =>
    val code = """
      |let x = Record.Build(`type` = 1, B = "2")
      |in
      |x
      |""".stripMargin
    dotAutoCompleteTest(code, 4, 1, Seq("`type`", "B"))
  }

  test("escape field name special char") { _ =>
    val specialChars = Seq(" ", "*", "+", "/", "-", ":")
    specialChars.foreach { c =>
      val idn = s"`$c`"
      val code = s"""
        |let x = Record.Build($idn = 1, B = "2")
        |in
        |x
        |""".stripMargin
      dotAutoCompleteTest(code, 4, 1, Seq(idn, "B"))
    }
  }

  test("escape field name in collection with  special char") { _ =>
    val specialChars = Seq(" ", "*", "+", "/", "-", ":")
    specialChars.foreach { c =>
      val idn = s"`$c`"
      val code = s"""
        |let x = Collection.Build({$idn: 1, B: "2"})
        |in
        |x
        |""".stripMargin
      dotAutoCompleteTest(code, 4, 1, Seq(idn, "B"))
    }
  }

  test("escape field name in list with  special char") { _ =>
    val specialChars = Seq(" ", "*", "+", "/", "-", ":")
    specialChars.foreach { c =>
      val idn = s"`$c`"
      val code = s"""
        |let x = List.Build({$idn: 1, B: "2"})
        |in
        |x
        |""".stripMargin
      dotAutoCompleteTest(code, 4, 1, Seq(idn, "B"))
    }
  }

  test("escaped function name with space") { _ =>
    val code = """
      |let f1(v: int) = 1,
      |    `f 2`(v: string) = v
      |in
      |f
      |""".stripMargin
    wordAutoCompleteTest(code, 5, 1, "f", Seq("f1", "`f 2`"))
  }

  test("escaped function name with reserved keyword") { _ =>
    val code = """
      |let test(v: int) = 1,
      |    `type`(v: string) = v
      |in
      |t
      |""".stripMargin
    wordAutoCompleteTest(code, 5, 1, "t", Seq("test", "`type`"))
  }

  test("escape function name special char") { _ =>
    val specialChars = Seq(" ", "*", "+", "/", "-", ":", "#")
    specialChars.foreach { c =>
      val idn = s"`f$c`"
      val code = s"""
        |let f1(v: int) = 1,
        |    $idn(v: string) = v
        |in
        |f
        |""".stripMargin
      wordAutoCompleteTest(code, 5, 1, "f", Seq("f1", idn))
    }
  }

//  test("escape library load name") { _ =>
//    val specialChars = Seq(" ", "*", "+", "/", "-", ":", "#", "^", "&", "(", ")", "[", "]")
//    specialChars.foreach { c =>
//      val idn = s"`my$c`"
//      val code = s"""let
//        |    myVar = 123,
//        |    $idn = Library.Load("$simpleLib")
//        |in
//        |m""".stripMargin
//      wordAutoCompleteTest(code, 5, 1, "m", Seq(idn, "myVar"))
//    }
//  }

}
