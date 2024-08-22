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

package com.rawlabs.snapi.compiler.tests.builtin.collection

import com.rawlabs.snapi.frontend.utils._
import com.rawlabs.snapi.frontend.rql2.errors.ItemsNotComparable
import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext

import java.nio.file.Path

class CollectionPackageTest extends Rql2TruffleCompilerTestContext {

  // a test to check if there are multiple instances of generators
  test("""let a = Collection.Build(1,2,3),
    | z = Collection.Build(Collection.Count(a)),
    | b = Collection.Filter(a, s -> s == 1),
    | c = Collection.Filter(a, s -> s == 2)
    | in Collection.Build(b,c,z)""".stripMargin) { it =>
    it should typeAs("collection(collection(long))")
    it should evaluateTo("Collection.Build(Collection.Build(1L), Collection.Build(2L), Collection.Build(3L))")
  }

  test("""Collection.Build(1,2,3)""")(it => it should typeAs("collection(int)"))

  test("""Collection.Build(1,"hello")""")(it => it should typeErrorAs("expected compatible with int but got string"))

  test("""Collection.Build(
    |  Collection.Build(1,2,3),
    |  Collection.Build(),
    |  Collection.Build(4,5,6)
    |)
    |""".stripMargin) { it =>
    it should typeAs("collection(collection(int))")
    it should run
  }

  test("""Collection.Build(
    |  Collection.Build(1,2,3),
    |  Collection.Build(),
    |  Collection.Build(4f,5f,6f)
    |)
    |""".stripMargin) { it =>
    it should typeAs("collection(collection(float))")
    it should run
  }

  test("""Collection.Build(1, 2, 3.14d)""")(_ should (typeAs("collection(double)") and run))

  test("""let items = [1,2,3],
    |    collections = List.Transform(items, i -> Collection.Build(i))
    |in collections""".stripMargin) { it =>
    it should evaluateTo("Collection.Build(Collection.Build(1), Collection.Build(2), Collection.Build(3))")
  }

  test("Collection.Filter(Collection.Build(1,2,3), s -> s > 1)") { it =>
    it should typeAs("collection(int)")
    it should evaluateTo("Collection.Build(2,3)")
  }

  test("Collection.Filter(Collection.Build(1,2,3), s -> s)")(it =>
    it should typeErrorAs("expected (int) -> bool but got (int) -> int")
  )

  test("Collection.Filter(Collection.Build(1,2,3), (s) -> s > 1)")(it => it should typeAs("collection(int)"))

  test("""Collection.Filter(Collection.Build(1,2,3), (s: bool) -> s)""")(it =>
    it should typeErrorAs("expected (int) -> bool but got (bool) -> bool")
  )

  test("""
    |let a = Collection.Build(1,2,3),
    |    b = Collection.Filter(a, s -> s > 1)
    |in
    |    Collection.Count(b)""".stripMargin) { it =>
    it should typeAs("long")
    it should evaluateTo("2L")
  }

  test("""
    |let a = Collection.Build(1,2,3),
    |    b = Collection.Filter(a, s -> s < 0)
    |in
    |    Collection.Count(b)""".stripMargin) { it =>
    it should typeAs("long")
    it should evaluateTo("0L")
  }

  test("""Collection.Transform(
    |  Collection.Build(1,2,3),
    |  v -> v * 10
    |)""".stripMargin)(_ should evaluateTo("""Collection.Build(10, 20, 30)"""))

  // First/FindFirst

