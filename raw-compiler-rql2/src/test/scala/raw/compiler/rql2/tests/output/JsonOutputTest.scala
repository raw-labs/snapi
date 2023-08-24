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

package raw.compiler.rql2.tests.output

import raw.compiler.rql2.tests.CompilerTestContext
import raw.utils._

import java.nio.file.{Files, Path}

trait JsonOutputTest extends CompilerTestContext {

  option("output-format", "json")

  test("""[
    |{byteCol: Int.From("1"), shortCol:Int.From("10"), intCol: Int.From("100"), longCol: Int.From("1000"),
    | floatCol: Double.From("3.14"), doubleCol: Double.From("6.28"), decimalCol: Double.From("9.42"), boolCol: true,
    | dateCol: Date.Parse("12/25/2023", "M/d/yyyy"), timeCol: Time.Parse("01:02:03", "H:m:s"),
    | timestampCol: Timestamp.Parse("12/25/2023 01:02:03", "M/d/yyyy H:m:s"), binaryCol: Binary.FromString("Hello World!")},
    |{byteCol: Int.From("120"), shortCol:Int.From("2500"), intCol: Int.From("25000"), longCol: Int.From("250000"),
    | floatCol: Double.From("30.14"), doubleCol: Double.From("60.28"), decimalCol: Double.From("90.42"), boolCol: false,
    | dateCol: Date.Parse("2/5/2023", "M/d/yyyy"), timeCol: Time.Parse("11:12:13", "H:m:s"),
    | timestampCol: Timestamp.Parse("2/5/2023 11:12:13", "M/d/yyyy H:m:s"), binaryCol: Binary.FromString("Olala!")}
    |]""".stripMargin) { it =>
    val path: Path = Files.createTempFile("", "")
    try {
      it should saveToInFormat(path, "json")
      path should contain(
        """[{"byteCol":1,"shortCol":10,"intCol":100,"longCol":1000,"floatCol":3.14,"doubleCol":6.28,"decimalCol":9.42,"boolCol":true,""" +
          """"dateCol":"2023-12-25","timeCol":"01:02:03.000","timestampCol":"2023-12-25T01:02:03.000","binaryCol":"SGVsbG8gV29ybGQh"},""" +
          """{"byteCol":120,"shortCol":2500,"intCol":25000,"longCol":250000,"floatCol":30.14,"doubleCol":60.28,"decimalCol":90.42,"boolCol":false,""" +
          """"dateCol":"2023-02-05","timeCol":"11:12:13.000","timestampCol":"2023-02-05T11:12:13.000","binaryCol":"T2xhbGEh"}]""".stripMargin
      )
    } finally {
      deleteTestPath(path)
    }
  }

  // duplicated fields
  test("""{a: 1, b: 2, a: 3, c: 4, a: 5}""") { it =>
    val path: Path = Files.createTempFile("", "")
    try {
      it should saveToInFormat(path, "json")
      // fields 'a' are renamed as 'a', 'a_1' and 'a_2'
      path should contain("""{"a":1,"b":2,"a_1":3,"c":4,"a_2":5}""")
    } finally {
      deleteTestPath(path)
    }
  }

  // duplicated fields
  test("""{a: 1, b: 2, a: 3, c: 4, a_1: 5}""") { it =>
    val path: Path = Files.createTempFile("", "")
    try {
      it should saveToInFormat(path, "json")
      // fields 'a' are renamed as 'a', 'a_2' because 'a_1' exists already
      path should contain("""{"a":1,"b":2,"a_2":3,"c":4,"a_1":5}""")
    } finally {
      deleteTestPath(path)
    }
  }

  // duplicated fields
  test("""{a_1: 1, b: 2, a: 3, c: 4, a: 5}""") { it =>
    val path: Path = Files.createTempFile("", "")
    try {
      it should saveToInFormat(path, "json")
      // fields 'a' are renamed as 'a', 'a_2' because 'a_1' exists already
      path should contain("""{"a_1":1,"b":2,"a":3,"c":4,"a_2":5}""")
    } finally {
      deleteTestPath(path)
    }
  }

  // duplicated fields
  test("""{a: 1, b: 2, a: 3, c: 4, a_2: 5, a_1: 6}""") { it =>
    val path: Path = Files.createTempFile("", "")
    try {
      it should saveToInFormat(path, "json")
      path should contain("""{"a":1,"b":2,"a_3":3,"c":4,"a_2":5,"a_1":6}""")
    } finally {
      deleteTestPath(path)
    }
  }
}
