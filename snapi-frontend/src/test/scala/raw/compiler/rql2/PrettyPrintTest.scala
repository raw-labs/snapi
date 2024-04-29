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

package raw.compiler.rql2

import org.scalatest.Assertion
import raw.compiler.rql2.source.SourceCommentsPrettyPrinter
import raw.utils.RawTestSuite

class PrettyPrintTest extends RawTestSuite {

  def assertFormattedCode(
      code: String,
      expected: String,
      indentation: Option[Int] = None,
      width: Option[Int] = None
  ): Assertion = {
    val prettyPrinter = new SourceCommentsPrettyPrinter(indentation, width)
    prettyPrinter.prettyCode(code) match {
      case Left((error, _)) => fail(error)
      case Right(prettyCode) => assert(prettyCode.trim == expected.trim)
    }
  }

  test("comment after binary exp") { _ =>
    assertFormattedCode("""1 + 1 // a comment""", """1 + 1 // a comment""".stripMargin)
  }

  test("comment at line") { _ =>
    assertFormattedCode(
      """let x = 1 // comment 1
        |in x
        |""".stripMargin,
      """let
        |    x = 1 // comment 1
        |in
        |    x""".stripMargin
    )
  }

  test("comments before") { _ =>
    val code = """
      |let
      |// comment 1
      |
      |
      |// comment 2
      |x = 1 // a
      |in
      |x
      |""".stripMargin
    assertFormattedCode(
      code,
      """let
        |    // comment 1
        |    // comment 2
        |    x = 1 // a
        |in
        |    x""".stripMargin
    )
  }

  test("small comments before") { _ =>
    val code = """
      |let
      |//1
      |
      |x = 1 + 1 //2
      |in
      |x
      |""".stripMargin
    assertFormattedCode(
      code,
      """let
        |    //1
        |    x = 1 + 1 //2
        |in
        |    x""".stripMargin
    )
  }

  test("comments after") { _ =>
    val code = """
      |let
      |x = 1
      |in
      |x
      |
      |          // some comments at the end
      |
      |
      |          // one more
      |""".stripMargin
    assertFormattedCode(
      code,
      """let
        |    x = 1
        |in
        |    x
        |// some comments at the end
        |// one more""".stripMargin
    )
  }

  test("comments single line function") { _ =>
    val code = """// top comment
      |f(a)//single comment""".stripMargin
    assertFormattedCode(
      code,
      """// top comment
        |f(a) //single comment
        |""".stripMargin
    )
  }

  test("comments in binary exp") { _ =>
    val code = """
      |     1  // one
      |+ // plus
      | 1 // one
      |""".stripMargin
    assertFormattedCode(
      code,
      """1 // one
        | + // plus
        |    1 // one
        |""".stripMargin
    )
  }

  test("comments in unary exp") { _ =>
    val code = """       not // not
      |
      |
      |true // true""".stripMargin
    assertFormattedCode(
      code,
      """not // not
        |    true // true""".stripMargin
    )
  }

  test("big binary exp without comments") { _ =>
    val code = """f(aaaaaaaaaaaaaaaa, bbbbbbbbbbbbb, cccccccccccccc,
      |not (not (not (not (a == 1) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2)""".stripMargin
    assertFormattedCode(
      code,
      """f(
        |    aaaaaaaaaaaaaaaa,
        |    bbbbbbbbbbbbb,
        |    cccccccccccccc,
        |    not (not (not (not (a == 1) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2
        |)""".stripMargin
    )
  }

  test("comments in unary exp 2") { _ =>
    val code = """f(aaaaaaaaaaaaaaaa, bbbbbbbbbbbbb, cccccccccccccc, not // not
      |     (a == 1) // blah
      |     and b > 12 or c == 2)""".stripMargin
    assertFormattedCode(
      code,
      """f(
        |    aaaaaaaaaaaaaaaa,
        |    bbbbbbbbbbbbb,
        |    cccccccccccccc,
        |    not // not
        |        (a == 1) // blah
        |     and
        |        b > 12 or
        |        c == 2
        |)""".stripMargin
    )
  }

