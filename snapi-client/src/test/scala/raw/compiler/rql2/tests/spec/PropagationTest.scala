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

import raw.compiler.rql2.tests.CompilerTestContext

trait PropagationTest extends CompilerTestContext {
  // lists
  test("""let l = List.Build(1,2,3,2,1)
    |in TestPackage.StrictArgs(l)""".stripMargin)(_ should (typeAs("float") and evaluateTo("5.0f")))

  test("""let l = List.Transform(List.Build(1,2,3,2,1), x -> if (x == 50) then null else x)
    |in TestPackage.StrictArgs(l)""".stripMargin)(_ should (typeAs("float") and evaluateTo("5.0f")))

  test("""let l = List.Transform(List.Build(1,2,3,2,1), x -> 1000/x)
    |in TestPackage.StrictArgs(l)""".stripMargin)(_ should (typeAs("float") and evaluateTo("5.0f")))

  test("""let f(data: list(int)) = TestPackage.StrictArgs(data),
    |         l = List.Build(1,2,3,2,1)
    |in f(l)""".stripMargin)(_ should (typeAs("float") and evaluateTo("5.0f")))

  test("""let l = List.Transform(List.Build(1,2,3,2,1), x -> if (x == 3) then null else x)
    |in TestPackage.StrictArgs(l)""".stripMargin)(_ should (typeAs("float") and evaluateTo("null")))

  test("""let l = List.Transform(List.Build(1,2,3,2,1), x -> 1000/(x-3))
    |in TestPackage.StrictArgs(l)""".stripMargin)(_ should (typeAs("float") and runErrorAs("/ by zero")))

  test("""let f(data: list(int)) = TestPackage.StrictArgs(data),
    |         l = List.Transform(List.Build(1,2,3,2,1), x -> if (x == 3) then null else x)
    |in f(l)""".stripMargin)(_ should (typeAs("float") and evaluateTo("null")))

  test("""let f(data: list(int)) = TestPackage.StrictArgs(data),
    |         l = List.Transform(List.Build(1,2,3,2,1), x -> 1000/(x-3))
    |in f(l)""".stripMargin)(_ should (typeAs("float") and runErrorAs("/ by zero")))

  test("""TestPackage.StrictArgs(List.Build(), r=null)""")(_ should evaluateTo("null"))

  test("""let items = List.Build(Record.Build(a=1, b=2), null)
    |in TestPackage.StrictArgs(List.Build(), r=List.Get(items, 0))""".stripMargin)(
    _ should evaluateTo("3.0f")
  )
  test("""let items = List.Build(Record.Build(a=1, b=2), null)
    |in TestPackage.StrictArgs(List.Build(), r=List.Get(items, 1))""".stripMargin)(
    _ should evaluateTo("null")
  )
  test("""let items = List.Build(Record.Build(a=1, b=2), null)
    |in TestPackage.StrictArgs(List.Build(), r=List.First(items))""".stripMargin)(
    _ should evaluateTo("3.0f")
  )
  test("""let items = List.Filter(List.Build(null, Record.Build(a=1, b=2)), x -> false)
    |in TestPackage.StrictArgs(List.Build(), r=List.First(items))""".stripMargin)(
    _ should evaluateTo("null")
  )

  // records
  test("""let l = List.Build(1,2,3,2,1),
    |    data = {a: List.Get(l, 0), b: 3.0f}
    |in TestPackage.StrictArgs(l, r=data)""".stripMargin)(_ should (typeAs("float") and evaluateTo("5.0f + 1 + 3.0f")))

  test("""let l = List.Build(1,2,3,2,1),
    |    data = {a: List.Get(l, 2), b: 3.0f}
    |in TestPackage.StrictArgs(l, r=data)""".stripMargin)(_ should (typeAs("float") and evaluateTo("5.0f + 3 + 3.0f")))

  test("""let l = List.Build(1,2,3,2,1),
    |    data = {a: List.Get(l, 20), b: 3.0f}
    |in TestPackage.StrictArgs(l, r=data)""".stripMargin)(_ should (typeAs("float") and runErrorAs("out of bound")))

  test("""let l = List.Build(1,2,3,2,1),
    |    l2 = List.Transform(l, x -> if (x == 3) then null else x),
    |    data = {a: List.Get(l2, 0), b: 3.0f}
    |in TestPackage.StrictArgs(l, r=data)""".stripMargin)(_ should (typeAs("float") and evaluateTo("5 + 1 + 3.0f")))

