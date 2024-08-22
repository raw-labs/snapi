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

import com.rawlabs.snapi.frontend.utils._
import com.rawlabs.snapi.frontend.rql2.errors.KeyNotComparable
import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext
import com.rawlabs.utils.sources.filesystem.local.LocalLocationsTestContext

class CollectionJoinTest extends Rql2TruffleCompilerTestContext with LocalLocationsTestContext {

  test(snapi"""let regions = Csv.InferAndRead("$tpchRegionCsvLocal"),
    |    nations = Csv.InferAndRead("$tpchNationCsvLocal")
    |in
    |Collection.Transform(
    |  Collection.EquiJoin(regions, nations, r -> r.r_regionkey, n -> n.n_regionkey),
    |  row -> {region: row.r_name, nation: row.n_name})
    |""".stripMargin)(it => it should evaluateTo(listOfCountries))

  // nullable/tryable records
  test(snapi"""let rType = type collection(record(r_regionkey: int, r_name: string, r_comment: string)),
    |    nType = type collection(
    |        record(
    |            n_nationkey: int,
    |            n_name: string,
    |            n_regionkey: int,
    |            n_comment: string)),
    |    regions = Csv.Read("$tpchRegionCsvLocal", type rType),
    |    nations = Csv.Read("$tpchNationCsvLocal", type nType)
    |in
    |Collection.Transform(
    |  Collection.EquiJoin(regions, nations, r -> r.r_regionkey, n -> n.n_regionkey),
    |  row -> {region: row.r_name, nation: row.n_name})
    |""".stripMargin)(it => it should evaluateTo(listOfCountries))

  test(snapi"""let regions = Csv.InferAndRead("$tpchRegionCsvLocal"),
    |    nations = Csv.InferAndRead("$tpchNationCsvLocal")
    |in
    |Collection.Transform(
    |  Collection.Join(regions, nations, row -> row.r_regionkey == row.n_regionkey),
    |  row -> {region: row.r_name, nation: row.n_name})
    |""".stripMargin)(it => it should evaluateTo(listOfCountries))

  test(snapi"""let regions = Csv.InferAndRead("$tpchRegionCsvLocal"),
    |    nations = Csv.InferAndRead("$tpchNationCsvLocal"),
    |    countries = Collection.Transform(
    |      Collection.EquiJoin(regions, nations, r -> r.r_regionkey, n -> n.n_regionkey),
    |      row -> {region: row.r_name, nation: row.n_name}),
    |    countriesOf(region: string) = Collection.Transform(Collection.Filter(countries, c -> c.region == region), c -> c.nation)
    |in {
    |  africa: countriesOf("AFRICA"),
    |  asia: countriesOf("ASIA")
    |}
    |""".stripMargin) { it =>
    it should evaluateTo("""{
      |  africa: ["ALGERIA", "ETHIOPIA", "KENYA", "MOROCCO", "MOZAMBIQUE", null],
      |  asia: ["CHINA", "INDIA", "INDONESIA", "JAPAN", "VIETNAM"]
      |}""".stripMargin)
  }

  test(snapi"""let regions = Csv.InferAndRead("$tpchRegionCsvLocal"),
    |    nations = Csv.InferAndRead("$tpchNationCsvLocal"),
    |    countries = Collection.Transform(
    |      Collection.Join(regions, nations, row -> row.r_regionkey == row.n_regionkey),
    |      row -> {region: row.r_name, nation: row.n_name}),
    |    countriesOf(region: string) = Collection.Transform(Collection.Filter(countries, c -> c.region == region), c -> c.nation)
    |in {
    |  africa: countriesOf("AFRICA"),
    |  asia: countriesOf("ASIA")
    |}
    |""".stripMargin) { it =>
    it should evaluateTo("""{
      |  africa: ["ALGERIA", "ETHIOPIA", "KENYA", "MOROCCO", "MOZAMBIQUE", null],
      |  asia: ["CHINA", "INDIA", "INDONESIA", "JAPAN", "VIETNAM"]
      |}""".stripMargin)
  }

  // nullable/tryable records
  test(snapi"""let rType = type collection(record(r_regionkey: int, r_name: string, r_comment: string)),
    |    nType = type collection(
    |        record(
    |            n_nationkey: int,
    |            n_name: string,
    |            n_regionkey: int,
    |            n_comment: string)),
    |    regions = Csv.Read("$tpchRegionCsvLocal", type rType),
    |    nations = Csv.Read("$tpchNationCsvLocal", type nType),
    |    countries = Collection.Transform(
    |      Collection.Join(regions, nations, row -> row.r_regionkey == row.n_regionkey),
    |      row -> {region: row.r_name, nation: row.n_name}),
    |    countriesOf(region: string) = Collection.Transform(Collection.Filter(countries, c -> c.region == region), c -> c.nation)
    |in {
    |  africa: countriesOf("AFRICA"),
    |  asia: countriesOf("ASIA")
    |}
    |""".stripMargin) { it =>
    it should evaluateTo("""{
      |  africa: ["ALGERIA", "ETHIOPIA", "KENYA", "MOROCCO", "MOZAMBIQUE", null],
      |  asia: ["CHINA", "INDIA", "INDONESIA", "JAPAN", "VIETNAM"]
      |}""".stripMargin)
  }

