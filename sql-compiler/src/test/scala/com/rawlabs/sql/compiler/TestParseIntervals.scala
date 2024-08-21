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

package com.rawlabs.sql.compiler

import com.rawlabs.compiler.api.RawInterval
import com.rawlabs.sql.compiler.SqlIntervals.parseInterval
import org.scalatest.funsuite.AnyFunSuite

class TestParseIntervals extends AnyFunSuite {

  test("parse singular interval items") {
    Map(
      "1 year" -> RawInterval(1, 0, 0, 0, 0, 0, 0, 0),
      "1 mon" -> RawInterval(0, 1, 0, 0, 0, 0, 0, 0),
      "1 day" -> RawInterval(0, 0, 0, 1, 0, 0, 0, 0),
      "01:00:00" -> RawInterval(0, 0, 0, 0, 1, 0, 0, 0),
      "00:01:00" -> RawInterval(0, 0, 0, 0, 0, 1, 0, 0),
      "00:00:01" -> RawInterval(0, 0, 0, 0, 0, 0, 1, 0),
      "00:00:00.001" -> RawInterval(0, 0, 0, 0, 0, 0, 0, 1),
      "2 years" -> RawInterval(2, 0, 0, 0, 0, 0, 0, 0),
      "2 mons" -> RawInterval(0, 2, 0, 0, 0, 0, 0, 0),
      "2 days" -> RawInterval(0, 0, 0, 2, 0, 0, 0, 0),
      "02:00:00" -> RawInterval(0, 0, 0, 0, 2, 0, 0, 0),
      "00:02:00" -> RawInterval(0, 0, 0, 0, 0, 2, 0, 0),
      "00:00:02" -> RawInterval(0, 0, 0, 0, 0, 0, 2, 0),
      "00:00:00.002" -> RawInterval(0, 0, 0, 0, 0, 0, 0, 2)
    ).foreach {
      case (k, v) =>
        val interval = parseInterval(k)
        assert(interval == v)
    }
  }

  test("parse multiple items") {
    val interval = parseInterval("1 year 2 mons 3 days 04:05:06.007")
    assert(interval == RawInterval(1, 2, 0, 3, 4, 5, 6, 7))
  }

  test("parse milliseconds") {
    var interval = parseInterval("00:00:00.01")
    assert(interval == RawInterval(0, 0, 0, 0, 0, 0, 0, 10))
    interval = parseInterval("00:00:00.1234567")
    assert(interval == RawInterval(0, 0, 0, 0, 0, 0, 0, 123))
  }
}