  test("""let l = List.Build(1,2,3,2,1),
    |    l2 = List.Transform(l, x -> if (x == 3) then null else x),
    |    data = {a: List.Get(l2, 2), b: 3.0f}
    |in TestPackage.StrictArgs(l, r=data)""".stripMargin)(_ should (typeAs("float") and evaluateTo("null")))

  test("""let l = List.Build(1,2,3,2,1),
    |    l2 = List.Transform(l, x -> 1000/(x-3)),
    |    data = {a: List.Get(l2, 0), b: 3.0f}
    |in TestPackage.StrictArgs(l, r=data)""".stripMargin)(_ should (typeAs("float") and evaluateTo("5 - 500 + 3.0f")))

  test("""let l = List.Build(1,2,3,2,1),
    |    l2 = List.Transform(l, x -> 1000/(x-3)),
    |    data = {a: List.Get(l2, 2), b: 3.0f}
    |in TestPackage.StrictArgs(l, r=data)""".stripMargin)(_ should (typeAs("float") or runErrorAs("/ by zero")))

  // collections
  test("""let l = Collection.Build(1,2,3,2,1)
    |in TestPackage.StrictArgsColPassThrough(l)""".stripMargin)(
    _ should (typeAs("collection(int)") and evaluateTo("Collection.Build(10,20,30,20,10)"))
  )

  test("""let l = Collection.Transform(Collection.Build(1,2,3,2,1), x -> if (x == 50) then null else x)
    |in TestPackage.StrictArgsColPassThrough(l)""".stripMargin)(
    _ should (typeAs("collection(int)") and evaluateTo("Collection.Build(10,20,30,20,10)"))
  )

  test("""let l = Collection.Transform(Collection.Build(1,2,3,2,1), x -> 1000/x)
    |in TestPackage.StrictArgsColPassThrough(l)""".stripMargin)(
    _ should (typeAs("collection(int)") and evaluateTo("Collection.Build(10000,5000,3330,5000,10000)"))
  )

  test("""let f(data: collection(int)) = TestPackage.StrictArgsColPassThrough(data),
    |         l = Collection.Build(1,2,3,2,1)
    |in f(l)""".stripMargin)(_ should (typeAs("collection(int)") and evaluateTo("Collection.Build(10,20,30,20,10)")))

  test("""let l = Collection.Transform(Collection.Build(1,2,3,2,1), x -> if (x == 3) then null else x)
    |in TestPackage.StrictArgsColPassThrough(l)""".stripMargin)(
    _ should (typeAs("collection(int)") and runErrorAs("unexpected null value found"))
  )

  test("""let l = Collection.Transform(Collection.Build(1,2,3,2,1), x -> 1000/(x-3))
    |in TestPackage.StrictArgsColPassThrough(l)""".stripMargin)(
    _ should (typeAs("collection(int)") and runErrorAs("/ by zero"))
  )

  test("""let f(data: collection(int)) = TestPackage.StrictArgsColPassThrough(data),
    |         l = Collection.Transform(Collection.Build(1,2,3,2,1), x -> if (x == 3) then null else x)
    |in f(l)""".stripMargin)(_ should (typeAs("collection(int)") and runErrorAs("unexpected null value found")))

  test("""let f(data: collection(int)) = TestPackage.StrictArgsColPassThrough(data),
    |         l = Collection.Transform(Collection.Build(1,2,3,2,1), x -> 1000/(x-3))
    |in f(l)""".stripMargin)(_ should (typeAs("collection(int)") and runErrorAs("/ by zero")))

  test("Math.Sin(null)")(_ should evaluateTo("null"))
  test("""Math.Sin(Error.Build("failed"))""")(_ should runErrorAs("failed"))
  test("let x: double = null in Math.Sin(x)")(_ should evaluateTo("null"))
  test("""let x: double = Error.Build("failed") in Math.Sin(x)""")(_ should runErrorAs("failed"))

  test("not null")(_ should evaluateTo("null"))
  test("""- (1 + null)""")(_ should evaluateTo("null"))
  test("if null then 1 else 0")(_ should evaluateTo("null"))

  test("""not Error.Build("failed")""")(_ should runErrorAs("failed"))
  test("""- (1 + Error.Build("failed"))""")(_ should runErrorAs("failed"))
  test("""if Error.Build("failed") then 1 else 0""")(_ should runErrorAs("failed"))

