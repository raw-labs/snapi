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
import com.rawlabs.snapi.frontend.rql2.errors.KeyNotComparable
import com.rawlabs.snapi.compiler.tests.Rql2TestContext
import com.rawlabs.utils.sources.filesystem.local.LocalLocationsTestContext

class ListJoinTest extends Rql2TestContext with LocalLocationsTestContext {

  private val listOfCountries = """[
    | {region: "AFRICA", nation: "ALGERIA"},
    | {region: "AFRICA", nation: "ETHIOPIA"},
    | {region: "AFRICA", nation: "KENYA"},
    | {region: "AFRICA", nation: "MOROCCO"},
    | {region: "AFRICA", nation: "MOZAMBIQUE"},
    | {region: "AFRICA", nation: null},
    | {region: "AMERICA", nation: "ARGENTINA"},
    | {region: "AMERICA", nation: "BRAZIL"},
    | {region: "AMERICA", nation: "CANADA"},
    | {region: "AMERICA", nation: "PERU"},
    | {region: "AMERICA", nation: "UNITED STATES"},
    | {region: "ASIA", nation: "CHINA"},
    | {region: "ASIA", nation: "INDIA"},
    | {region: "ASIA", nation: "INDONESIA"},
    | {region: "ASIA", nation: "JAPAN"},
    | {region: "ASIA", nation: "VIETNAM"},
    | {region: "EUROPE", nation: "FRANCE"},
    | {region: "EUROPE", nation: "GERMANY"},
    | {region: "EUROPE", nation: "ROMANIA"},
    | {region: "EUROPE", nation:"RUSSIA"},
    | {region: "EUROPE", nation: "UNITED KINGDOM"},
    | {region: "MIDDLE EAST", nation: "EGYPT"},
    | {region: "MIDDLE EAST", nation: "IRAN"},
    | {region: "MIDDLE EAST", nation: "IRAQ"},
    | {region: "MIDDLE EAST", nation: "JORDAN"},
    | {region: "MIDDLE EAST", nation: "SAUDI ARABIA"}
    |]
    |""".stripMargin

  test(snapi"""let regions = List.From(Csv.InferAndRead("$tpchRegionCsvLocal")),
    |    nations = List.From(Csv.InferAndRead("$tpchNationCsvLocal"))
    |in
    |List.Transform(
    |  List.EquiJoin(regions, nations, r -> r.r_regionkey, n -> n.n_regionkey),
    |  row -> {region: row.r_name, nation: row.n_name})
    |""".stripMargin)(it => it should evaluateTo(listOfCountries))

  test(snapi"""let regions = List.From(Csv.InferAndRead("$tpchRegionCsvLocal")),
    |    nations = List.From(Csv.InferAndRead("$tpchNationCsvLocal"))
    |in
    |List.Transform(
    |  List.Join(regions, nations, row -> row.r_regionkey == row.n_regionkey),
    |  row -> {region: row.r_name, nation: row.n_name})
    |""".stripMargin)(it => it should evaluateTo(listOfCountries))

  // Functions aren't comparable
  test("""let l = List.Build({fName: "f1", f: (x: int) -> x + 1}, {fName: "f2", f: (x: int) -> x * 2})
    |in List.EquiJoin(l, l, r -> r.f, r -> r.f)""".stripMargin)(_ should runErrorAs(KeyNotComparable.message))

  // docs examples (RD-9034, with a function with two parameters)
  test("""let
    |  first = List.Build( {v: 1}, {v: 2}, {v: 3} ),
    |  second = List.Build( {n: 1, name: "One"}, {n: 2, name: "Two"} )
    |in
    |  List.Join(first, second, (row1, row2) -> row1.v == row2.n)""".stripMargin)(_ should evaluateTo("""
    |[ { v: 1, n: 1, name: "One" }, { v: 2, n: 2, name: "Two" } ]""".stripMargin))

  test("""let
    |  first = List.Build( {v: 1}, {v: 2}, {v: 3} ),
    |  second = List.Build( {n: 1, name: "One"}, {n: 2, name: "Two"} )
    |in
    |  List.Join(first, second, (row1: record(v: int), row2) -> row1.v == row2.n)""".stripMargin)(
    _ should evaluateTo("""
      |[ { v: 1, n: 1, name: "One" }, { v: 2, n: 2, name: "Two" } ]""".stripMargin)
  )

  test("""let
    |  first = List.Build( {v: 1}, {v: 2}, {v: 3} ),
    |  second = List.Build( {n: 1, name: "One"}, {n: 2, name: "Two"} )
    |in
    |  List.Join(first, second, (row1, row2: record(n: int, name: string)) -> row1.v == row2.n)""".stripMargin)(
    _ should evaluateTo("""
      |[ { v: 1, n: 1, name: "One" }, { v: 2, n: 2, name: "Two" } ]""".stripMargin)
  )

  test(
    """let
      |  first = List.Build( {v: 1}, {v: 2}, {v: 3} ),
      |  second = List.Build( {n: 1, name: "One"}, {n: 2, name: "Two"} )
      |in
      |  List.Join(first, second, (row1: record(v: int), row2: record(n: int, name: string)) -> row1.v == row2.n)""".stripMargin
  )(_ should evaluateTo("""
    |[ { v: 1, n: 1, name: "One" }, { v: 2, n: 2, name: "Two" } ]""".stripMargin))

  test("""// items of a list that isn't a list of records, appear with an automatically generated
    |// field name in the joined record.
    |let
    |  first = List.Build(1,2,3),
    |  second = List.Build( {n: 1, name: "One"}, {n: 2, name: "Two"} )
    |in
    |  List.Join(first, second, (v, row2) -> v == row2.n)""".stripMargin)(_ should evaluateTo("""
    |[ { _1: 1, n: 1, name: "One" }, { _1: 2, n: 2, name: "Two" } ]""".stripMargin))

}