  test("comments one line function") { _ =>
    val code = """
      |// a simple function
      |main( a : int, b: int) =
      |
      |a + b // result
      |
      |// calling main
      |main(1, 2)""".stripMargin

    assertFormattedCode(
      code,
      """// a simple function
        |main(a: int, b: int) = a + b // result
        |
        |// calling main
        |main(1, 2)""".stripMargin
    )
  }

  test("comment function ") { _ =>
    val code = """
      |// a function
      |main(
      |   a : int, // arg a
      |   b: int = 1,   // arg b
      |   c: int
      |) = // body
      | a + b """.stripMargin

    assertFormattedCode(
      code,
      """// a function
        |main(
        |    a: int, // arg a
        |    b: int = 1, // arg b
        |    c: int
        |) =
        |    // body
        |    a + b""".stripMargin
    )
  }

  test("comment function 2") { _ =>
    // long comments will make it go to the other formatting
    val code = """main(
      |   a : int,      // arg a
      |   b: int // ########################## type of b #####################
      |   = 1,          // arg b
      |   c: int
      |) = // body
      | a + b
      |
      |
      | main(
      |   1, // arg 1
      |   2, // ######################### arg 2 #######################
      |   3 // arg 3
      | )""".stripMargin

    assertFormattedCode(
      code,
      """main(
        |    a: int, // arg a
        |    b: int // ########################## type of b #####################
        |     = 1, // arg b
        |    c: int
        |) =
        |    // body
        |    a + b
        |
        |main(
        |    1, // arg 1
        |    2, // ######################### arg 2 #######################
        |    3 // arg 3
        |)""".stripMargin
    )
  }

  test("comment record") { _ =>
    val code = """
      |// comment 1
      |{
      |a: 1, // a field
      |b: "Hello" // b field
      |} // comment 2
      |// comment 3""".stripMargin

    assertFormattedCode(
      code,
      """// comment 1
        |{
        |    a: 1, // a field
        |    b: "Hello" // b field
        |} // comment 2
        |// comment 3
        |""".stripMargin
    )
  }

  test("comment list") { _ =>
    val code = """// comment before
      |[ // comment before 1
      |1, // comment at 1
      | //comment before 2
      | 2, // comment at 2
      | 3 ]
      | // comment after""".stripMargin

    assertFormattedCode(
      code,
      """// comment before
        |[
        |    // comment before 1
        |    1, // comment at 1
        |    //comment before 2
        |    2, // comment at 2
        |    3
        |]
        |// comment after
        |""".stripMargin
    )
  }

  test("comments in let") { _ =>
    val code = """
      |// comment before
      |let
      |// comment 1
      |a = 1, // comment 2
      |// comment 3
      |b = a * 3, // comment 4
      |// comment 5
      |c = 5,
      |d = [a, b, c]
      |in d // comment 6
      |// comment after
      |""".stripMargin
    assertFormattedCode(
      code,
      """// comment before
        |let
        |    // comment 1
        |    a = 1, // comment 2
        |    // comment 3
        |    b = a * 3, // comment 4
        |    // comment 5
        |    c = 5,
        |    d = [a, b, c]
        |in
        |    d // comment 6
        |// comment after""".stripMargin
    )
  }