  test("""
    |Collection.First(Collection.Build(1,2,3,4,5))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("""
    |Collection.First(Collection.Build(1,2,3,null))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("""
    |Collection.First(Collection.Filter(Collection.Build(1), x -> x == 0))""".stripMargin) { it =>
    it should typeAs("int")
    it should run
    it should evaluateTo("null")
  }

  test("""
    |Collection.FindFirst(Collection.Build(1), x -> x == 0)""".stripMargin) { it =>
    it should typeAs("int")
    it should run
    it should evaluateTo("null")
  }

  test("""
    |Collection.FindFirst(Collection.Build(1,2,3), x -> x >= 2)""".stripMargin) { it =>
    it should typeAs("int")
    it should run
    it should evaluateTo("2")
  }

  // Last/FindLast

  test("""
    |Collection.Last(Collection.Build(1,2,3,4,5))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("5")
  }

  test("""
    |Collection.Last(Collection.Build(1,2,null,3))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("3")
  }

  test("""
    |Collection.Last(Collection.Filter(Collection.Build(1), x -> x == 0))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("null")
  }

  test("""
    |Collection.FindLast(Collection.Build(1), x -> x == 0)""".stripMargin) { it =>
    it should typeAs("int")
    it should run
    it should evaluateTo("null")
  }

  test("""
    |Collection.FindLast(Collection.Build(1,2,3), x -> x <= 2)""".stripMargin) { it =>
    it should typeAs("int")
    it should run
    it should evaluateTo("2")
  }

  // Take

  test("""
    |Collection.Take(Collection.Build(1,2,3,4,5), 3L)""".stripMargin) { it =>
    it should typeAs("collection(int)")
    it should evaluateTo("Collection.Build(1,2,3)")
  }

  test("""
    |Collection.Take(Collection.Build(1,2,3,4,5), 30L)""".stripMargin) { it =>
    it should typeAs("collection(int)")
    it should evaluateTo("Collection.Build(1,2,3,4,5)")
  }

  test("""
    |Collection.Take(Collection.Build(1,2,3,4,5), 0L)""".stripMargin) { it =>
    it should typeAs("collection(int)")
    it should evaluateTo("Collection.Build()")
  }

  test("""Collection.Take(Collection.Build(1,2,3,4,5), -10)""".stripMargin) { it =>
    it should typeAs("collection(int)")
    it should evaluateTo("Collection.Build()")
  }

  test("""Collection.Take(Collection.Build(1,2,3,4,5), 0)""".stripMargin) { it =>
    it should typeAs("collection(int)")
    it should evaluateTo("Collection.Build()")
  }

  // Unnest

  test("""
    |let x = Collection.Build(
    |   Record.Build(a = 1, b = Collection.Build(2,3)),
    |   Record.Build(a = 4, b = Collection.Build(5,6)),
    |   Record.Build(a = 7, b = Collection.Build(8,9))
    |)
    |in Collection.Unnest(x, i -> Collection.Transform(i.b, v -> Record.Build(a = i.a, b = v)))""".stripMargin) { it =>
    it should typeAs("collection(record(a: int, b: int))")
    it should run
    it should evaluateTo("""Collection.Build(
      |   Record.Build(a = 1, b = 2),
      |   Record.Build(a = 1, b = 3),
      |   Record.Build(a = 4, b = 5),
      |   Record.Build(a = 4, b = 6),
      |   Record.Build(a = 7, b = 8),
      |   Record.Build(a = 7, b = 9)
      |)""".stripMargin)
  }

  test("""
    |let x = Collection.Build(
    |   Record.Build(a = 1, b = Collection.Build(2,3)),
    |   Record.Build(a = 4, b = Collection.Filter(Collection.Build(1), x -> x == 0)),
    |   Record.Build(a = 7, b = Collection.Build(8,9))
    |)
    |in Collection.Unnest(x, i -> Collection.Transform(i.b, v -> Record.Build(a = i.a, b = v)))""".stripMargin) { it =>
    it should typeAs("collection(record(a: int, b: int))")
    it should run
    it should evaluateTo("""Collection.Build(
      |   Record.Build(a = 1, b = 2),
      |   Record.Build(a = 1, b = 3),
      |   Record.Build(a = 7, b = 8),
      |   Record.Build(a = 7, b = 9)
      |)""".stripMargin)
  }

  test("""
    |let x = Collection.Build(
    |   Record.Build(a = 1, b = Collection.Build(2,3)),
    |   Record.Build(a = 4, b = Collection.Build(5,6))
    |)
    |in Collection.Unnest(x, i -> Collection.Transform(i.b, v -> Record.Build(a = i.a, b = v)))""".stripMargin) { it =>
    it should typeAs("collection(record(a: int, b: int))")
    it should run
    it should evaluateTo("""Collection.Build(
      |   Record.Build(a = 1, b = 2),
      |   Record.Build(a = 1, b = 3),
      |   Record.Build(a = 4, b = 5),
      |   Record.Build(a = 4, b = 6)
      |)""".stripMargin)
  }

  test("""
    |let x = Collection.Build(
    |   Record.Build(a = 4, b = Collection.Build(5,6)),
    |   Record.Build(a = 7, b = Collection.Build(8,9))
    |)
    |in Collection.Unnest(x, i -> Collection.Transform(i.b, v -> Record.Build(a = i.a, b = v)))""".stripMargin) { it =>
    it should typeAs("collection(record(a: int, b: int))")
    it should run
    it should evaluateTo("""Collection.Build(
      |   Record.Build(a = 4, b = 5),
      |   Record.Build(a = 4, b = 6),
      |   Record.Build(a = 7, b = 8),
      |   Record.Build(a = 7, b = 9)
      |)""".stripMargin)
  }

  test("""
    |let bug: collection(int) = Error.Build("bug"),
    |   x = Collection.Build(
    |   Record.Build(a = 4, b = bug),
    |   Record.Build(a = 7, b = Collection.Build(8,9))
    |)
    |in Collection.Unnest(x, i -> Collection.Transform(i.b, v -> Record.Build(a = i.a, b = v)))""".stripMargin) { it =>
    it should typeAs("collection(record(a: int, b: int))")
    it should run
    it should evaluateTo("""Collection.Build(
      |   Record.Build(a = 7, b = 8),
      |   Record.Build(a = 7, b = 9)
      |)""".stripMargin)
  }

  test("""
    |let bug: collection(int) = null,
    |   x = Collection.Build(
    |   Record.Build(a = 4, b = bug),
    |   Record.Build(a = 7, b = Collection.Build(8,9))
    |)
    |in Collection.Unnest(x, i -> Collection.Transform(i.b, v -> Record.Build(a = i.a, b = v)))""".stripMargin) { it =>
    it should typeAs("collection(record(a: int, b: int))")
    it should run
    it should evaluateTo("""Collection.Build(
      |   Record.Build(a = 7, b = 8),
      |   Record.Build(a = 7, b = 9)
      |)""".stripMargin)
  }

  // Sum

  test("""Collection.Sum(Collection.Build(4,2,7,3,1,5))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("22")
  }

  test("""
    |Collection.Sum(Collection.Build(4.0f,2.0f,7.0f,3.0f,1.0f,5.0f))""".stripMargin) { it =>
    it should typeAs("float")
    it should evaluateTo("22f")
  }

  test("""
    |Collection.Sum(Collection.Build(4.0d,2.0d,7.0d,3.0d,1.0d,5.0d))""".stripMargin) { it =>
    it should typeAs("double")
    it should evaluateTo("22d")
  }

  test(
    """
      |Collection.Sum(Collection.Build(4.0d, 2.0d, null))""".stripMargin
  ) { it =>
    it should typeAs("double")
    it should evaluateTo("6d")
  }

  ignore("""
    |Collection.Sum(Collection.Build())""".stripMargin) { it =>
    // what to do with sum(empty), long, int, etc.
    it should typeAs("undefined")
    it should evaluateTo("0")
  }

  // empty

  test("""Collection.Empty(type int)""")(_ should (typeAs("collection(int)") and run))

  test("""Collection.Transform(
    |  Collection.Empty(type int),
    |  v -> v * 10.0f
    |)""".stripMargin)(_ should evaluateTo("Collection.Empty(type float)"))

  test("""let f(l: collection(int)) = Collection.Transform(l, x -> x * 1.2f)
    |in f(Collection.Build())""".stripMargin)(_ should evaluateTo("Collection.Empty(type float)"))

  // filter support for null/error

  test("""let c = Collection.Build(true, true, null, false)
    |in Collection.Filter(c, s -> s)""".stripMargin) { it =>
    it should typeAs("collection(bool)")
    it should evaluateTo("Collection.Build(true, true)")
  }

  test("""let c = Collection.Build(true, true, null, false)
    |in Collection.Filter(c, s -> not s)""".stripMargin) { it =>
    it should typeAs("collection(bool)")
    // not null evaluates to null, therefore the predicate is false
    it should evaluateTo("Collection.Build(false)")
  }

  test("""let c = Collection.Build(true, true, Error.Build("failure"), false)
    |in Collection.Filter(c, s -> s)""".stripMargin) { it =>
    it should typeAs("collection(bool)")
    it should evaluateTo("Collection.Build(true, true)")
  }

  test("""let c = Collection.Build(true, true, Error.Build("failure"), false)
    |in Collection.Filter(c, s -> not s)""".stripMargin) { it =>
    it should typeAs("collection(bool)")
    it should evaluateTo("""Collection.Build(false)""")
  }

  test("""let c = Collection.Build(true, true, Error.Build("failure"), false, null)
    |in Collection.Filter(c, s -> s)""".stripMargin) { it =>
    it should typeAs("collection(bool)")
    it should evaluateTo("Collection.Build(true, true)")
  }

  test("""let c = Collection.Build(true, true, Error.Build("failure"), false, null)
    |in Collection.Filter(c, s -> not s)""".stripMargin) { it =>
    it should typeAs("collection(bool)")
    it should evaluateTo("""Collection.Build(false)""")
  }

  test("""let c = Collection.Build(true, true, Success.Build(true), false, null)
    |in Collection.Filter(c, s -> s)""".stripMargin) { it =>
    it should typeAs("collection(bool)")
    it should evaluateTo("""Collection.Build(true, true, Success.Build(true))""")
  }

  test("""let c = Collection.Build(true, true, Success.Build(true), false, null)
    |in Collection.Filter(c, s -> not s)""".stripMargin) { it =>
    it should typeAs("collection(bool)")
    it should evaluateTo("""Collection.Build(false)""")
  }

  test("Collection.From(List.Build(1,2,3,4,5))")(
    _ should (typeAs("collection(int)") and evaluateTo("Collection.Build(1,2,3,4,5)"))
  )

  test("""Collection.Build(Record.Build(a=1, b=2), Record.Build(a=1, b=null))""")(_ should run)
  test("""Collection.Build(Record.Build(a=1, b=null), Record.Build(a=1, b=2))""")(_ should run)
  test("""Collection.Build(Collection.Build(1,2), Collection.Build(null))""")(_ should run)
  test("""Collection.Build(Collection.Build(null), Collection.Build(1,2))""")(_ should run)

  test("""let numbers = Collection.From([14, 65, 23])
    |in Collection.Avg(numbers)""".stripMargin)(_ should evaluateTo("Decimal.From(34)"))

  test("""let numbers = Collection.From([14f, 65f, 23f])
    |in Collection.Avg(numbers)""".stripMargin)(_ should evaluateTo("Decimal.From(34)"))

  test("""let numbers = Collection.From([14d, 65d, 23d])
    |in Collection.Avg(numbers)""".stripMargin)(_ should evaluateTo("Decimal.From(34)"))

  test("""let numbers = Collection.From([14b, 65b, 23b])
    |in Collection.Avg(numbers)""".stripMargin)(_ should evaluateTo("Decimal.From(34)"))

  test("""let
    |  students = Collection.Build(
    |    {name: "john", age: 18},
    |    {name: "jane", age: 21},
    |    {name: "bob", age: 19}
    |  )
    |in students.name
    |""".stripMargin) { it =>
    it should typeAs("collection(string)")
    it should evaluateTo("""["john", "jane", "bob"]""".stripMargin)
  }

  test("""let
    |  students = Collection.Build(1, 2, 3)
    |in students.name
    |""".stripMargin) { it =>
    it should typeErrorAs(
      "expected package, record, collection or list with field name but got collection(int)".stripMargin
    )
  }

  val people: Path = tempFile("""name, age, occupation, salary
    |john, 18, marketing, 12
    |jane, 21, engineering, 13.7
    |bob, 19, sales, 14.2""".stripMargin)

  test(snapi"""let people = Csv.InferAndRead("$people")
    |in people.name""".stripMargin)(it => it should evaluateTo("""["john", "jane", "bob"]""".stripMargin))

  test(snapi"""let people = Try.Transform(Csv.InferAndRead("$people"), x -> x)
    |in people.name""".stripMargin)(it => it should evaluateTo("""["john", "jane", "bob"]""".stripMargin))

  test(
    snapi"""let people =  Csv.Read("$people", type collection(record(name: string, age: int, occupation: string, salary: double)), skip = 1)
      |in Collection.Max(people.salary)""".stripMargin
  )(it => it should evaluateTo("""14.2""".stripMargin))

  test(
    snapi"""let people =  Csv.InferAndRead("$people") in Collection.Max(people.salary)""".stripMargin
  )(it => it should evaluateTo("""14.2""".stripMargin))

  // Exists
  test("Collection.Exists(Collection.Build(1,2,3,4), x -> x == 3)")(_ should evaluateTo("true"))
  test("Collection.Exists(Collection.Build(1,2,3,4), x -> x < 30)")(_ should evaluateTo("true"))
  test("Collection.Exists(Collection.Build(1,2,3,4), x -> x > 30)")(_ should evaluateTo("false"))
  test("Collection.Exists(Collection.Build(1.0d,2.0d,3.0d,4.0d), x -> x == 3)")(_ should evaluateTo("true"))
  test("Collection.Exists(Collection.Build(1.0d,2.0d,3.0d,4.0d), x -> x + 10)")(
    _ should runErrorAs("expected (double) -> bool but got")
  )
  test("Collection.Exists(Collection.Build(1,2,3), [1,2,3])")(_ should runErrorAs("expected (int) -> bool but got"))
  test("""Collection.Exists(Json.Read("file:/not/found", type collection(int)), x -> x == 3)""")(
    _ should runErrorAs("path not found")
  )

  // Contains
  test("Collection.Contains(Collection.Build(1,2,3,4), 3)")(_ should evaluateTo("true"))
  test("Collection.Contains(Collection.Build(1,2,3,4), 30)")(_ should evaluateTo("false"))
  test("Collection.Contains(Collection.Build(1.0d,2.0d,3.0d,4.0d), 3)")(_ should evaluateTo("true"))
  test("Collection.Contains(Collection.Build(1,2,3), [1,2,3])")(_ should runErrorAs("expected int but got"))
  test("""Collection.Contains(Json.Read("file:/not/found", type collection(int)), 3)""")(
    _ should runErrorAs("path not found")
  )
  // Functions aren't comparable
  test("""let l = Collection.Build((x: int) -> x + 1, (x: int) -> x * 2)
    |in Collection.Contains(l, (x: int) -> x + 1)""".stripMargin)(_ should runErrorAs(ItemsNotComparable.message))

  // Zip
  test("""Collection.Zip(Collection.Build(1,2,3), Collection.Build("a", "b", "c"))""")(
    _ should evaluateTo("""Collection.Build({1, "a"}, {2, "b"}, {3, "c"})""")
  )

  test("""Collection.Zip(Collection.Build(1,2,3), Collection.Build("a", "b"))""")(
    _ should evaluateTo("""Collection.Build({1, "a"}, {2, "b"})""")
  )

  test("""Collection.Zip(Collection.Build(1,2), Collection.Build("a", "b", "c"))""")(
    _ should evaluateTo("""Collection.Build({1, "a"}, {2, "b"})""")
  )

  test("""Collection.Zip(Collection.Build(1,2), Collection.Build())""")(_ should evaluateTo("""Collection.Build()"""))

  test("""Collection.Zip(Collection.Build(1,2), 14)""")(_ should runErrorAs("""expected collection but got"""))
  test("""Collection.Zip(14, Collection.Build(1,2))""")(_ should runErrorAs("""expected collection but got"""))

}
