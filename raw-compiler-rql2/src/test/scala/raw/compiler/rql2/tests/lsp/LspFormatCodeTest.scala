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

import raw.compiler.{FormatCodeLSPRequest, FormatCodeLSPResponse, ProgramEnvironment}
import raw.compiler.rql2.tests.CompilerTestContext

trait LspFormatCodeTest extends CompilerTestContext {

  val queryEnvironment: ProgramEnvironment = ProgramEnvironment(Some("snapi"), Set.empty, Map.empty)

  def assertFormattedCode(code: String, expected: String) = {
    val response = doLsp(FormatCodeLSPRequest(code, None, None, queryEnvironment))
    response match {
      case FormatCodeLSPResponse(formattedCode, errors) =>
        logger.info(s"formattedCode:\n$formattedCode")
        // this is like a rtrim, kiama is putting extra spaces in the end
        assert(formattedCode.replaceAll("\\s+$", "") == expected)
        assert(errors.isEmpty)
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("simple code format test") { _ =>
    val code = """
      |   let x = 1
      |in
      |x
      |""".stripMargin
    assertFormattedCode(
      code,
      """let
        |  x = 1
        |in
        |  x""".stripMargin
    )

  }

  test("multiline let format test") { _ =>
    val code = """
      |   let x = 1, y=2, z = 8
      |in
      |x + y
      |""".stripMargin
    assertFormattedCode(
      code,
      """let
        |  x = 1,
        |  y = 2,
        |  z = 8
        |in
        |  x + y""".stripMargin
    )
  }

  test("if then else test") { _ =>
    val code = """if 5 == 5 then 3 else 8""".stripMargin
    assertFormattedCode(
      code,
      """if 5 == 5 then
        |  3
        |else
        |  8""".stripMargin
    )
  }

  test("binary expressions with unary - parenthesis test") { _ =>
    val code = """8 + -5 * (8 + 10) * 3 + 2""".stripMargin
    assertFormattedCode(
      code,
      """8 + -5 * (8 + 10) * 3 + 2""".stripMargin
    )
  }

  test("binary expressions with parenthesis test") { _ =>
    val code = """8 * 8 * (10 + 10) * 8""".stripMargin
    assertFormattedCode(
      code,
      """8 * 8 * (10 + 10) * 8""".stripMargin
    )
  }

  test("binary expressions with non needed parenthesis test") { _ =>
    val code = """(8 * 8) * 10 + 10""".stripMargin
    assertFormattedCode(
      code,
      """8 * 8 * 10 + 10""".stripMargin
    )
  }

  test("unary expressions with parenthesis test") { _ =>
    val code = """-(-(-1))""".stripMargin
    assertFormattedCode(
      code,
      """---1""".stripMargin
    )
  }

  test("unary expression not test") { _ =>
    val code = """not true""".stripMargin
    assertFormattedCode(
      code,
      """not true""".stripMargin
    )
  }

  test("nested let test") { _ =>
    val code = """
      |   let x = 1, y=2, z = 8
      |in
      |let k=5
      |in k
      |""".stripMargin
    assertFormattedCode(
      code,
      """let
        |  x = 1,
        |  y = 2,
        |  z = 8
        |in
        |  let
        |    k = 5
        |  in
        |    k""".stripMargin
    )
  }

  test("tuples") { _ =>
    val code = """{"Hello",
      |"World"}""".stripMargin
    assertFormattedCode(code, """{"Hello", "World"}""")
  }

  // Record building with proj will use the identifier as the field name
  test("tuple with an record proj") { _ =>
    val code = """let
      |  r = {a: 1, b: "hello"}
      |in
      |  {r.a, r.b}""".stripMargin
    assertFormattedCode(
      code,
      """let
        |  r = {a: 1, b: "hello"}
        |in
        |  {a: r.a, b: r.b}""".stripMargin
    )
  }

  test("larger program test") { _ =>
    val code = """let
      |    buildCollection = (lastElement:int) -> let
      |                                            a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c),
      |    buildCollection2 = (lastElement:int) -> let
      |        a = Collection.Build(1,2,3,lastElement),
      |        b = Collection.Build(1,2,3,lastElement),
      |        c = Collection.Build(1,2,3,lastElement)
      |    in Collection.Build(a,b,c),
      |    z = 5,
      |    k = Collection.Filter(Collection.Build(3,3,3), f -> f > 5)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 and t > 1 and t > 1 and t > 1 and t > 1 and t > 1 and t > 1 and t > 1 and t > 1 and t > 1 )""".stripMargin
    assertFormattedCode(
      code,
      """let
        |  buildCollection = (lastElement: int) ->
        |    let
        |      a = Collection.Build(1, 2, 3, lastElement),
        |      b = Collection.Build(1, 2, 3, lastElement),
        |      c = Collection.Build(1, 2, 3, lastElement)
        |    in
        |      Collection.Build(a, b, c),
        |  buildCollection2 = (lastElement: int) ->
        |    let
        |      a = Collection.Build(1, 2, 3, lastElement),
        |      b = Collection.Build(1, 2, 3, lastElement),
        |      c = Collection.Build(1, 2, 3, lastElement)
        |    in
        |      Collection.Build(a, b, c),
        |  z = 5,
        |  k = Collection.Filter(Collection.Build(3, 3, 3), (f) -> f > 5)
        |in
        |  let
        |    bbb = buildCollection(5),
        |    ttt = Collection.Build(1, 2, 3)
        |  in
        |    Collection.Filter(
        |      ttt,
        |      (t) -> t > 1 and t > 1 and t > 1 and t > 1 and t > 1 and t > 1 and t > 1 and t > 1 and t > 1 and t > 1
        |    )""".stripMargin
    )
  }

  test("sugar test") { _ =>
    val code = """
      |   let x = [1, 2, 3],
      |       y = {a: x, b: "Hello"}
      |   in
      |       {_1:x, _2: y}
      |""".stripMargin
    assertFormattedCode(
      code,
      """let
        |  x = [1, 2, 3],
        |  y = {a: x, b: "Hello"}
        |in
        |  {x, y}""".stripMargin
    )
  }

  test("sugar test 2") { _ =>
    val code = """
      |   let x = [1, 2, 3],
      |       y = {a: x, b: "Hello"}
      |   in
      |       {_1:x, _2: y}
      |""".stripMargin
    assertFormattedCode(
      code,
      """let
        |  x = [1, 2, 3],
        |  y = {a: x, b: "Hello"}
        |in
        |  {x, y}""".stripMargin
    )
  }

}
