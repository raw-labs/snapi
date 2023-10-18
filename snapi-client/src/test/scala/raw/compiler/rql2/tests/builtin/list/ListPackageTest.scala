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

package raw.compiler.rql2.tests.builtin.list

import raw.compiler.SnapiInterpolator
import raw.compiler.rql2.errors.ItemsNotComparable
import raw.compiler.rql2.tests.CompilerTestContext

import java.nio.file.Path

trait ListPackageTest extends CompilerTestContext {

  test("""["Hello", Error.Build("Argh!!"), null]""") {
    _ should evaluateTo("""["Hello", Error.Build("Argh!!"), null]""")
  }

  test("""List.Build(1,2,3)""")(it => it should typeAs("list(int)"))

  test("""List.Build(1,"hello")""")(it => it should typeErrorAs("expected compatible with int but got string"))

  test("""List.Build(
    |  List.Build(1,2,3),
    |  List.Build(),
    |  List.Build(4,5,6)
    |)
    |""".stripMargin) { it =>
    it should typeAs("list(list(int))")
    it should run
  }

  test("""List.Build(
    |  List.Build(1,2,3),
    |  List.Build(),
    |  List.Build(4f,5f,6f)
    |)
    |""".stripMargin) { it =>
    it should typeAs("list(list(float))")
    it should run
  }

  test("""List.Build(1, 2, 3.14d)""")(_ should (typeAs("list(double)") and run))

  test("""List.Get(List.Build(1,2,3), 0)""")(it => it should evaluateTo("1"))
  test("""List.Get(List.Build(1,2,3), 1)""")(it => it should evaluateTo("2"))
  test("""List.Get(List.Build(1,2,3), 2)""")(it => it should evaluateTo("3"))
  test("""List.Get(List.Build(1,2,3), 3)""")(it => it should runErrorAs("index out of bounds"))
  test("""List.Get(List.Build(1,2,3), -2)""")(it => it should runErrorAs("index out of bounds"))

  test("""
    |let numbers = List.Build(4,3,2,1,0),
    |    divs = List.Transform(numbers, x -> 24 / x)
    |in List.Get(divs, 2)""".stripMargin)(_ should evaluateTo("12"))

  test("""
    |let numbers = List.Build(4,3,2,1,0),
    |    divs = List.Transform(numbers, x -> 24 / x)
    |in List.Get(divs, 10)""".stripMargin)(_ should runErrorAs("index out of bounds"))

  test("""
    |let numbers = List.Build(4,3,2,1,0),
    |    divs = List.Transform(numbers, x -> 24 / x)
    |in List.Get(divs, 4)""".stripMargin)(_ should runErrorAs("/ by zero"))

  test("List.Filter(List.Build(1,2,3), s -> s > 1)") { it =>
    it should typeAs("list(int)")
    it should evaluateTo("List.Build(2,3)")
  }

  test("List.Filter(List.Build(1,2,3), s -> s)")(it =>
    it should typeErrorAs("expected (int) -> bool but got (int) -> int")
  )

  test("List.Filter(List.Build(1,2,3), (s) -> s > 1)")(it => it should typeAs("list(int)"))

  test("""List.Filter(List.Build(1,2,3), (s: bool) -> s)""")(it =>
    it should typeErrorAs("expected (int) -> bool but got (bool) -> bool")
  )

  test("""
    |let a = List.Build(1,2,3),
    |    b = List.Filter(a, s -> s > 1)
    |in
    |    List.Count(b)""".stripMargin) { it =>
    it should typeAs("long")
    it should evaluateTo("2L")
  }

  test("""
    |let a = List.Build(1,2,3),
    |    b = List.Filter(a, s -> s < 0)
    |in
    |    List.Count(b)""".stripMargin) { it =>
    it should typeAs("long")
    it should evaluateTo("0L")
  }

  test("""List.Transform(
    |  List.Build(1,2,3),
    |  v -> v * 10
    |)""".stripMargin)(_ should evaluateTo("""List.Build(10, 20, 30)"""))

  // Min

  test("""
    |List.Min(List.Build(4,2,7,3,1,5))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("""
    |List.Min(List.Build(4,2,null,7,3))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("2")
  }

