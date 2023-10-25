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

trait LetFunTest extends CompilerTestContext {

  test("""let f(x: int) = x + 1
    |in f(0)
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("""let f(x: int): int = x + 1
    |in f(0)
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("""let f(x: int): float = x + 1
    |in f(0)
    |""".stripMargin) { it =>
    it should typeAs("float")
    it should evaluateTo("1f")
  }

  test("""let f(x: int): int = x + 1
    |in f
    |""".stripMargin)(it => it should typeAs("(int) -> int"))

  test("""
    |let f(v: float) = v * 2
    |in f(1)
    |""".stripMargin) { it =>
    it should typeAs("float")
    it should evaluateTo("2f")
  }

  test("""let f(a: float) = a * 2
    |in f(1l)
    |""".stripMargin) { it =>
    it should typeAs("float")
    it should evaluateTo("2f")
  }

  // Optional arguments

  test("""let f(x: int = 1): int = x + 1
    |in f()
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("2")
  }

  test("""let f(x: int = 1): int = x + 1
    |in f(2)
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("3")
  }

  test("""let f(x: int = 1): int = x + 1
    |in f(x = 3)
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("4")
  }

  test("""let f(x: int, y: int = 1): int = x + y
    |in f(1)
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("2")
  }

  test("""let f(x: int, y: int = 1): int = x + y
    |in f(1, 2)
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("3")
  }

  test("""let f(x: int, y: int = 1): int = x + y
    |in f(1, y = 3)
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("4")
  }

  test("""let f(x: int, y: int = 1, z: int = 2): int = x + y + z
    |in f(1)
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("4")
  }

  test("""let f(x: int, y: int = 1, z: int = 2): int = x + y + z
    |in f(1,  2)
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("5")
  }

  test("""let f(x: int, y: int = 1, z: int = 2): int = x + y + z
    |in f(1, y = 2)
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("5")
  }

  test("""let f(x: int, y: int = 1, z: int = 2): int = x + y + z
    |in f(1, z = 1)
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("3")
  }

  test("""let f(x: int, y: int = 1, z: int = 2): int = x + y + z
    |in f(1, 2, 3)
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("6")
  }

  test("""let f(x: int, y: int = 1, z: int = 2): int = x + y + z
    |in f(1, y = 2, z = 3)
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("6")
  }

  test(
    """let f(x: int, y: int = 1, z: int = 2): int = (x + y) * z
      |in f(1, z = 3, y = 2)
      |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("9")
  }

  test("""let f(x: int, y: int = 1, z: int = 2): int = x + y + z
    |in f(1, y = 2, 3)
    |""".stripMargin)(it => it should typeErrorAs("mandatory arguments must be before optional arguments"))

  test("""let f(x: int, y: int = 1, z: int = 2): int = x + y + z,
    |    y = Record.Build(function=f)
    |in
    |    y.function.f(1)
    |""".stripMargin)(it => it should typeErrorAs("expected package, record, collection or list with field f"))

  test("""let f(x: int, y: int = 1, z: int = 2): int = x + y + z,
    |    y = Record.Build(function=f)
    |in
    |    y.function(1)
    |""".stripMargin)(it => it should typeAs("int"))

  test("""let f(x: int, y: int = 1, z: int = 2): int = x + y + z,
    |    y = Record.Build(function=f)
    |in
    |    y.function(1, y = 1)
    |""".stripMargin)(it => it should typeAs("int"))

  test("""let f(x: int, y: int = 1, z: int = 2): int = x + y + z,
    |    y = Record.Build(function=f)
    |in
    |    y.function(1, 2, 3)
    |""".stripMargin)(it => it should evaluateTo("6"))

  test("""let f(x: int, y: int = 1, z: int = 2): int = x + y + z,
    |    y = Record.Build(function=f)
    |in
    |    y.function(1, y = 2, z = 3)
    |""".stripMargin)(it => it should evaluateTo("6"))

  test("""let f(x: int, y: int = 1, z: int = 2): int = x * y + z,
    |    y = Record.Build(function=f)
    |in
    |    y.function(1, z = 3, y = 2)
    |""".stripMargin)(it => it should evaluateTo("5"))

  test("""let f(x: int, y: int = 1, z: int = 2): int = x * y + z,
    |    y = Record.Build(function=f)
    |in
    |    y.function(1, y = 3, z = 2)
    |""".stripMargin)(it => it should evaluateTo("5"))

  test("""let f(x: bool, y: string = "hello", z: int = 2) = if (x) then y else String.From(z)
    |in f(true)
    |""".stripMargin)(it => it should evaluateTo(""" "hello" """))

  test("""let f(x: bool, y: string = "hello", z: int = 2) = if (x) then y else String.From(z)
    |in f()
    |""".stripMargin)(it => it should typeErrorAs("missing mandatory arguments"))

  test("""let f(x: bool, y: string = "hello", z: int = 2) = if (x) then y else String.From(z)
    |in f(false)
    |""".stripMargin)(it => it should evaluateTo(""" "2" """))

  test("""let f(x: bool, y: string = "hello", z: int = 2) = if (x) then y else String.From(z)
    |in f(true, y = "bye")
    |""".stripMargin)(it => it should evaluateTo(""" "bye" """))

  test("""let f(x: bool, y: string = "hello", z: int = 2) = if (x) then y else String.From(z)
    |in f(false, y = "bye")
    |""".stripMargin)(it => it should evaluateTo(""" "2" """))

  test("""let f(x: bool, y: string = "hello", z: int = 2) = if (x) then y else String.From(z)
    |in f(true, z = 3)
    |""".stripMargin)(it => it should evaluateTo(""" "hello" """))

  test("""let f(x: bool, y: string = "hello", z: int = 2) = if (x) then y else String.From(z)
    |in f(false, z = 3)
    |""".stripMargin)(it => it should evaluateTo(""" "3" """))

  test("""let f(x: bool, y: string = "hello", z: int = 2) = if (x) then y else String.From(z)
    |in f(true, z = 3, y = "bye")
    |""".stripMargin)(it => it should evaluateTo(""" "bye" """))

  test("""let f(x: bool, y: string = "hello", z: int = 2) = if (x) then y else String.From(z)
    |in f(false, z = 3, y = "bye")
    |""".stripMargin)(it => it should evaluateTo(""" "3" """))

  test("""let f(y: string = "hello", z: int = 2) = y + String.From(z)
    |in f( z = 3, y = "bye")
    |""".stripMargin)(it => it should evaluateTo(""" "bye3" """))

  test("""let f(y: string = "hello") = y
    |in f("bye", y = "hello")
    |""".stripMargin)(it => it should typeErrorAs("repeated optional arguments"))

  test("""let f(x: bool, y: string = "hello", z: int) = if (x) then y else String.From(z)
    |in f(false)
    |""".stripMargin)(it => it should typeErrorAs("""mandatory parameters must be before optional parameters"""))

  // functions which default parameter value is dynamic
  test("""let numbers = [1,2,3,4],
    |    functions = List.Transform(numbers, n -> let f(x: int, v: int = n) = x * v in f)
    |in List.Transform(functions, f -> f(10))
    |""".stripMargin) { it =>
    assume(language == "rql2-truffle") // The scala executor fails to turn this code to L0
    it should evaluateTo("[10, 20, 30, 40]")
  }

}
