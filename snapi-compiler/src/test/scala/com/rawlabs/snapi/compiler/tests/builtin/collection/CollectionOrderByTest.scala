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

import com.rawlabs.snapi.frontend.snapi._
import com.rawlabs.snapi.frontend.snapi.errors.{InvalidOrderSpec, KeyNotComparable, OrderSpecMustFollowOrderingFunction}
import com.rawlabs.snapi.compiler.tests.SnapiTestContext
import com.rawlabs.utils.sources.filesystem.local.LocalLocationsTestContext

class CollectionOrderByTest extends SnapiTestContext with LocalLocationsTestContext {

  private val coolBandsWithoutHeader = tempFile("""Bee Gees|Maurice|Gibb|1949
    |Bee Gees|Robin|Gibb|1949
    |Bee Gees|Barry|Gibb|1946
    |Neville Brothers|Aaron|Neville|1941
    |Neville Brothers|Art|Neville|1937
    |Neville Brothers|Charles|Neville|1938
    |Neville Brothers|Cyril|Neville|1948
    |Beach Boys|Brian|Wilson|1942
    |Beach Boys|Mike|Love|1941
    |Beach Boys|Al|Jardine|1942
    |Beach Boys|Bruce|Johnston|1942
    |Beach Boys|David|Marks|1948""".stripMargin)

  private val manualCsv: String = snapi"""
    |let tokens = Collection.Transform(String.ReadLines("$coolBandsWithoutHeader"), l -> String.Split(l, "|"))
    |in Collection.Transform(tokens, i -> {
    |     band: List.Get(i, 0),
    |     firstName: List.Get(i, 1),
    |     lastName: List.Get(i, 2),
    |     birthYear: Int.From(List.Get(i, 3))
    |})""".stripMargin

  test(
    snapi"""let bands = $manualCsv,
      |    orderedBySomething = Collection.OrderBy(bands, p -> p.birthYear, "DESC", p -> p.lastName, "ASC")
      |    in Collection.Transform(orderedBySomething, p -> p.lastName + " (" + String.From(p.birthYear) + ")")""".stripMargin
  ) {
    _ should orderEvaluateTo("""[
      |"Gibb (1949)",
      |"Gibb (1949)",
      |"Marks (1948)",
      |"Neville (1948)",
      |"Gibb (1946)",
      |"Jardine (1942)",
      |"Johnston (1942)",
      |"Wilson (1942)",
      |"Love (1941)",
      |"Neville (1941)",
      |"Neville (1938)",
      |"Neville (1937)"
      |]""".stripMargin)
  }

  // using String.ReadLines
  test(snapi"""let regions = Collection.Transform(
    |  String.ReadLines("$tpchRegionTblLocal"),
    |  l ->
    |      let tokens = String.Split(l, "|")
    |      in {
    |          r_regionkey: Int.From(List.Get(tokens, 0)),
    |          r_name: List.Get(tokens, 1),
    |          r_comment: List.Get(tokens, 2)
    |      }
    |  )
    |in Collection.Transform(
    |     Collection.OrderBy(regions, r -> r.r_name, "ASC"),
    |     r -> r.r_name)""".stripMargin)(
    _ should orderEvaluateTo("""["AFRICA", "AMERICA", "ASIA", "EUROPE", "MIDDLE EAST"]""")
  )

  test(snapi"""let regions = Collection.Transform(
    |  String.ReadLines("$tpchRegionTblLocal"),
    |  l ->
    |      let tokens = String.Split(l, "|")
    |      in {
    |          r_regionkey: Int.From(List.Get(tokens, 0)),
    |          r_name: List.Get(tokens, 1),
    |          r_comment: List.Get(tokens, 2)
    |      }
    |  )
    |in Collection.Transform(
    |     Collection.OrderBy(regions, r -> r.r_name, "DESC"),
    |     r -> r.r_name)""".stripMargin)(
    _ should orderEvaluateTo("""["MIDDLE EAST", "EUROPE", "ASIA", "AMERICA", "AFRICA"]""")
  )
  test(snapi"""let bands = $manualCsv,
    |    orderedBySomething = Collection.OrderBy(bands, p -> p.firstName, "ASC", p -> p.lastName, "ASC")
    |    in Collection.Transform(orderedBySomething, p -> p.firstName + " " + p.lastName)""".stripMargin) {
    _ should orderEvaluateTo("""[
      |"Aaron Neville",
      |"Al Jardine",
      |"Art Neville",
      |"Barry Gibb",
      |"Brian Wilson",
      |"Bruce Johnston",
      |"Charles Neville",
      |"Cyril Neville",
      |"David Marks",
      |"Maurice Gibb",
      |"Mike Love",
      |"Robin Gibb"
      |]""".stripMargin)
  }

