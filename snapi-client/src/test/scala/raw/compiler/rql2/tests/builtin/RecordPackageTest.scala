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

package raw.compiler.rql2.tests.builtin

import raw.compiler.rql2.tests.CompilerTestContext

trait RecordPackageTest extends CompilerTestContext {
//
//  test("""Record.Build(a = 1, b = "Hello")""".stripMargin)(_ should (typeAs("record(a: int, b: string)") and run))
//
//  test("""Record.Build(a = 1, a = 3, b = "Hello")""".stripMargin)(it =>
//    it should typeAs("record(a: int, a: int, b: string)")
//  )
//
//  test("""(Record.Build(a = 1, b = "Hello")).a""".stripMargin) { it =>
//    it should typeAs("int")
//    it should evaluateTo("1")
//  }
//
//  test("""(Record.Build(a = 1, b = "Hello")).b""".stripMargin) { it =>
//    it should typeAs("string")
//    it should evaluateTo(""" "Hello" """)
//  }
//
//  test("""(Record.Build(a = 1, b = "Hello")).c""".stripMargin)(it =>
//    it should typeErrorAs("expected package, record, collection or list with field c but got record(a: int,b: string)")
//  )
//
//  test("""
//    |let r = Record.Build(a = 1, b = "Hello")
//    |in
//    |    Record.AddField(r, a = "World")""".stripMargin)(it => it should typeErrorAs("field already exists in record"))

//  test("""
//    |let r = Record.Build(a = 1, b = "Hello")
//    |in
//    |    Record.AddField(r, c = "World")""".stripMargin) { it =>
//    it should typeAs("record(a: int, b: string, c: string)")
//    it should evaluateTo("""Record.Build(a=1, b="Hello", c="World")""")
//  }

  test("""
    |let r = Record.Build(a = 1, b = "Hello")
    |in
    |    Record.RemoveField(r, "a")""".stripMargin) { it =>
    it should typeAs("record(b: string)")
    it should evaluateTo("""Record.Build(b = "Hello")""")
  }

  test("""
    |let r = Record.Build(a = 1, b = "Hello")
    |in
    |    Record.RemoveField(r, "b")""".stripMargin) { it =>
    it should typeAs("record(a: int)")
    it should evaluateTo("""Record.Build(a = 1)""")
  }

  test("""
    |let r = Record.Build(a = 1, b = "Hello")
    |in
    |    Record.RemoveField(r, "c")""".stripMargin)(it => it should typeErrorAs("field c not found"))

  test("""
    |let r = Record.Build(a = 1, b = "Hello"),
    |    c = "x"
    |in
    |    Record.RemoveField(r, c)""".stripMargin)(it => it should typeErrorAs("field x not found"))

  test("""
    |let r = Record.Build(a = 1, b = "Hello"),
    |    c = "b"
    |in
    |    Record.RemoveField(r, c)""".stripMargin) { it =>
    it should typeAs("record(a: int)")
    it should evaluateTo("""Record.Build(a = 1)""")
  }

  test("""
    |let r = Record.Build(a = 1, b = "Hello")
    |in
    |    Record.Fields(r)
    |""".stripMargin) { it =>
    it should typeAs("list(string)")
    it should evaluateTo("""List.Build("a", "b")""")
  }

  test("""
    |let r = Record.Build(a = 1, b = "Hello"),
    |    r1 = Record.AddField(r, c = "World"),
    |    r2 = Record.RemoveField(r1, "a")
    |in
    |    r2""".stripMargin) { it =>
    it should typeAs("record(b: string, c: string)")
    it should evaluateTo("""Record.Build(b = "Hello", c = "World")""")
  }

  test("""
    |let r = Record.Build(a = 1, b = "Hello")
    |in Record.GetFieldByIndex(r, 1)
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("""
    |let r = Record.Build(a = 1, b = "Hello")
    |in Record.GetFieldByIndex(r, 2)
    |""".stripMargin) { it =>
    it should typeAs("string")
    it should evaluateTo(""""Hello"""")
  }

  test("""
    |let r = Record.Build(a = 1, b = "Hello")
    |in Record.GetFieldByIndex(r, 0)
    |""".stripMargin)(it => it should typeErrorAs("field at index 0 not found"))

  test("""
    |let r = Record.Build(a = 1, b = "Hello")
    |in Record.GetFieldByIndex(r, 3)
    |""".stripMargin)(it => it should typeErrorAs("field at index 3 not found"))

  test("""let r1 = {a: 12, b: 14},
    |     r2 = {c: "tralala", d: [1,2,3]}
    |in Record.Concat(r1, r2)
    |""".stripMargin) { it =>
    it should typeAs("record(a: int, b: int, c: string, d: list(int))")
    it should evaluateTo("""{a: 12, b: 14, c: "tralala", d: [1,2,3]}""")
  }

  test("""let r1 = {a: 12, b: 14},
    |     r2 = {c: "tralala", d: [1,2,3]}
    |in Record.Concat(r2, r1)
    |""".stripMargin) { it =>
    it should typeAs("record(c: string, d: list(int), a: int, b: int)")
    it should evaluateTo("""{c: "tralala", d: [1,2,3], a: 12, b: 14}""")
  }

  test("""let r1 = if false then {a: 12, b: 14} else null,
    |     r2 = {c: "tralala", d: [1,2,3]}
    |in Record.Concat(r1, r2)
    |""".stripMargin) { it =>
    it should typeAs("record(a: int, b: int, c: string, d: list(int))")
    it should evaluateTo("""null""")
  }

  test("""let r1 = if true then {a: 12, b: 14} else null,
    |     r2 = {c: "tralala", d: [1,2,3]}
    |in Record.Concat(r1, r2)
    |""".stripMargin) { it =>
    it should typeAs("record(a: int, b: int, c: string, d: list(int))")
    it should evaluateTo("""{a: 12, b: 14, c: "tralala", d: [1,2,3]}""")
  }

  test("""let r1 = if false then {a: 12, b: 14} else Error.Build("broken record"),
    |     r2 = {c: "tralala", d: [1,2,3]}
    |in Record.Concat(r1, r2)
    |""".stripMargin) { it =>
    it should typeAs("record(a: int, b: int, c: string, d: list(int))")
    it should runErrorAs("""broken record""")
  }

  test("""let r1 = if true then {a: 12, b: 14} else Error.Build("broken record"),
    |     r2 = {c: "tralala", d: [1,2,3]}
    |in Record.Concat(r1, r2)
    |""".stripMargin) { it =>
    it should typeAs("record(a: int, b: int, c: string, d: list(int))")
    it should evaluateTo("""{a: 12, b: 14, c: "tralala", d: [1,2,3]}""")
  }

}
