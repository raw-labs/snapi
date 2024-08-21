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

import raw.client.api._
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext

class RD5851Test extends Rql2TruffleCompilerTestContext {

  def autoCompleteNames(entries: Array[Completion]): Seq[String] = {
    entries.map {
      case FieldCompletion(n, _) => n
      case LetBindCompletion(n, _) => n
      case LetFunCompletion(n, _) => n
      case LetFunRecCompletion(n, _) => n
      case FunParamCompletion(n, _) => n
      case PackageCompletion(n, _) => n
      case PackageEntryCompletion(n, _) => n
      case TypeCompletion(n, _) => n
    }
  }

  private def dotAutoCompleteTest(code: String, line: Int, col: Int, expectedFields: Seq[String]): Unit = {
    val AutoCompleteResponse(entries) = dotAutoComplete(code, Pos(line, col))
    val actual = autoCompleteNames(entries)
    assert(actual == expectedFields)
  }

  private def wordAutoCompleteTest(
      code: String,
      line: Int,
      col: Int,
      prefix: String,
      expected: Seq[String]
  ): Unit = {
    val AutoCompleteResponse(entries) = wordAutoComplete(code, prefix, Pos(line, col))
    val actual = autoCompleteNames(entries)
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
