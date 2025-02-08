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

package com.rawlabs.snapi.compiler.tests

import com.rawlabs.utils.core.{
  RawException,
  RawTestSuite,
  RawUid,
  RawUtils,
  SettingsTestContext,
  TestData,
  TrainingWheelsContext
}
import org.scalatest.Tag
import org.scalatest.exceptions.TestFailedException
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.{MatchResult, Matcher}
import com.rawlabs.snapi.frontend.base.source.{BaseNode, BaseProgram, Type}
import com.rawlabs.snapi.frontend.inferrer.local.{LocalInferrerService, LocalInferrerTestContext}
import com.rawlabs.protocol.compiler.{
  DropboxAccessTokenConfig,
  HttpHeadersConfig,
  LocationConfig,
  MySqlConfig,
  OracleConfig,
  PostgreSQLConfig,
  S3AccessSecretKey,
  S3Config,
  SQLServerConfig,
  SnowflakeConfig
}
import com.rawlabs.snapi.frontend.api._
import com.rawlabs.snapi.frontend.base
import com.rawlabs.snapi.frontend.base.CompilerContext
import com.rawlabs.snapi.frontend.base.errors._
import com.rawlabs.snapi.frontend.snapi.{LspAnalyzer, ProgramContext, SemanticAnalyzer, TreeWithPositions}
import com.rawlabs.snapi.frontend.snapi.antlr4.{Antlr4SyntaxAnalyzer, ParseProgramResult, ParseTypeResult, ParserErrors}
import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.snapi.frontend.snapi.errors._
import org.bitbucket.inkytonik.kiama.relation.LeaveAlone
import org.bitbucket.inkytonik.kiama.util.{Position, Positions}
import org.graalvm.polyglot.{Context, PolyglotAccess, PolyglotException, Source}

import java.io.OutputStream
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{Files, Path}
import scala.collection.mutable
import scala.collection.JavaConverters._

object TestCredentials {

  private def getEnv(name: String): String = sys.env.getOrElse(name, "credential_not_defined_missing_env_file")

  /////////////////////////////////////////////////////////////////////////////
  // HTTP Headers
  /////////////////////////////////////////////////////////////////////////////

  val dropboxLongLivedAccessToken = getEnv("RAW_DROPBOX_TEST_LONG_LIVED_ACCESS_TOKEN")
  // The client ID to use for Dropbox API calls, once the access token is obtained.
  val dropboxClientId = getEnv("RAW_DROPBOX_TEST_CLIENT_ID")

  /////////////////////////////////////////////////////////////////////////////
  // S3 Credentials
  /////////////////////////////////////////////////////////////////////////////

  val accessKeyId = getEnv("RAW_AWS_ACCESS_KEY_ID")
  val secretKeyId = getEnv("RAW_AWS_SECRET_ACCESS_KEY")

  // Bucket with public access
  val UnitTestPublicBucket = "rawlabs-public-test-data"
  val UnitTestPublicBucketCred = S3Config.newBuilder().setRegion("eu-west-1").build()

  // IAM user 'unit-test-private-bucket', which only has permissions only to access bucket 'rawlabs-private-test-data'
  val UnitTestPrivateBucket = "rawlabs-private-test-data"
  val UnitTestPrivateBucketCred = S3Config
    .newBuilder()
    .setAccessSecretKey(S3AccessSecretKey.newBuilder().setAccessKey(accessKeyId).setSecretKey(secretKeyId))
    .setRegion("eu-west-1")
    .build()

  val UnitTestPrivateBucket2 = "rawlabs-unit-tests"
  val UnitTestPrivateBucket2Cred = S3Config
    .newBuilder()
    .setAccessSecretKey(S3AccessSecretKey.newBuilder().setAccessKey(accessKeyId).setSecretKey(secretKeyId))
    .setRegion("eu-west-1")
    .build()

  val UnitTestEmptyBucketPrivateBucket = "rawlabs-unit-test-empty-bucket"
  val UnitTestEmptyBucketPrivateBucketCred = S3Config
    .newBuilder()
    .setAccessSecretKey(S3AccessSecretKey.newBuilder().setAccessKey(accessKeyId).setSecretKey(secretKeyId))
    .setRegion("eu-west-1")
    .build()

  val UnitTestListRootPrivateBucket = "rawlabs-unit-test-list-root"
  val UnitTestListRootPrivateBucketCred = S3Config
    .newBuilder()
    .setAccessSecretKey(S3AccessSecretKey.newBuilder().setAccessKey(accessKeyId).setSecretKey(secretKeyId))
    .setRegion("eu-west-1")
    .build()

  val unitTestPrivateBucketUsEast1 = "rawlabs-unit-tests-us-east-1"
  val unitTestPrivateBucketUsEast1Cred = S3Config
    .newBuilder()
    .setAccessSecretKey(S3AccessSecretKey.newBuilder().setAccessKey(accessKeyId).setSecretKey(secretKeyId))
    .setRegion("us-east-1")
    .build()

