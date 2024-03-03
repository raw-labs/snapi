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

package raw.compiler.rql2.tests.benchmark

import raw.compiler.rql2.tests.CompilerTestContext

trait BenchmarkTests extends CompilerTestContext {

  property("raw.training-wheels", "false")

  //testing if the code is running
//  test(
//    """let
//      |    lineitemsType = type collection(record(l_orderkey: int, l_payedammount: double)),
//      |    customerType = type record(
//      |        customer: collection(record(c_custkey: string, c_name: string, c_address: string, c_phone: string))
//      |    ),
//      |    ordersType = type collection(record(o_orderkey: int, o_custkey: int)),
//      |    lineitems = PostgreSQL.Query(
//      |        "postgres",
//      |        "select l_orderkey, (l_extendedprice * (1 - l_discount)) as l_payedammount from tpch10.lineitem",
//      |        lineitemsType,
//      |        host = "localhost",
//      |        username = "postgres",
//      |        password = "1234"
//      |    ),
//      |    customers = Json.Read("file:///home/ld/workspace/TPCH/10GB/customer.json", customerType), // an object with an array inside
//      |    orders = Csv.Read("file:///home/ld/workspace/TPCH/10GB/orders.csv", ordersType, delimiter = "\t"),
//      |    customersOrders = Collection.EquiJoin(customers.customer, orders, (c) -> Int.From(c.c_custkey), (o) -> o.o_custkey),
//      |    ordersItems = Collection.EquiJoin(orders, lineitems, (o) -> o.o_orderkey, (l) -> l.l_orderkey),
//      |    customerOrdersItems = Collection.EquiJoin(customersOrders, ordersItems, (co) -> Int.From(co.c_custkey), (oi) -> oi.o_custkey),
//      |    grouped = Collection.GroupBy(
//      |        customerOrdersItems,
//      |        (c) -> c.c_custkey + "|" + c.c_name + "|" + c.c_address + "|" + c.c_phone
//      |    ),
//      |    result = Collection.Transform(
//      |        grouped,
//      |        (g) ->
//      |            let
//      |                splited = String.Split(g.key, "|"),
//      |                name = List.Get(splited, 1),
//      |                address = List.Get(splited, 2),
//      |                phone = List.Get(splited, 3)
//      |            in
//      |                {name: name, address: address, phone: phone, total_payed: Collection.Sum(g.group.l_payedammount)}
//      |    )
//      |in
//      |    Collection.Take(ordersItems, 5)""".stripMargin
//  )(_ should evaluateTo("[]"))

  test("Range Join File with db test") { _ =>
    assume(false, "This test is disabled by default")
    fastExecute(
      """let
        |    lineitemsType = type collection(
        |        record(
        |           l_orderkey: int,
        |           l_partkey: int,
        |           l_suppkey: int,
        |           l_linenumber: int,
        |           l_quantity: double,
        |           l_extendedprice: double,
        |           l_discount: double,
        |           l_tax: double,
        |           l_returnflag: string,
        |           l_linestatus: string,
        |           l_shipdate: date,
        |           l_commitdate: date,
        |           l_receiptdate: date,
        |           l_shipinstruct: string,
        |           l_shipmode: string,
        |           l_comment: string,
        |           l_dummy: string
        |        )
        |    ),
        |    customerType = type record(
        |        customer: collection(
        |            record(
        |                c_custkey: string,
        |                c_name: string,
        |                c_address: string,
        |                c_nationkey: string,
        |                c_phone: string,
        |                c_acctbal: string,
        |                c_mktsegment: string,
        |                c_comment: string,
        |                c_dummy: string
        |            )
        |        )
        |    ),
        |    ordersType = type collection(
        |        record(
        |            o_orderkey: int,
        |            o_custkey: int,
        |            o_orderstatus: string,
        |            o_totalprice: double,
        |            o_orderdate: string,
        |            o_orderpriority: string,
        |            o_clerk: string,
        |            o_shippriority: int,
        |            o_comment: string,
        |            o_dummy: string
        |        )
        |    ),
        |    lineitems = PostgreSQL.Read(
        |        "postgres",
        |        "tpch10",
        |        "lineitem",
        |        lineitemsType,
        |        host = "localhost",
        |        username = "postgres",
        |        password = "1234"
        |    ),
        |    customers = Json.Read("file:///home/ld/workspace/TPCH/10GB/customer.json", customerType), // an object with an array inside
        |    orders = Csv.Read("file:///home/ld/workspace/TPCH/10GB/orders.csv", ordersType, delimiter = "\t")
        |in
        |    List.Build(Collection.Count(suppliers), Collection.Count(customers.customer), Collection.Count(orders))""".stripMargin
    )

  }

  test("Range Count test") { _ =>
    assume(false, "This test is disabled by default")

    for (i <- 0 to 20) {
      fastExecute(
        """let
          |    range = 1000L
          |in
          |    Collection.Count(Collection.Transform(Collection.Filter(Long.Range(0, range), (x) -> x % 2 == 0), (y) -> y + 1))""".stripMargin
      )
    }

    val values = Array.fill(7)(0L)

    for (i <- 0 to 6) {
      logger.info("++++++++++ 10^" + (i + 3))
      val startedIn = System.currentTimeMillis()
      fastExecute(
        s"""let
          |    range = ${Math.pow(10, i + 3).toLong}L
          |in
          |    Collection.Count(Collection.Transform(Collection.Filter(Long.Range(0, range), (x) -> x % 2 == 0), (y) -> y + 1))""".stripMargin
      )
      val elapsedIn = System.currentTimeMillis()
      values(i) = elapsedIn - startedIn
    }
    logger.info("++++++++++ Values: " + values.mkString(", "))

//    Execution with STD
//    val numberOfRuns = 100
//    fastExecute("""let a = "hello" in a """) // some random query
//    fastExecute("""let a = 2 + 2 in a """) // some random query
//    fastExecute("""let a = 2/2 in a """) // some random query
//
//    val prog = """let
//      |    range = 100000000L
//      |in
//      |    Collection.Count(Collection.Transform(Collection.Filter(Long.Range(0, range), (x) -> x % 2 == 0), (y) -> y + 1))""".stripMargin
//
//    //Warmup
//    for (i <- 0 to 20) {
//      fastExecute(prog)
//    }
//    logger.info("++++++++++ Warmup finished")
//    val values = Array.fill(numberOfRuns + 1)(0L)
//
//    val started = System.currentTimeMillis()
//    for (i <- 0 to numberOfRuns) {
//      val startedIn = System.currentTimeMillis()
//      fastExecute(prog)
//      val elapsedIn = System.currentTimeMillis()
//      values(i) = elapsedIn - startedIn
//    }
//    val elapsed = System.currentTimeMillis()
//
//    val mean = (elapsed - started) / numberOfRuns
//
//    var standardDeviation = 0.0
//    for (num <- values) {
//      standardDeviation += Math.pow(num - mean, 2)
//    }
//
//    logger.info("++++++++++ Average execution time: " + mean)
//    logger.info("++++++++++ Standard deviation is: " + Math.sqrt(standardDeviation / numberOfRuns))
  }
}
