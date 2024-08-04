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

package raw.compiler.rql2.tests

import org.scalatest.Tag
import org.scalatest.exceptions.TestFailedException
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.{MatchResult, Matcher}
import raw.client.api._
import raw.client.rql2.api._
import raw.compiler.base.source.{BaseProgram, Type}
import raw.compiler.rql2.api.{Rql2CompilerServiceTestContext, Rql2OutputTestContext}
import raw.inferrer.local.LocalInferrerTestContext
import raw.utils._

import java.io.{ByteArrayOutputStream, FileWriter}
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{Files, Path, StandardOpenOption}
import scala.collection.mutable
import scala.io.Source

object TestCredentials {

  /////////////////////////////////////////////////////////////////////////////
  // Dropbox Credentials
  /////////////////////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////////////////////
  // S3 Credentials
  /////////////////////////////////////////////////////////////////////////////

  val accessKeyId = sys.env("RAW_AWS_ACCESS_KEY_ID")
  val secretKeyId = sys.env("RAW_AWS_SECRET_ACCESS_KEY")

  // Bucket with public access
  val UnitTestPublicBucket = "rawlabs-public-test-data"
  val UnitTestPublicBucketCred = S3Credential(None, None, Some("eu-west-1"))

  // IAM user 'unit-test-private-bucket', which only has permissions only to access bucket 'rawlabs-private-test-data'
  val UnitTestPrivateBucket = "rawlabs-private-test-data"
  val UnitTestPrivateBucketCred = S3Credential(Some(accessKeyId), Some(secretKeyId), Some("eu-west-1"))

  val UnitTestPrivateBucket2 = "rawlabs-unit-tests"
  val UnitTestPrivateBucket2Cred = S3Credential(Some(accessKeyId), Some(secretKeyId), Some("eu-west-1"))

  val UnitTestEmptyBucketPrivateBucket = "rawlabs-unit-test-empty-bucket"
  val UnitTestEmptyBucketPrivateBucketCred = S3Credential(Some(accessKeyId), Some(secretKeyId), Some("eu-west-1"))

  val UnitTestListRootPrivateBucket = "rawlabs-unit-test-list-root"
  val UnitTestListRootPrivateBucketCred = S3Credential(Some(accessKeyId), Some(secretKeyId), Some("eu-west-1"))

  val unitTestPrivateBucketUsEast1 = "rawlabs-unit-tests-us-east-1"
  val unitTestPrivateBucketUsEast1Cred = S3Credential(Some(accessKeyId), Some(secretKeyId), Some("us-east-1"))

  ///////////////////////////////////////////////////////////////////////////
  // Jdbc Credentials
  ///////////////////////////////////////////////////////////////////////////

