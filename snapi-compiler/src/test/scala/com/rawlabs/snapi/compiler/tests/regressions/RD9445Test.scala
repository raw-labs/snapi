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

import com.rawlabs.utils.core.TestData
import org.scalatest.matchers.{MatchResult, Matcher}
import com.rawlabs.snapi.compiler.tests.SnapiTestContext

import java.nio.file.Files
import scala.io.Source

class RD9445Test extends SnapiTestContext {

  def outputAs(expected: String, format: String = "json") = new OutputAs(expected, format)

  class OutputAs(expected: String, format: String = "json") extends Matcher[TestData] {
    def apply(q: TestData): MatchResult = {
      val tmpFile = Files.createTempFile("", "")
      try {
        q should saveToInFormat(tmpFile, format)
        val s = Source.fromFile(tmpFile.toFile)
        try {
          val output = s.getLines().toList.mkString("\n")
          MatchResult(output == expected, s"Expected '$expected' but got '$output'", "")
        } finally {
          s.close()
        }
      } finally {
        Files.delete(tmpFile)
      }
    }
  }

  test("Timestamp.Build(1975, 6, 23, 9, 0)")(it => it should outputAs(""""1975-06-23T09:00:00.000""""))

  test("Timestamp.Build(1975, 6, 23, 9, 0, millis = 123)")(it => it should outputAs(""""1975-06-23T09:00:00.123""""))

  test("Time.Build(9, 0)")(it => it should outputAs(""""09:00:00.000""""))

  test("Time.Build(9, 0, millis=123)")(it => it should outputAs(""""09:00:00.123""""))

}
