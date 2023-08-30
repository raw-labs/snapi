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

trait ImplicitCastTest extends CompilerTestContext {

  // compatible options as function param
  test(s"""let f = (x: int) -> Record.Build(o = x)
    |   in f(1b)""".stripMargin)(_ should returnA("record(o: int)"))

  test(s"""let f = (x: int) -> Record.Build(o = x)
    |   in f(1b + null)""".stripMargin)(_ should returnA("record(o: int)"))

  // compatible options in a collection
  test("""let f: float = 1.0f,
    |    i: int = 2
    |in Collection.Build(f, i)""".stripMargin)(_ should returnA("collection(float)"))

  // compatible options in a list
  test("""let f: float = 1.0f,
    |    i: int = 2
    |in List.Build(f, i)""".stripMargin)(_ should returnA("list(float)"))

  test("""let f = (l: collection(double)) -> Collection.Filter(l, x -> x > 2.0d)
       in f(Collection.Build(1,2,3))""") { it =>
    it should typeAs("collection(double)")
    it should evaluateTo("Collection.Build(3d)")
  }

  test("""Collection.Build(
    |  Collection.Build(1.0f + null, 2),
    |  Collection.Build(3,4)
    |)""".stripMargin) { it =>
    it should typeAs("collection(collection(float))")
    it should evaluateTo("""Collection.Build(
      |  Collection.Build(null, 2.0f),
      |  Collection.Build(3.0f, 4.0f)
      |)""".stripMargin)
  }

  // compatible lists as function param
  test("""let f = (l: list(double)) -> l
       in f(List.Build(1,2,3))""") { it =>
    it should typeAs("list(double)")
    it should evaluateTo("List.Build(1d, 2d, 3d)")
  }

  test("""List.Build(
    |  List.Build(1.0f + null, 2),
    |  List.Build(3,4)
    |)""".stripMargin) { it =>
    it should typeAs("list(list(float))")
    it should evaluateTo("""List.Build(
      |  List.Build(null, 2.0f),
      |  List.Build(3.0f, 4.0f)
      |)""".stripMargin)
  }

  test("""Collection.Build(
    |  List.Build(1.0f + null, 2),
    |  List.Build(3,4)
    |)""".stripMargin) { it =>
    it should typeAs("collection(list(float))")
    it should evaluateTo("""Collection.Build(
      |  List.Build(null, 2.0f),
      |  List.Build(3.0f, 4.0f)
      |)""".stripMargin)
  }

  // compatible records as function param
  test("""let f = (r: record(a: double, b: int)) -> r.a + r.b
       in f(Record.Build(a = 12, b = 1b))""") { it =>
    it should typeAs("double")
    it should evaluateTo("13d")
  }

  // compatible records as list items
  test("""Collection.Build(
    |   Record.Build(a = 21f, b = 14 + null),
    |   Record.Build(a = 14b, b = 6s)
    |)""".stripMargin)(it => it should returnA("collection(record(a: float, b: int))"))

  private def returnA(t: String) = typeAs(t) and run

  test(
    """let numbers: list(int) = [1,2,3,4,5]
      |in List.Get(numbers, 0) + List.Get(numbers, 1) + List.Get(numbers, 2) + List.Get(numbers, 3) + List.Get(numbers, 4)""".stripMargin
  )(
    _ should evaluateTo("15")
  )

}
