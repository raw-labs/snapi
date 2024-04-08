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

package raw.compiler.rql2.tests.spec

import raw.compiler.utils._
import raw.compiler.rql2.tests.CompilerTestContext

trait StagedCompilerTest extends CompilerTestContext {

  val data = tempFile("""
    |[
    |  {"a": 1, "b": 10, "c": 100},
    |  {"a": 2, "b": 20, "c": 200},
    |  {"a": 3, "b": 30, "c": 300}
    |]""".stripMargin)

  val dataXml = tempFile("""
    |  <item> <a>1</a> <b>10</b> <c>100</c> </item>
    |  <item> <a>2</a> <b>20</b> <c>200</c> </item>
    |  <item> <a>3</a> <b>30</b> <c>300</c> </item>
    |""".stripMargin)

  val dataCsv = tempFile("""a, b, c
    |1, 10, 100
    |2, 20 , 200
    |3, 30, 300
    |""".stripMargin)

//  test(rql"""
//    |let f = "$data",
//    |    data = Json.InferAndRead(f)
//    |in
//    |    Collection.Count(data)""".stripMargin) { it =>
//    it should typeAs("long")
//    it should evaluateTo("3")
//  }

  test(snapi"""
    |let f: string = "$data",
    |    data = Json.InferAndRead(f)
    |in
    |    data""".stripMargin)(it => it should run)

  test(snapi"""
    |let f: string = "$dataXml",
    |    data = Xml.InferAndRead(f)
    |in
    |    data""".stripMargin)(it => it should run)

  test(snapi"""
    |let f: string = "$dataCsv",
    |    data = Csv.InferAndRead(f)
    |in
    |    data""".stripMargin)(it => it should run)

  test(snapi"""
    |let f: string = "$data",
    |    data = Json.InferAndRead(f)
    |in
    |    Collection.Count(data)""".stripMargin) { it =>
    it should typeAs("long")
    it should evaluateTo("3")
  }

  val localFilePath = data.toAbsolutePath.toString.replace("\\", "\\\\")

  test(s"""
    |let fix_path(v: string) = "file:" + v,
    |    data = Json.InferAndRead(fix_path("$localFilePath"))
    |in
    |    Collection.Count(data)""".stripMargin) { it =>
    it should typeAs("long")
    it should evaluateTo("3")
  }

  // Create a file whose contents are the location of the first file.
  val data1 = tempFile(data.toAbsolutePath.toString.replace("\\", "\\\\"))

  test(s"""
    |let fix_path(v: string) = "file:" + v,
    |    location = String.Read(fix_path("$data1"), encoding = "utf-8"),
    |    data = Json.InferAndRead(fix_path(location))
    |in
    |    Collection.Count(data)""".stripMargin) { it =>
    it should typeAs("long")
    it should evaluateTo("3")
  }

  test(s"""
    |let read_data(url: string) = Json.InferAndRead(url),
    |    data = read_data("$data")
    |in
    |    Collection.Count(data)""".stripMargin)(
    _ should typeErrorAs("value cannot be determined for function parameter")
  )

  test(s"""
    |let rec factorial(i: int): int = if (i <= 0) then 1 else i * factorial(i - 1),
    |    location = "file://" + String.From(factorial(0)) + ".txt",
    |    data = Json.InferAndRead(location)
    |in
    |    Collection.Count(data)""".stripMargin)(
    _ should typeErrorAs("value cannot be determined for recursive definitions")
  )

  test(s"""
    |let rec factorial(i: int): int = if (i <= 0) then 1 else i * factorial(i - 1),
    |    fix_path(v: string) = "file:" + v,
    |    location = String.Read(fix_path("$data1"), encoding = "utf-8"),
    |    data = Json.InferAndRead(fix_path(location))
    |in
    |    Collection.Count(data)""".stripMargin)(
    _ should typeAs("long")
  )

  test(s"""
    |fix_path(v: string) = "file:" + v
    |
    |let location = String.Read(fix_path("$data1"), encoding = "utf-8"),
    |    data = Json.InferAndRead(fix_path(location))
    |in
    |    Collection.Count(data)""".stripMargin) { it =>
    it should typeAs("long")
    it should evaluateTo("3")
  }

  test(s"""
    |fix_path(v: string) = "file:" + v
    |
    |location() = String.Read(fix_path("$data1"), encoding = "utf-8")
    |
    |let data = Json.InferAndRead(fix_path(location()))
    |in
    |    Collection.Count(data)""".stripMargin) { it =>
    it should typeAs("long")
    it should evaluateTo("3")
  }

  test(s"""
    |fix_path(v: string) = "file:" + v
    |
    |location() = String.Read(fix_path("$data1"), encoding = "utf-8")
    |
    |data() = Json.InferAndRead(fix_path(location()))
    |
    |Collection.Count(data())""".stripMargin) { it =>
    it should typeAs("long")
    it should evaluateTo("3")
  }
}
