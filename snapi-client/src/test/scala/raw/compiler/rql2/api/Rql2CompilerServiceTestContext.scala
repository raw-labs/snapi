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
import raw.client.api.CompilerServiceProvider
import raw.client.rql2.api.Rql2CompilerService
import raw.utils.RawTestSuite

trait Rql2CompilerServiceTestContext extends BeforeAndAfterAll {
  this: RawTestSuite =>

  private var instance: Rql2CompilerService = _

  private var language: Set[String] = _

  def compilerService: Rql2CompilerService = instance

  def setCompilerService(compilerService: Rql2CompilerService): Unit = {
    // Remember the language we have set.
    if (compilerService != null) {
      language = compilerService.language
    }
    instance = compilerService
    CompilerServiceProvider.set(language, compilerService)
  }

  override def afterAll(): Unit = {
    instance.stop()
    setCompilerService(null)
    super.afterAll()
  }

}
