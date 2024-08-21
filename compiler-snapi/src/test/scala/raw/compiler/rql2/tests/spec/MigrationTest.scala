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

import com.rawlabs.compiler.snapi.rql2.source.{Rql2IntType, Rql2IsNullableTypeProperty, Rql2IsTryableTypeProperty, Rql2UndefinedType}
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext

class MigrationTest extends Rql2TruffleCompilerTestContext {

  test("1") { it =>
    it should typeAs("int")
    it should astTypeAs(Rql2IntType())
    it should evaluateTo("1")
  }

  test("""let x: int = 1
    |  in x
    |""".stripMargin) { it =>
    // Implicit cast of Let should force '1' to go from 'int not null not try' to 'int'
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("""Error.Build("broken!")""") { it =>
    it should typeAs("undefined")
    it should astTypeAs(Rql2UndefinedType(Set(Rql2IsTryableTypeProperty())))
    it should runErrorAs("""broken!""")
  }

  test("Success.Build(1)") { it =>
    it should typeAs("int")
    it should astTypeAs(Rql2IntType(Set(Rql2IsTryableTypeProperty())))
    it should evaluateTo("1")
  }

  test("""let x: int = Error.Build("a broken number")
    |  in x
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should runErrorAs("""a broken number""")
  }

  test("""let x: int = Success.Build(1)
    |  in x
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("""null""") { it =>
    it should typeAs("undefined")
    it should astTypeAs(Rql2UndefinedType(Set(Rql2IsNullableTypeProperty())))
  }

  test("""let x: int = null
    |  in x
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should astTypeAs(Rql2IntType(Set(Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty())))
    it should evaluateTo("null")
  }

  test("""let x(): int = 1
    |  in x()
    |""".stripMargin) { it =>
    // Implicit cast of Let should force '1' to go from 'int not null not try' to 'int'
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("""let x(): int = Error.Build("a broken number")
    |  in x
    |""".stripMargin)(it => it should typeAs("() -> int"))

  test("""let x(): int = Error.Build("a broken number")
    |  in x()
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should runErrorAs("""a broken number""")
  }

  test("""let x(): int = Success.Build(1)
    |  in x()
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  // binary exp

  // unary exp

  // if then else

}
