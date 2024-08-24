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

package com.rawlabs.snapi.compiler.tests.builtin.collection

import com.rawlabs.snapi.frontend.rql2._
import com.rawlabs.snapi.compiler.tests.SnapiTestContext
import com.rawlabs.utils.sources.filesystem.local.LocalLocationsTestContext

class CollectionUnionTest extends SnapiTestContext with LocalLocationsTestContext {

  test("Collection.Union(Collection.Build(1,2,3), Collection.Build(4,5,6))")(_ should evaluateTo("[1,2,3,4,5,6]"))

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

  test(snapi"""let beeGees = Csv.InferAndRead("$beeGees"),
    |    beachBoys = Csv.InferAndRead("$beachBoys")
    |in {bg: Collection.Count(beeGees), bb: Collection.Count(beachBoys)}
    |""".stripMargin)(_ should evaluateTo("{bg: 3, bb: 5}"))

  test(snapi"""let beeGees = Csv.InferAndRead("$beeGees"),
    |    beachBoys = Csv.InferAndRead("$beachBoys")
    |in Collection.Count(Collection.Union(beeGees, beachBoys))
    |""".stripMargin)(_ should evaluateTo("8"))

  // try/null URLs
  test(snapi"""let b: string = "$beeGees",
    |    bb: string = "$beachBoys",
    |    beeGees = Csv.InferAndRead(b),
    |    beachBoys = Csv.InferAndRead(bb)
    |in Collection.Count(Collection.Union(beeGees, beachBoys))
    |""".stripMargin)(_ should evaluateTo("8"))

  test(snapi"""let beeGees = Csv.InferAndRead("$beeGees"),
    |    beachBoys = Csv.InferAndRead("$beachBoys")
    |in {bg: Collection.Count(beeGees), bb: Collection.Count(beachBoys), bgbb: Collection.Count(Collection.Union(beeGees, beachBoys))}
    |""".stripMargin)(_ should evaluateTo("{bg: 3, bb: 5, bgbb: 8}"))

  test(snapi"""let beeGees = Csv.InferAndRead("$beeGees"),
    |    beachBoys = Csv.InferAndRead("$beachBoys"),
    |    nevilleBrothers = Csv.InferAndRead("$nevilleBrothers")
    |in Collection.Distinct(Collection.Transform(Collection.Union(beeGees, beachBoys, nevilleBrothers), x -> x.lastName))
    |""".stripMargin)(_ should evaluateTo("""["Neville", "Gibb", "Love", "Wilson", "Jardine", "Johnston", "Marks"]"""))

  // same without Csv.InferAndRead
  test(snapi"""let readCsv(url: string) =
    |      let rows = Collection.Transform(String.ReadLines(url), l -> String.Split(l, "|")),
    |          records = Collection.Transform(rows, r -> {band: List.Get(r, 0), firstName: List.Get(r, 1),
    |                                                     lastName: List.Get(r, 2), birthYear: Int.From(List.Get(r, 3))})
    |      in Collection.Filter(records, r -> not (r.band == "band")),
    |    beeGees = readCsv("$beeGees"),
    |    beachBoys = readCsv("$beachBoys"),
    |    nevilleBrothers = readCsv("$nevilleBrothers")
    |in Collection.Distinct(Collection.Transform(Collection.Union(beeGees, beachBoys, nevilleBrothers), x -> x.lastName))
    |""".stripMargin)(
    _ should evaluateTo("""["Neville", "Gibb", "Love", "Wilson", "Jardine", "Johnston", "Marks"]""")
  )

  private val integers = tempFile("[1,2,3,4,5]")
  // try/null string
  test(snapi"""let url: string = "$integers",
    |    numbers = Json.InferAndRead(url)
    |in Collection.Union(numbers, numbers)
    |""".stripMargin)(_ should evaluateTo("[1,2,3,4,5,1,2,3,4,5]"))

}
