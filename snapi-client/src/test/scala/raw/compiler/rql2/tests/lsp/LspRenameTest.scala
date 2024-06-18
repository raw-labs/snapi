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

import raw.compiler.rql2.tests.Rql2CompilerTestContext
import raw.client.api._

trait LspRenameTest extends Rql2CompilerTestContext {

  test("rename identifier at usage test") { _ =>
    val code = """let
      |a = 5
      |in
      |a
      |""".stripMargin
    val RenameResponse(positions) = rename(code, Pos(2, 1))
    assert(positions.exists(p => p.line == 2 && p.column == 1))
    assert(positions.exists(p => p.line == 4 && p.column == 1))
  }

  test("another rename identifier at usage test") { _ =>
    val code = """let b = 5
      |in b""".stripMargin
    val RenameResponse(positions) = rename(code, Pos(2, 4))
    assert(positions.exists(p => p.line == 1 && p.column == 5))
    assert(positions.exists(p => p.line == 2 && p.column == 4))
  }

  test("rename definition identifier at definition test") { _ =>
    val code = """let
      |a = 5
      |in
      |a
      |""".stripMargin
    val RenameResponse(positions) = rename(code, Pos(4, 1))
    assert(positions.exists(p => p.line == 2 && p.column == 1))
    assert(positions.exists(p => p.line == 4 && p.column == 1))
  }

  test("rename function identifier recursive function at usage test") { _ =>
    val code = """let
      |  a = 1,
      |rec b(v: int): int = if (v >= 0) then 0 else v * b(v - 1)
      |in
      |b(2)""".stripMargin
    val RenameResponse(positions) = rename(code, Pos(5, 1))
    assert(positions.exists(p => p.line == 5 && p.column == 1))
    assert(positions.exists(p => p.line == 3 && p.column == 5))
    assert(positions.exists(p => p.line == 3 && p.column == 50))
  }

  test("rename let function identifier recursive function at usage test") { _ =>
    val code = """let
      |rec b(v: int): int = if (v >= 0) then 0 else v * b(v - 1)
      |in
      |b(2)""".stripMargin
    val RenameResponse(positions) = rename(code, Pos(2, 5))
    assert(positions.exists(p => p.line == 4 && p.column == 1))
    assert(positions.exists(p => p.line == 2 && p.column == 5))
    assert(positions.exists(p => p.line == 2 && p.column == 50))
  }

  test("rename let function entity recursive function at definition test") { _ =>
    val code = """let
      |rec b(v: int): int = if (v >= 0) then 0 else v * b(v - 1)
      |in
      |b(2)""".stripMargin
    val RenameResponse(positions) = rename(code, Pos(2, 5))
    assert(positions.exists(p => p.line == 4 && p.column == 1))
    assert(positions.exists(p => p.line == 2 && p.column == 5))
    assert(positions.exists(p => p.line == 2 && p.column == 50))
  }

  test("rename function identifier at definition test") { _ =>
    val code = """let
      |buildCollection = (lastElement:int) -> let
      |                                            a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val RenameResponse(positions) = rename(code, Pos(2, 5))
    assert(positions.exists(p => p.line == 8 && p.column == 11))
    assert(positions.exists(p => p.line == 2 && p.column == 1))
  }

  test("rename let function entity function at definition test") { _ =>
    val code = """let
      |b(v: int): int = v
      |in
      |b(2)""".stripMargin
    val RenameResponse(positions) = rename(code, Pos(2, 1))
    assert(positions.exists(p => p.line == 4 && p.column == 1))
    assert(positions.exists(p => p.line == 2 && p.column == 1))
  }

  test("rename function identifier at usage test") { _ =>
    val code = """let
      |buildCollection = (lastElement:int) -> let
      |                                            a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val RenameResponse(positions) = rename(code, Pos(8, 11))
    assert(positions.exists(p => p.line == 8 && p.column == 11))
    assert(positions.exists(p => p.line == 2 && p.column == 1))
  }

  test("rename function parameter test") { _ =>
    val code = """let
      |buildCollection = (lastElement:int) -> let
      |a = Collection.Build(1,2,3,lastElement),
      |b = Collection.Build(1,2,3,lastElement),
      |c = Collection.Build(1,2,3,lastElement)
      | in Collection.Build(a,b,c)
      |in
      |let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val RenameResponse(positions) = rename(code, Pos(2, 20))
    assert(positions.exists(p => p.line == 2 && p.column == 20))
    assert(positions.exists(p => p.line == 3 && p.column == 28))
    assert(positions.exists(p => p.line == 4 && p.column == 28))
    assert(positions.exists(p => p.line == 5 && p.column == 28))
  }

  test("rename function parameter usage test") { _ =>
    val code = """let
      |buildCollection = (lastElement:int) -> let
      |a = Collection.Build(1,2,3,lastElement),
      |b = Collection.Build(1,2,3,lastElement),
      |c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val RenameResponse(positions) = rename(code, Pos(4, 28))
    assert(positions.exists(p => p.line == 2 && p.column == 20))
    assert(positions.exists(p => p.line == 3 && p.column == 28))
    assert(positions.exists(p => p.line == 4 && p.column == 28))
    assert(positions.exists(p => p.line == 5 && p.column == 28))
  }

  test("rename let should return empty array") { _ =>
    val code = """let
      |buildCollection = (lastElement:int) -> let
      |                                            a = Collection.Build(1,2,3,lastElement),
      |                                            b = Collection.Build(1,2,3,lastElement),
      |                                            c = Collection.Build(1,2,3,lastElement)
      |                                        in Collection.Build(a,b,c)
      |in
      |    let bbb = buildCollection(5), ttt = Collection.Build(1,2,3) in Collection.Filter(ttt, t -> t > 1 )""".stripMargin
    val RenameResponse(positions) = rename(code, Pos(1, 1))
    assert(positions.length == 0)
  }

  test("RD-10698 crash fix") { _ =>
    val code = """main(`1 + 2`: int) = let
      |    a = 1,
      |    a = 2
      |in a""".stripMargin
    val RenameResponse(positions) = rename(code, Pos(4, 4))
    assert(positions.length == 2)
  }

}
