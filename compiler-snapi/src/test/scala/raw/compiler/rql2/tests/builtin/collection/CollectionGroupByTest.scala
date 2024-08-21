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

package raw.compiler.rql2.tests.builtin.collection

import com.rawlabs.compiler.snapi.utils._
import com.rawlabs.compiler.snapi.rql2.errors.KeyNotComparable
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import com.rawlabs.utils.sources.filesystem.local.LocalLocationsTestContext

class CollectionGroupByTest extends Rql2TruffleCompilerTestContext with LocalLocationsTestContext {

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
  test(snapi"""let lineitem = Csv.InferAndRead("$tpchLineitemCsvLocal"),
    |    groups = Collection.GroupBy(lineitem, row -> row.l_returnflag)
    |in groups
    |""".stripMargin) { it =>
    it should typeAs("""collection(record(key: string, group: collection(
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
  test(snapi"""let lineitem = Csv.InferAndRead("$tpchLineitemCsvLocal"),
    |    groups = Collection.GroupBy(lineitem, row -> row.l_returnflag)
    |in Collection.Transform(groups, g -> {
    |        _1: g.key,
    |        _2: Collection.Count(g.group)
    |})
    |""".stripMargin) { it =>
    it should typeAs("collection(record(_1: string, _2: long))")
    it should evaluateTo("""[{_1: "R", _2: 16L}, {_1: "A", _2: 17L}, {_1: null, _2: 1L}, {_1: "N", _2: 34L}]""")
  }

  // test with a hardcoded record type (file is larger)
  test(
    snapi"""let lineitem = Csv.Read("$tpchLineitemTblLocal", type collection($lineitemType), skip=0, delimiter="|"),
      |    groups = Collection.GroupBy(lineitem, row -> row.l_returnflag)
      |    in Collection.Transform(groups, g -> {
      |        _1: g.key,
      |        _2: Collection.Count(g.group)
      |})
      |""".stripMargin
  )(it => it should evaluateTo("""[{_1: "R", _2: 14902}, {_1: "A", _2: 14876}, {_1: "N", _2: 30397}]"""))

  test(snapi"""let articles = Json.InferAndRead("$publicationsJsonLocal")
    |in Collection.GroupBy(articles, a -> Collection.Count(a.authors))""".stripMargin)(_ should run)

  test(snapi"""let articles = Json.InferAndRead("$publicationsJsonLocal")
    |in Collection.Transform(
    |     Collection.GroupBy(articles, a -> Collection.Count(a.authors)),
    |     g -> {nAuthors: g.key, count: Collection.Count(g.group)}
    |)""".stripMargin)(_ should evaluateTo("""[
    |  {nAuthors: 4, count: 161},
    |  {nAuthors: 6, count: 168},
    |  {nAuthors: 1, count: 160},
    |  {nAuthors: 2, count: 163},
    |  {nAuthors: 3, count: 175},
    |  {nAuthors: 5, count: 173}
    |]""".stripMargin))

  private val jsonWithDuplicates = tempFile("""[
    |   {"a": 1, "b": [1,2,3,4,5,6]},
    |   {"a": 1, "b": [1,2,3,4,5,6]},
    |   {"a": 1, "b": [1,2,3,4,5,6]},
    |   {"a": 1, "b": [1,2,3,4,5,6]},
    |   {"a": 1, "b": [1,2,3,5,6]},
    |   {"a": 1, "b": [1,2,3,5,6]}
    |]""".stripMargin)

  test(snapi"""
    |let groupByRow = Collection.GroupBy(Json.InferAndRead("$jsonWithDuplicates"), r -> r),
    |    pseudoDistinct = Collection.Transform(groupByRow, g -> g.key)
    |in pseudoDistinct""".stripMargin) { it =>
    it should evaluateTo("""[ {a: 1, b: [1,2,3,4,5,6] }, { a: 1, b: [1,2,3,5,6] } ]""".stripMargin)
  }
  // Non-comparable types aren't accepted.
  test("""
    |let f(x: int) = x + 1,
    |    g(x: int) = x + 2,
    |    group = Collection.First(Collection.GroupBy(Collection.Build(f, g), f -> f))
    |in group.key(10)""".stripMargin)(_ should runErrorAs(KeyNotComparable.message))

  test("""
    |let f(x: int) = x + 1,
    |    g(x: int) = x + 2,
    |    r1 = {f1: f, f2: g},
    |    r2 = {f1: g, f2: f},
    |    group = Collection.First(Collection.GroupBy(Collection.Build(r1, r2), r -> r))
    |in group.key(10) * group.key(10)""".stripMargin)(_ should runErrorAs(KeyNotComparable.message))

}
