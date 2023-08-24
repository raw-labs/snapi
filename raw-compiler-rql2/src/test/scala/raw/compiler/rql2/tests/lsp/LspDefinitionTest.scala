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

import raw.compiler.{DefinitionLSPRequest, DefinitionLSPResponse, ErrorLSPResponse, Pos}
import raw.compiler.rql2.tests.CompilerTestContext
import raw.runtime.ProgramEnvironment

trait LspDefinitionTest extends CompilerTestContext {

  val programEnvironment: ProgramEnvironment = ProgramEnvironment(Some("snapi"), Set.empty, Map.empty)

  test("go to definition identifier at usage test") { _ =>
    val code = """let
      |a = 5
      |in
      |a
      |""".stripMargin
    val response = doLsp(DefinitionLSPRequest(code, programEnvironment, Pos(4, 1)))
    response match {
      case DefinitionLSPResponse(Pos(line, column), _) =>
        assertResult(2)(line)
        assertResult(1)(column)
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("got to definition identifier at definition test") { _ =>
    val code = """let
      |a = 5
      |in
      |a
      |""".stripMargin
    val response = doLsp(DefinitionLSPRequest(code, programEnvironment, Pos(2, 1)))
    response match {
      case DefinitionLSPResponse(Pos(line, column), _) =>
        assertResult(2)(line)
        assertResult(1)(column)
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("go to definition function identifier recursive function at usage test") { _ =>
    val code = """let
      |  a = 1,
      |  rec b(v: int): int = if (v >= 0) then 0 else v * b(v - 1)
      |in
      |b(2)""".stripMargin
    val response = doLsp(DefinitionLSPRequest(code, programEnvironment, Pos(5, 1)))
    response match {
      case DefinitionLSPResponse(Pos(line, char), _) =>
        assertResult(3)(line)
        assertResult(7)(char)
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("go to definition let function identifier recursive function at usage test") { _ =>
    val code = """let
      |  rec b(v: int): int = if (v >= 0) then 0 else v * b(v - 1)
      |in
      |b(2)""".stripMargin
    val response = doLsp(DefinitionLSPRequest(code, programEnvironment, Pos(4, 1)))
    response match {
      case DefinitionLSPResponse(Pos(line, char), _) =>
        assertResult(2)(line)
        assertResult(7)(char)
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("go to definition let function entity recursive function at definition test") { _ =>
    val code = """let
      |  rec b(v: int): int = if (v >= 0) then 0 else v * b(v - 1)
      |in
      |b(2)""".stripMargin
    val response = doLsp(DefinitionLSPRequest(code, programEnvironment, Pos(2, 7)))
    response match {
      case DefinitionLSPResponse(Pos(line, char), _) =>
        assertResult(2)(line)
        assertResult(7)(char)
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("go to definition function identifier at definition test") { _ =>
    val code = """let
      |buildCollection = lastElement:int -> let
      |                                            a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val response = doLsp(DefinitionLSPRequest(code, programEnvironment, Pos(2, 3)))
    response match {
      case DefinitionLSPResponse(Pos(line, char), _) =>
        assertResult(2)(line)
        assertResult(1)(char)
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("go to definition let function entity function at definition test") { _ =>
    val code = """let
      |  b(v: int): int = v
      |in
      |b(2)""".stripMargin
    val response = doLsp(DefinitionLSPRequest(code, programEnvironment, Pos(2, 3)))
    response match {
      case DefinitionLSPResponse(Pos(line, char), _) =>
        assertResult(2)(line)
        assertResult(3)(char)
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("go to definition function identifier at usage test") { _ =>
    val code = """let
      |buildCollection = lastElement:int -> let
      |                                            a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val response = doLsp(DefinitionLSPRequest(code, programEnvironment, Pos(8, 15)))
    response match {
      case DefinitionLSPResponse(Pos(line, char), _) =>
        assertResult(2)(line)
        assertResult(1)(char)
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("go to definition function parameter test") { _ =>
    val code = """let
      |buildCollection = lastElement:int -> let
      |a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val response = doLsp(DefinitionLSPRequest(code, programEnvironment, Pos(2, 20)))
    response match {
      case DefinitionLSPResponse(Pos(line, char), _) =>
        assertResult(2)(line)
        assertResult(19)(char)
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("go to definition function parameter usage test") { _ =>
    val code = """let
      |buildCollection = lastElement:int -> let
      |a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val response = doLsp(DefinitionLSPRequest(code, programEnvironment, Pos(3, 29)))
    response match {
      case DefinitionLSPResponse(Pos(line, char), _) =>
        assertResult(2)(line)
        assertResult(19)(char)
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("go to definition let should return empty response") { _ =>
    val code = """let
      |buildCollection = lastElement:int -> let
      |                                            a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val response = doLsp(DefinitionLSPRequest(code, programEnvironment, Pos(1, 1)))
    response shouldBe a[ErrorLSPResponse]
  }

  test("go to definition field of a collection") { _ =>
    val code = """let
      |    data = Collection.Build(Record.Build(aaaaaaaaaaaa = Record.Build(cccccccccccc = "takis", d = 6), b = 3))
      |in
      |Collection.Filter(data, d -> d.aaaaaaaaaaaa.cccccccccccc > 0)""".stripMargin
    val response = doLsp(DefinitionLSPRequest(code, programEnvironment, Pos(4, 32)))
    response match {
      case DefinitionLSPResponse(Pos(line, char), _) =>
        assertResult(2)(line)
        assertResult(42)(char)
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("go to definition field of a collection nested") { _ =>
    val code = """let
      |    data = Collection.Build(Record.Build(aaaaaaaaaaaa = Record.Build(cccccccccccc = "takis", d = 6), b = 3))
      |in
      |Collection.Filter(data, d -> d.aaaaaaaaaaaa.cccccccccccc > 0)""".stripMargin
    val response = doLsp(DefinitionLSPRequest(code, programEnvironment, Pos(4, 45)))
    response match {
      case DefinitionLSPResponse(Pos(line, char), _) =>
        assertResult(2)(line)
        assertResult(70)(char)
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("go to definition of space should return empty result") { _ =>
    val code = """  let a = "hello" in a """.stripMargin
    val response = doLsp(DefinitionLSPRequest(code, programEnvironment, Pos(1, 1)))
    response shouldBe a[ErrorLSPResponse]
  }

}