  ///////////////////////////////////////////////////////////////////////////
  // Jdbc Credentials
  ///////////////////////////////////////////////////////////////////////////

  val mysqlTestHost = getEnv("RAW_MYSQL_TEST_HOST")
  val mysqlTestDB = getEnv("RAW_MYSQL_TEST_DB")
  val mysqlTestUser = getEnv("RAW_MYSQL_TEST_USER")
  val mysqlTestPassword = getEnv("RAW_MYSQL_TEST_PASSWORD")
  val mysqlCreds = MySqlConfig
    .newBuilder()
    .setHost(mysqlTestHost)
    .setPort(3306)
    .setDatabase(mysqlTestDB)
    .setUser(mysqlTestUser)
    .setPassword(mysqlTestPassword)
    .build()
  val pgsqlTestHost = getEnv("RAW_PGSQL_TEST_HOST")
  val pgsqlTestDB = getEnv("RAW_PGSQL_TEST_DB")
  val pgsqlTestUser = getEnv("RAW_PGSQL_TEST_USER")
  val pgsqlTestPassword = getEnv("RAW_PGSQL_TEST_PASSWORD")
  val pgsqlCreds = PostgreSQLConfig
    .newBuilder()
    .setHost(pgsqlTestHost)
    .setPort(5432)
    .setDatabase(pgsqlTestDB)
    .setUser(pgsqlTestUser)
    .setPassword(pgsqlTestPassword)
    .build()
  val oracleTestHost = getEnv("RAW_ORACLE_TEST_HOST")
  val oracleTestDB = getEnv("RAW_ORACLE_TEST_DB")
  val oracleTestUser = getEnv("RAW_ORACLE_TEST_USER")
  val oracleTestPassword = getEnv("RAW_ORACLE_TEST_PASSWORD")
  val oracleCreds = OracleConfig
    .newBuilder()
    .setHost(oracleTestHost)
    .setPort(1521)
    .setDatabase(oracleTestDB)
    .setUser(oracleTestUser)
    .setPassword(oracleTestPassword)
    .build()
  val sqlServerTestHost = getEnv("RAW_SQLSERVER_TEST_HOST")
  val sqlserverTestDB = getEnv("RAW_SQLSERVER_TEST_DB")
  val sqlServerTestUser = getEnv("RAW_SQLSERVER_TEST_USER")
  val sqlServerTestPassword = getEnv("RAW_SQLSERVER_TEST_PASSWORD")
  val sqlServerCreds = SQLServerConfig
    .newBuilder()
    .setHost(sqlServerTestHost)
    .setPort(1433)
    .setDatabase(sqlserverTestDB)
    .setUser(sqlServerTestUser)
    .setPassword(sqlServerTestPassword)
    .build()
  val snowflakeTestHost = getEnv("RAW_SNOWFLAKE_TEST_HOST")
  val snowflakeTestDB = getEnv("RAW_SNOWFLAKE_TEST_DB")
  val snowflakeTestUser = getEnv("RAW_SNOWFLAKE_TEST_USER")
  val snowflakeTestPassword = getEnv("RAW_SNOWFLAKE_TEST_PASSWORD")
  val snowflakeCreds = SnowflakeConfig
    .newBuilder()
    .setDatabase(snowflakeTestDB)
    .setUser(snowflakeTestUser)
    .setPassword(snowflakeTestPassword)
    .setAccountIdentifier(snowflakeTestHost)
    .putParameters("timezone", "UTC")
    .build()
  val badMysqlCreds = MySqlConfig
    .newBuilder()
    .setHost("does-not-exist.raw-labs.com")
    .setPort(3306)
    .setDatabase("rdbmstest")
    .setUser("t0or")
    .setPassword("$up3r$3cr3tValu3")
    .build()

}