  val mysqlTestHost = sys.env("RAW_MYSQL_TEST_HOST")
  val mysqlTestDB = sys.env("RAW_MYSQL_TEST_DB")
  val mysqlTestUser = sys.env("RAW_MYSQL_TEST_USER")
  val mysqlTestPassword = sys.env("RAW_MYSQL_TEST_PASSWORD")
  val mysqlCreds = MySqlJdbcLocation(mysqlTestHost, 3306, mysqlTestDB, mysqlTestUser, mysqlTestPassword)
  val pgsqlTestHost = sys.env("RAW_PGSQL_TEST_HOST")
  val pgsqlTestDB = sys.env("RAW_PGSQL_TEST_DB")
  val pgsqlTestUser = sys.env("RAW_PGSQL_TEST_USER")
  val pgsqlTestPassword = sys.env("RAW_PGSQL_TEST_PASSWORD")
  val pgsqlCreds = PostgresJdbcLocation(pgsqlTestHost, 5432, pgsqlTestDB, pgsqlTestUser, pgsqlTestPassword)
  val oracleTestHost = sys.env("RAW_ORACLE_TEST_HOST")
  val oracleTestDB = sys.env("RAW_ORACLE_TEST_DB")
  val oracleTestUser = sys.env("RAW_ORACLE_TEST_USER")
  val oracleTestPassword = sys.env("RAW_ORACLE_TEST_PASSWORD")
  val oracleCreds = OracleJdbcLocation(oracleTestHost, 1521, oracleTestDB, oracleTestUser, oracleTestPassword)
  val sqlServerTestHost = sys.env("RAW_SQLSERVER_TEST_HOST")
  val sqlserverTestDB = sys.env("RAW_SQLSERVER_TEST_DB")
  val sqlServerTestUser = sys.env("RAW_SQLSERVER_TEST_USER")
  val sqlServerTestPassword = sys.env("RAW_SQLSERVER_TEST_PASSWORD")
  val sqlServerCreds = SqlServerJdbcLocation(
    sqlServerTestHost,
    1433,
    sqlserverTestDB,
    sqlServerTestUser,
    sqlServerTestPassword
  )
  val teradataTestHost = sys.env("RAW_TERADATA_TEST_HOST")
  val teradataTestUser = sys.env("RAW_TERADATA_TEST_USER")
  val teradataTestPassword = sys.env("RAW_TERADATA_TEST_PASSWORD")
  val teradataCreds = TeraDataJdbcLocation(teradataTestHost, 1025, teradataTestUser, teradataTestPassword)
  val snowflakeTestHost = sys.env("RAW_SNOWFLAKE_TEST_HOST")
  val snowflakeTestDB = sys.env("RAW_SNOWFLAKE_TEST_DB")
  val snowflakeTestUser = sys.env("RAW_SNOWFLAKE_TEST_USER")
  val snowflakeTestPassword = sys.env("RAW_SNOWFLAKE_TEST_PASSWORD")
  val snowflakeCreds = SnowflakeJdbcLocation(
    snowflakeTestHost,
    snowflakeTestDB,
    snowflakeTestUser,
    snowflakeTestPassword,
    Map("timezone" -> "UTC")
  )
  val badMysqlCreds = MySqlJdbcLocation("does-not-exist.raw-labs.com", 3306, "rdbmstest", "t0or", "$up3r$3cr3tValu3")

}