  test("""let x: bool = Error.Build("failed") in not x""")(_ should runErrorAs("failed"))
  test("""let x: int = Error.Build("failed") in -x""")(_ should runErrorAs("failed"))
  test("""let x: bool = Error.Build("failed") in if x then 1 else 0""")(
    _ should runErrorAs("failed")
  )

  test("""let x: bool = null in not x""")(_ should evaluateTo("null"))
  test("""let x: int = null in -x""")(_ should evaluateTo("null"))
  test("""let x: bool = null in if x then 1 else 0""")(_ should evaluateTo("null"))

  test("""let x: bool = true in if x then Error.Build("failed") else 0""")(
    _ should runErrorAs("failed")
  )

  test("""let x: bool = true in if x then 1 else Error.Build("failed")""")(
    _ should evaluateTo("1")
  )

  test("""let x: bool = true in if x then Error.Build("failed 1") else Error.Build("failed 0")""")(
    _ should runErrorAs("failed 1")
  )

  test("""let r: record(a: int, b: int) = null in r.a""")(_ should evaluateTo("null"))
  test("""let r: record(a: int, b: int) = null in r.b""")(_ should evaluateTo("null"))
  test("""let r: record(a: int, b: int) = null in r.a + r.b""")(_ should evaluateTo("null"))

  test("""let r: record(a: int, b: int) = Error.Build("wrong record") in r.a""")(_ should runErrorAs("wrong record"))
  test("""let r: record(a: int, b: int) = Error.Build("wrong record") in r.b""")(_ should runErrorAs("wrong record"))
  test("""let r: record(a: int, b: int) = Error.Build("wrong record") in r.a + r.b""")(
    _ should runErrorAs("wrong record")
  )
  test("""TestPackage.StrictArgs(List.Build(), r=Error.Build("wrong record"))""")(_ should runErrorAs("wrong record"))

  test("let r: record(a: int, b: int) = Record.Build(a=1, b=2) in r.a")(_ should evaluateTo("1"))
  test("let r: record(a: int, b: int) = Record.Build(a=1, b=2) in r.b")(_ should evaluateTo("2"))
  test("let r: record(a: int, b: int) = Record.Build(a=1, b=2) in r.a + r.b")(_ should evaluateTo("3"))

  test("""10 / 2""".stripMargin)(_ should evaluateTo("5"))
  test("""1 + 4""".stripMargin)(_ should evaluateTo("5"))
  test("""1 / 0""".stripMargin)(_ should runErrorAs("/ by zero"))

  test("""
    |1 + Type.Cast(type int, null)""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("Type.Cast(type int, null)")
  }

  test("""
    |1 + null""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("null")
  }

  test("""
    |null + 1""".stripMargin)(_ should evaluateTo("null"))

  test("""
    |1 + Error.Build("failed int")""".stripMargin)(_ should runErrorAs("failed int"))

  test("""
    |Error.Build("failed int") + 1""".stripMargin)(_ should runErrorAs("failed int"))

  test("""
    |let x: int = null
    |in x + 1""".stripMargin)(_ should evaluateTo("null"))

  test("""
    |let x: int = null
    |in 1 + x""".stripMargin)(_ should evaluateTo("null"))

  test("""
    |let x: int = null
    |in x + x""".stripMargin)(_ should evaluateTo("null"))

  test("""
    |let x: int = Error.Build("failed int")
    |in 1 + x""".stripMargin)(_ should runErrorAs("failed int"))

  test("""
    |let x: int = Error.Build("failed int")
    |in x + 1""".stripMargin)(_ should runErrorAs("failed int"))

  test("""
    |let x: int = Error.Build("failed int")
    |in x + x""".stripMargin)(_ should runErrorAs("failed int"))

  // comparison

  test("""
    |let x: int = null
    |in x > 1""".stripMargin)(_ should evaluateTo("null"))

  test("""
    |let x: int = 1
    |in x > 1""".stripMargin)(_ should evaluateTo("false"))

  test("""
    |let x: int = null
    |in 1 > x""".stripMargin)(_ should evaluateTo("null"))

  test("""
    |let x: int = 0
    |in 1 > x""".stripMargin)(_ should evaluateTo("true"))

  test("""
    |let x: int = null
    |in x > x""".stripMargin)(_ should evaluateTo("null"))

