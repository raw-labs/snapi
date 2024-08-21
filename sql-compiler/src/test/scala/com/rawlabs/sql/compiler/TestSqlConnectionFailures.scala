/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.sql.compiler

import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import com.rawlabs.compiler.api.{
  AutoCompleteResponse,
  CompilerService,
  ExecutionResponse,
  ExecutionRuntimeFailure,
  ExecutionSuccess,
  GetProgramDescriptionFailure,
  GetProgramDescriptionResponse,
  GetProgramDescriptionSuccess,
  HoverResponse,
  LetBindCompletion,
  Pos,
  ProgramEnvironment,
  RawInt,
  TypeCompletion,
  ValidateResponse
}
import com.rawlabs.sql.compiler.impl.SqlCompilerService
import org.scalatest.matchers.must.Matchers.be
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.testcontainers.utility.DockerImageName
import com.rawlabs.utils.core._

import java.io.ByteArrayOutputStream
import java.sql.DriverManager
import java.util.concurrent.{Executors, TimeUnit}
import scala.io.Source

class TestSqlConnectionFailures
    extends RawTestSuite
    with ForAllTestContainer
    with SettingsTestContext
    with TrainingWheelsContext {

  // The test suite triggers connection failures for both 'no connections
  // available' (the pool can't open a new connection) and 'too many
  // connections active' (the user runs too many queries in parallel)
  // for all implemented calls: execute, validate, getProgramDescription,
  // hover, dotCompletion, wordCompletion. For each we use sequential
  // or parallel queries to exhaust the pool in some way and assert the
  // failure is hit as expected.

  // Number of users to run with. This allows testing errors that occur
  // when a single user exhausts their allocated share.
  val nUsers = 3

  // We run a test container emulating FDW. It has the example schema.
  override val container: PostgreSQLContainer = PostgreSQLContainer(
    dockerImageNameOverride = DockerImageName.parse("postgres:15-alpine")
  )
  Class.forName("org.postgresql.Driver")

  private var users: Set[RawUid] = _

  private def jdbcUrl(user: RawUid) = {
    val dbPort = container.mappedPort(5432).toString
    val dbName = user.uid
    val username = container.username
    val password = container.password
    s"jdbc:postgresql://localhost:$dbPort/$dbName?user=$username&password=$password"
  }

  override def beforeAll(): Unit = {
    super.beforeAll()

    // For each user we create a specific database and load the example schema.
    users = {
      val items = for (i <- 1 to nUsers) yield RawUid(s"db$i")
      items.toSet
    }

    val exampleSchemaCreation = {
      val resource = Source.fromResource("example.sql")
      try {
        resource.mkString
      } finally {
        resource.close()
      }
    }

    val conn = DriverManager.getConnection(container.jdbcUrl, container.username, container.password);
    try {
      val stmt = conn.createStatement()
      for (user <- users) {
        val r = stmt.executeUpdate(s"CREATE DATABASE ${user.uid}")
        assert(r == 0)
      }
    } finally {
      conn.close()
    }

    for (user <- users) {
      val conn = DriverManager.getConnection(jdbcUrl(user), container.username, container.password);
      try {
        val stmt = conn.createStatement()
        stmt.execute(exampleSchemaCreation)
      } finally {
        conn.close()
      }
    }

  }

  test("[lsp] enough connections in total") { _ =>
    // A single user calls LSP, while all others are running a long query. The user manages to get results
    // because it could pick a connection.
    val joe = users.head
    val others = users.tail
    property("raw.client.sql.pool.max-connections", s"$nUsers") // enough for everyone
    property("raw.client.sql.pool.max-connections-per-db", s"1")
    val compilerService = new SqlCompilerService()
    val pool = Executors.newFixedThreadPool(others.size)
    try {
      // All other users run a long query which picks a connection for them
      val futures = others.map(user => pool.submit(() => runExecute(compilerService, user, longRunningQuery, 5)))
      val results = futures.map(_.get(60, TimeUnit.SECONDS))
      results.foreach {
        case ExecutionSuccess(complete) => complete shouldBe true
        case r => fail(s"unexpected result $r")
      }
      // The user is able to get a connection to run all LSP calls.
      // hover over 'example' picks a connection from the postgre metadata cache
      val hoverResponse = runHover(compilerService, joe, "SELECT * FROM example.airports", Pos(1, 17))
      assert(hoverResponse.completion.contains(TypeCompletion("example", "schema")))
      // hover over ':id' picks a connection by asking the statement metadata to infer the type
      val hoverResponse2 =
        runHover(compilerService, joe, "SELECT * FROM example.airports WHERE :id = airport_id", Pos(1, 40))
      assert(hoverResponse2.completion.contains(TypeCompletion("id", "integer")))
      val wordCompletionResponse = runWordCompletion(compilerService, joe, "SELECT * FROM exa", "exa", Pos(1, 17))
      assert(wordCompletionResponse.completions.contains(LetBindCompletion("example", "schema")))
      val dotCompletionResponse = runDotCompletion(compilerService, joe, "SELECT * FROM example.", Pos(1, 22))
      assert(dotCompletionResponse.completions.collect {
        case LetBindCompletion(name, _) => name
      }.toSet === Set("airports", "trips", "machines"))
    } finally {
      pool.close()
      compilerService.stop()
    }
  }

  test("[lsp] not enough connections in total") { _ =>
    // A single user calls LSP, while all others are running a long query. The user can't get results
    // because the number of max-connections is set to others.size, they're all taken.
    val joe = users.head
    val others = users.tail
    property("raw.client.sql.pool.max-connections", s"${others.size}") // one less than needed
    property("raw.client.sql.pool.max-connections-per-db", s"1")
    val compilerService = new SqlCompilerService()
    val pool = Executors.newFixedThreadPool(others.size)
    try {
      val futures = others.map(user => pool.submit(() => runExecute(compilerService, user, longRunningQuery, 5)))
      val results = futures.map(_.get(60, TimeUnit.SECONDS))
      results.foreach {
        case ExecutionSuccess(complete) => complete shouldBe true
        case r => fail(s"unexpected result $r")
      }
      // hover returns nothing
      val hoverResponse = runHover(compilerService, joe, "SELECT * FROM example.airports", Pos(1, 17))
      assert(hoverResponse.completion.isEmpty)
      val hoverResponse2 =
        runHover(compilerService, joe, "SELECT * FROM example.airports WHERE :id = airport_id", Pos(1, 40))
      assert(hoverResponse2.completion.isEmpty)
      // we get no word completions
      val wordCompletionResponse = runWordCompletion(compilerService, joe, "SELECT * FROM exa", "exa", Pos(1, 17))
      assert(wordCompletionResponse.completions.isEmpty)
      // we get no dot completions
      val dotCompletionResponse = runDotCompletion(compilerService, joe, "SELECT * FROM example.", Pos(1, 22))
      assert(dotCompletionResponse.completions.isEmpty)
    } finally {
      pool.close()
      compilerService.stop()
    }
  }

  test("[lsp] enough connections per user") { _ =>
    // Again, a single user runs LSP calls while all (including itself) are running long connections.
    // With two connections available, the single user manages to run all LSP calls.
    val joe = users.head
    property("raw.client.sql.pool.max-connections", s"${nUsers * 2}")
    property("raw.client.sql.pool.max-connections-per-db", s"2")
    val compilerService = new SqlCompilerService()
    val pool = Executors.newFixedThreadPool(users.size)
    try {
      val futures = users.map(user => pool.submit(() => runExecute(compilerService, user, longRunningQuery, 5)))
      Thread.sleep(2000) // give some time to make sure they're all running
      val hoverResponse = runHover(compilerService, joe, "SELECT * FROM example.airports", Pos(1, 17))
      assert(hoverResponse.completion.contains(TypeCompletion("example", "schema")))
      val hoverResponse2 =
        runHover(compilerService, joe, "SELECT * FROM example.airports WHERE :id = airport_id", Pos(1, 40))
      assert(hoverResponse2.completion.contains(TypeCompletion("id", "integer")))
      val wordCompletionResponse = runWordCompletion(compilerService, joe, "SELECT * FROM exa", "exa", Pos(1, 17))
      assert(wordCompletionResponse.completions.contains(LetBindCompletion("example", "schema")))
      val dotCompletionResponse = runDotCompletion(compilerService, joe, "SELECT * FROM example.", Pos(1, 22))
      assert(dotCompletionResponse.completions.collect {
        case LetBindCompletion(name, _) => name
      }.toSet === Set("airports", "trips", "machines"))
      val results = futures.map(_.get(60, TimeUnit.SECONDS))
      results.foreach {
        case ExecutionSuccess(complete) => complete shouldBe true
        case r => fail(s"unexpected result $r")
      }
    } finally {
      pool.close()
      compilerService.stop()
    }
  }

  test("[lsp] not enough connections per user") { _ =>
    // All users run a long query, including the one who then issues an LSP request. The LSP fails because
    // only one connection per user is allowed.
    val joe = users.head
    property("raw.client.sql.pool.max-connections", s"${nUsers * 2}") // plenty
    property("raw.client.sql.pool.max-connections-per-db", s"1") // only one per user
    val compilerService = new SqlCompilerService()
    val pool = Executors.newFixedThreadPool(users.size)
    try {
      // All users run a long query
      val futures = users.map(user => pool.submit(() => runExecute(compilerService, user, longRunningQuery, 5)))
      Thread.sleep(2000) // give some time to make sure they're all running
      // hover is None
      val hoverResponse = runHover(compilerService, joe, "SELECT * FROM example.airports", Pos(1, 17))
      assert(hoverResponse.completion.isEmpty)
      val hoverResponse2 =
        runHover(compilerService, joe, "SELECT * FROM example.airports WHERE :id = airport_id", Pos(1, 40))
      assert(hoverResponse2.completion.isEmpty)
      // word and dot completion return an empty list
      val wordCompletionResponse = runWordCompletion(compilerService, joe, "SELECT * FROM exa", "exa", Pos(1, 17))
      assert(wordCompletionResponse.completions.isEmpty)
      val dotCompletionResponse = runDotCompletion(compilerService, joe, "SELECT * FROM example.", Pos(1, 22))
      assert(dotCompletionResponse.completions.isEmpty)
      val results = futures.map(_.get(60, TimeUnit.SECONDS))
      results.foreach {
        case ExecutionSuccess(complete) => complete shouldBe true
        case r => fail(s"unexpected result $r")
      }
    } finally {
      pool.close()
      compilerService.stop()
    }
  }

  test("[execute] enough connections in total") { _ =>
    /* Each user runs three times the same long query, one call at a time. The same connection is reused per user.
     * This is confirmed by setting max-connections-per-db to 1 although several calls are performed per DB.
     * In total, there's one connection per user. Setting max-connections to nUsers is working.
     */
    val nCalls = 2
    property("raw.client.sql.pool.max-connections", s"$nUsers")
    property("raw.client.sql.pool.max-connections-per-db", s"1")
    val compilerService = new SqlCompilerService()
    val iterations = 1 to nCalls
    try {
      val results = users
        .map(user => user -> iterations.map(_ => runExecute(compilerService, user, longRunningQuery, 0)))
        .toMap
      for (userResults <- results.values; r <- userResults) r match {
        case ExecutionSuccess(complete) => complete shouldBe true
        case _ => fail(s"unexpected result $r")
      }
    } finally {
      compilerService.stop()
    }
  }

  test("[execute] enough connections per user") { _ =>
    // We run `execute` _in parallel_ using a long query. Each user runs it `nCalls` times. So we have
    // a total number of queries of nUsers x nCalls. We set max-connections to that value to be sure and
    // set max-connections-per-db to nCalls so that all concurrent queries can run.
    val nCalls = 3
    property("raw.client.sql.pool.max-connections", s"${nUsers * nCalls}") // enough total
    property("raw.client.sql.pool.max-connections-per-db", s"$nCalls") // exactly what is needed per user
    val compilerService = new SqlCompilerService()
    val pool = Executors.newFixedThreadPool(nUsers * nCalls)
    val iterations = 1 to nCalls
    try {
      val futures = users
        .map(user =>
          user -> iterations.map(_ => pool.submit(() => runExecute(compilerService, user, longRunningQuery, 5)))
        )
        .toMap
      val results = futures.mapValues(_.map(_.get(60, TimeUnit.SECONDS)))
      for (userResults <- results.values; r <- userResults) r match {
        case ExecutionSuccess(complete) => complete shouldBe true
        case _ => fail(s"unexpected result $r")
      }
    } finally {
      pool.close()
      compilerService.stop()
    }
  }

  test("[execute] not enough connections") { _ =>
    /* Each user runs twice execute, one call at a time. The same connection can be reused per user.
     * In total, there's one connection per user. Setting max-connections to nUsers - 1 triggers the
     * expected failure. The number of errors hit should be positive (checked in the end)
     */
    val nCalls = 2
    property("raw.client.sql.pool.max-connections", s"${nUsers - 1}")
    property("raw.client.sql.pool.max-connections-per-db", s"1")
    val compilerService = new SqlCompilerService()
    val iterations = 1 to nCalls
    try {
      val results = users
        .map(user => user -> iterations.map(_ => runExecute(compilerService, user, longRunningQuery, 0)))
        .toMap
      for (userResults <- results.values; r <- userResults) r match {
        case ExecutionSuccess(complete) => complete shouldBe true
        case ExecutionRuntimeFailure(error) => error shouldBe "no connections available"
        case _ => fail(s"unexpected result $r")
      }
      val errorCount = results.values.map(_.count(_.isInstanceOf[ExecutionRuntimeFailure])).sum
      errorCount should be > 0
    } finally {
      compilerService.stop()
    }
  }

  test("[getProgramDescription] not enough connections") { _ =>
    /* Each user runs twice getProgramDescription, one call at a time. The same connection can be reused per user.
     * In total, there's one connection per user. Setting max-connections to nUsers - 1 triggers the
     * expected failure. The number of errors hit should be positive (checked in the end)
     */
    val nCalls = 2
    property("raw.client.sql.pool.max-connections", s"${nUsers - 1}")
    property("raw.client.sql.pool.max-connections-per-db", s"1")
    val compilerService = new SqlCompilerService()
    val iterations = 1 to nCalls
    try {
      val results = users
        .map(user => user -> iterations.map(_ => runGetProgramDescription(compilerService, user, longValidateQuery)))
        .toMap
      for (userResults <- results.values; r <- userResults) r match {
        case GetProgramDescriptionSuccess(_) =>
        case GetProgramDescriptionFailure(errors) =>
          errors.size shouldBe 1
          errors.head.message shouldBe "no connections available"
      }
      val errorCount = results.values.map(_.count(_.isInstanceOf[GetProgramDescriptionFailure])).sum
      errorCount should be > 0
    } finally {
      compilerService.stop()
    }
  }

  test("[validate] not enough connections") { _ =>
    /* Each user runs twice validate, one call at a time. The same connection can be reused per user.
     * In total, there's one connection per user. Setting max-connections to nUsers - 1 triggers the
     * expected failure. The number of errors hit should be positive (checked in the end)
     */
    val nCalls = 2
    property("raw.client.sql.pool.max-connections", s"${nUsers - 1}")
    property("raw.client.sql.pool.max-connections-per-db", s"1")
    val compilerService = new SqlCompilerService()
    val iterations = 1 to nCalls
    try {
      val results = users
        .map(user => user -> iterations.map(_ => runValidate(compilerService, user, longValidateQuery)))
        .toMap
      for (userResults <- results.values; r <- userResults) r match {
        case ValidateResponse(errors) if errors.isEmpty =>
        case ValidateResponse(errors) =>
          errors.size shouldBe 1
          errors.head.message shouldBe "no connections available"
      }
      val errorCount = results.values.map(_.count(_.messages.nonEmpty)).sum
      errorCount should be > 0
    } finally {
      compilerService.stop()
    }
  }

  test("[execute] not enough connections per user") { _ =>
    // We run `execute` in parallel using a long query. Each user runs it `nCalls` times. So we have
    // a total number of queries of nUsers x nCalls. We set max-connections to that value to be sure but
    // set max-connections-per-db to two so that all concurrent queries cannot all get a connection although
    // max-connections would allow it.
    val nCalls = 10
    property("raw.client.sql.pool.max-connections", s"${nUsers * nCalls}") // in principle enough
    property("raw.client.sql.pool.max-connections-per-db", s"1") // but only few connections per user
    val compilerService = new SqlCompilerService()
    val pool = Executors.newFixedThreadPool(nUsers * nCalls)
    val iterations = 1 to nCalls
    try {
      val futures = users
        .map(user =>
          user -> iterations.map(_ => pool.submit(() => runExecute(compilerService, user, longRunningQuery, 5)))
        )
        .toMap
      val results = futures.mapValues(_.map(_.get(60, TimeUnit.SECONDS)))
      for (userResults <- results.values; r <- userResults) r match {
        case ExecutionSuccess(complete) => complete shouldBe true
        case ExecutionRuntimeFailure(error) => error shouldBe "too many connections active"
        case _ => fail(s"unexpected result $r")
      }
      val errorCount = results.values.map(_.count(_.isInstanceOf[ExecutionRuntimeFailure])).sum
      errorCount should be > 0
    } finally {
      pool.close()
      compilerService.stop()
    }
  }

  test("[getProgramDescription] not enough connections per user") { _ =>
    // We run `getProgramDescription` in parallel using a long query. Each user runs it `nCalls` times. So we have
    // a total number of queries of nUsers x nCalls. We set max-connections to that value to be sure but
    // set max-connections-per-db to two so that all concurrent queries cannot all get a connection although
    // max-connections would allow it.
    val nCalls = 10
    property("raw.client.sql.pool.max-connections", s"${nUsers * nCalls}") // in principle enough
    property("raw.client.sql.pool.max-connections-per-db", s"1") // but only few connections per user
    val compilerService = new SqlCompilerService()
    val pool = Executors.newFixedThreadPool(nUsers * nCalls)
    val iterations = 1 to nCalls
    try {
      val futures = users
        .map(user =>
          user -> iterations.map(_ =>
            pool.submit(() => runGetProgramDescription(compilerService, user, longValidateQuery))
          )
        )
        .toMap
      val results = futures.mapValues(_.map(_.get(60, TimeUnit.SECONDS)))
      for (userResults <- results.values; r <- userResults) r match {
        case GetProgramDescriptionSuccess(_) =>
        case GetProgramDescriptionFailure(errors) =>
          errors.size shouldBe 1
          errors.head.message shouldBe "too many connections active"
      }
      val errorCount = results.values.map(_.count(_.isInstanceOf[GetProgramDescriptionFailure])).sum
      errorCount should be > 0
    } finally {
      pool.close()
      compilerService.stop()
    }
  }

  test("[validate] not enough connections per user") { _ =>
    // We run `validate` in parallel using a long query. Each user runs it `nCalls` times. So we have
    // a total number of queries of nUsers x nCalls. We set max-connections to that value to be sure but
    // set max-connections-per-db to two so that all concurrent queries cannot all get a connection although
    // max-connections would allow it.
    val nCalls = 10
    property("raw.client.sql.pool.max-connections", s"${nUsers * nCalls}") // in principle enough
    property("raw.client.sql.pool.max-connections-per-db", s"1") // but only few connections per user
    val compilerService = new SqlCompilerService()
    val pool = Executors.newFixedThreadPool(nUsers * nCalls)
    val iterations = 1 to nCalls
    try {
      val futures = users
        .map(user =>
          user -> iterations.map(_ => pool.submit(() => runValidate(compilerService, user, longValidateQuery)))
        )
        .toMap
      val results = futures.mapValues(_.map(_.get(60, TimeUnit.SECONDS)))
      for (userResults <- results.values; r <- userResults) r match {
        case ValidateResponse(errors) if errors.isEmpty =>
        case ValidateResponse(errors) =>
          errors.size shouldBe 1
          errors.head.message shouldBe "too many connections active"
      }
      val errorCount = results.values.map(_.count(_.messages.nonEmpty)).sum
      errorCount should be > 0
    } finally {
      pool.close()
      compilerService.stop()
    }
  }

  private def runExecute(
      compilerService: CompilerService,
      user: RawUid,
      code: String,
      arg: Int
  ): ExecutionResponse = {
    val env = ProgramEnvironment(
      user,
      Some(Array("arg" -> RawInt(arg))),
      Set.empty,
      Map.empty,
      Map.empty,
      Map("output-format" -> "json"),
      jdbcUrl = Some(jdbcUrl(user))
    )
    val baos = new ByteArrayOutputStream()
    try {
      compilerService.execute(code, env, None, baos)
    } finally {
      baos.close()
    }
  }

  private def runHover(
      compilerService: CompilerService,
      user: RawUid,
      code: String,
      pos: Pos
  ): HoverResponse = {
    val env = ProgramEnvironment(
      user,
      None,
      Set.empty,
      Map.empty,
      Map.empty,
      Map("output-format" -> "json"),
      jdbcUrl = Some(jdbcUrl(user))
    )
    compilerService.hover(code, env, pos)
  }

  private def runWordCompletion(
      compilerService: CompilerService,
      user: RawUid,
      code: String,
      prefix: String,
      pos: Pos
  ): AutoCompleteResponse = {
    val env = ProgramEnvironment(
      user,
      None,
      Set.empty,
      Map.empty,
      Map.empty,
      Map("output-format" -> "json"),
      jdbcUrl = Some(jdbcUrl(user))
    )
    compilerService.wordAutoComplete(code, env, prefix, pos)
  }

  private def runDotCompletion(
      compilerService: CompilerService,
      user: RawUid,
      code: String,
      pos: Pos
  ): AutoCompleteResponse = {
    val env = ProgramEnvironment(
      user,
      None,
      Set.empty,
      Map.empty,
      Map.empty,
      Map("output-format" -> "json"),
      jdbcUrl = Some(jdbcUrl(user))
    )
    compilerService.dotAutoComplete(code, env, pos)
  }

  private def runGetProgramDescription(
      compilerService: CompilerService,
      user: RawUid,
      code: String
  ): GetProgramDescriptionResponse = {
    val env = ProgramEnvironment(
      user,
      None,
      Set.empty,
      Map.empty,
      Map.empty,
      Map("output-format" -> "json"),
      jdbcUrl = Some(jdbcUrl(user))
    )
    compilerService.getProgramDescription(code, env)
  }

  private def runValidate(
      compilerService: CompilerService,
      user: RawUid,
      code: String
  ): ValidateResponse = {
    val env = ProgramEnvironment(
      user,
      None,
      Set.empty,
      Map.empty,
      Map.empty,
      Map("output-format" -> "json"),
      jdbcUrl = Some(jdbcUrl(user))
    )
    compilerService.validate(code, env)
  }

  // it sleeps for 'arg' seconds (default 1)
  private val longRunningQuery = """-- @default arg 1
    |SELECT CAST(pg_sleep(:arg) AS VARCHAR)""".stripMargin

  // it runs fast but its default parameter value takes long to compute. That permits to
  // slow down validation calls.
  private val longValidateQuery = """-- @default arg SELECT 1 WHERE pg_sleep(5) IS NOT NULL
    |SELECT :arg""".stripMargin

  override def afterAll(): Unit = {
    container.stop()
    super.afterAll()
  }

}
