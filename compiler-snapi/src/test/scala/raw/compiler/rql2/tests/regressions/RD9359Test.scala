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

package raw.compiler.rql2.tests.regressions

import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import com.rawlabs.compiler.snapi.utils._

import java.nio.file.{Files, Path}

class RD9359Test extends Rql2TruffleCompilerTestContext {

  private val duplicateCsvString = """a,b,a
    |1,2,3
    |4,5,6
    |7,8,9""".stripMargin
  private val duplicateColumnCsv = tempFile(duplicateCsvString)
  private val ttt = "\"\"\""

  test(s"""Csv.InferAndParse($ttt$duplicateCsvString$ttt)""") { it =>
    it should evaluateTo(s"""[{a:1,b:2,a:3},{a:4,b:5,a:6},{a:7,b:8,a:9}]""")
    val path: Path = Files.createTempFile("", "")
    try {
      it should saveToInFormat(path, "json")
      path should contain("""[{"a":1,"b":2,"a_1":3},{"a":4,"b":5,"a_1":6},{"a":7,"b":8,"a_1":9}]""")
    } finally {
      Files.deleteIfExists(path)
    }
  }

  test(snapi"""Csv.InferAndRead("$duplicateColumnCsv")""") { it =>
    it should evaluateTo(s"""[{a:1,b:2,a:3},{a:4,b:5,a:6},{a:7,b:8,a:9}]""")
    val path: Path = Files.createTempFile("", "")
    try {
      it should saveToInFormat(path, "json")
      path should contain("""[{"a":1,"b":2,"a_1":3},{"a":4,"b":5,"a_1":6},{"a":7,"b":8,"a_1":9}]""")
    } finally {
      Files.deleteIfExists(path)
    }
  }

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