  test("""
    |let x: int = Error.Build("failed int")
    |in 1 > x""".stripMargin)(_ should runErrorAs("failed int"))

  test("""
    |let x: int = Error.Build("failed int")
    |in x > 1""".stripMargin)(_ should runErrorAs("failed int"))

  test("""
    |let x: int = Error.Build("failed int")
    |in x > x""".stripMargin)(_ should runErrorAs("failed int"))

  // division

  test("""
    |1 / null""".stripMargin)(_ should evaluateTo("null"))

  test("""
    |null / 1""".stripMargin)(_ should evaluateTo("null"))

  test("""
    |1 / Error.Build("failed int")""".stripMargin)(_ should runErrorAs("failed int"))

  test("""
    |Error.Build("failed int") / 1""".stripMargin)(_ should runErrorAs("failed int"))

  test("""
    |let x: int = null
    |in x / 1""".stripMargin)(_ should evaluateTo("null"))

  test("""
    |let x: int = null
    |in x / 0""".stripMargin)(_ should evaluateTo("null"))

  test("""
    |let x: int = 1
    |in x / 0""".stripMargin)(_ should runErrorAs("/ by zero"))

  test("""
    |let x: int = null
    |in 1 / x""".stripMargin)(_ should evaluateTo("null"))

  test("""
    |let x: int = null
    |in x / x""".stripMargin)(_ should evaluateTo("null"))

  test("""
    |let x: int = Error.Build("failed int")
    |in 1 / x""".stripMargin)(_ should runErrorAs("failed int"))

  test("""
    |let x: int = Error.Build("failed int")
    |in x / 1""".stripMargin)(_ should runErrorAs("failed int"))

  test("""
    |let x: int = Error.Build("failed int")
    |in x / x""".stripMargin)(_ should runErrorAs("failed int"))

  ///////////////////////////////////////////////////////////////////
  //
  // ExpParam section. We try all possible versions of arg vs. param
  //
  ///////////////////////////////////////////////////////////////////

  // checking expected result
  test("TestPackage.MandatoryExpArgs(1, 2)")(_ should evaluateTo("3"))

  // null arg 1 / null arg 2
  test("TestPackage.MandatoryExpArgs(Nullable.Build(1), 2)")(_ should evaluateTo("3"))
  test("TestPackage.MandatoryExpArgs(1 + null, 2)")(_ should evaluateTo("null"))
  test("TestPackage.MandatoryExpArgs(null, 2)")(_ should evaluateTo("null"))
  test("TestPackage.MandatoryExpArgs(1, Nullable.Build(2))")(_ should evaluateTo("3"))
  test("TestPackage.MandatoryExpArgs(1, 2 + null)")(_ should evaluateTo("null"))
  test("TestPackage.MandatoryExpArgs(1, null)")(_ should evaluateTo("null"))

  // try arg 1 / try arg 2
  test("""TestPackage.MandatoryExpArgs(1, 2 + Error.Build("argh!"))""")(_ should runErrorAs("argh!"))
  test("""TestPackage.MandatoryExpArgs(1 + Error.Build("argh!"), 2)""")(_ should runErrorAs("argh!"))
  test("""TestPackage.MandatoryExpArgs(1, Success.Build(2))""")(_ should evaluateTo("3"))
  test("""TestPackage.MandatoryExpArgs(Success.Build(1), 2)""")(_ should evaluateTo("3"))
  test("""TestPackage.MandatoryExpArgs(1, Error.Build("argh!"))""")(_ should runErrorAs("argh!"))
  test("""TestPackage.MandatoryExpArgs(Error.Build("argh!"), 2)""")(_ should runErrorAs("argh!"))

