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

package com.rawlabs.python.compiler

import com.rawlabs.compiler.{CompilerService, ExecutionSuccess, ProgramEnvironment, RawInt}
import com.rawlabs.utils.core.{RawTestSuite, RawUid, SettingsTestContext, TrainingWheelsContext}

import java.io.ByteArrayOutputStream

class TestPythonCompilerService extends RawTestSuite with SettingsTestContext with TrainingWheelsContext {

  var compilerService: CompilerService = _

  val user = RawUid("uid")

  override def beforeAll(): Unit = {
    super.beforeAll()
    compilerService = new PythonCompilerService

  }

  override def afterAll(): Unit = {
    if (compilerService != null) {
      compilerService.stop()
      compilerService = null
    }
    super.afterAll()
  }

  test("basic execute test") { _ =>
    val environment = ProgramEnvironment(
      user,
      None,
      Set.empty,
      Map.empty,
      Map.empty,
      Map("output-format" -> "json")
    )
    val baos = new ByteArrayOutputStream()
    assert(compilerService.execute("1+1", environment, None, baos) == ExecutionSuccess(true))
    assert(baos.toString() == "2")
  }

  test("basic execute test w/ decl") { _ =>
    val environment = ProgramEnvironment(
      user,
      None,
      Set.empty,
      Map.empty,
      Map.empty,
      Map("output-format" -> "json")
    )
    val baos = new ByteArrayOutputStream()
    assert(compilerService.execute("def f(): return 1+1", environment, Some("f"), baos) == ExecutionSuccess(true))
    assert(baos.toString() == "2")
  }

  test("basic execute test w/ decl and arguments") { _ =>
    val environment = ProgramEnvironment(
      user,
      Some(Array("v" -> RawInt(2))),
      Set.empty,
      Map.empty,
      Map.empty,
      Map("output-format" -> "json")
    )
    val baos = new ByteArrayOutputStream()
    assert(compilerService.execute("def f(v): return v*2", environment, Some("f"), baos) == ExecutionSuccess(true))
    assert(baos.toString() == "4")
  }

}
