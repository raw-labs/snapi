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

package raw.compiler.rql2.tests.spec

import raw.compiler.rql2.tests.Rql2CompilerTestContext

trait PackageNameTest extends Rql2CompilerTestContext {

  test("""Record""")(it => it should typeAs("""package("Record")"""))

  test("""Rekord""")(it => it should typeErrorAs("Rekord is not declared"))

  test("""Record.Build""")(it => it should typeAs("""package("Record", "Build")"""))

  test("""Record.Buyld""")(it => it should typeErrorAs("Buyld is not declared in package Record"))

  test("""
    |let Collection = 1
    |in Collection
    |""".stripMargin)(it => it should typeAs("int"))

  test("""
    |let A = Record,
    |    Record = 1
    |in A(x=Record)
    |""".stripMargin)(it => it should typeErrorAs("""function or method expected but got package"""))

  test("""
    |let A = Record,
    |    Record = 1,
    |    C = A.Build
    |in C(x=Record)
    |""".stripMargin) { it =>
    it should typeAs("record(x: int)")
    it should evaluateTo("Record.Build(x=1)")
  }

  // This triggers an ImplicitCast, which adds a List.Transform node.
  // This List.Transform node must refer to the real List.Transform and not to the user-created List value with the
  // same name.
  test("""
    |let List = List.Build(1,2,3),
    |    f(v: list(int)) = v
    |in f(List)
    |""".stripMargin) { it =>
    it should typeAs("list(int)")
    it should evaluateTo("List.Build(1,2,3)")
  }

}