  test("""
    |let empty = List.Filter(List.Build(1), x -> x == 0)
    |in List.Min(empty)""".stripMargin) { it =>
    it should typeAs("int")
    it should run
  }

  test("""let
    |    data = List.Build("1", "2", "3", "4", "5", "6")
    |in
    |    List.Filter(data, n -> n == "4")""".stripMargin)(it => it should evaluateTo("""List.Build("4") """))
  // Max

  test("""
    |List.Max(List.Build(4,2,7,3,1,5))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("7")
  }

  test("""
    |List.Max(List.Build(4,2,7,null,3))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("7")
  }

  test("""
    |let empty = List.Filter(List.Build(1), x -> x == 0)
    |in List.Max(empty)""".stripMargin) { it =>
    it should typeAs("int")
    it should run
  }

  // First/FindFirst

  test("""
    |List.First(List.Empty(type int))""".stripMargin)(it => it should evaluateTo("null"))

  test("""
    |List.First(List.Build(1,2,3,4,5))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("""
    |List.First(List.Build(1,2,3,null))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("""
    |List.First(List.Filter(List.Build(1), x -> x == 0))""".stripMargin) { it =>
    it should typeAs("int")
    it should run
    it should evaluateTo("null")
  }

  test("""
    |List.FindFirst([1], x -> x == 0)""".stripMargin) { it =>
    it should typeAs("int")
    it should run
    it should evaluateTo("null")
  }

  test("""
    |List.FindFirst([1,2,3], x -> x >= 2)""".stripMargin) { it =>
    it should typeAs("int")
    it should run
    it should evaluateTo("2")
  }

  // Last/FindLast

  test("""
    |List.Last(List.Build(1,2,3,4,5))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("5")
  }

  test("""
    |List.Last(List.Build(1,2,null,3))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("3")
  }

  test("""
    |List.Last(List.Filter(List.Build(1), x -> x == 0))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("null")
  }

  test("""
    |List.FindLast([1], x -> x == 0)""".stripMargin) { it =>
    it should typeAs("int")
    it should run
    it should evaluateTo("null")
  }

  test("""
    |List.FindLast([1,2,3], x -> x <= 2)""".stripMargin) { it =>
    it should typeAs("int")
    it should run
    it should evaluateTo("2")
  }

  // Take

  test("""
    |List.Take(List.Build(1,2,3,4,5), 3L)""".stripMargin) { it =>
    it should typeAs("list(int)")
    it should evaluateTo("List.Build(1,2,3)")
  }

  test("""
    |List.Take(List.Build(1,2,3,4,5), 30L)""".stripMargin) { it =>
    it should typeAs("list(int)")
    it should evaluateTo("List.Build(1,2,3,4,5)")
  }

  test("""
    |List.Take(List.Build(1,2,3,4,5), 0L)""".stripMargin) { it =>
    it should typeAs("list(int)")
    it should evaluateTo("List.Build()")
  }

  test("""
    |List.Take(List.Build(1,2,3,4,5), 10)""".stripMargin) { it =>
    it should typeAs("list(int)")
    it should evaluateTo("List.Build(1,2,3,4,5)")
  }

  // Unnest

  test("""
    |let x = List.Build(
    |   Record.Build(a = 1, b = List.Build(2,3)),
    |   Record.Build(a = 4, b = List.Build(5,6)),
    |   Record.Build(a = 7, b = List.Build(8,9))
    |)
    |in List.Unnest(x, i -> List.Transform(i.b, v -> Record.Build(a = i.a, b = v)))""".stripMargin) { it =>
    it should typeAs("list(record(a: int, b: int))")
    it should run
    it should evaluateTo("""List.Build(
      |   Record.Build(a = 1, b = 2),
      |   Record.Build(a = 1, b = 3),
      |   Record.Build(a = 4, b = 5),
      |   Record.Build(a = 4, b = 6),
      |   Record.Build(a = 7, b = 8),
      |   Record.Build(a = 7, b = 9)
      |)""".stripMargin)
  }

  test("""
    |let x = List.Build(
    |   Record.Build(a = 1, b = List.Build(2,3)),
    |   Record.Build(a = 4, b = List.Filter(List.Build(1), x -> x == 0)),
    |   Record.Build(a = 7, b = List.Build(8,9))
    |)
    |in List.Unnest(x, i -> List.Transform(i.b, v -> Record.Build(a = i.a, b = v)))""".stripMargin) { it =>
    it should typeAs("list(record(a: int, b: int))")
    it should run
    it should evaluateTo("""List.Build(
      |   Record.Build(a = 1, b = 2),
      |   Record.Build(a = 1, b = 3),
      |   Record.Build(a = 7, b = 8),
      |   Record.Build(a = 7, b = 9)
      |)""".stripMargin)
  }

  test("""
    |let x = List.Build(
    |   Record.Build(a = 1, b = List.Build(2,3)),
    |   Record.Build(a = 4, b = List.Build(5,6))
    |)
    |in List.Unnest(x, i -> List.Transform(i.b, v -> Record.Build(a = i.a, b = v)))""".stripMargin) { it =>
    it should typeAs("list(record(a: int, b: int))")
    it should run
    it should evaluateTo("""List.Build(
      |   Record.Build(a = 1, b = 2),
      |   Record.Build(a = 1, b = 3),
      |   Record.Build(a = 4, b = 5),
      |   Record.Build(a = 4, b = 6)
      |)""".stripMargin)
  }

  test("""
    |let x = List.Build(
    |   Record.Build(a = 4, b = List.Build(5,6)),
    |   Record.Build(a = 7, b = List.Build(8,9))
    |)
    |in List.Unnest(x, i -> List.Transform(i.b, v -> Record.Build(a = i.a, b = v)))""".stripMargin) { it =>
    it should typeAs("list(record(a: int, b: int))")
    it should run
    it should evaluateTo("""List.Build(
      |   Record.Build(a = 4, b = 5),
      |   Record.Build(a = 4, b = 6),
      |   Record.Build(a = 7, b = 8),
      |   Record.Build(a = 7, b = 9)
      |)""".stripMargin)
  }

  // Sum

  test("""
    |List.Sum(List.Build(4,2,7,3,1,5))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("22")
  }

  test("""
    |List.Sum(List.Build(4.0f,2.0f,7.0f,3.0f,1.0f,5.0f))""".stripMargin) { it =>
    it should typeAs("float")
    it should evaluateTo("22f")
  }

  test("""
    |List.Sum(List.Build(4.0d,2.0d,7.0d,3.0d,1.0d,5.0d))""".stripMargin) { it =>
    it should typeAs("double")
    it should evaluateTo("22d")
  }

  test(
    """
      |List.Sum(List.Build(4.0d, 2.0d, null))""".stripMargin
  ) { it =>
    it should typeAs("double")
    it should evaluateTo("6d")
  }

  ignore("""
    |List.Sum(List.Build())""".stripMargin) { it =>
    // what to do with sum(empty), long, int, etc.
    it should typeAs("undefined")
    it should evaluateTo("0")
  }

  // empty

  test("""List.Empty(type int)""")(_ should (typeAs("list(int)") and run))

  test("""List.Transform(
    |  List.Empty(type int),
    |  v -> v * 10.0f
    |)""".stripMargin)(_ should evaluateTo("List.Empty(type float)"))

  test("""let f(l: list(int)) = List.Transform(l, x -> x * 1.2f)
    |in f(List.Build())""".stripMargin)(_ should evaluateTo("List.Empty(type float)"))

  // filter support for null/error

  test("""let c = List.Build(true, true, null, false)
    |in List.Filter(c, s -> s)""".stripMargin) { it =>
    it should typeAs("list(bool)")
    it should evaluateTo("List.Build(true, true)")
  }

  test("""let c = List.Build(true, true, null, false)
    |in List.Filter(c, s -> not s)""".stripMargin) { it =>
    it should typeAs("list(bool)")
    // not null evaluates to null, therefore the predicate is false
    it should evaluateTo("List.Build(false)")
  }

  test("""let c = List.Build(true, true, Error.Build("failure"), false)
    |in List.Filter(c, s -> s)""".stripMargin) { it =>
    it should typeAs("list(bool)")
    it should evaluateTo("List.Build(true, true)")
  }

  test("""let c = List.Build(true, true, Error.Build("failure"), false)
    |in List.Filter(c, s -> not s)""".stripMargin) { it =>
    it should typeAs("list(bool)")
    it should evaluateTo("""List.Build(false)""")
  }

  test("""let c = List.Build(true, true, Error.Build("failure"), false, null)
    |in List.Filter(c, s -> s)""".stripMargin) { it =>
    it should typeAs("list(bool)")
    it should evaluateTo("List.Build(true, true)")
  }

  test("""let c = List.Build(true, true, Error.Build("failure"), false, null)
    |in List.Filter(c, s -> not s)""".stripMargin) { it =>
    it should typeAs("list(bool)")
    it should evaluateTo("""List.Build(false)""")
  }

  test("""let c = List.Build(true, true, Success.Build(true), false, null)
    |in List.Filter(c, s -> s)""".stripMargin) { it =>
    it should typeAs("list(bool)")
    it should evaluateTo("""List.Build(true, true, Success.Build(true))""")
  }

  test("""let c = List.Build(true, true, Success.Build(true), false, null)
    |in List.Filter(c, s -> not s)""".stripMargin) { it =>
    it should typeAs("list(bool)")
    it should evaluateTo("""List.Build(false)""")
  }

  test("List.From(Collection.Build(1,2,3,4,5))")(
    _ should (typeAs("list(int)") and evaluateTo("List.Build(1,2,3,4,5)"))
  )

  test("""List.From(Csv.Read("file:/not/found", type collection(record(a: int, b: int))))""")(
    _ should (typeAs("list(record(a: int, b: int))") and runErrorAs("path not found"))
  )

  test("""{
    |   not_bug: List.From(Collection.Build(1,2,3,4,5)),
    |   bug: List.From(Csv.Read("file:/not/found", type collection(record(a: int, b: int))))
    |}""".stripMargin)(
    _ should (typeAs("record(not_bug: list(int), bug: list(record(a: int, b: int)))") and evaluateTo("""
      |{not_bug: [1,2,3,4,5], bug: Error.Build("file system error: path not found: /not/found") }""".stripMargin))
  )

  test("""List.Build(Record.Build(a=1, b=2), Record.Build(a=1, b=null))""")(_ should run)
  test("""List.Build(Record.Build(a=1, b=2), Record.Build(a=1, b=1+null))""")(_ should run)
  test("""List.Build(Record.Build(a=1, b=2), Record.Build(a=1, b=1.4+null))""")(_ should run)
  test("""List.Build(Record.Build(a=1, b=2.4), Record.Build(a=1, b=1+null))""")(_ should run)
  test("""List.Build(Record.Build(a=1, b=null), Record.Build(a=1, b=2))""")(_ should run)
  test("""List.Build(List.Build(1,2), List.Build(null))""")(_ should run)
  test("""List.Build(List.Build(null), List.Build(1,2))""")(_ should run)

  test("""let numbers = [14, 65, 23]
    |in List.Avg(numbers)""".stripMargin)(_ should evaluateTo("Decimal.From(34)"))

  test("""let numbers = [14f, 65f, 23f]
    |in List.Avg(numbers)""".stripMargin)(_ should evaluateTo("Decimal.From(34)"))

  test("""let numbers = [14d, 65d, 23d]
    |in List.Avg(numbers)""".stripMargin)(_ should evaluateTo("Decimal.From(34)"))

  test("""let numbers = [14b, 65b, 23b]
    |in List.Avg(numbers)""".stripMargin)(_ should evaluateTo("Decimal.From(34)"))

  test("""let
    |  students = List.Build(
    |    {name: "john", age: 18},
    |    {name: "jane", age: 21},
    |    {name: "bob", age: 19}
    |  )
    |in students.name
    |""".stripMargin) { it =>
    it should typeAs("list(string)")
    it should evaluateTo("""["john", "jane", "bob"]""".stripMargin)
  }

  test("""let
    |  students = List.Build(
    |    {name: "john", age: 18},
    |    {name: "jane", age: 21},
    |    {name: "bob", age: 19}
    |  )
    |in List.Max(students.age)
    |""".stripMargin)(it => it should evaluateTo("""21""".stripMargin))

  test("""let
    |  students = List.Build(1, 2, 3)
    |in students.name
    |""".stripMargin) { it =>
    it should typeErrorAs("expected package, record, collection or list with field name but got list(int)".stripMargin)
  }

  val people: Path = tempFile("""name, age, occupation, salary
    |john, 18, marketing, 12
    |jane, 21, engineering, 13.7
    |bob, 19, sales, 14.2""".stripMargin)

  test(snapi"""let people = List.From(Csv.InferAndRead("$people"))
    |in people.name""".stripMargin)(it => it should evaluateTo("""["john", "jane", "bob"]""".stripMargin))

  test(snapi"""let people = List.From(Csv.InferAndRead("$people"))
    |in List.Max(people.salary)""".stripMargin)(it => it should evaluateTo("""14.2""".stripMargin))

  test(snapi"""let people = Try.Transform(List.From(Csv.InferAndRead("$people")), x -> x)
    |in people.name""".stripMargin)(it => it should evaluateTo("""["john", "jane", "bob"]""".stripMargin))

  test(
    """let people =  List.From(Csv.Read("file:/not/found", type collection(record(name: string, age: int, occupation: string, salary: int)), skip = 1))
      |in {salary: people.salary, occupation: people.occupation}""".stripMargin
  ) { it =>
    it should typeAs("record(salary: list(int), occupation: list(string))")
    it should evaluateTo("""{
      |salary: Error.Build("file system error: path not found: /not/found"),
      |occupation: Error.Build("file system error: path not found: /not/found")
      |}""".stripMargin)
  }

  // Exists
  test("List.Exists(List.Build(1,2,3,4), x -> x == 3)")(_ should evaluateTo("true"))
  test("List.Exists(List.Build(1,2,3,4), x -> x < 30)")(_ should evaluateTo("true"))
  test("List.Exists(List.Build(1,2,3,4), x -> x > 30)")(_ should evaluateTo("false"))
  test("List.Exists(List.Build(1.0d,2.0d,3.0d,4.0d), x -> x == 3)")(_ should evaluateTo("true"))
  test("List.Exists(List.Build(1.0d,2.0d,3.0d,4.0d), x -> x + 10)")(
    _ should runErrorAs("expected (double) -> bool but got")
  )
  test("List.Exists(List.Build(1,2,3), [1,2,3])")(_ should runErrorAs("expected (int) -> bool but got"))

  // Contains
  test("List.Contains(List.Build(1,2,3,4), 3)")(_ should evaluateTo("true"))
  test("List.Contains(List.Build(1,2,3,4), 30)")(_ should evaluateTo("false"))
  test("List.Contains(List.Build(1.0d,2.0d,3.0d,4.0d), 3)")(_ should evaluateTo("true"))
  test("List.Contains(List.Build(1,2,3), [1,2,3])")(_ should runErrorAs("expected int but got"))
  // Functions aren't comparable
  test("""let l = List.Build((x: int) -> x + 1, (x: int) -> x * 2)
    |in List.Contains(l, (x: int) -> x + 1)""".stripMargin)(_ should runErrorAs(ItemsNotComparable.message))

  // Zip
  test("""List.Zip(List.Build(1,2,3), List.Build("a", "b", "c"))""")(
    _ should evaluateTo("""List.Build({1, "a"}, {2, "b"}, {3, "c"})""")
  )

  test("""List.Zip(List.Build(1,2,3), List.Build("a", "b"))""")(
    _ should evaluateTo("""List.Build({1, "a"}, {2, "b"})""")
  )

  test("""List.Zip(List.Build(1,2), List.Build("a", "b", "c"))""")(
    _ should evaluateTo("""List.Build({1, "a"}, {2, "b"})""")
  )

  test("""List.Zip(List.Build(1,2), List.Build())""")(_ should evaluateTo("""List.Build()"""))

  test("""List.Zip(List.Build(1,2), 14)""")(_ should runErrorAs("""expected list but got"""))
  test("""List.Zip(14, List.Build(1,2))""")(_ should runErrorAs("""expected list but got"""))

}
