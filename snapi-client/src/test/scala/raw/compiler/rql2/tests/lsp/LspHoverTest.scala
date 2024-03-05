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

import raw.client.api._
import raw.compiler.rql2.tests.CompilerTestContext

trait LspHoverTest extends CompilerTestContext {

  test("hover identifier at definition test") { _ =>
    val code = """let
      |a = 5
      |in
      |a
      |""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe))) = hover(code, Pos(2, 1))
    assertResult("a")(name)
    assertResult("int")(tipe)
  }

  test("hover package doc type output test") { _ =>
    val code = """String""".stripMargin
    val HoverResponse(Some(PackageCompletion(name, doc))) = hover(code, Pos(1, 2))
    assertResult("String")(name)
    assertResult("Library of functions for the string type.")(doc.description)
  }

  test("hover entry doc type output test") { _ =>
    val code = """String.Lower("HI")""".stripMargin
    val HoverResponse(Some(PackageEntryCompletion(name, doc))) = hover(code, Pos(1, 9))
    assertResult("String.Lower")(name)
    assertResult("Convert a string to lowercase.")(doc.summary)
  }

  test("hover non existing entry test") { _ =>
    val code = """String.NonExistingFunction("HI")""".stripMargin
    val hoverResponse = hover(code, Pos(1, 9))
    assert(hoverResponse.completion.isEmpty)
  }

  test("hover correct type output test") { _ =>
    val code = """let
      |a: float = 5
      |in
      |a
      |""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe))) = hover(code, Pos(2, 1))
    assertResult("a")(name)
    assertResult("float")(tipe)
  }

  test("hover identifier at usage test") { _ =>
    val code = """let
      |aaaaaaa = Record.Build(a = 1, b = "c")
      |in
      |aaaaaaa
      |""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe))) = hover(code, Pos(4, 3))
    assertResult("aaaaaaa")(name)
    assertResult("record(a: int, b: string)")(tipe)
  }

  test("hover function identifier at definition test") { _ =>
    val code = """let
      |buildCollection = (lastElement:int) -> let
      |                                            a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe))) = hover(code, Pos(2, 3))
    assertResult("buildCollection")(name)
    assertResult("buildCollection(lastElement: int)")(tipe)
  }

  test("hover function identifier recursive function at usage test") { _ =>
    val code = """let
      |  a = 1,
      |  rec b(v: int): int = if (v >= 0) then 0 else v * b(v - 1)
      |in
      |b(2)""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe))) = hover(code, Pos(5, 1))
    assertResult("b")(name)
    assertResult("(int) -> int")(tipe)
  }

  test("hover let function identifier recursive function at usage test") { _ =>
    val code = """let
      |  rec b(v: int): int = if (v >= 0) then 0 else v * b(v - 1)
      |in
      |b(2)""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe))) = hover(code, Pos(4, 1))
    assertResult("b")(name)
    assertResult("(int) -> int")(tipe)
  }

  test("hover let function entity recursive function at definition test") { _ =>
    val code = """let
      |  rec b(v: int): int = if (v >= 0) then 0 else v * b(v - 1)
      |in
      |b(2)""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe))) = hover(code, Pos(2, 7))
    assertResult("b")(name)
    assertResult("recursive function: b(v: int) -> int")(tipe)
  }

  test("hover let function entity function at definition test") { _ =>
    val code = """let
      |  b(v: int): int = v
      |in
      |b(2)""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe))) = hover(code, Pos(2, 3))
    assertResult("b")(name)
    assertResult("b(v: int) -> int")(tipe)
  }

  test("hover function identifier at usage test") { _ =>
    val code = """let
      |buildCollection = (lastElement:int) -> let
      |                                            a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe))) = hover(code, Pos(8, 16))
    assertResult("buildCollection")(name)
    assertResult("(int) -> collection(collection(int))")(tipe)
  }

  test("hover function parameter test") { _ =>
    val code = """let
      |buildCollection = (lastElement:int) -> let
      |                                            a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe))) = hover(code, Pos(2, 20))
    assertResult("lastElement")(name)
    assertResult("int")(tipe)
  }

  test("hover function parameter usage test") { _ =>
    val code = """let
      |buildCollection = (lastElement:int) -> let
      |a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe))) = hover(code, Pos(3, 30))
    assertResult("lastElement")(name)
    assertResult("int")(tipe)
  }

  test("hover let should return empty response") { _ =>
    val code = """let
      |buildCollection = (lastElement:int) -> let
      |                                            a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val hoverResponse = hover(code, Pos(1, 1))
    assert(hoverResponse.completion.isEmpty)
  }

  test("hover field of a collection") { _ =>
    val code = """let
      |    data = Collection.Build(Record.Build(aaaaaaaaaaaa = Record.Build(cccccccccccc = "takis", d = 6), b = 3))
      |in
      |Collection.Filter(data, d -> d.aaaaaaaaaaaa.cccccccccccc > 0)""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe))) = hover(code, Pos(4, 33))
    assertResult("aaaaaaaaaaaa")(name)
    assertResult("record(cccccccccccc: string, d: int)")(tipe)
  }

  test("hover field of a collection nested") { _ =>
    val code = """let
      |    data = Collection.Build(Record.Build(aaaaaaaaaaaa = Record.Build(cccccccccccc = "takis", d = 6), b = 3))
      |in
      |Collection.Filter(data, d -> d.aaaaaaaaaaaa.cccccccccccc > 0)""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe))) = hover(code, Pos(4, 48))
    assertResult("cccccccccccc")(name)
    assertResult("string")(tipe)
  }

  test("RD-8323 hover variable in the end of the code (RD-8323)") { _ =>
    val code = """let c = 2
      |in c""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe))) = hover(code, Pos(2, 4))
    name shouldBe "c"
    tipe shouldBe "int"
  }

  test("RD-10698 crush fix") { _ =>
    val code = """main(`1 + 2`: int) = let
      |    a = 1,
      |    a = 2
      |in a""".stripMargin

    val HoverResponse(Some(TypeCompletion(name, tipe))) = hover(code, Pos(4, 4))
    assert(tipe == "error")
  }
}
