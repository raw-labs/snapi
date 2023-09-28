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

package raw.compiler.rql2.api

import org.scalatest.BeforeAndAfterAll
import raw.auth.api.AuthServiceProvider
import raw.compiler.api.{CompilerService, CompilerServiceProvider}
import raw.utils.RawTestSuite

trait CompilerServiceTestContext extends BeforeAndAfterAll {
  this: RawTestSuite =>

  private var instance: CompilerService = _

  def compilerService: CompilerService = instance

  def setCompilerService(compilerService: CompilerService): Unit = {
    instance = compilerService
    CompilerServiceProvider.set(compilerService)
  }

  override def afterAll(): Unit = {
    setCompilerService(null)
    super.afterAll()
  }

}
