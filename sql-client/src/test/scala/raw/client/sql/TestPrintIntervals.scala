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

package raw.client.sql

import org.scalatest.funsuite.AnyFunSuite
import raw.client.api.RawInterval
import raw.client.sql.SqlIntervals.intervalToString
class TestPrintIntervals extends AnyFunSuite {

  test("parse singular interval items") {
    Map(
      RawInterval(1, 0, 0, 0, 0, 0, 0, 0) -> "P1Y",
      RawInterval(0, 1, 0, 0, 0, 0, 0, 0) -> "P1M",
      RawInterval(0, 0, 1, 0, 0, 0, 0, 0) -> "P1W",
      RawInterval(0, 0, 0, 1, 0, 0, 0, 0) -> "P1D",
      RawInterval(0, 0, 0, 0, 1, 0, 0, 0) -> "PT1H",
      RawInterval(0, 0, 0, 0, 0, 1, 0, 0) -> "PT1M",
      RawInterval(0, 0, 0, 0, 0, 0, 1, 0) -> "PT1.000S",
      RawInterval(0, 0, 0, 0, 0, 0, 0, 1) -> "PT0.001S",
      RawInterval(2, 0, 0, 0, 0, 0, 0, 0) -> "P2Y",
      RawInterval(0, 2, 0, 0, 0, 0, 0, 0) -> "P2M",
      RawInterval(0, 0, 2, 0, 0, 0, 0, 0) -> "P2W",
      RawInterval(0, 0, 0, 2, 0, 0, 0, 0) -> "P2D",
      RawInterval(0, 0, 0, 0, 2, 0, 0, 0) -> "PT2H",
      RawInterval(0, 0, 0, 0, 0, 2, 0, 0) -> "PT2M",
      RawInterval(0, 0, 0, 0, 0, 0, 2, 0) -> "PT2.000S",
      RawInterval(0, 0, 0, 0, 0, 0, 0, 2) -> "PT0.002S"
    ).foreach { case (k, v) => assert(intervalToString(k) == v) }
  }

  test("parse multiple items") {
    val interval = RawInterval(1, 2, 3, 4, 5, 6, 7, 8)
    assert(intervalToString(interval) == "P1Y2M3W4DT5H6M7.008S")
  }

}
