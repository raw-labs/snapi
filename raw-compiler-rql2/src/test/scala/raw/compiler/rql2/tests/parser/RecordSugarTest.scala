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

package raw.compiler.rql2.tests.parser

import raw.compiler.rql2.tests.CompilerTestContext

trait RecordSugarTest extends CompilerTestContext {

  test("""{a: 1, b: "2"}""") { it =>
    it should typeAs("record(a: int, b: string)")
    it should evaluateTo("""Record.Build(a=1, b="2")""")
  }

  test("{}") { it =>
    it should typeAs("record()")
    it should evaluateTo("""Record.Build()""")
  }

  // This is allowed according to our syntax.
  // It is only forbidden on use.
  test("Record.Build(a=1, a=1, b=2)")(it => it should typeAs("record(a: int, a: int, b: int)"))

  // This is allowed according to our syntax.
  // It is only forbidden on use.
  test("{a: 1, a: 1, b: 2}")(it => it should typeAs("record(a: int, a: int, b: int)"))

  test("""
    |let x = {a: 1, b: "hello"}
    |in {x.a, x.b}""".stripMargin) { it =>
    it should typeAs("record(a: int, b: string)")
    it should evaluateTo("""Record.Build(a=1, b="hello")""")
  }

  test("""
    |let x = {a: 1, b: "hello"},
    |    y = {x.a, x.a, x.b}
    |in y.b""".stripMargin) { it =>
    it should typeAs("string")
    it should evaluateTo(""""hello"""")
  }

  test("""
    |let x = {a: 1, b: "hello"},
    |    y = {x.a, x.a, x.b}
    |in y.a""".stripMargin)(it => it should typeErrorAs("record has more than one field with the same name: a"))

  test("""let x = {a: 1, "hello"}
    |in {x.a, x._2, 3}
    |""".stripMargin) { it =>
    it should typeAs("record(a: int, _2: string, _3: int)")
    it should evaluateTo("""{a: 1, _2: "hello", _3: 3}""")
  }

  test("""{_1: 1, 2, _2: 3}""".stripMargin)(it => it should typeAs("record(_1: int, _2: int, _2: int)"))

}
