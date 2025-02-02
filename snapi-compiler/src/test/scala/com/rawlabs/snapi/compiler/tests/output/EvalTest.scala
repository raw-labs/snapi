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

package com.rawlabs.snapi.compiler.tests.output

import com.rawlabs.protocol.raw.{Value, ValueInt, ValueList}
import com.rawlabs.snapi.compiler.tests.SnapiTestContext
import com.rawlabs.snapi.frontend.snapi.SnapiInterpolator

class EvalTest extends SnapiTestContext {

  // Test single value
  test(snapi"""42""")(it => it should eval(Value.newBuilder().setInt(ValueInt.newBuilder().setV(42)).build()))

  // Test iterator
  test(snapi"""Collection.Build(1,2,3)""") { it =>
    it should eval(
      Value.newBuilder().setInt(ValueInt.newBuilder().setV(1)).build(),
      Value.newBuilder().setInt(ValueInt.newBuilder().setV(2)).build(),
      Value.newBuilder().setInt(ValueInt.newBuilder().setV(3)).build()
    )
  }

  // Test list (vs iterator)
  test(snapi"""[1,2,3]""") { it =>
    it should eval(
      Value
        .newBuilder()
        .setList(
          ValueList
            .newBuilder()
            .addValues(Value.newBuilder().setInt(ValueInt.newBuilder().setV(1)).build())
            .addValues(Value.newBuilder().setInt(ValueInt.newBuilder().setV(2)).build())
            .addValues(Value.newBuilder().setInt(ValueInt.newBuilder().setV(3)).build())
            .build()
        )
        .build()
    )
  }

  // Test validation failure
  test(snapi"""x + 1""")(it =>
    it should evalTypeErrorAs("x is not declared")
  )


  // Test runtime failure
  test(snapi"""Regex.Groups("aaa", "(\\d+)") """)(it =>
    it should evalRunErrorAs("string 'aaa' does not match pattern '(\\d+)'")
  )

  // Test all types

  /*

  test(snapi"""Csv.Read("$csvWithAllTypes", type collection(
    |    record(
    |        byteCol: byte,
    |        shortCol: short,
    |        intCol: int,
    |        longCol: long,
    |        floatCol: float,
    |        doubleCol: double,
    |        decimalCol: decimal,
    |        boolCol: bool,
    |        nullBoolCol: bool,
    |        dateCol: date,
    |        timeCol: time,
    |        timestampCol: timestamp
    |    )
    |), delimiter = ";", skip = 1)""".stripMargin) { it =>
    val path = Files.createTempFile("", "")
    try {
      it should saveToInFormat(path, "csv")
      path should contain(
        """byteCol,shortCol,intCol,longCol,floatCol,doubleCol,decimalCol,boolCol,nullBoolCol,dateCol,timeCol,timestampCol
          |1,10,100,1000,3.14,6.28,9.42,true,false,2023-12-25,01:02:03,2023-12-25T01:02:03
          |120,2500,25000,9223372036854775807,30.14,60.28,90.42,false,,2023-02-05,11:12:13,2023-02-05T11:12:13
          |""".stripMargin
      )
    } finally {
      RawUtils.deleteTestPath(path)
    }
  }
   */
}
