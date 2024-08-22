/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.compiler.tests.regressions

import com.rawlabs.compiler.{GetProgramDescriptionSuccess, ProgramEnvironment}
import com.rawlabs.snapi.frontend.rql2.SnapiInterpolator
import com.rawlabs.snapi.compiler.tests.Rql2TestContext

class RD10767Test extends Rql2TestContext {
  private val data = tempFile("""
    |[
    |  {"a": 1, "b": 10, "c": 100},
    |  {"a": 2, "b": 20, "c": 200},
    |  {"a": 3, "b": 30, "c": 300}
    |]""".stripMargin)

  test(snapi"""data_type() = type collection(record(a: int, b: int, c: int))
    |
    |Json.Read("$data", data_type())
    |""".stripMargin) { it =>
    val programEnvironment = ProgramEnvironment(
      authorizedUser,
      None,
      Set.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      None,
      None
    )
    compilerService.getProgramDescription(it.q, programEnvironment) match {
      case GetProgramDescriptionSuccess(desc) =>
        assert(desc.maybeRunnable.isDefined, "Expected a runnable program")
        val decls = desc.decls("data_type")
        assert(decls.head.outType.isEmpty)
      case other => fail(s"Expected GetProgramDescriptionSuccess, got $other")
    }
  }

  test(snapi"""func() = let f(i: int) = i +1 in f
    |
    |func()(1)
    |""".stripMargin) { it =>
    val programEnvironment = ProgramEnvironment(
      authorizedUser,
      None,
      Set.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      None,
      None
    )
    compilerService.getProgramDescription(it.q, programEnvironment) match {
      case GetProgramDescriptionSuccess(desc) =>
        assert(desc.maybeRunnable.isDefined, "Expected a runnable program")
        val decls = desc.decls("func")
        assert(decls.head.outType.isEmpty)
      case other => fail(s"Expected GetProgramDescriptionSuccess, got $other")
    }
  }

}
