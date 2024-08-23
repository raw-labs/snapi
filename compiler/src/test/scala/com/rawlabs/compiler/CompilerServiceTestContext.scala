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

package com.rawlabs.compiler

import com.rawlabs.utils.core.RawTestSuite
import org.scalatest.BeforeAndAfterAll

trait CompilerServiceTestContext extends BeforeAndAfterAll {
  this: RawTestSuite =>

  private var instance: CompilerService = _

  private var language: Set[String] = _

  def compilerService: CompilerService = instance

  def setCompilerService(compilerService: CompilerService): Unit = {
    // Remember the language we have set.
    if (compilerService != null) {
      language = compilerService.language
    }
    instance = compilerService
    CompilerServiceProvider.set(language, compilerService)
  }

  override def afterAll(): Unit = {
    setCompilerService(null)
    super.afterAll()
  }

}