  test(snapi"""let regions = Csv.InferAndRead("$tpchRegionCsvLocal"),
    |    keys = Collection.From([0, 2, 4])
    |in Collection.Transform(Collection.EquiJoin(regions, keys, r -> r.r_regionkey, k -> k), row -> row.r_name)
    |""".stripMargin)(it => it should evaluateTo("""["AFRICA", "ASIA", "MIDDLE EAST"]"""))

  test(snapi"""let regions = Csv.InferAndRead("$tpchRegionCsvLocal"),
    |    keys = Collection.From([0, 2, 4])
    |in Collection.Transform(Collection.Join(regions, keys, row -> row.r_regionkey == row._1), row -> row.r_name)
    |""".stripMargin)(it => it should evaluateTo("""["AFRICA", "ASIA", "MIDDLE EAST"]"""))

  test(snapi"""let keys = Collection.From([0, 2, 4])
    |in Collection.EquiJoin(keys, keys, row -> row, row -> row + 2)
    |""".stripMargin)(it => it should evaluateTo("""[{_1: 2, _2: 0}, {_1: 4, _2: 2}]"""))

  test(snapi"""let keys = Collection.From([0, 2, 4])
    |in Collection.Join(keys, keys, row -> row._1 == row._2 + 2)
    |""".stripMargin)(it => it should evaluateTo("""[{_1: 2, _2: 0}, {_1: 4, _2: 2}]"""))

  test(snapi"""let regions = Csv.InferAndRead("$tpchRegionCsvLocal"),
    |    keys = Collection.From([])
    |in Collection.Transform(Collection.Join(regions, keys, row -> row.r_regionkey == row._1), row -> row.r_name)
    |""".stripMargin)(it => it should evaluateTo("""[]"""))

  test(snapi"""let regions = Csv.InferAndRead("$tpchRegionCsvLocal"),
    |    keys = Collection.From([])
    |in Collection.Transform(Collection.Join(keys, regions, row -> row.r_regionkey == row._1), row -> row.r_name)
    |""".stripMargin)(it => it should evaluateTo("""[]"""))

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

  // Functions aren't comparable
  test("""let l = Collection.Build({fName: "f1", f: (x: int) -> x + 1}, {fName: "f2", f: (x: int) -> x * 2})
    |in Collection.EquiJoin(l, l, r -> r.f, r -> r.f)""".stripMargin)(_ should runErrorAs(KeyNotComparable.message))

  // docs examples (RD-9034, with a function with two parameters)
  test("""let
    |  first = Collection.Build( {v: 1}, {v: 2}, {v: 3} ),
    |  second = Collection.Build( {n: 1, name: "One"}, {n: 2, name: "Two"} )
    |in
    |  Collection.Join(first, second, (row1, row2) -> row1.v == row2.n)""".stripMargin)(_ should evaluateTo("""
    |[ { v: 1, n: 1, name: "One" }, { v: 2, n: 2, name: "Two" } ]""".stripMargin))

  test("""let
    |  first = Collection.Build( {v: 1}, {v: 2}, {v: 3} ),
    |  second = Collection.Build( {n: 1, name: "One"}, {n: 2, name: "Two"} )
    |in
    |  Collection.Join(first, second, (row1: record(v: int), row2) -> row1.v == row2.n)""".stripMargin)(
    _ should evaluateTo("""
      |[ { v: 1, n: 1, name: "One" }, { v: 2, n: 2, name: "Two" } ]""".stripMargin)
  )

  test("""let
    |  first = Collection.Build( {v: 1}, {v: 2}, {v: 3} ),
    |  second = Collection.Build( {n: 1, name: "One"}, {n: 2, name: "Two"} )
    |in
    |  Collection.Join(first, second, (row1, row2: record(n: int, name: string)) -> row1.v == row2.n)""".stripMargin)(
    _ should evaluateTo("""
      |[ { v: 1, n: 1, name: "One" }, { v: 2, n: 2, name: "Two" } ]""".stripMargin)
  )

  test(
    """let
      |  first = Collection.Build( {v: 1}, {v: 2}, {v: 3} ),
      |  second = Collection.Build( {n: 1, name: "One"}, {n: 2, name: "Two"} )
      |in
      |  Collection.Join(first, second, (row1: record(v: int), row2: record(n: int, name: string)) -> row1.v == row2.n)""".stripMargin
  )(_ should evaluateTo("""
    |[ { v: 1, n: 1, name: "One" }, { v: 2, n: 2, name: "Two" } ]""".stripMargin))

  test("""// items of a collection that isn't a collection of records, appear with an automatically generated
    |// field name in the joined record.
    |let
    |  first = Collection.Build(1,2,3),
    |  second = Collection.Build( {n: 1, name: "One"}, {n: 2, name: "Two"} )
    |in
    |  Collection.Join(first, second, (v, row2) -> v == row2.n)""".stripMargin)(_ should evaluateTo("""
    |[ { _1: 1, n: 1, name: "One" }, { _1: 2, n: 2, name: "Two" } ]""".stripMargin))

}