  test("TestPackage.OptionalExpArgs(14)")(_ should evaluateTo("14*10*10"))
  test("TestPackage.OptionalExpArgs(14, x=2)")(_ should evaluateTo("14*2*10"))
  test("TestPackage.OptionalExpArgs(14, y=3)")(_ should evaluateTo("14*10*3"))
  test("TestPackage.OptionalExpArgs(14, x=2, y=3)")(_ should evaluateTo("14*2*3"))
  test("TestPackage.OptionalExpArgs(14, x=null)")(_ should evaluateTo("null"))
  test("TestPackage.OptionalExpArgs(14, y=null)")(_ should evaluateTo("null"))
  test("TestPackage.OptionalExpArgs(14, x=null, y=null)")(_ should evaluateTo("null"))
  test("TestPackage.OptionalExpArgs(14, x=2, y=null)")(_ should evaluateTo("null"))
  test("TestPackage.OptionalExpArgs(14, x=null, y=3)")(_ should evaluateTo("null"))
  test("""TestPackage.OptionalExpArgs(14, x=Error.Build("argh!"))""")(_ should runErrorAs("argh!"))
  test("""TestPackage.OptionalExpArgs(14, y=Error.Build("argh!"))""")(_ should runErrorAs("argh!"))
  test("""TestPackage.OptionalExpArgs(14, x=Error.Build("argh!"), y=Error.Build("argh!"))""")(
    _ should runErrorAs("argh!")
  )
  test("""TestPackage.OptionalExpArgs(14, x=1, y=Error.Build("argh!"))""")(_ should runErrorAs("argh!"))
  test("""TestPackage.OptionalExpArgs(14, x=Error.Build("argh!"), y=3)""")(_ should runErrorAs("argh!"))

  test("TestPackage.VarExpArgs(1, 2, 3)")(_ should evaluateTo("6"))
  test("TestPackage.VarExpArgs(1, 2, null)")(_ should evaluateTo("null"))
  test("TestPackage.VarExpArgs(1, null, 3)")(_ should evaluateTo("null"))
  test("TestPackage.VarExpArgs(null, 2, 3)")(_ should evaluateTo("null"))
  test("""TestPackage.VarExpArgs(1, 2, Error.Build("argh!"))""")(_ should runErrorAs("argh!"))
  test("""TestPackage.VarExpArgs(1, Error.Build("argh!"), 3)""")(_ should runErrorAs("argh!"))
  test("""TestPackage.VarExpArgs(Error.Build("argh!"), 2, 3)""")(_ should runErrorAs("argh!"))

  // ValueParam

  // checking expected result
  test("TestPackage.MandatoryValueArgs(1, 2)")(_ should evaluateTo("3"))
  test("let x: int = null in TestPackage.MandatoryValueArgs(x, 2)")(_ should runErrorAs("null value found"))
  test("let x = Nullable.Build(1b) in TestPackage.MandatoryValueArgs(x, 2)")(_ should evaluateTo("3"))

  // null arg 1 / null arg 2
  test("TestPackage.MandatoryValueArgs(Nullable.Build(1), 2)")(_ should evaluateTo("3"))
  test("TestPackage.MandatoryValueArgs(1 + null, 2)")(_ should runErrorAs("null value found"))
  test("TestPackage.MandatoryValueArgs(null, 2)")(_ should runErrorAs("null value found"))
  test("TestPackage.MandatoryValueArgs(1, Nullable.Build(2))")(_ should evaluateTo("3"))
  test("TestPackage.MandatoryValueArgs(1, 2 + null)")(_ should runErrorAs("null value found"))
  test("TestPackage.MandatoryValueArgs(1, null)")(_ should runErrorAs("null value found"))

  // try arg 1 / try arg 2
  test("""TestPackage.MandatoryValueArgs(1, 2 + Error.Build("argh!"))""")(_ should runErrorAs("argh!"))
  test("""TestPackage.MandatoryValueArgs(1 + Error.Build("argh!"), 2)""")(_ should runErrorAs("argh!"))
  test("""TestPackage.MandatoryValueArgs(1, Success.Build(2))""")(_ should evaluateTo("3"))
  test("""TestPackage.MandatoryValueArgs(Success.Build(1), 2)""")(_ should evaluateTo("3"))
  test("""TestPackage.MandatoryValueArgs(1, Error.Build("argh!"))""")(_ should runErrorAs("argh!"))
  test("""TestPackage.MandatoryValueArgs(Error.Build("argh!"), 2)""")(_ should runErrorAs("argh!"))

