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

import raw.compiler.{Pos, RenameLSPRequest, RenameLSPResponse}
import raw.compiler.rql2.tests.CompilerTestContext
import raw.runtime.ProgramEnvironment

trait LspRenameTest extends CompilerTestContext {

  val programEnvironment: ProgramEnvironment = ProgramEnvironment(Some("snapi"), Set.empty, Map.empty)

  test("rename identifier at usage test") { _ =>
    val code = """let
      |a = 5
      |in
      |a
      |""".stripMargin
    val response = doLsp(RenameLSPRequest(code, programEnvironment, Pos(2, 1)))
    response match {
      case RenameLSPResponse(positions, _) =>
        assert(positions.exists(p => p.line == 2 && p.column == 1))
        assert(positions.exists(p => p.line == 4 && p.column == 1))
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("another rename identifier at usage test") { _ =>
    val code = """let b = 5
      |in b""".stripMargin
    val response = doLsp(RenameLSPRequest(code, programEnvironment, Pos(2, 4)))
    response match {
      case RenameLSPResponse(positions, _) =>
        assert(positions.exists(p => p.line == 1 && p.column == 5))
        assert(positions.exists(p => p.line == 2 && p.column == 4))
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("rename definition identifier at definition test") { _ =>
    val code = """let
      |a = 5
      |in
      |a
      |""".stripMargin
    val response = doLsp(RenameLSPRequest(code, programEnvironment, Pos(4, 1)))
    response match {
      case RenameLSPResponse(positions, _) =>
        assert(positions.exists(p => p.line == 2 && p.column == 1))
        assert(positions.exists(p => p.line == 4 && p.column == 1))
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("rename function identifier recursive function at usage test") { _ =>
    val code = """let
      |  a = 1,
      |rec b(v: int): int = if (v >= 0) then 0 else v * b(v - 1)
      |in
      |b(2)""".stripMargin
    val response = doLsp(RenameLSPRequest(code, programEnvironment, Pos(5, 1)))
    response match {
      case RenameLSPResponse(positions, _) =>
        assert(positions.exists(p => p.line == 5 && p.column == 1))
        assert(positions.exists(p => p.line == 3 && p.column == 5))
        assert(positions.exists(p => p.line == 3 && p.column == 50))
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("rename let function identifier recursive function at usage test") { _ =>
    val code = """let
      |rec b(v: int): int = if (v >= 0) then 0 else v * b(v - 1)
      |in
      |b(2)""".stripMargin
    val response = doLsp(RenameLSPRequest(code, programEnvironment, Pos(2, 5)))
    response match {
      case RenameLSPResponse(positions, _) =>
        assert(positions.exists(p => p.line == 4 && p.column == 1))
        assert(positions.exists(p => p.line == 2 && p.column == 5))
        assert(positions.exists(p => p.line == 2 && p.column == 50))
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("rename let function entity recursive function at definition test") { _ =>
    val code = """let
      |rec b(v: int): int = if (v >= 0) then 0 else v * b(v - 1)
      |in
      |b(2)""".stripMargin
    val response = doLsp(RenameLSPRequest(code, programEnvironment, Pos(2, 5)))
    response match {
      case RenameLSPResponse(positions, _) =>
        assert(positions.exists(p => p.line == 4 && p.column == 1))
        assert(positions.exists(p => p.line == 2 && p.column == 5))
        assert(positions.exists(p => p.line == 2 && p.column == 50))
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("rename function identifier at definition test") { _ =>
    val code = """let
      |buildCollection = lastElement:int -> let
      |                                            a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val response = doLsp(RenameLSPRequest(code, programEnvironment, Pos(2, 5)))
    response match {
      case RenameLSPResponse(positions, _) =>
        assert(positions.exists(p => p.line == 8 && p.column == 11))
        assert(positions.exists(p => p.line == 2 && p.column == 1))
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("rename let function entity function at definition test") { _ =>
    val code = """let
      |b(v: int): int = v
      |in
      |b(2)""".stripMargin
    val response = doLsp(RenameLSPRequest(code, programEnvironment, Pos(2, 1)))
    response match {
      case RenameLSPResponse(positions, _) =>
        assert(positions.exists(p => p.line == 4 && p.column == 1))
        assert(positions.exists(p => p.line == 2 && p.column == 1))
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("rename function identifier at usage test") { _ =>
    val code = """let
      |buildCollection = lastElement:int -> let
      |                                            a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val response = doLsp(RenameLSPRequest(code, programEnvironment, Pos(8, 11)))
    response match {
      case RenameLSPResponse(positions, _) =>
        assert(positions.exists(p => p.line == 8 && p.column == 11))
        assert(positions.exists(p => p.line == 2 && p.column == 1))
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("rename function parameter test") { _ =>
    val code = """let
      |buildCollection = lastElement:int -> let
      |a = Collection.Build(1,2,3,lastElement),
      |b = Collection.Build(1,2,3,lastElement),
      |c = Collection.Build(1,2,3,lastElement)
      | in Collection.Build(a,b,c)
      |in
      |let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val response = doLsp(RenameLSPRequest(code, programEnvironment, Pos(2, 19)))
    response match {
      case RenameLSPResponse(positions, _) =>
        assert(positions.exists(p => p.line == 2 && p.column == 19))
        assert(positions.exists(p => p.line == 3 && p.column == 28))
        assert(positions.exists(p => p.line == 4 && p.column == 28))
        assert(positions.exists(p => p.line == 5 && p.column == 28))
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("rename function parameter usage test") { _ =>
    val code = """let
      |buildCollection = lastElement:int -> let
      |a = Collection.Build(1,2,3,lastElement),
      |b = Collection.Build(1,2,3,lastElement),
      |c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val response = doLsp(RenameLSPRequest(code, programEnvironment, Pos(4, 28)))
    response match {
      case RenameLSPResponse(positions, _) =>
        assert(positions.exists(p => p.line == 2 && p.column == 19))
        assert(positions.exists(p => p.line == 3 && p.column == 28))
        assert(positions.exists(p => p.line == 4 && p.column == 28))
        assert(positions.exists(p => p.line == 5 && p.column == 28))
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("rename let should return empty array") { _ =>
    val code = """let
      |buildCollection = lastElement:int -> let
      |                                            a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val response = doLsp(RenameLSPRequest(code, programEnvironment, Pos(1, 1)))
    response match {
      case RenameLSPResponse(positions, _) => assert(positions.length == 0)
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

}
