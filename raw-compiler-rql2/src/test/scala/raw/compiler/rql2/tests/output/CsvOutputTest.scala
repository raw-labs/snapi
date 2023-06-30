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
import raw.compiler.RQLInterpolator

import java.nio.file.Files

trait CsvOutputTest extends CompilerTestContext {

  option("output-format", "csv")

  private val csvWithAllTypes =
    tempFile("""byteCol;shortCol;intCol;longCol;floatCol;doubleCol;decimalCol;boolCol;dateCol;timeCol;timestampCol
      |1;10;100;1000;3.14;6.28;9.42;true;2023-12-25;01:02:03;2023-12-25T01:02:03
      |120;2500;25000;250000;30.14;60.28;90.42;false;2023-02-05;11:12:13;2023-02-05T11:12:13""".stripMargin)

  test("""[
    |{byteCol: Int.From("1"), shortCol:Int.From("10"), intCol: Int.From("100"), longCol: Int.From("1000"),
    | floatCol: Double.From("3.14"), doubleCol: Double.From("6.28"), decimalCol: Double.From("9.42"), boolCol: true,
    | dateCol: Date.Parse("12/25/2023", "M/d/yyyy"), timeCol: Time.Parse("01:02:03", "H:m:s"),
    | timestampCol: Timestamp.Parse("12/25/2023 01:02:03", "M/d/yyyy H:m:s")},
    |{byteCol: Int.From("120"), shortCol:Int.From("2500"), intCol: Int.From("25000"), longCol: Int.From("250000"),
    | floatCol: Double.From("30.14"), doubleCol: Double.From("60.28"), decimalCol: Double.From("90.42"), boolCol: false,
    | dateCol: Date.Parse("2/5/2023", "M/d/yyyy"), timeCol: Time.Parse("11:12:13", "H:m:s"),
    | timestampCol: Timestamp.Parse("2/5/2023 11:12:13", "M/d/yyyy H:m:s")}
    |]""".stripMargin) { it =>
    val path = Files.createTempFile("", "")
    try {
      it should saveToInFormat(path, "csv")
      path should contain
      ("""byteCol,shortCol,intCol,longCol,floatCol,doubleCol,decimalCol,boolCol,dateCol,timeCol,timestampCol
        |1,10,100,1000,3.14,6.28,9.42,true,2023-12-25,01:02:03,2023-12-25T01:02:03
        |120,2500,25000,250000,30.14,60.28,90.42,false,2023-02-05,11:12:13,2023-02-05T11:12:13
        |""".stripMargin)
    } finally {
      Files.deleteIfExists(path)
    }
  }

  test(rql"""Csv.InferAndRead("$csvWithAllTypes")""") { it =>
    val path = Files.createTempFile("", "")
    try {
      it should saveToInFormat(path, "csv")
      path should contain
      ("""byteCol,shortCol,intCol,longCol,floatCol,doubleCol,decimalCol,boolCol,dateCol,timeCol,timestampCol
        |1,10,100,1000,3.14,6.28,9.42,true,2023-12-25,01:02:03,2023-12-25T01:02:03
        |120,2500,25000,250000,30.14,60.28,90.42,false,2023-02-05,11:12:13,2023-02-05T11:12:13
        |""".stripMargin)
    } finally {
      Files.deleteIfExists(path)
    }
  }

  test(rql"""Csv.Read("$csvWithAllTypes", type collection(
    |    record(
    |        byteCol: byte,
    |        shortCol: short,
    |        intCol: int,
    |        longCol: long,
    |        floatCol: float,
    |        doubleCol: double,
    |        decimalCol: decimal,
    |        boolCol: bool,
    |        dateCol: date,
    |        timeCol: time,
    |        timestampCol: timestamp
    |    )
    |), delimiter = ";", skip = 1)""".stripMargin) { it =>
    val path = Files.createTempFile("", "")
    try {
      it should saveToInFormat(path, "csv")
      path should contain
      ("""byteCol,shortCol,intCol,longCol,floatCol,doubleCol,decimalCol,boolCol,dateCol,timeCol,timestampCol
        |1,10,100,1000,3.14,6.28,9.42,true,2023-12-25,01:02:03,2023-12-25T01:02:03
        |120,2500,25000,250000,30.14,60.28,90.42,false,2023-02-05,11:12:13,2023-02-05T11:12:13
        |""".stripMargin)
    } finally {
      Files.deleteIfExists(path)
    }
  }

  test(rql"""Csv.Read("$csvWithAllTypes", type collection(
    |    record(
    |        byteCol: byte,
    |        shortCol: short,
    |        intCol: int,
    |        longCol: long,
    |        floatCol: float,
    |        doubleCol: double,
    |        decimalCol: decimal,
    |        boolCol: bool,
    |        dateCol: date,
    |        timeCol: time,
    |        timestampCol: timestamp
    |    )
    |), delimiter = ";", skip = 0)""".stripMargin) { it =>
    val path = Files.createTempFile("", "")
    try {
      it should saveToInFormat(path, "csv")
      path should contain
      ("""cannot cast 'byteCol' to byte,cannot cast 'shortCol' to short,cannot cast 'intCol' to int,cannot cast 'longCol' to long,cannot cast 'floatCol' to float,cannot cast 'doubleCol' to double,cannot cast 'decimalCol' to decimal,cannot cast 'boolCol' to bool,cannot cast 'dateCol' to date,cannot cast 'timeCol' to time,cannot cast 'timestampCol' to timestamp
        |1,10,100,1000,3.14,6.28,9.42,true,2023-12-25,01:02:03,2023-12-25T01:02:03
        |120,2500,25000,250000,30.14,60.28,90.42,false,2023-02-05,11:12:13,2023-02-05T11:12:13
        |""".stripMargin)
    } finally {
      Files.deleteIfExists(path)
    }
  }
}
