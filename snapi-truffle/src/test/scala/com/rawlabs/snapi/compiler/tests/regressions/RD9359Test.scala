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

import com.rawlabs.snapi.compiler.tests.SnapiTestContext
import com.rawlabs.snapi.frontend.snapi._

class RD9359Test extends SnapiTestContext {

  private val duplicateCsvString = """a,b,a
    |1,2,3
    |4,5,6
    |7,8,9""".stripMargin
  private val duplicateColumnCsv = tempFile(duplicateCsvString)
  private val ttt = "\"\"\""

  test(s"""Csv.InferAndParse($ttt$duplicateCsvString$ttt).b""")(it => it should evaluateTo(s"""[2,5,8]"""))

  test(snapi"""Csv.InferAndRead("$duplicateColumnCsv").b""")(it => it should evaluateTo(s"""[2,5,8]"""))

  test(s"""Collection.Transform(Csv.InferAndParse($ttt$duplicateCsvString$ttt), r -> r.a)""") { it =>
    it should runErrorAs("record has more than one field with the same name: a")
  }

  test(snapi"""Collection.Transform(Csv.InferAndRead("$duplicateColumnCsv"), r -> r.a)""") { it =>
    it should runErrorAs("record has more than one field with the same name: a")
  }

  test(s"""Csv.InferAndParse($ttt$duplicateCsvString$ttt).a""") { it =>
    it should runErrorAs("record has more than one field with the same name: a")
  }

  test(snapi"""Csv.InferAndRead("$duplicateColumnCsv").a""") { it =>
    it should runErrorAs("record has more than one field with the same name: a")
  }

}
