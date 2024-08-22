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

package com.rawlabs.snapi.compiler.tests.regressions

import com.rawlabs.utils.core.RawUtils
import org.scalatest.BeforeAndAfterEach
import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext
import com.rawlabs.utils.core._

import java.nio.file.Files

class RD7974Test extends Rql2TruffleCompilerTestContext with BeforeAndAfterEach {

  private val tmpFile = Files.createTempFile("csv-output-test", ".csv")

  override def afterEach(): Unit = {
    super.afterEach()
    RawUtils.deleteTestPath(tmpFile)
  }

  test("""let l = [3,2,1,0,-1,-2,-3]
    |in List.Transform(l, v -> {error: 6 / v, ok: 6 * v})""".stripMargin) { it =>
    it should saveToInFormat(tmpFile, "csv")
    tmpFile should contain("""error,ok
      |2,18
      |3,12
      |6,6
      |/ by zero,0
      |-6,-6
      |-3,-12
      |-2,-18
      |""".stripMargin)
  }

  test("""let l = Collection.Build(3,2,1,0,-1,-2,-3)
    |in Collection.Transform(l, v -> {error: 6 / v, ok: 6 * v})""".stripMargin) { it =>
    it should saveToInFormat(tmpFile, "csv")
    tmpFile should contain("""error,ok
      |2,18
      |3,12
      |6,6
      |/ by zero,0
      |-6,-6
      |-3,-12
      |-2,-18
      |""".stripMargin)
  }
}
