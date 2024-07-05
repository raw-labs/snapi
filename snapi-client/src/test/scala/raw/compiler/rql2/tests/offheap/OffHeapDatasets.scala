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

package raw.compiler.rql2.tests.offheap

import raw.compiler.utils._
import raw.compiler.rql2.tests.Rql2CompilerTestContext

trait OffHeapDatasets extends Rql2CompilerTestContext {

  protected val N = 100

  private val csvBandMembers = tempFile("""Bee Gees|Maurice|Gibb|1949
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
    |Beach Boys|David|Marks|1948
    |Dream Theater|John|Petrucci|1967
    |Dream Theater|John|Myung|1967
    |Dream Theater|James|LaBrie|1963
    |Dream Theater|Jordan|Ruddess|1956
    |Dream Theater|Mike|Mangini|1963""".stripMargin)

  private val onceTheBandMembers = snapi"""String.ReadLines("$csvBandMembers")"""
  private val nTimesTheBandMembers = s"""Collection.Unnest(Int.Range(0, $N), _ -> $onceTheBandMembers)"""

  protected val olympics = snapi"""Collection.Build(
    |   {year: 2016, city: "Rio de Janeiro"},
    |   {year: 2012, city: "London"},
    |   {year: 2008, city: "Beijing"},
    |   {year: 2004, city: "Athens"},
    |   {year: 2000, city: "Sydney"},
    |   {year: 1996, city: "Atlanta"},
    |   {year: 1992, city: "Barcelona"},
    |   {year: 1988, city: "Seoul"},
    |   {year: 1984, city: "Los Angeles"},
    |   {year: 1980, city: "Moscow"},
    |   {year: 1976, city: "Montreal"},
    |   {year: 1972, city: "Munich"},
    |   {year: 1968, city: "Mexico City"},
    |   {year: 1964, city: "Tokyo"},
    |   {year: 1960, city: "Rome"},
    |   {year: 1956, city: "Melbourne"},
    |   {year: 1952, city: "Helsinki"},
    |   {year: 1948, city: "London"},
    |   {year: 1936, city: "Berlin"},
    |   {year: 1932, city: "Los Angeles"},
    |   {year: 1928, city: "Amsterdam"},
    |   {year: 1924, city: "Paris"},
    |   {year: 1920, city: "Antwerp"},
    |   {year: 1912, city: "Stockholm"},
    |   {year: 1908, city: "London"},
    |   {year: 1904, city: "St. Louis"},
    |   {year: 1900, city: "Paris"},
    |   {year: 1896, city: "Athens"}
    |)""".stripMargin

  protected val bandMembers: String = snapi"""
    |let tokens = Collection.Transform($onceTheBandMembers, l -> String.Split(l, "|"))
    |in Collection.Transform(tokens, i -> {
    |     band: List.Get(i, 0),
    |     firstName: List.Get(i, 1),
    |     lastName: List.Get(i, 2),
    |     birthYear: Int.From(List.Get(i, 3))
    |})""".stripMargin

  protected val NTimesBandMembers: String = snapi"""
    |let tokens = Collection.Transform($nTimesTheBandMembers, l -> String.Split(l, "|"))
    |in Collection.Transform(tokens, i -> {
    |     band: List.Get(i, 0),
    |     firstName: List.Get(i, 1),
    |     lastName: List.Get(i, 2),
    |     birthYear: Int.From(List.Get(i, 3))
    |})""".stripMargin

}
