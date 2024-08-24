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

package com.rawlabs.snapi.compiler.tests.spec

import com.rawlabs.snapi.frontend.rql2.source.{
  SnapiIntType,
  SnapiIsNullableTypeProperty,
  SnapiIsTryableTypeProperty,
  SnapiUndefinedType
}
import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class MigrationTest extends SnapiTestContext {

  test("1") { it =>
    it should typeAs("int")
    it should astTypeAs(SnapiIntType())
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
    it should astTypeAs(SnapiUndefinedType(Set(SnapiIsTryableTypeProperty())))
    it should runErrorAs("""broken!""")
  }

  test("Success.Build(1)") { it =>
    it should typeAs("int")
    it should astTypeAs(SnapiIntType(Set(SnapiIsTryableTypeProperty())))
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
    it should astTypeAs(SnapiUndefinedType(Set(SnapiIsNullableTypeProperty())))
  }

  test("""let x: int = null
    |  in x
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should astTypeAs(SnapiIntType(Set(SnapiIsNullableTypeProperty(), SnapiIsTryableTypeProperty())))
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
