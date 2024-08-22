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

package com.rawlabs.snapi.compiler.tests.builtin.list

import com.rawlabs.snapi.frontend.rql2._
import com.rawlabs.snapi.compiler.tests.Rql2TestContext
import com.rawlabs.utils.sources.filesystem.local.LocalLocationsTestContext

class ListUnionTest extends Rql2TestContext with LocalLocationsTestContext {

  test("List.Union([1,2,3], [4,5,6])")(_ should evaluateTo("[1,2,3,4,5,6]"))

  private val beeGees = tempFile("""band|firstName|lastName|birthYear
    |Bee Gees|Maurice|Gibb|1949
    |Bee Gees|Robin|Gibb|1949
    |Bee Gees|Barry|Gibb|1946""".stripMargin)

  private val nevilleBrothers = tempFile("""band|firstName|lastName|birthYear
    |Neville Brothers|Aaron|Neville|1941
    |Neville Brothers|Art|Neville|1937
    |Neville Brothers|Charles|Neville|1938
    |Neville Brothers|Cyril|Neville|1948""".stripMargin)

  private val beachBoys = tempFile("""band|firstName|lastName|birthYear
    |Beach Boys|Brian|Wilson|1942
    |Beach Boys|Mike|Love|1941
    |Beach Boys|Al|Jardine|1942
    |Beach Boys|Bruce|Johnston|1942
    |Beach Boys|David|Marks|1948""".stripMargin)

  private val integers = tempFile("[1,2,3,4,5]")
  private val doubles = tempFile("[1.9,2.9,3.9,4.9,5.9]")
  // try/null string
  test(snapi"""let url: string = "$integers",
    |    numbers = Json.InferAndRead(url)
    |in Collection.Union(numbers, numbers)
    |""".stripMargin)(_ should evaluateTo("[1,2,3,4,5,1,2,3,4,5]"))

  // try list (because of List.From)
  test(snapi"""let numbers = List.From(Json.InferAndRead("$doubles"))
    |in List.Union(numbers, numbers)
    |""".stripMargin)(_ should evaluateTo("[1.9,2.9,3.9,4.9,5.9,1.9,2.9,3.9,4.9,5.9]"))

  // merge-like Union (doubles/ints are turned into doubles)
  test(snapi"""
    |let doubles = List.From(Json.InferAndRead("$doubles")),
    |    integers = List.From(Json.InferAndRead("$integers"))
    |in List.Union(doubles, integers)
    |""".stripMargin) { it =>
    it should evaluateTo(snapi"""
      |let doubles = List.From(Json.InferAndRead("$doubles")),
      |    integers = List.From(Json.InferAndRead("$integers"))
      |in List.Union(integers, doubles)""".stripMargin)
    it should evaluateTo("""[1.0,2.0,3.0,4.0,5.0,1.9,2.9,3.9,4.9,5.9]""")
  }

  test(snapi"""let beeGees = List.From(Csv.InferAndRead("$beeGees")),
    |    beachBoys = List.From(Csv.InferAndRead("$beachBoys")),
    |    nevilleBrothers = List.From(Csv.InferAndRead("$nevilleBrothers"))
    |in List.Distinct(List.Transform(List.Union(beeGees, beachBoys, nevilleBrothers), x -> x.lastName))
    |""".stripMargin)(
    _ should evaluateTo("""["Neville", "Gibb", "Love", "Wilson", "Jardine", "Johnston", "Marks"]""")
  )

  // same without Csv.InferAndRead
  test(snapi"""let readCsv(url: string) =
    |      let rows = List.Transform(List.From(String.ReadLines(url)), l -> String.Split(l, "|")),
    |          records = List.Transform(rows, r -> {band: List.Get(r, 0), firstName: List.Get(r, 1),
    |                                                     lastName: List.Get(r, 2), birthYear: Int.From(List.Get(r, 3))})
    |      in List.Filter(records, r -> not (r.band == "band")),
    |    beeGees = readCsv("$beeGees"),
    |    beachBoys = readCsv("$beachBoys"),
    |    nevilleBrothers = readCsv("$nevilleBrothers")
    |in List.Distinct(List.Transform(List.Union(beeGees, beachBoys, nevilleBrothers), x -> x.lastName))
    |""".stripMargin)(
    _ should evaluateTo("""["Neville", "Gibb", "Love", "Wilson", "Jardine", "Johnston", "Marks"]""")
  )

}
