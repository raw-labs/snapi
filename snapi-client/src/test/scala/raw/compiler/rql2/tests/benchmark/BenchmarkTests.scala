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

  //  testing if the code is running
//    test(
//      """let
//        |    lineitemsType = type collection(record(l_orderkey: int, l_payedammount: double)),
//        |    customerType = type record(customer: collection(record(c_custkey: string))),
//        |    ordersType = type collection(record(o_orderkey: int, o_custkey: int)),
//        |    lineitems = PostgreSQL.Query(
//        |        "postgres",
//        |        "select l_orderkey, (l_extendedprice * (1 - l_discount)) as l_payedammount from tpch1.lineitem",
//        |        lineitemsType,
//        |        host = "localhost:44444",
//        |        username = "postgres",
//        |        password = "1234"
//        |    ),
//        |    customers = Json.Read("file:///Users/alexzerntev/workspace/TPCH/1GB/customer.json", customerType), // an object with an array inside
//        |    orders = Csv.Read("file:///Users/alexzerntev/workspace/TPCH/1GB/orders.csv", ordersType, delimiter = "\t"),
//        |    customersOrders = Collection.EquiJoin(customers.customer, orders, (c) -> Int.From(c.c_custkey), (o) -> o.o_custkey),
//        |    customerOrdersItems = Collection.EquiJoin(
//        |        customersOrders,
//        |        lineitems,
//        |        (co) -> Int.From(co.o_orderkey),
//        |        (oi) -> oi.l_orderkey
//        |    ),
//        |    grouped = Collection.GroupBy(customerOrdersItems, (c) -> c.c_custkey),
//        |    result = Collection.Transform(grouped, (g) -> {id: g, total_payed: Collection.Sum(g.group.l_payedammount)}),
//        |    finalResult = Collection.Filter(result, (r) -> r.total_payed > 6000000)
//        |in
//        |    Collection.Count(finalResult)""".stripMargin
//    )(_ should evaluateTo("9L"))

  test("Debugging") { _ =>
    assume(false, "This test is disabled by default")

    val prog = """let
      |    col1 = Collection.Transform(Long.Range(0, 10000000), (i) -> {a1: i, b1: String.From(i)}),
      |    col2 = Collection.Transform(Long.Range(0, 1000000), (i) -> {a2: i, b2: String.From(i)}),
      |    col3 = Collection.Transform(Long.Range(0, 1000000), (i) -> {a3: i, b3: String.From(i)}),
      |    result1 = Collection.EquiJoin(col1, col2, i -> i.a1, i->i.a2),
      |    result2 = Collection.EquiJoin(result1, col3, i -> i.a1, i->i.a3)
      |in
      |    Collection.Count(result2)""".stripMargin

    val startedIn = System.currentTimeMillis()
    fastExecute(prog)
    val elapsedIn = System.currentTimeMillis()
    logger.info("++++++++++ First run: " + (elapsedIn - startedIn))

    val numberOfRuns = 0

    val values = Array.fill(numberOfRuns)(0L)

    val lastIdx = numberOfRuns - 1

    for (i <- 0 to lastIdx) {
      val startedIn = System.currentTimeMillis()
      fastExecute(prog)
      val elapsedIn = System.currentTimeMillis()
      values(i) = elapsedIn - startedIn
      logger.info("++++++++++ Next run: " + values(i))
    }

    val mean = (values.sum) / 1

    var standardDeviation = 0.0
    for (num <- values) {
      standardDeviation += Math.pow(num - mean, 2)
    }

    logger.info("++++++++++ Average execution time: " + mean)
    logger.info("++++++++++ Standard deviation is: " + Math.sqrt(standardDeviation / numberOfRuns))
  }

  test("Range Join File with db test tpch1") { _ =>
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
      |    customers = Json.Read("file:///Users/alexzerntev/workspace/TPCH/1GB/customer.json", customerType), // an object with an array inside
      |    orders = Csv.Read("file:///Users/alexzerntev/workspace/TPCH/1GB/orders.csv", ordersType, delimiter = "\t"),
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

    val startedIn = System.currentTimeMillis()
    fastExecute(prog)
    val elapsedIn = System.currentTimeMillis()
    logger.info("++++++++++ First run: " + (elapsedIn - startedIn))

    val numberOfRuns = 10

    val values = Array.fill(numberOfRuns)(0L)

    val lastIdx = numberOfRuns - 1

    for (i <- 0 to lastIdx) {
      val startedIn = System.currentTimeMillis()
      fastExecute(prog)
      val elapsedIn = System.currentTimeMillis()
      values(i) = elapsedIn - startedIn
      logger.info("++++++++++ Next run: " + values(i))
    }

    val mean = (values.sum) / numberOfRuns

    var standardDeviation = 0.0
    for (num <- values) {
      standardDeviation += Math.pow(num - mean, 2)
    }

    logger.info("++++++++++ Average execution time: " + mean)
    logger.info("++++++++++ Standard deviation is: " + Math.sqrt(standardDeviation / numberOfRuns))
  }

  test("Range Join File with db test tpch10") { _ =>
    assume(false, "This test is disabled by default")

    val prog = """let
      |    lineitemsType = type collection(record(l_orderkey: int, l_payedammount: double)),
      |    customerType = type record(customer: collection(record(c_custkey: string))),
      |    ordersType = type collection(record(o_orderkey: int, o_custkey: int)),
      |    lineitems = PostgreSQL.Query(
      |        "postgres",
      |        "select l_orderkey, (l_extendedprice * (1 - l_discount)) as l_payedammount from tpch10.lineitem",
      |        lineitemsType,
      |        host = "localhost:44444",
      |        username = "postgres",
      |        password = "1234"
      |    ),
      |    customers = Json.Read("file:///Users/alexzerntev/workspace/TPCH/10GB/customer.json", customerType), // an object with an array inside
      |    orders = Csv.Read("file:///Users/alexzerntev/workspace/TPCH/10GB/orders.csv", ordersType, delimiter = "|"),
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

    val startedIn = System.currentTimeMillis()
    fastExecute(prog)
    val elapsedIn = System.currentTimeMillis()
    logger.info("++++++++++ First run: " + (elapsedIn - startedIn))

    val numberOfRuns = 0

    val values = Array.fill(numberOfRuns)(0L)

    val lastIdx = numberOfRuns - 1

    for (i <- 0 to lastIdx) {
      val startedIn = System.currentTimeMillis()
      fastExecute(prog)
      val elapsedIn = System.currentTimeMillis()
      values(i) = elapsedIn - startedIn
      logger.info("++++++++++ Next run: " + values(i))
    }

    val mean = (values.sum) / 1

    var standardDeviation = 0.0
    for (num <- values) {
      standardDeviation += Math.pow(num - mean, 2)
    }

    logger.info("++++++++++ Average execution time: " + mean)
    logger.info("++++++++++ Standard deviation is: " + Math.sqrt(standardDeviation / numberOfRuns))
  }

  test("Range Count test") { _ =>
    assume(false, "This test is disabled by default")

    //    for (i <- 0 to 20) {
    //      fastExecute(
    //        """let
    //          |    range = 1000L
    //          |in
    //          |    Collection.Count(Collection.Transform(Collection.Filter(Long.Range(0, range), (x) -> x % 2 == 0), (y) -> y + 1))""".stripMargin
    //      )
    //    }
    //
    //    val values = Array.fill(7)(0L)
    //
    //    for (i <- 0 to 6) {
    //      logger.info("++++++++++ 10^" + (i + 3))
    //      val startedIn = System.currentTimeMillis()
    //      fastExecute(
    //        s"""let
    //          |    range = ${Math.pow(10, i + 3).toLong}L
    //          |in
    //          |    Collection.Count(Collection.Transform(Collection.Filter(Long.Range(0, range), (x) -> x % 2 == 0), (y) -> y + 1))""".stripMargin
    //      )
    //      val elapsedIn = System.currentTimeMillis()
    //      values(i) = elapsedIn - startedIn
    //    }
    //    logger.info("++++++++++ Values: " + values.mkString(", "))

    //    Execution with STD

    val prog = """let
      |    range = 100000000L
      |in
      |    Collection.Count(Collection.Transform(Collection.Filter(Long.Range(0, range), (x) -> x % 2 == 0), (y) -> y + 1))""".stripMargin

    val startedIn = System.currentTimeMillis()
    fastExecute(prog)
    val elapsedIn = System.currentTimeMillis()
    logger.info("++++++++++ First run: " + (elapsedIn - startedIn))

    val numberOfRuns = 20
    fastExecute("""let a = "hello" in a """) // some random query
    fastExecute("""let a = 2 + 2 in a """) // some random query
    fastExecute("""let a = 2/2 in a """) // some random query

    val values = Array.fill(numberOfRuns + 1)(0L)

    val started = System.currentTimeMillis()
    for (i <- 0 to numberOfRuns) {
      val startedIn = System.currentTimeMillis()
      fastExecute(prog)
      val elapsedIn = System.currentTimeMillis()
      values(i) = elapsedIn - startedIn
    }
    val elapsed = System.currentTimeMillis()

    val mean = (elapsed - started) / numberOfRuns

    var standardDeviation = 0.0
    for (num <- values) {
      standardDeviation += Math.pow(num - mean, 2)
    }

    logger.info("++++++++++ Average execution time: " + mean)
    logger.info("++++++++++ Standard deviation is: " + Math.sqrt(standardDeviation / numberOfRuns))
  }
}