  test("nested lets, functions") { _ =>
    val code = """let
      |    // assignment 1
      |    a = f1(
      |         a1, // arg11
      |          a2, // arg12
      |          a3, // arg13
      |          a4, // arg14
      |          // top arg15
      |          a5, // arg15
      |          // inner function
      |          f12(
      |             a1, // arg121
      |              a2, // arg122
      |              a3, // arg123
      |              a4, // arg124
      |              a5, // arg125
      |             // inner let
      |             let
      |                 a = 1
      |                 in a // at line let arg
      |          )
      |    ),
      |    // assignment 2
      |    b = let
      |          c =  f22(
      |             a1, // arg21
      |             a2, // arg22
      |             a3, // arg23
      |             a4, // arg24
      |             // top arg25
      |             a5, // arg25
      |             // inner function
      |             f22(
      |                 a1, // arg221
      |                 a2, // arg222
      |                 a3, // arg223
      |                 a4, // arg224
      |                 a5, // arg225
      |                 // inner let 2
      |                 let
      |                     a = 1
      |                     in a // at line let arg
      |             )
      |         )
      |    in c
      |in
      |   a + b
      |
      |
      |""".stripMargin
    assertFormattedCode(
      code,
      """let
        |    // assignment 1
        |    a = f1(
        |        a1, // arg11
        |        a2, // arg12
        |        a3, // arg13
        |        a4, // arg14
        |        // top arg15
        |        a5, // arg15
        |        // inner function
        |        f12(
        |            a1, // arg121
        |            a2, // arg122
        |            a3, // arg123
        |            a4, // arg124
        |            a5, // arg125
        |            // inner let
        |            let
        |                a = 1
        |            in
        |                a // at line let arg
        |        )
        |    ),
        |    // assignment 2
        |    b = let
        |        c = f22(
        |            a1, // arg21
        |            a2, // arg22
        |            a3, // arg23
        |            a4, // arg24
        |            // top arg25
        |            a5, // arg25
        |            // inner function
        |            f22(
        |                a1, // arg221
        |                a2, // arg222
        |                a3, // arg223
        |                a4, // arg224
        |                a5, // arg225
        |                // inner let 2
        |                let
        |                    a = 1
        |                in
        |                    a // at line let arg
        |            )
        |        )
        |    in
        |        c
        |in
        |    a + b""".stripMargin
    )
  }

  test("comments in function abstraction") { _ =>
    val code = """ Collection.Transform( dockerInspect,
      |              // function to apply
      |              (x) ->
      |               // getting only a few fields
      |                  {
      |                      MachineId: Int.From(String.SubString(x.Name, 9, 1)), // extracting "machine" from the name
      |                      ExitCode: x.State.ExitCode,   // the exit code is the most important
      |                      Error: x.State.Error, FinishedAt: x.State.FinishedAt
      |                  }
      |          )""".stripMargin
    assertFormattedCode(
      code,
      """Collection.Transform(
        |    dockerInspect,
        |    // function to apply
        |    (x) ->
        |        // getting only a few fields
        |        {
        |            MachineId: Int.From(String.SubString(x.Name, 9, 1)), // extracting "machine" from the name
        |            ExitCode: x.State.ExitCode, // the exit code is the most important
        |            Error: x.State.Error,
        |            FinishedAt: x.State.FinishedAt
        |        }
        |)""".stripMargin
    )
  }

  test("change width and indentation") { _ =>
    val code = """let
      |  a = 1,
      |  b = 2, // a short line
      |  c = 3
      |in
      |  not (not (not (not (a == 1) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2
      | """.stripMargin

    assertFormattedCode(
      code,
      """let
        |  a = 1,
        |  b = 2, // a short line
        |  c = 3
        |in
        |  not (not (not (not (a == 1) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2
        |""".stripMargin,
      Some(2)
    )

    assertFormattedCode(
      code,
      """let
        |    a = 1,
        |    b = 2, // a short line
        |    c = 3
        |in
        |    not (not (not (not (a == 1) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2""".stripMargin,
      Some(4)
    )

    assertFormattedCode(
      code,
      """let
        |    a = 1,
        |    b = 2, // a short line
        |    c = 3
        |in
        |    not
        |        (not (not (not (a == 1) and b > 12 or c == 2) and b > 12 or c == 2) and
        |            b > 12 or
        |            c == 2) and
        |        b > 12 or
        |        c == 2""".stripMargin,
      Some(4),
      Some(80)
    )
  }
}
