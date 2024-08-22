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

import com.rawlabs.snapi.frontend.utils._
import com.rawlabs.snapi.frontend.rql2.errors.KeyNotComparable
import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext
import com.rawlabs.utils.sources.filesystem.local.LocalLocationsTestContext

class ListGroupByTest extends Rql2TruffleCompilerTestContext with LocalLocationsTestContext {

  private val lineitemType = """record(
    |   l_orderkey: int,
    |   l_partkey: int,
    |   l_suppkey: int,
    |   l_linenumber: int,
    |   l_quantity: decimal,
    |   l_extendedprice: decimal,
    |   l_discount: decimal,
    |   l_tax: decimal,
    |   l_returnflag: string,
    |   l_linestatus: string,
    |   l_shipdate: date,
    |   l_commitdate: date,
    |   l_receiptdate: date,
    |   l_shipinstruct: string,
    |   l_shipmode: string,
    |   l_comment: string
    |)""".stripMargin

  // test against a short but easy to infer CSV file
  test(snapi"""let lineitem = List.From(Csv.InferAndRead("$tpchLineitemCsvLocal")),
    |    groups = List.GroupBy(lineitem, row -> row.l_returnflag)
    |in groups
    |""".stripMargin) { it =>
    it should typeAs("""list(record(key: string, group: list(
      |            record(
      |                l_orderkey: int,
      |                l_partkey: int,
      |                l_suppkey: int,
      |                l_linenumber: int,
      |                l_quantity: int,
      |                l_extendedprice: double,
      |                l_discount: double,
      |                l_tax: double,
      |                l_returnflag: string,
      |                l_linestatus: string,
      |                l_shipdate: string,
      |                l_commitdate: string,
      |                l_receiptdate: string,
      |                l_shipinstruct: string,
      |                l_shipmode: string,
      |                l_comment: string))))""".stripMargin)
    it should run
  }

  // test against a short but easy to infer CSV file
  test(snapi"""let lineitem = List.From(Csv.InferAndRead("$tpchLineitemCsvLocal")),
    |    groups = List.GroupBy(lineitem, row -> row.l_returnflag)
    |in List.Transform(groups, g -> {
    |        _1: g.key,
    |        _2: List.Count(g.group)
    |})
    |""".stripMargin) { it =>
    it should typeAs("list(record(_1: string, _2: long))")
    it should evaluateTo("""[{_1: "R", _2: 16L}, {_1: "A", _2: 17L}, {_1: null, _2: 1L}, {_1: "N", _2: 34L}]""")
  }

  // test with a hardcoded record type (file is larger)
  test(
    snapi"""let lineitem = List.From(Csv.Read("$tpchLineitemTblLocal", type collection($lineitemType), skip=0, delimiter="|")),
      |    groups = List.GroupBy(lineitem, row -> row.l_returnflag)
      |    in List.Transform(groups, g -> {
      |        _1: g.key,
      |        _2: List.Count(g.group)
      |})
      |""".stripMargin
  )(it => it should evaluateTo("""[{_1: "R", _2: 14902}, {_1: "A", _2: 14876}, {_1: "N", _2: 30397}]"""))

  // Non-comparable types aren't accepted.
  test("""
    |let f(x: int) = x + 1,
    |    g(x: int) = x + 2,
    |    group = List.First(List.GroupBy(List.Build(f, g), f -> f))
    |in group.key(10)""".stripMargin)(_ should runErrorAs(KeyNotComparable.message))

  test("""
    |let f(x: int) = x + 1,
    |    g(x: int) = x + 2,
    |    r1 = {f1: f, f2: g},
    |    r2 = {f1: g, f2: f},
    |    group = List.First(List.GroupBy(List.Build(r1, r2), r -> r))
    |in group.key(10) * group.key(10)""".stripMargin)(_ should runErrorAs(KeyNotComparable.message))

}