trait SnapiTestContext
    extends RawTestSuite
    with Matchers
    with SettingsTestContext
    with TrainingWheelsContext
    with LocalInferrerTestContext {

  private val secrets = new mutable.HashMap[String, String]()

  private val locationConfigs = new mutable.HashMap[String, LocationConfig]()

  protected val programOptions = new mutable.HashMap[String, String]()

  def authorizedUser: RawUid = RawUid("janeUid")

  def runnerScopes: Set[String] = Set.empty

  def options: Map[String, String] = Map("output-format" -> "json")

  def maybeTraceId: Option[String] = None

  def secret(name: String, value: String): Unit = {
    secrets.put(name, value)
  }

  def locationConfig(name: String, locationConfig: LocationConfig): Unit = {
    locationConfigs.put(name, locationConfig)
  }

  def s3Bucket(name: String, bucket: S3Config): Unit = {
    locationConfig(name, LocationConfig.newBuilder().setS3(bucket).build())
  }

  def rdbms(name: String, db: MySqlConfig): Unit = {
    locationConfig(name, LocationConfig.newBuilder().setMysql(db).build())
  }

  def rdbms(name: String, db: OracleConfig): Unit = {
    locationConfig(name, LocationConfig.newBuilder().setOracle(db).build())
  }

  def rdbms(name: String, db: PostgreSQLConfig): Unit = {
    locationConfig(name, LocationConfig.newBuilder().setPostgresql(db).build())
  }

  def rdbms(name: String, db: SQLServerConfig): Unit = {
    locationConfig(name, LocationConfig.newBuilder().setSqlserver(db).build())
  }

  def rdbms(name: String, db: SnowflakeConfig): Unit = {
    locationConfig(name, LocationConfig.newBuilder().setSnowflake(db).build())
  }

  def httpHeaders(name: String, headers: Map[String, String]): Unit = {
    val httpConfig = HttpHeadersConfig.newBuilder().putAllHeaders(headers.asJava).build()
    locationConfig(name, LocationConfig.newBuilder().setHttpHeaders(httpConfig).build())
  }

  def dropbox(name: String, cred: DropboxAccessTokenConfig): Unit = {
    val dropboxConfig = LocationConfig.newBuilder().setDropboxAccessToken(cred).build()
    locationConfig(name, dropboxConfig)
  }

  def option(key: String, value: String): Unit = {
    programOptions.put(key, value)
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    dataFiles.foreach { case ViewFileContent(content, charset, path) => Files.write(path, content.getBytes(charset)) }

    /*

  private def withTruffleContext[T](
      environment: ProgramEnvironment,
      f: Context => T
  ): T = {
    val ctx = buildTruffleContext(environment)
    ctx.initialize("snapi")
    ctx.enter()
    try {
      f(ctx)
    } finally {
      ctx.leave()
      ctx.close()
    }
  }

  private def buildTruffleContext(
      environment: ProgramEnvironment,
      maybeOutputStream: Option[OutputStream] = None
  ): Context = {
    // Add environment settings as hardcoded environment variables.
    val ctxBuilder = Context
      .newBuilder("snapi")
      .environment("RAW_PROGRAM_ENVIRONMENT", ProgramEnvironment.serializeToString(environment))
      .allowExperimentalOptions(true)
      .allowPolyglotAccess(PolyglotAccess.ALL)
    ctxBuilder.option("snapi.settings", settings.renderAsString)

    maybeOutputStream.foreach(os => ctxBuilder.out(os))
    val ctx = ctxBuilder.build()
    ctx
  }

     */

  }

  override def afterAll(): Unit = {
    for (f <- dataFiles) {
      RawUtils.deleteTestPath(f.path)
    }
    super.afterAll()
  }

  private case class ViewFileContent(content: String, charset: Charset, path: Path)

  private val dataFiles = mutable.ArrayBuffer.empty[ViewFileContent]

  protected def dataFile(content: String, charset: Charset, path: Path) = {
    dataFiles.append(ViewFileContent(content, charset, path))
  }
  def tempFile(data: String, extension: String = "data", charset: Charset = StandardCharsets.UTF_8): Path = {
    val path = RawUtils.saveToTemporaryFileNoDeleteOnExit(data, "tempFile", s".$extension", charset)
    dataFile(data, charset, path)
    path
  }

  def prettyPrint(node: BaseNode): String = {
    SourcePrettyPrinter.format(node)
  }

  /////////////////////////////////////////////////////////////////////////
  // orderEvaluateTo
  /////////////////////////////////////////////////////////////////////////

  class OrderEvaluateTo(expectedQuery: String, delta: Option[Int] = None) extends Matcher[TestData] {
    def apply(actualQuery: TestData): MatchResult = {
      val (expected, expectedType) =
        executeQuery(expectedQuery, precision = delta, ordered = true, floatingPointAsString = true)
      val (actual, actualType) =
        executeQuery(actualQuery.q, precision = delta, ordered = true, floatingPointAsString = true)
      MatchResult(
        actual == expected,
        s"""ordered results didn't match!
          |expected: $expected ($expectedType)
          |actual:   $actual ($actualType)""".stripMargin,
        s"""ordered results matched:
          |$actual""".stripMargin
      )
    }
  }

  def orderEvaluateTo(q: String, delta: Option[Int] = None) = new OrderEvaluateTo(q, delta)

  /////////////////////////////////////////////////////////////////////////
  // astTypeAs
  /////////////////////////////////////////////////////////////////////////

  class AstTypeAs(expectedType: Type) extends Matcher[TestData] {
    def apply(data: TestData) = {
      tryToType(data.q).fold(
        errors => MatchResult(false, s"didn't type: ${errors.mkString("\n")}", "???"),
        tipe => {
          val tipeStr = prettyPrint(tipe)
          MatchResult(
            tipe == expectedType,
            s"typed as $tipeStr instead of ${prettyPrint(expectedType)}",
            s"typed as $tipeStr"
          )
        }
      )
    }
  }

  def astTypeAs(t: Type) = new AstTypeAs(t)

  /////////////////////////////////////////////////////////////////////////
  // Run
  /////////////////////////////////////////////////////////////////////////

  class Run() extends Matcher[TestData] {
    def apply(q: TestData): MatchResult = {
      doValidate(q.q) match {
        case Right(Some(t)) =>
        case Right(None) => fail("top-level was not an expression!")
        case Left(err) => fail(s"query fails to validate; error was: $err")
      }

      val r = doExecute(q.q)
      r match {
        case Left(error) => MatchResult(false, s"didn't run due to: $error", "???")
        case Right(_) => MatchResult(true, "???", "query run successfully")
      }
    }
  }

  def run: Run = new Run()

  /////////////////////////////////////////////////////////////////////////
  // parse
  /////////////////////////////////////////////////////////////////////////

  object parse extends Matcher[TestData] {
    def apply(data: TestData) = {
      tryToParse(data.q).fold(
        s => MatchResult(false, s"did not parse: $s", "???"),
        s => {
          MatchResult(true, "???", s"parsed as ${prettyPrint(s)}")
        }
      )
    }
  }

  class ParseErrorAs(err: String) extends Matcher[TestData] {
    override def apply(actualQuery: TestData): MatchResult = {
      val r = tryToParse(actualQuery.q)
      MatchResult(
        r.isLeft && r.left.get.contains(err),
        s"""Query didn't fail to parse as expected!
          |Expected: $err
          |Actual: ${r.left.getOrElse("query parsed with no errors!")}""".stripMargin,
        """Query failed to parse as expected"""
      )
    }
  }

  /**
   * Parses query and ensure it fails to parse with a message *containing* the error.
   */
  def parseErrorAs(err: String) = new ParseErrorAs(err)

  /////////////////////////////////////////////////////////////////////////
  // CLoseTo
  /////////////////////////////////////////////////////////////////////////
  class CloseTo(expectedQuery: String, delta: Double) extends Matcher[TestData] {
    private def inDelta(actual: Any, expected: Any): Boolean = (actual, expected) match {
      case (v1: Double, v2: Double) => v1 >= v2 - delta && v1 <= v2 + delta
      case (v1: Float, v2: Float) => v1 >= v2 - delta && v1 <= v2 + delta
      case (v1: Double, v2: Float) => v1 >= v2 - delta && v1 <= v2 + delta
      case (v1: Float, v2: Double) => v1 >= v2 - delta && v1 <= v2 + delta
      case _ => throw new AssertionError("CloseTo only works with Double/Float")
    }

    def apply(actualQuery: TestData): MatchResult = {
      val (expected, expectedType) = executeQuery(expectedQuery, floatingPointAsString = false)
      val (actual, actualType) = executeQuery(actualQuery.q, floatingPointAsString = false)
      MatchResult(
        inDelta(actual, expected),
        s"""results didn't match!
          |expected: $expected ($expectedType)
          |actual:   $actual ($actualType)""".stripMargin,
        s"""results matched:
          |$actual""".stripMargin
      )
    }
  }
  def beCloseTo(q: String, delta: Double = 1e-12) = new CloseTo(q, delta)

  /////////////////////////////////////////////////////////////////////////
  // parseAs
  /////////////////////////////////////////////////////////////////////////

  class ParseAs(expected: String) extends Matcher[TestData] {
    def apply(data: TestData) = {
      tryToParse(data.q).fold(
        error => MatchResult(false, s"did not parse: $error", "???"),
        actualAst => {
          val actual = prettyPrint(actualAst)
          val expectedExp = parseQuery(expected)
          MatchResult(
            actualAst == expectedExp,
            s"""parsed as $actual instead of $expected
              |expected AST: $expectedExp
              |  actual AST: $actualAst
             """.stripMargin,
            s"parsed as expected: $expected"
          )
        }
      )
    }
  }

  def parseAs(expected: String) = new ParseAs(expected)

  /////////////////////////////////////////////////////////////////////////
  // astParseAs
  /////////////////////////////////////////////////////////////////////////
  class AstParseAs(expectedAst: BaseProgram) extends Matcher[TestData] {
    def apply(data: TestData) = {
      tryToParse(data.q).fold(
        error => MatchResult(false, s"did not parse: $error", "???"),
        actualAst => {
          MatchResult(
            actualAst == expectedAst,
            s"""parsed as $actualAst instead of $expectedAst
              |expected AST: $expectedAst
              |  actual AST: $actualAst""".stripMargin,
            s"parsed as expected: $expectedAst"
          )
        }
      )
    }
  }
  def astParseAs(expected: BaseProgram) = new AstParseAs(expected)

  /////////////////////////////////////////////////////////////////////////
  // runErrorAs
  /////////////////////////////////////////////////////////////////////////

  class RunErrorAs(msg: String) extends Matcher[TestData] {
    override def apply(actualQuery: TestData): MatchResult = {
      val r = doExecute(actualQuery.q)
      MatchResult(
        r.isLeft && r.left.get.contains(msg),
        s"""Query didn't fail as expected!
          |Expected: $msg
          |Actual: ${r.left.getOrElse("query succeeded with no errors!")}""".stripMargin,
        """Query failed as expected"""
      )
    }
  }

  /**
   * Execute query and ensure it fails at runtime with a message *containing* the error.
   */
  def runErrorAs(msg: String) = new RunErrorAs(msg)

  /////////////////////////////////////////////////////////////////////////
  // typeAs
  /////////////////////////////////////////////////////////////////////////

  class TypeAs(expected: String) extends Matcher[TestData] {
    def apply(data: TestData) = {
      tryToType(data.q).fold(
        errors => MatchResult(false, s"didn't type: ${errors.mkString("\n")}", "???"),
        tipe => {
          val tipeStr = prettyPrint(tipe)
          val expectedType = prettyPrint(parseType(expected))
          MatchResult(
            tipeStr == expectedType,
            s"typed as $tipeStr instead of $expectedType",
            s"typed as $tipeStr"
          )
        }
      )
    }
  }

  def typeAs(t: String) = new TypeAs(t)

  /////////////////////////////////////////////////////////////////////////
  // typeError
  /////////////////////////////////////////////////////////////////////////

  object TypeError extends Matcher[TestData] {
    def apply(data: TestData) = {
      val attempt = tryToType(data.q)
      val output = attempt.fold(
        error => error,
        t1 => prettyPrint(t1)
      )
      MatchResult(attempt.isLeft, s"typed as $output", s"didn't type: $output")
    }
  }

  val typeError = parse and TypeError

  /////////////////////////////////////////////////////////////////////////
  // typeErrorAs
  /////////////////////////////////////////////////////////////////////////

  class TypeErrorAs(expectedErrors: Seq[String]) extends Matcher[TestData] {
    def apply(data: TestData) = {
      tryToType(data.q).fold(
        errors => {
          val leftOvers = expectedErrors.filter(expectedError => !errors.exists(_.contains(expectedError)))
          MatchResult(
            leftOvers.isEmpty,
            s"didn't include error '${leftOvers.mkString(",")}' in '$errors'",
            s"didn't type: ${errors.mkString("\n")}"
          )
        },
        t =>
          MatchResult(
            false,
            s"typed as ${prettyPrint(t)} instead of failing to type with ${expectedErrors.mkString(",")}",
            "???"
          )
      )
    }
  }

  def typeErrorAs(expectedErrors: String*) = new TypeErrorAs(expectedErrors)

  /////////////////////////////////////////////////////////////////////////
  // evaluateTo
  /////////////////////////////////////////////////////////////////////////

  class EvaluateTo(expectedQuery: String, delta: Option[Int] = None) extends Matcher[TestData] {
    def apply(actualQuery: TestData): MatchResult = {
      val (expected, expectedType) = executeQuery(expectedQuery, precision = delta, floatingPointAsString = true)
      val (actual, actualType) = executeQuery(actualQuery.q, precision = delta, floatingPointAsString = true)
      MatchResult(
        actual == expected,
        s"""results didn't match!
          |expected: $expected ($expectedType)
          |actual:   $actual ($actualType)""".stripMargin,
        s"""results matched:
          |$actual""".stripMargin
      )
    }
  }

  def evaluateTo(q: String, delta: Option[Int] = None) = new EvaluateTo(q, delta)

  /////////////////////////////////////////////////////////////////////////
  // tipe
  /////////////////////////////////////////////////////////////////////////
  object tipe extends Matcher[TestData] {
    def apply(data: TestData) = {
      val attempt = tryToType(data.q)
      val output = attempt.fold(
        errors => errors.mkString("\n"),
        t1 => prettyPrint(t1)
      )
      MatchResult(attempt.isRight, s"didn't type: $output", s"typed as $output")
    }
  }

  /////////////////////////////////////////////////////////////////////////
  // Helper Functions
  /////////////////////////////////////////////////////////////////////////

  private def getQueryEnvironment(
      scopes: Set[String] = Set.empty,
      options: Map[String, String] = Map.empty
  ): ProgramEnvironment = {
    val user = authorizedUser
    ProgramEnvironment(
      user,
      this.runnerScopes ++ scopes,
      secrets.toMap,
      locationConfigs.toMap,
      this.options ++ options ++ programOptions,
      None, // jdbcUrl
      maybeTraceId
    )
  }

  def parseQuery(code: String): BaseProgram = tryToParse(code) match {
    case Right(p) => p
    case Left(error) => throw new TestFailedException(Some(s"$code didn't parse: " + error), None, 4)
  }

  def maybeParseType(tipe: String): Either[List[Message], Type] = {
    val positions = new Positions()
    val parser = new Antlr4SyntaxAnalyzer(positions, true)
    parser.parseType(tipe) match {
      case ParseTypeResult(errors, tipe) if errors.isEmpty => Right(tipe)
      case ParseTypeResult(errors, _) => Left(errors)
    }
  }

  def parseType(tipe: String): Type = {
    maybeParseType(tipe) match {
      case Right(t) => t
      case Left(error) => throw new TestFailedException(Some(s"$tipe didn't parse: " + error), None, 4)
    }
  }

  private def tryToParse(q: String): Either[List[Message], BaseProgram] = {
    val positions = new Positions()
    val parser = new Antlr4SyntaxAnalyzer(positions, true)
    val parseResult = parser.parse(q)
    if (parseResult.isSuccess) {
      Right(parseResult.tree)
    } else {
      Left(parseResult.errors)
    }
  }

  private def getType(
      source: String,
      environment: ProgramEnvironment
  ) = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        val tree = new TreeWithPositions(source, ensureTree = false, frontend = true)(programContext)
        if (tree.valid) {
          Right(tree.rootType)
        } else {
          Left(tree.errors)
        }
      }
    )
  }

  def tryToType(s: String): Either[Seq[String], Type] = {
    try {
      getType(s, getQueryEnvironment()) match {
        case Right(t) => Right(t.get)
        case Left(errors) =>
          val messages = errors.map { err =>
            val message = err.message.replaceAll("""\n\s*""", "")
            val positions = err.positions
              .map(r => "[" + r.begin.line + ":" + r.begin.column + " - " + r.end.line + ":" + r.end.column + "]")
              .mkString(",")
            message + " " + positions
          }
          // Reporting of an ErrorType() should never be included in (public) error messages
          messages.foreach(m =>
            assert(
              !m.contains("but got error"),
              "Error message contained 'error' which means an ErrorType() is being leaked out to the user"
            )
          )
          Left(messages)
      }
    } catch {
      case ex: RawException => Left(Seq(ex.getMessage))
    }
  }

  private def withTruffleContext[T](
      environment: ProgramEnvironment,
      f: Context => T
  ): T = {
    val ctx = buildTruffleContext(environment)
    ctx.initialize("snapi")
    ctx.enter()
    try {
      f(ctx)
    } finally {
      ctx.leave()
      ctx.close()
    }
  }

  private def buildTruffleContext(
      environment: ProgramEnvironment,
      maybeOutputStream: Option[OutputStream] = None
  ): Context = {
    // Add environment settings as hardcoded environment variables.
    val ctxBuilder = Context
      .newBuilder("snapi")
      .environment("RAW_PROGRAM_ENVIRONMENT", ProgramEnvironment.serializeToString(environment))
      .allowExperimentalOptions(true)
      .allowPolyglotAccess(PolyglotAccess.ALL)
    ctxBuilder.option("snapi.settings", settings.renderAsString)

    maybeOutputStream.foreach(os => ctxBuilder.out(os))
    val ctx = ctxBuilder.build()
    ctx
  }

  private def withLspTree[T](source: String, f: LspAnalyzer => T)(
      implicit programContext: base.ProgramContext
  ): Either[(String, Position), T] = {
    val positions = new Positions()
    val parser = new Antlr4SyntaxAnalyzer(positions, true)
    val ParseProgramResult(errors, program) = parser.parse(source)
    val tree = new org.bitbucket.inkytonik.kiama.relation.Tree[SourceNode, SourceProgram](
      program,
      shape = LeaveAlone // The LSP parser can create "cloned nodes" so this protects it.
    )
    val analyzer = new SemanticAnalyzer(tree)(programContext.asInstanceOf[ProgramContext])
    // Handle the LSP request.
    val lspService = new LspAnalyzer(errors, analyzer, positions)(programContext.asInstanceOf[ProgramContext])
    Right(f(lspService))
  }

  private def getCompilerContext(user: RawUid): CompilerContext = {
    new CompilerContext(user, new LocalInferrerService())
  }

  private def getProgramContext(user: RawUid, environment: ProgramEnvironment): ProgramContext = {
    val compilerContext = getCompilerContext(user)
    new ProgramContext(environment, compilerContext)
  }

  private def parseError(error: String, position: Position): List[ErrorMessage] = {
    val range = ErrorRange(ErrorPosition(position.line, position.column), ErrorPosition(position.line, position.column))
    List(ErrorMessage(error, List(range), ParserErrors.ParserErrorCode))
  }

  def validate(source: String, environment: ProgramEnvironment = getQueryEnvironment()): ValidateResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        withLspTree(
          source,
          lspService => lspService.validate
        )(programContext) match {
          case Right(value) => value
          case Left((err, pos)) => ValidateResponse(parseError(err, pos))
        }
      }
    )
  }

  def aiValidate(source: String, environment: ProgramEnvironment = getQueryEnvironment()): ValidateResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        // Will analyze the code and return only unknown declarations errors.
        val positions = new Positions()
        val parser = new Antlr4SyntaxAnalyzer(positions, true)
        val parseResult = parser.parse(source)
        if (parseResult.isSuccess) {
          val sourceProgram = parseResult.tree
          val kiamaTree = new org.bitbucket.inkytonik.kiama.relation.Tree[SourceNode, SourceProgram](
            sourceProgram
          )
          val analyzer = new SemanticAnalyzer(kiamaTree)(programContext.asInstanceOf[ProgramContext])

          // Selecting only a subset of the errors
          val selection = analyzer.errors.filter {
            // For the case of a function that does not exist in a package
            case UnexpectedType(_, PackageType(_), ExpectedProjType(_), _, _) => true
            case _: UnknownDecl => true
            case _: OutputTypeRequiredForRecursiveFunction => true
            case _: UnexpectedOptionalArgument => true
            case _: NoOptionalArgumentsExpected => true
            case _: KeyNotComparable => true
            case _: ItemsNotComparable => true
            case _: MandatoryArgumentAfterOptionalArgument => true
            case _: RepeatedFieldNames => true
            case _: UnexpectedArguments => true
            case _: MandatoryArgumentsMissing => true
            case _: RepeatedOptionalArguments => true
            case _: PackageNotFound => true
            case _: NamedParameterAfterOptionalParameter => true
            case _: ExpectedTypeButGotExpression => true
            case _ => false
          }
          ValidateResponse(formatErrors(selection, positions))
        } else {
          ValidateResponse(parseResult.errors)
        }
      }
    )
  }

  private def formatErrors(errors: Seq[CompilerMessage], positions: Positions): List[Message] = {
    errors.map { err =>
      val ranges = positions.getStart(err.node) match {
        case Some(begin) =>
          val Some(end) = positions.getFinish(err.node)
          List(ErrorRange(ErrorPosition(begin.line, begin.column), ErrorPosition(end.line, end.column)))
        case _ => List.empty
      }
      CompilationMessageMapper.toMessage(err, ranges, ErrorsPrettyPrinter.format)
    }.toList
  }

  def hover(source: String, position: Pos, environment: ProgramEnvironment = getQueryEnvironment()): HoverResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        withLspTree(source, lspService => lspService.hover(source, environment, position))(programContext) match {
          case Right(value) => value
          case Left(_) => HoverResponse(None)
        }
      }
    )

  }

  def formatCode(
      source: String,
      maybeIndent: Option[Int] = None,
      maybeWidth: Option[Int] = None,
      environment: ProgramEnvironment = getQueryEnvironment()
  ): FormatCodeResponse = {
    val pretty = new SourceCommentsPrettyPrinter(maybeIndent, maybeWidth)
    pretty.prettyCode(source) match {
      case Right(code) => FormatCodeResponse(Some(code))
      case Left(_) => FormatCodeResponse(None)
    }
  }

  def dotAutoComplete(
      source: String,
      position: Pos,
      environment: ProgramEnvironment = getQueryEnvironment()
  ): AutoCompleteResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        withLspTree(source, lspService => lspService.dotAutoComplete(source, environment, position))(
          programContext
        ) match {
          case Right(value) => value
          case Left(_) => AutoCompleteResponse(Array.empty)
        }
      }
    )
  }

  def wordAutoComplete(
      source: String,
      prefix: String,
      position: Pos,
      environment: ProgramEnvironment = getQueryEnvironment()
  ): AutoCompleteResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        withLspTree(source, lspService => lspService.wordAutoComplete(source, environment, prefix, position))(
          programContext
        ) match {
          case Right(value) => value
          case Left(_) => AutoCompleteResponse(Array.empty)
        }
      }
    )
  }

  def goToDefinition(
      source: String,
      position: Pos,
      environment: ProgramEnvironment = getQueryEnvironment()
  ): GoToDefinitionResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        withLspTree(source, lspService => lspService.definition(source, environment, position))(
          programContext
        ) match {
          case Right(value) => value
          case Left(_) => GoToDefinitionResponse(None)
        }
      }
    )
  }

  def rename(
      source: String,
      position: Pos,
      environment: ProgramEnvironment = getQueryEnvironment()
  ): RenameResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        withLspTree(source, lspService => lspService.rename(source, environment, position))(programContext) match {
          case Right(value) => value
          case Left(_) => RenameResponse(Array.empty)
        }
      }
    )

  }

  def executeQuery(
      queryString: String,
      ordered: Boolean = false,
      precision: Option[Int] = None,
      floatingPointAsString: Boolean = false,
      options: Map[String, String] = Map.empty,
      scopes: Set[String] = Set.empty
  ): (Any, String) = {
    tryExecuteQuery(queryString, ordered, precision, floatingPointAsString, options, scopes) match {
      case Right(r) => r
      case Left(error) => fail(s"query is invalid; error was: $error")
    }
  }

  def tryExecuteQuery(
      queryString: String,
      ordered: Boolean = false,
      precision: Option[Int] = None,
      floatingPointAsString: Boolean = false,
      options: Map[String, String] = Map.empty,
      scopes: Set[String] = Set.empty
  ): Either[String, (Any, String)] = {
    val allOptions = this.options ++ options ++ programOptions
    val allScopes = this.runnerScopes ++ scopes

    // Doing validation first to obtain output type.
    val tipe = doValidate(queryString, options = allOptions, scopes = allScopes) match {
      case Right(Some(t)) => t
      case Right(_) => fail("top-level was not an expression!")
      case Left(err) => fail(s"query fails to validate; error was: $err")
    }

    doExecute(queryString, options = allOptions, scopes = allScopes).right.flatMap(data => Right((data, tipe)))
  }

  def doValidate(
      query: String,
      options: Map[String, String] = Map.empty,
      scopes: Set[String] = Set.empty
  ): Either[String, Option[String]] = {
    try {
      getType(query, getQueryEnvironment(scopes, options)) match {
        case Right(Some(t)) => Right(Some(prettyPrint(t)))
        case Right(None) => Right(None)
        case Left(errs) => Left(errs.map(err => err.toString).mkString(","))
      }
    } catch {
      case ex: RawException => fail(ex)
    }
  }

  /**
   * Executes queries.
   * Saves output to disk temporarily (to cope with larger outputs)
   */
  def doExecute(
      source: String,
      options: Map[String, String] = Map.empty,
      scopes: Set[String] = Set.empty
  ): Either[String, Any] = {
    val ctx = buildTruffleContext(getQueryEnvironment(scopes, options))
    ctx.initialize("snapi")
    ctx.enter()
    try {
      val truffleSource = Source
        .newBuilder("snapi", source, "unnamed")
        .cached(false) // Disable code caching because of the inferrer.
        .build()
      val result = ctx.eval(truffleSource)
      Right(result)
    } catch {
      case ex: PolyglotException =>
        // (msb): The following are various "hacks" to ensure the inner language InterruptException propagates "out".
        // Unfortunately, I do not find a more reliable alternative; the branch that does seem to work is the one
        // that does startsWith. That said, I believe with Truffle, the expectation is that one is supposed to
        // "cancel the context", but in our case this doesn't quite match the current architecture, where we have
        // other non-Truffle languages and also, we have parts of the pipeline that are running outside of Truffle
        // and which must handle interruption as well.
        if (ex.isInterrupted) {
          throw new InterruptedException()
        } else if (ex.getCause.isInstanceOf[InterruptedException]) {
          throw ex.getCause
        } else if (ex.getMessage.startsWith("java.lang.InterruptedException")) {
          throw new InterruptedException()
        } else if (ex.isGuestException) {
          if (ex.isInternalError) {
            // An internal error. It means a regular Exception thrown from the language (e.g. a Java Exception,
            // or a RawTruffleInternalErrorException, which isn't an AbstractTruffleException)
            throw ex
          } else {
            val err = ex.getGuestObject
            if (err != null && err.hasMembers && err.hasMember("errors")) {
              // A validation exception, semantic or syntax error (both come as the same kind of error)
              // that has a list of errors and their positions.
              val errorsValue = err.getMember("errors")
              val errors = (0L until errorsValue.getArraySize).map { i =>
                val errorValue = errorsValue.getArrayElement(i)
                val message = errorValue.asString
                val positions = (0L until errorValue.getArraySize).map { j =>
                  val posValue = errorValue.getArrayElement(j)
                  val beginValue = posValue.getMember("begin")
                  val endValue = posValue.getMember("end")
                  val begin = ErrorPosition(beginValue.getMember("line").asInt, beginValue.getMember("column").asInt)
                  val end = ErrorPosition(endValue.getMember("line").asInt, endValue.getMember("column").asInt)
                  ErrorRange(begin, end)
                }
                ErrorMessage(message, positions.to, ParserErrors.ParserErrorCode)
              }
              Left(errors.map(err => err.toString).mkString(","))
            } else {
              // A runtime failure during execution. The query could be a failed tryable, or a runtime error (e.g. a
              // file not found) hit when processing a reader that evaluates as a _collection_ (processed outside the
              // evaluation of the query).
              Left(ex.getMessage)
            }
          }
        } else {
          // Unexpected error. For now we throw the PolyglotException.
          throw ex
        }
    } finally {
      ctx.leave()
      ctx.close()
    }
  }

  private class ShouldFail(testName: String, testTags: Tag*) extends ResultOfTestInvocation(testName, testTags: _*) {

    override def apply(
        testFun: FixtureParam => Any /* Assertion */
    )(implicit pos: org.scalactic.source.Position): Unit = {

      // a wrapping test function which asserts the original one throws
      val testFun2: FixtureParam => Any = {
        case f: FixtureParam => assertThrows[Exception] {
            testFun(f)
          }
      }
      super.apply(testFun2)(pos) // regular execution of the wrapping test code
    }
  }

  def knownBug(bug: String, q: String, testTags: Tag*) = expectedFailure(q, testTags: _*)

  def expectedFailure(q: String, testTags: Tag*): ResultOfTestInvocation = failUntil(q, None, testTags: _*)

  def expectedFailure(q: String, deadline: String, testTags: Tag*): ResultOfTestInvocation =
    failUntil(q, Some(deadline), testTags: _*)

  private def failUntil(q: String, maybeDate: Option[String], testTags: Tag*): ResultOfTestInvocation = {
    val maybeDeadline = maybeDate.map(java.time.LocalDate.parse)
    // regular execution of the test if the deadline is defined and passed, otherwise execution asserts on test failure
    if (maybeDeadline.exists(_.compareTo(java.time.LocalDate.now()) < 0)) new ResultOfTestInvocation(q, testTags: _*)
    else new ShouldFail(q, testTags: _*)
  }

}
