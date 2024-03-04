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
//      |    customerType = type record(customer: collection(record(c_custkey: string))),
//      |    ordersType = type collection(record(o_orderkey: int, o_custkey: int)),
//      |    lineitems = PostgreSQL.Query(
//      |        "postgres",
//      |        "select l_orderkey, (l_extendedprice * (1 - l_discount)) as l_payedammount from tpch1.lineitem",
//      |        lineitemsType,
//      |        host = "localhost",
//      |        username = "postgres",
//      |        password = "1234"
//      |    ),
//      |    customers = Json.Read("file:///home/ld/workspace/TPCH/1GB/customer.json", customerType), // an object with an array inside
//      |    orders = Csv.Read("file:///home/ld/workspace/TPCH/1GB/orders.csv", ordersType, delimiter = "\t"),
//      |    customersOrders = Collection.EquiJoin(customers.customer, orders, (c) -> Int.From(c.c_custkey), (o) -> o.o_custkey),
//      |    customerOrdersItems = Collection.EquiJoin(
//      |        customersOrders,
//      |        lineitems,
//      |        (co) -> Int.From(co.o_orderkey),
//      |        (oi) -> oi.l_orderkey
//      |    ),
//      |    grouped = Collection.GroupBy(customerOrdersItems, (c) -> c.c_custkey),
//      |    result = Collection.Transform(grouped, (g) -> {id: g, total_payed: Collection.Sum(g.group.l_payedammount)}),
//      |    finalResult = Collection.Filter(result, (r) -> r.total_payed > 6000000)
//      |in
//      |    Collection.Count(finalResult)""".stripMargin
//  )(_ should evaluateTo("[]"))

  test("Range Join File with db test") { _ =>
    assume(false, "This test is disabled by default")
    val prog = """let
      |    lineitemsType = type collection(record(l_orderkey: int, l_payedammount: double)),
      |    customerType = type record(customer: collection(record(c_custkey: string))),
      |    ordersType = type collection(record(o_orderkey: int, o_custkey: int)),
      |    lineitems = PostgreSQL.Query(
      |        "postgres",
      |        "select l_orderkey, (l_extendedprice * (1 - l_discount)) as l_payedammount from tpch1.lineitem",
      |        lineitemsType,
      |        host = "localhost:44444",
      |        username = "postgres",
      |        password = "1234"
      |    ),
      |    customers = Json.Read("file:///home/ld/workspace/TPCH/1GB/customer.json", customerType), // an object with an array inside
      |    orders = Csv.Read("file:///home/ld/workspace/TPCH/1GB/orders.csv", ordersType, delimiter = "\t"),
      |    customersOrders = Collection.EquiJoin(customers.customer, orders, (c) -> Int.From(c.c_custkey), (o) -> o.o_custkey),
      |    customerOrdersItems = Collection.EquiJoin(
      |        customersOrders,
      |        lineitems,
      |        (co) -> Int.From(co.o_orderkey),
      |        (oi) -> oi.l_orderkey
      |    ),
      |    grouped = Collection.GroupBy(customerOrdersItems, (c) -> c.c_custkey),
      |    result = Collection.Transform(grouped, (g) -> {id: g, total_payed: Collection.Sum(g.group.l_payedammount)}),
      |    finalResult = Collection.Filter(result, (r) -> r.total_payed > 6000000)
      |in
      |    Collection.Count(finalResult)""".stripMargin

//    fastExecute("""let a = "hello" in a """) // some random query
//    fastExecute("""let a = 2 + 2 in a """) // some random query
//    fastExecute("""let a = 2/2 in a """) // some random query

    fastExecute(prog)
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