  test(snapi"""let bands = $manualCsv,
    |    orderedBySomething = Collection.OrderBy(bands, p -> p.lastName, "ASC", p -> p.firstName, "ASC")
    |    in Collection.Transform(orderedBySomething, p -> p.firstName + " " + p.lastName)""".stripMargin) {
    _ should orderEvaluateTo("""[
      |"Barry Gibb",
      |"Maurice Gibb",
      |"Robin Gibb",
      |"Al Jardine",
      |"Bruce Johnston",
      |"Mike Love",
      |"David Marks",
      |"Aaron Neville",
      |"Art Neville",
      |"Charles Neville",
      |"Cyril Neville",
      |"Brian Wilson"
      |]""".stripMargin)
  }

  test(snapi"""let bands = $manualCsv,
    |    orderedBySomething = Collection.OrderBy(bands, p -> p.lastName, "ASC", p -> p.firstName, "DESC")
    |    in Collection.Transform(orderedBySomething, p -> p.firstName + " " + p.lastName)""".stripMargin) {
    _ should orderEvaluateTo("""[
      |"Robin Gibb",
      |"Maurice Gibb",
      |"Barry Gibb",
      |"Al Jardine",
      |"Bruce Johnston",
      |"Mike Love",
      |"David Marks",
      |"Cyril Neville",
      |"Charles Neville",
      |"Art Neville",
      |"Aaron Neville",
      |"Brian Wilson"
      |]""".stripMargin)
  }

  test(snapi"""let regions = Csv.InferAndRead("$tpchRegionCsvLocal")
    |in Collection.Transform(
    |     Collection.OrderBy(regions, r -> r.r_name, "ASC"),
    |     r -> r.r_name)""".stripMargin)(
    _ should orderEvaluateTo("""[null, "AFRICA", "AMERICA", "ASIA", "EUROPE", "MIDDLE EAST"]""")
  )

  test(snapi"""let regions = Csv.InferAndRead("$tpchRegionCsvLocal")
    |in Collection.Transform(
    |     Collection.OrderBy(regions, r -> r.r_name, "DESC"),
    |     r -> r.r_name)""".stripMargin)(
    _ should orderEvaluateTo("""["MIDDLE EAST", "EUROPE", "ASIA", "AMERICA", "AFRICA", null]""")
  )

  test(snapi"""let regions = Csv.InferAndRead("$tpchRegionCsvLocal")
    |in Collection.Transform(
    |     Collection.OrderBy(regions, r -> String.Length(r.r_name), "ASC"),
    |     r -> {name: r.r_name, len: String.Length(r.r_name)})""".stripMargin)(_ should orderEvaluateTo("""[
    |  {name: null, len: null},
    |  {name: "ASIA", len: 4},
    |  {name: "AFRICA", len: 6},
    |  {name: "EUROPE", len: 6},
    |  {name: "AMERICA", len: 7},
    |  {name: "MIDDLE EAST", len: 11}
    |]""".stripMargin))

  test(snapi"""let regions = Csv.InferAndRead("$tpchRegionCsvLocal")
    |in Collection.Transform(
    |     Collection.OrderBy(regions, r -> String.Length(r.r_name), "DESC"),
    |     r -> {name: r.r_name, len: String.Length(r.r_name)})""".stripMargin)(_ should orderEvaluateTo("""[
    |  {name: "MIDDLE EAST", len: 11},
    |  {name: "AMERICA", len: 7},
    |  {name: "AFRICA", len: 6},
    |  {name: "EUROPE", len: 6},
    |  {name: "ASIA", len: 4},
    |  {name: null, len: null}
    |]""".stripMargin))

  // default ordering
  test(snapi"""let regions = Csv.InferAndRead("$tpchRegionCsvLocal")
    |in Collection.OrderBy(regions, r -> r.r_name, "ASC")""".stripMargin)(
    _ should orderEvaluateTo(snapi"""let regions = Csv.InferAndRead("$tpchRegionCsvLocal")
      |in Collection.OrderBy(regions, r -> r.r_name, "ASC")""".stripMargin)
  )

  private val coolBands = tempFile("""band|firstName|lastName|birthYear
    |Bee Gees|Maurice|Gibb|1949
    |Bee Gees|Robin|Gibb|1949
    |Bee Gees|Barry|Gibb|1946
    |Neville Brothers|Aaron|Neville|1941
    |Neville Brothers|Art|Neville|1937
    |Neville Brothers|Charles|Neville|1938
    |Neville Brothers|Cyril|Neville|1948
    |Beach Boys|Brian|Wilson|1942
    |Beach Boys|Mike|Love|1941
    |Beach Boys|Al|Jardine|1942
    |Beach Boys|Bruce|Johnston|1942
    |Beach Boys|David|Marks|1948""".stripMargin)

