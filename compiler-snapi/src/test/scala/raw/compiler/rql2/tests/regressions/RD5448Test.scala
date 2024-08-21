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

import com.rawlabs.compiler.snapi.utils._
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext

class RD5448Test extends Rql2TruffleCompilerTestContext {

  private val ttt = "\"\"\""
  private val jsonString = """[
    |  {"a": 1, "b": 10, "c": 100},
    |  {"a": 2, "b": 20, "c": 200},
    |  {"a": 3, "b": 30, "c": 300}
    |]""".stripMargin
  private val jsonFile = tempFile(jsonString)
  private val jsonType = "collection(record(a: int, b: int, c: int))"

  test(snapi"""Json.Parse($ttt$jsonString$ttt, type $jsonType)""")(_ should run)
  test(snapi"""Json.InferAndRead("$jsonFile")""")(_ should run)
  test(snapi"""Json.Read("$jsonFile", type $jsonType)""")(_ should run)

  test(snapi"""let s: string = $ttt$jsonString$ttt in Json.Parse(s, type $jsonType)""".stripMargin)(
    _ should run
  )
  test(snapi"""let url: string = "$jsonFile" in Json.InferAndRead(url)""")(_ should run)
  test(snapi"""let url: string = "$jsonFile" in Json.Read(url, type $jsonType)""")(_ should run)

  private val xmlCollectionString = """
    |<person place="world"> <name>john</name> <age>34</age> </person>
    |<person place="venus"> <name>jane</name> <age>32</age> </person>
    |<person place="moon"> <name>bob</name> <age>36</age> </person>
    |""".stripMargin

  private val xmlCollectionFile = tempFile(xmlCollectionString)
  private val xmlCollectionType = "collection(record(person: string, name: string, age: int))"

  test(snapi"""Xml.Parse($ttt$xmlCollectionString$ttt, type $xmlCollectionType)""")(_ should run)
  test(snapi"""Xml.InferAndRead("$xmlCollectionFile")""")(_ should run)
  test(snapi"""Xml.Read("$xmlCollectionFile", type $xmlCollectionType)""")(_ should run)

  test(snapi"""let s: string = $ttt$xmlCollectionString$ttt in Xml.Parse(s, type $xmlCollectionType)""")(_ should run)
  test(snapi"""let url: string = "$xmlCollectionFile" in Xml.InferAndRead(url)""")(_ should run)
  test(snapi"""let url: string = "$xmlCollectionFile" in Xml.Read(url, type $xmlCollectionType)""")(_ should run)

  private val csvString = """a|b|c
    |1|10|100
    |2|20|200
    |3|30|300""".stripMargin

  private val csvFile = tempFile(csvString)
  private val csvType = "collection(record(a: int, b: int, c: int))"

  test(snapi"""Csv.Parse($ttt$csvString$ttt, type $csvType, skip = 1, delimiter = "|")""")(_ should run)
  test(snapi"""Csv.InferAndRead("$csvFile")""")(_ should run)
  test(snapi"""Csv.Read("$csvFile", type $csvType, skip = 1, delimiter = "|")""")(_ should run)

  test(snapi"""let s: string = $ttt$csvString$ttt in Csv.Parse(s, type $csvType, skip = 1, delimiter = "|")""")(
    _ should run
  )
  test(snapi"""let url: string = "$csvFile" in Csv.InferAndRead(url)""")(_ should run)
  test(snapi"""let url: string = "$csvFile" in Csv.Read(url, type $csvType, skip = 1, delimiter = "|")""")(_ should run)
}