trait Rql2CompilerTestContext
    extends RawTestSuite
    with Matchers
    with SettingsTestContext
    with TrainingWheelsContext
    with Rql2CompilerServiceTestContext
    with Rql2OutputTestContext

    // Simple inferrer
    with LocalInferrerTestContext {

  private val secrets = new mutable.HashMap[String, String]()

  private val s3Credentials = new mutable.HashMap[String, S3Credential]()

  private val rdbmsServers = new mutable.HashMap[String, JdbcLocation]()

  protected val programOptions = new mutable.HashMap[String, String]()

  def authorizedUser: InteractiveUser = InteractiveUser(Uid("janeUid"), "Jane Smith", "jane@example.com")

  def runnerScopes: Set[String] = Set.empty

  def options: Map[String, String] = Map("output-format" -> "json")

  def maybeTraceId: Option[String] = None

  def secret(name: String, value: String): Unit = {
    secrets.put(name, value)
  }

  def s3Bucket(name: String, bucket: S3Credential): Unit = {
    s3Credentials.put(name, bucket)
  }

  def rdbms(name: String, db: JdbcLocation): Unit = {
    rdbmsServers.put(name, db)
  }

  def option(key: String, value: String): Unit = {
    programOptions.put(key, value)
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    dataFiles.foreach { case ViewFileContent(content, charset, path) => Files.write(path, content.getBytes(charset)) }
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

  protected def lib(contents: String): String = {
    val path = Files.createTempFile(s"lib", s".lib")
    Files.write(path, contents.getBytes(StandardCharsets.UTF_8))
    s"file://${path.toAbsolutePath.toString}"
  }

  // Environment variables to save unit tests to file, see function "checkSaveTestToFile" bellow.
  // This file is used by the python scripts in the "ai/collect-samples" folder to populate the database.
  protected val saveTest = sys.env.getOrElse("RAW_SAVE_UNIT_TESTS", "false")
  protected val unitTestsFileName = sys.env.getOrElse("RAW_UNIT_TESTS_FILENAME", "/tmp/unit-tests.txt")

  protected def checkSaveTestToFile(test: String, code: String, tipe: Option[String], errors: Option[String]) = {
    if (saveTest.toLowerCase == "true") {
      val fw = new FileWriter(unitTestsFileName, true)
      try {
        fw.write(s"---------------------- Test:$test -------------------------\n")
        fw.write(s"---------------------- code -------------------------\n")
        fw.write(code)
        fw.write("\n")
        tipe.foreach { t =>
          fw.write(s"---------------------- type -------------------------\n")
          fw.write(t)
          fw.write("\n")
        }
        errors.foreach { e =>
          fw.write(s"---------------------- errors -------------------------\n")
          fw.write(e)
          fw.write("\n")
        }
      } finally {
        fw.close()
      }
    }
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
      checkSaveTestToFile("orderEvaluateTo", actualQuery.q, Some(expectedType), None)
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
          val tipeStr = compilerService.prettyPrint(tipe, authorizedUser)
          checkSaveTestToFile("astTypeAs", data.q, Some(tipeStr), None)
          MatchResult(
            tipe == expectedType,
            s"typed as $tipeStr instead of ${compilerService.prettyPrint(expectedType, authorizedUser)}",
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
        case Right(Some(t)) => checkSaveTestToFile("run", q.q, Some(t), None)
        case Right(None) => fail("top-level was not an expression!")
        case Left(err) => fail(s"query fails to validate; error was: $err")
      }

      val r = doExecute(q.q, None)
      r.right.foreach(path => Files.delete(path))
      r match {
        case Left(error) => MatchResult(false, s"didn't run due to: $error", "???")
        case Right(path) => MatchResult(true, "???", "query run successfully")
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
          checkSaveTestToFile("parse", data.q, None, None)
          MatchResult(true, "???", s"parsed as ${compilerService.prettyPrint(s, authorizedUser)}")
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
      checkSaveTestToFile("beCloseTo", actualQuery.q, Some(actualType), None)
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
          checkSaveTestToFile("parseAs", data.q, None, None)
          val actual = compilerService.prettyPrint(actualAst, authorizedUser)
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
          checkSaveTestToFile("astParseAs", data.q, None, None)
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
      r.right.foreach(path => Files.delete(path))
      checkSaveTestToFile("runErrorAs", actualQuery.q, None, r.left.toOption)
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
          val tipeStr = compilerService.prettyPrint(tipe, authorizedUser)
          val expectedType = compilerService.prettyPrint(parseType(expected), authorizedUser)
          checkSaveTestToFile("typeAs", data.q, Some(tipeStr), None)
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
        t1 => compilerService.prettyPrint(t1, authorizedUser)
      )
      checkSaveTestToFile("typeError", data.q, None, None)
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
          checkSaveTestToFile("typeErrorAs", data.q, None, Some(errors.mkString("\n")))
          MatchResult(
            leftOvers.isEmpty,
            s"didn't include error '${leftOvers.mkString(",")}' in '$errors'",
            s"didn't type: ${errors.mkString("\n")}"
          )
        },
        t =>
          MatchResult(
            false,
            s"typed as ${compilerService
              .prettyPrint(t, authorizedUser)} instead of failing to type with ${expectedErrors.mkString(",")}",
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
      checkSaveTestToFile("evaluateTo", actualQuery.q, Some(actualType), None)
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
        t1 => compilerService.prettyPrint(t1, authorizedUser)
      )
      MatchResult(attempt.isRight, s"didn't type: $output", s"typed as $output")
    }
  }

  /////////////////////////////////////////////////////////////////////////
  // saveTo
  /////////////////////////////////////////////////////////////////////////
  class SaveTo(path: Path, options: Map[String, String]) extends Matcher[TestData] {
    def apply(q: TestData): MatchResult = {
      val maybeQueryResult = doExecute(q.q, savePath = Some(path), options = options)
      maybeQueryResult match {
        case Right(_) =>
          checkSaveTestToFile("saveTo", q.q, None, None)
          MatchResult(true, "???", s"Done")
        case Left(err) => MatchResult(false, s"didn't run due to semantic errors:\n$err", "???")
      }
    }
  }

  def saveTo(
      path: Path,
      options: Map[String, String] = Map.empty
  ): SaveTo = new SaveTo(path, options)

  def saveToInFormat(
      path: Path,
      format: String,
      options: Map[String, String] = Map.empty
  ): SaveTo = new SaveTo(path, options + ("output-format" -> format))

  // a Matcher[Path] that compares the content of the file at the given path to the given string.
  protected def contain(content: String): Matcher[Path] = be(content) compose { p: Path =>
    val bufferedSource = Source.fromFile(p.toFile)
    val fileContent = bufferedSource.mkString
    bufferedSource.close()
    fileContent
  }

  /////////////////////////////////////////////////////////////////////////
  // Helper Functions
  /////////////////////////////////////////////////////////////////////////

  private def getQueryEnvironment(
      maybeArguments: Option[Array[(String, RawValue)]] = None,
      scopes: Set[String] = Set.empty,
      options: Map[String, String] = Map.empty
  ): ProgramEnvironment = {
    val user = authorizedUser
    ProgramEnvironment(
      user,
      maybeArguments,
      this.runnerScopes ++ scopes,
      secrets.toMap,
      rdbmsServers.toMap,
      Map.empty, // http headers
      s3Credentials.toMap,
      this.options ++ options ++ programOptions,
      None, // jdbcUrl
      maybeTraceId
    )
  }

  def parseQuery(code: String): BaseProgram = tryToParse(code) match {
    case Right(p) => p
    case Left(error) => throw new TestFailedException(Some(s"$code didn't parse: " + error), None, 4)
  }

  def parseType(tipe: String): Type = {
    compilerService.parseType(tipe, authorizedUser) match {
      case ParseTypeSuccess(t) => t
      case ParseTypeFailure(error) => throw new TestFailedException(Some(s"$tipe didn't parse: " + error), None, 4)
    }
  }

  private def tryToParse(q: String): Either[String, BaseProgram] = {
    try {
      compilerService.parse(q, getQueryEnvironment()) match {
        case ParseSuccess(p) => Right(p)
        case ParseFailure(error) => Left(error.head.message)
      }
    } catch {
      case ex: RawException => Left(ex.getMessage)
    }
  }

  def tryToType(s: String): Either[Seq[String], Type] = {
    try {
      compilerService.getType(s, getQueryEnvironment()) match {
        case GetTypeSuccess(t) => Right(t.get)
        case GetTypeFailure(errors) =>
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

  def validate(s: String, environment: ProgramEnvironment = getQueryEnvironment()): ValidateResponse = {
    compilerService.validate(s, environment)
  }

  def aiValidate(s: String, environment: ProgramEnvironment = getQueryEnvironment()): ValidateResponse = {
    compilerService.aiValidate(s, environment)
  }

  def hover(s: String, position: Pos, environment: ProgramEnvironment = getQueryEnvironment()): HoverResponse = {
    compilerService.hover(s, environment, position)
  }

  def formatCode(
      s: String,
      maybeIndent: Option[Int] = None,
      maybeWidth: Option[Int] = None,
      environment: ProgramEnvironment = getQueryEnvironment()
  ): FormatCodeResponse = {
    compilerService.formatCode(s, environment, maybeIndent, maybeWidth)
  }

  def dotAutoComplete(
      s: String,
      position: Pos,
      environment: ProgramEnvironment = getQueryEnvironment()
  ): AutoCompleteResponse = {
    compilerService.dotAutoComplete(s, environment, position)
  }

  def wordAutoComplete(
      s: String,
      prefix: String,
      position: Pos,
      environment: ProgramEnvironment = getQueryEnvironment()
  ): AutoCompleteResponse = {
    compilerService.wordAutoComplete(s, environment, prefix, position)
  }

  def goToDefinition(
      s: String,
      position: Pos,
      environment: ProgramEnvironment = getQueryEnvironment()
  ): GoToDefinitionResponse = {
    compilerService.goToDefinition(s, environment, position)
  }

  def rename(
      s: String,
      position: Pos,
      environment: ProgramEnvironment = getQueryEnvironment()
  ): RenameResponse = {
    compilerService.rename(s, environment, position)
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

    doExecute(queryString, options = allOptions, scopes = allScopes).right.flatMap { queryResultPath =>
      try {
        logger.debug("Test infrastructure now parsing output...")
        val data = outputParser(queryResultPath, tipe, ordered, precision, floatingPointAsString)
        logger.debug("... done.")
        Right((data, tipe))
      } finally {
        Files.delete(queryResultPath)
      }
    }
  }

  def fastExecute(
      query: String,
      maybeDecl: Option[String] = None,
      maybeArgs: Option[Array[(String, RawValue)]] = None,
      options: Map[String, String] = Map.empty,
      scopes: Set[String] = Set.empty
  ): Either[String, Path] = {
    val outputStream = new ByteArrayOutputStream()
    try {
      compilerService.execute(
        query,
        getQueryEnvironment(maybeArgs, scopes, options),
        maybeDecl,
        outputStream
      ) match {
        case ExecutionValidationFailure(errs) => Left(errs.map(err => err.toString).mkString(","))
        case ExecutionRuntimeFailure(err) => Left(err)
        case ExecutionSuccess(_) => Right(Path.of(outputStream.toString))
      }
    } finally {
      outputStream.close()
    }
  }

  def doValidate(
      query: String,
      maybeArgs: Option[Array[(String, RawValue)]] = None,
      options: Map[String, String] = Map.empty,
      scopes: Set[String] = Set.empty
  ): Either[String, Option[String]] = {
    try {
      compilerService.getType(query, getQueryEnvironment(maybeArgs, scopes, options)) match {
        case GetTypeSuccess(Some(t)) => Right(Some(compilerService.prettyPrint(t, authorizedUser)))
        case GetTypeSuccess(None) => Right(None)
        case GetTypeFailure(errs) => Left(errs.map(err => err.toString).mkString(","))
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
      query: String,
      maybeDecl: Option[String] = None,
      maybeArgs: Option[Array[(String, RawValue)]] = None,
      options: Map[String, String] = Map.empty,
      scopes: Set[String] = Set.empty,
      savePath: Option[Path] = None
  ): Either[String, Path] = {
    val (outputStream, path) = savePath match {
      case Some(path) =>
        (Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING), path)
      case None =>
        val path = Files.createTempFile("query", "result")
        (Files.newOutputStream(path, StandardOpenOption.WRITE), path)
    }

    logger.debug(s"Test infrastructure now writing output result to temporary location: $path")
    try {
      compilerService.execute(query, getQueryEnvironment(maybeArgs, scopes, options), maybeDecl, outputStream) match {
        case ExecutionValidationFailure(errs) => Left(errs.map(err => err.toString).mkString(","))
        case ExecutionRuntimeFailure(err) => Left(err)
        case ExecutionSuccess(_) => Right(path)
      }
    } finally {
      outputStream.close()
      logger.debug("... done.")
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

  def outputParser(
      queryResultPath: Path,
      tipe: String,
      ordered: Boolean = false,
      precision: Option[Int] = None,
      floatingPointAsString: Boolean = false
  ): Any

}