  test(snapi"""let bands = Csv.InferAndRead("$coolBands"),
    |    orderedBySomething = Collection.OrderBy(bands, p -> p.firstName, "ASC", p -> p.lastName, "ASC")
    |    in Collection.Transform(orderedBySomething, p -> p.firstName + " " + p.lastName)""".stripMargin) {
    _ should orderEvaluateTo("""[
      |"Aaron Neville",
      |"Al Jardine",
      |"Art Neville",
      |"Barry Gibb",
      |"Brian Wilson",
      |"Bruce Johnston",
      |"Charles Neville",
      |"Cyril Neville",
      |"David Marks",
      |"Maurice Gibb",
      |"Mike Love",
      |"Robin Gibb"
      |]""".stripMargin)
  }

  test(snapi"""let bands = Csv.InferAndRead("$coolBands"),
    |    orderedBySomething = Collection.OrderBy(bands, p -> p.lastName, "ASC", p -> p.firstName, "ASC")
    |    in Collection.Transform(orderedBySomething, p -> p.firstName + " " + p.lastName)""".stripMargin) {
    _ should orderEvaluateTo("""[
      |"Barry Gibb",
      |"Maurice Gibb",
      |"Robin Gibb",
      |"Al Jardine",
      |"Bruce Johnston",
      |"Mike Love",
      |"David Marks",
      |"Aaron Neville",
      |"Art Neville",
      |"Charles Neville",
      |"Cyril Neville",
      |"Brian Wilson"
      |]""".stripMargin)
  }

  test(snapi"""let bands = Csv.InferAndRead("$coolBands"),
    |    orderedBySomething = Collection.OrderBy(bands, p -> p.lastName, "ASC", p -> p.firstName, "DESC")
    |    in Collection.Transform(orderedBySomething, p -> p.firstName + " " + p.lastName)""".stripMargin) {
    _ should orderEvaluateTo("""[
      |"Robin Gibb",
      |"Maurice Gibb",
      |"Barry Gibb",
      |"Al Jardine",
      |"Bruce Johnston",
      |"Mike Love",
      |"David Marks",
      |"Cyril Neville",
      |"Charles Neville",
      |"Art Neville",
      |"Aaron Neville",
      |"Brian Wilson"
      |]""".stripMargin)
  }

  test(
    snapi"""let bands = Csv.InferAndRead("$coolBands"),
      |    orderedBySomething = Collection.OrderBy(bands, p -> p.birthYear, "DESC", p -> p.lastName, "ASC")
      |    in Collection.Transform(orderedBySomething, p -> p.lastName + " (" + String.From(p.birthYear) + ")")""".stripMargin
  ) {
    _ should orderEvaluateTo("""[
      |"Gibb (1949)",
      |"Gibb (1949)",
      |"Marks (1948)",
      |"Neville (1948)",
      |"Gibb (1946)",
      |"Jardine (1942)",
      |"Johnston (1942)",
      |"Wilson (1942)",
      |"Love (1941)",
      |"Neville (1941)",
      |"Neville (1938)",
      |"Neville (1937)"
      |]""".stripMargin)
  }

  // Non-comparable types aren't accepted.
  test("""
    |let f(x: int) = x + 1,
    |    g(x: int) = x + 2,
    |    min = Collection.First(Collection.OrderBy(Collection.Build(f, g), f -> f, "ASC"))
    |in min(10)""".stripMargin)(_ should runErrorAs(KeyNotComparable.message))

  test("""
    |let f(x: int) = x + 1,
    |    g(x: int) = x + 2,
    |    r1 = {f1: f, f2: g},
    |    r2 = {f1: g, f2: f},
    |    min = Collection.First(Collection.OrderBy(Collection.Build(r1, r2), r -> r, "ASC"))
    |in min.f2(10) * min.f2(10)""".stripMargin)(_ should runErrorAs(KeyNotComparable.message))

  test("""
    |let f(x: int) = x + 1,
    |    g(x: int) = x + 2,
    |    r1 = {f1: f, f2: g},
    |    r2 = {f1: g, f2: f},
    |    min = Collection.First(Collection.OrderBy(Collection.Build(r1, r2), r -> r, "ASC",  r -> r.f1, "DESC"))
    |in min.f2(10) * min.f2(10)""".stripMargin)(_ should runErrorAs(KeyNotComparable.message))

  test("""Collection.OrderBy(Collection.Build(1,2,3), n -> n * 10, "BIM",  n -> n / 2, "BOUM")""")(
    _ should runErrorAs(InvalidOrderSpec.message)
  )

  test("""Collection.OrderBy(Collection.Build(1,2,3), n -> n * 10, "ASC",  n -> n / 2)""")(
    _ should runErrorAs(OrderSpecMustFollowOrderingFunction.message)
  )

}
