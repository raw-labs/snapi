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
  EntryDoc,
  ErrorLSPResponse,
  HoverLSPRequest,
  HoverLSPResponse,
  PackageDoc,
  PackageEntryLSPHoverResponse,
  PackageLSPHoverResponse,
  Pos,
  ProgramEnvironment,
  TypeHoverResponse
}
import raw.compiler.rql2.tests.CompilerTestContext

trait LspHoverTest extends CompilerTestContext {

  val queryEnvironment: ProgramEnvironment = ProgramEnvironment(Some("snapi"), Set.empty, Map.empty)

  private def getVarInfoAt(code: String, line: Int, col: Int): TypeHoverResponse =
    doLsp(HoverLSPRequest(code, queryEnvironment, Pos(line, col))) match {
      case HoverLSPResponse(response: TypeHoverResponse, _) => response
      case r => throw new java.lang.Exception(s"Unexpected response: $r")
    }

  test("hover identifier at definition test") { _ =>
    val code = """let
      |a = 5
      |in
      |a
      |""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(2, 1)))
    response match {
      case HoverLSPResponse(hoverResponse, _) => hoverResponse match {
          case TypeHoverResponse(name: String, tipe: String) =>
            assertResult("a")(name)
            assertResult("int")(tipe)
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("hover package doc type output test") { _ =>
    val code = """String""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(1, 2)))
    response match {
      case HoverLSPResponse(hoverResponse, _) => hoverResponse match {
          case PackageLSPHoverResponse(name: String, doc: PackageDoc) =>
            assertResult("String")(name)
            assertResult("Library of functions for the string type.")(doc.description)
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("hover entry doc type output test") { _ =>
    val code = """String.Lower("HI")""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(1, 9)))
    response match {
      case HoverLSPResponse(hoverResponse, _) => hoverResponse match {
          case PackageEntryLSPHoverResponse(name: String, doc: EntryDoc) =>
            assertResult("Lower")(name)
            assertResult("Convert a string to lowercase.")(doc.summary)
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("hover non existing entry test") { _ =>
    val code = """String.NonExistingFunction("HI")""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(1, 9)))
    response shouldBe a[ErrorLSPResponse]
  }

  test("hover correct type output test") { _ =>
    val code = """let
      |a: float = 5
      |in
      |a
      |""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(2, 1)))
    response match {
      case HoverLSPResponse(hoverResponse, _) => hoverResponse match {
          case TypeHoverResponse(name: String, tipe: String) =>
            assertResult("a")(name)
            assertResult("float")(tipe)
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("hover identifier at usage test") { _ =>
    val code = """let
      |aaaaaaa = Record.Build(a = 1, b = "c")
      |in
      |aaaaaaa
      |""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(4, 3)))
    response match {
      case HoverLSPResponse(hoverResponse, _) => hoverResponse match {
          case TypeHoverResponse(name: String, tipe: String) =>
            assertResult("aaaaaaa")(name)
            assertResult("record(a: int, b: string)")(tipe)
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("hover function identifier at definition test") { _ =>
    val code = """let
      |buildCollection = lastElement:int -> let
      |                                            a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(2, 3)))
    response match {
      case HoverLSPResponse(hoverResponse, _) => hoverResponse match {
          case TypeHoverResponse(name: String, tipe: String) =>
            assertResult("buildCollection")(name)
            assertResult("buildCollection(lastElement: int)")(tipe)
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("hover function identifier recursive function at usage test") { _ =>
    val code = """let
      |  a = 1,
      |  rec b(v: int): int = if (v >= 0) then 0 else v * b(v - 1)
      |in
      |b(2)""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(5, 1)))
    response match {
      case HoverLSPResponse(hoverResponse, _) => hoverResponse match {
          case TypeHoverResponse(name: String, tipe: String) =>
            assertResult("b")(name)
            assertResult("(int) -> int")(tipe)
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("hover let function identifier recursive function at usage test") { _ =>
    val code = """let
      |  rec b(v: int): int = if (v >= 0) then 0 else v * b(v - 1)
      |in
      |b(2)""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(4, 1)))
    response match {
      case HoverLSPResponse(hoverResponse, _) => hoverResponse match {
          case TypeHoverResponse(name: String, tipe: String) =>
            assertResult("b")(name)
            assertResult("(int) -> int")(tipe)
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("hover let function entity recursive function at definition test") { _ =>
    val code = """let
      |  rec b(v: int): int = if (v >= 0) then 0 else v * b(v - 1)
      |in
      |b(2)""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(2, 7)))
    response match {
      case HoverLSPResponse(hoverResponse, _) => hoverResponse match {
          case TypeHoverResponse(name: String, tipe: String) =>
            assertResult("b")(name)
            assertResult("recursive function: b(v: int) => int")(tipe)
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("hover let function entity function at definition test") { _ =>
    val code = """let
      |  b(v: int): int = v
      |in
      |b(2)""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(2, 3)))
    response match {
      case HoverLSPResponse(hoverResponse, _) => hoverResponse match {
          case TypeHoverResponse(name: String, tipe: String) =>
            assertResult("b")(name)
            assertResult("b(v: int) => int")(tipe)
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("hover function identifier at usage test") { _ =>
    val code = """let
      |buildCollection = lastElement:int -> let
      |                                            a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(8, 16)))
    response match {
      case HoverLSPResponse(hoverResponse, _) => hoverResponse match {
          case TypeHoverResponse(name: String, tipe: String) =>
            assertResult("buildCollection")(name)
            assertResult("(int) -> collection(collection(int))")(tipe)
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("hover function parameter test") { _ =>
    val code = """let
      |buildCollection = lastElement:int -> let
      |                                            a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(2, 19)))
    response match {
      case HoverLSPResponse(hoverResponse, _) => hoverResponse match {
          case TypeHoverResponse(name: String, tipe: String) =>
            assertResult("lastElement")(name)
            assertResult("int")(tipe)
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("hover function parameter usage test") { _ =>
    val code = """let
      |buildCollection = lastElement:int -> let
      |a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(3, 30)))
    response match {
      case HoverLSPResponse(hoverResponse, _) => hoverResponse match {
          case TypeHoverResponse(name: String, tipe: String) =>
            assertResult("lastElement")(name)
            assertResult("int")(tipe)
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("hover let should return empty response") { _ =>
    val code = """let
      |buildCollection = lastElement:int -> let
      |                                            a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(1, 1)))
    response shouldBe a[ErrorLSPResponse]
  }

  test("hover field of a collection") { _ =>
    val code = """let
      |    data = Collection.Build(Record.Build(aaaaaaaaaaaa = Record.Build(cccccccccccc = "takis", d = 6), b = 3))
      |in
      |Collection.Filter(data, d -> d.aaaaaaaaaaaa.cccccccccccc > 0)""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(4, 33)))
    response match {
      case HoverLSPResponse(hoverResponse, _) => hoverResponse match {
          case TypeHoverResponse(name: String, tipe: String) =>
            assertResult("aaaaaaaaaaaa")(name)
            assertResult("record(cccccccccccc: string, d: int)")(tipe)
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("hover field of a collection nested") { _ =>
    val code = """let
      |    data = Collection.Build(Record.Build(aaaaaaaaaaaa = Record.Build(cccccccccccc = "takis", d = 6), b = 3))
      |in
      |Collection.Filter(data, d -> d.aaaaaaaaaaaa.cccccccccccc > 0)""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(4, 48)))
    response match {
      case HoverLSPResponse(hoverResponse, _) => hoverResponse match {
          case TypeHoverResponse(name: String, tipe: String) =>
            assertResult("cccccccccccc")(name)
            assertResult("string")(tipe)
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  knownBug("RD-8323", "hover variable in the end of the code (RD-8323)") { _ =>
    val code = """let c = 2
      |in c""".stripMargin
    val typeInfo = getVarInfoAt(code, 2, 5)
    typeInfo.name shouldBe "c"
    typeInfo.tipe shouldBe "int"
  }
}