  test("TestPackage.OptionalValueArgs(14)")(_ should evaluateTo("14*10*10"))
  test("TestPackage.OptionalValueArgs(14, x=2)")(_ should evaluateTo("14*2*10"))
  test("TestPackage.OptionalValueArgs(14, y=3)")(_ should evaluateTo("14*10*3"))
  test("TestPackage.OptionalValueArgs(14, x=2, y=3)")(_ should evaluateTo("14*2*3"))
  test("TestPackage.OptionalValueArgs(14, x=null)")(_ should runErrorAs("null value found"))
  test("TestPackage.OptionalValueArgs(14, y=null)")(_ should runErrorAs("null value found"))
  test("TestPackage.OptionalValueArgs(14, x=null, y=null)")(_ should runErrorAs("null value found"))
  test("TestPackage.OptionalValueArgs(14, x=2, y=null)")(_ should runErrorAs("null value found"))
  test("TestPackage.OptionalValueArgs(14, x=null, y=3)")(_ should runErrorAs("null value found"))
  test("""TestPackage.OptionalValueArgs(14, x=Error.Build("argh!"))""")(_ should runErrorAs("argh!"))
  test("""TestPackage.OptionalValueArgs(14, y=Error.Build("argh!"))""")(_ should runErrorAs("argh!"))
  test("""TestPackage.OptionalValueArgs(14, x=Error.Build("argh!"), y=Error.Build("argh!"))""")(
    _ should runErrorAs("argh!")
  )
  test("""TestPackage.OptionalValueArgs(14, x=1, y=Error.Build("argh!"))""")(_ should runErrorAs("argh!"))
  test("""TestPackage.OptionalValueArgs(14, x=Error.Build("argh!"), y=3)""")(_ should runErrorAs("argh!"))

  test("""TestPackage.OptionalValueArgSugar(14, x=2)""")(_ should evaluateTo("14*2*10"))
  test("""TestPackage.OptionalValueArgSugar(14, x=(if true then 2 else null))""")(_ should evaluateTo("14*2*10"))
  test("""TestPackage.OptionalValueArgSugar(14, x=(if true then 2 else Error.Build("argh!")))""")(
    _ should evaluateTo("14*2*10")
  )

  test("TestPackage.VarValueArgs(1, 2, 3)")(_ should evaluateTo("6"))
  test("TestPackage.VarValueArgs(1, 2, null)")(_ should runErrorAs("null value found"))
  test("TestPackage.VarValueArgs(1, null, 3)")(_ should runErrorAs("null value found"))
  test("TestPackage.VarValueArgs(null, 2, 3)")(_ should runErrorAs("null value found"))
  test("TestPackage.VarValueArgs(if true then 1 else null, 2, 3)")(_ should evaluateTo("6"))
  test("""TestPackage.VarValueArgs(1, 2, Error.Build("argh!"))""")(_ should runErrorAs("argh!"))
  test("""TestPackage.VarValueArgs(1, Error.Build("argh!"), 3)""")(_ should runErrorAs("argh!"))
  test("""TestPackage.VarValueArgs(Error.Build("argh!"), 2, 3)""")(_ should runErrorAs("argh!"))

  test("TestPackage.VarValueArgSugar(3)")(_ should evaluateTo("6"))
  test("TestPackage.VarValueArgSugar(if true then 3 else null)")(_ should evaluateTo("6"))
  test("TestPackage.VarValueArgSugar(if true then 3 else Error.Build(\"argh!\"))")(_ should evaluateTo("6"))

  test("""TestPackage.VarValueNullableStringArgs("a", "b", "c")""")(_ should evaluateTo(""" "abc" """))
  test("""TestPackage.VarValueNullableStringArgs("a", "b", null)""")(_ should evaluateTo(""" "ab" """))
  test("""TestPackage.VarValueNullableStringArgs("a", null, "c")""")(_ should evaluateTo(""" "ac" """))
  test("""TestPackage.VarValueNullableStringArgs(null, "b", "c")""")(_ should evaluateTo(""" "bc" """))
  test("""TestPackage.VarValueNullableStringArgs("a", "b", Error.Build("argh!"))""")(_ should runErrorAs("argh!"))
  test("""TestPackage.VarValueNullableStringArgs("a", Error.Build("argh!"), "c")""")(_ should runErrorAs("argh!"))
  test("""TestPackage.VarValueNullableStringArgs(Error.Build("argh!"), "b", "c")""")(_ should runErrorAs("argh!"))

  test("""let n: string = null in List.First(["a", "b", n])""")(_ should evaluateTo("\"a\""))
  test("""let n: string = null in TestPackage.VarValueNullableStringArgs("a", "b", n)""")(_ should evaluateTo("\"ab\""))
  test("""let n: string = null in TestPackage.VarExpNullableStringArgs("a", "b", n)""")(_ should evaluateTo("\"a\""))
}
