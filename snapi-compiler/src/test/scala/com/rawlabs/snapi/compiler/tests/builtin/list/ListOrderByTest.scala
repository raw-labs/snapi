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
import com.rawlabs.snapi.frontend.rql2.errors.{InvalidOrderSpec, KeyNotComparable}
import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext
import com.rawlabs.utils.sources.filesystem.local.LocalLocationsTestContext

class ListOrderByTest extends Rql2TruffleCompilerTestContext with LocalLocationsTestContext {

  // using String.ReadLines
  test(snapi"""let regions = List.From(Collection.Transform(
    |  String.ReadLines("$tpchRegionTblLocal"),
    |  l ->
    |      let tokens = String.Split(l, "|")
    |      in {
    |          r_regionkey: Int.From(List.Get(tokens, 0)),
    |          r_name: List.Get(tokens, 1),
    |          r_comment: List.Get(tokens, 2)
    |      }
    |  ))
    |in List.Transform(
    |     List.OrderBy(regions, r -> r.r_name, "ASC"),
    |     r -> r.r_name)""".stripMargin)(
    _ should orderEvaluateTo("""["AFRICA", "AMERICA", "ASIA", "EUROPE", "MIDDLE EAST"]""")
  )

  // using String.ReadLines
  test(snapi"""let regions = List.From(Collection.Transform(
    |  String.ReadLines("$tpchRegionTblLocal"),
    |  l ->
    |      let tokens = String.Split(l, "|")
    |      in {
    |          r_regionkey: Int.From(List.Get(tokens, 0)),
    |          r_name: List.Get(tokens, 1),
    |          r_comment: List.Get(tokens, 2)
    |      }
    |  ))
    |in List.Transform(
    |     List.OrderBy(regions, r -> r.r_name, "DESC"),
    |     r -> r.r_name)""".stripMargin)(
    _ should orderEvaluateTo("""["MIDDLE EAST", "EUROPE", "ASIA", "AMERICA", "AFRICA"]""")
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

  test(
    snapi"""let bands = List.From(Csv.InferAndRead("$coolBands")),
      |    orderedBySomething = List.OrderBy(bands, p -> p.birthYear, "DESC", p -> p.lastName, "ASC")
      |    in List.Transform(orderedBySomething, p -> p.lastName + " (" + String.From(p.birthYear) + ")")""".stripMargin
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
    |    min = List.First(List.OrderBy(List.Build(f, g), f -> f, "ASC"))
    |in min(10)""".stripMargin)(_ should runErrorAs(KeyNotComparable.message))

  test("""
    |let f(x: int) = x + 1,
    |    g(x: int) = x + 2,
    |    r1 = {f1: f, f2: g},
    |    r2 = {f1: g, f2: f},
    |    min = List.First(List.OrderBy(List.Build(r1, r2), r -> r, "ASC"))
    |in min.f2(10) * min.f2(10)""".stripMargin)(_ should runErrorAs(KeyNotComparable.message))

  test("""
    |let f(x: int) = x + 1,
    |    g(x: int) = x + 2,
    |    r1 = {f1: f, f2: g},
    |    r2 = {f1: g, f2: f},
    |    min = List.First(List.OrderBy(List.Build(r1, r2), r -> r, "ASC",  r -> r.f1, "DESC"))
    |in min.f2(10) * min.f2(10)""".stripMargin)(_ should runErrorAs(KeyNotComparable.message))

  test("""List.OrderBy(List.Build(1,2,3), n -> n * 10, "BIM",  n -> n / 2, "BOUM")""")(
    _ should runErrorAs(InvalidOrderSpec.message)
  )

}
