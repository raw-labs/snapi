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

package raw.client.sql

import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import org.testcontainers.utility.DockerImageName
import raw.client.api._
import raw.utils._

import java.io.ByteArrayOutputStream
import java.sql.DriverManager
import scala.io.Source

class TestSqlCompilerServiceAirports
    extends RawTestSuite
    with ForAllTestContainer
    with SettingsTestContext
    with TrainingWheelsContext {

  override val container: PostgreSQLContainer = PostgreSQLContainer(
    dockerImageNameOverride = DockerImageName.parse("postgres:15-alpine")
  )
  Class.forName("org.postgresql.Driver")

  private var compilerService: CompilerService = _
  private var jdbcUrl: String = _

  // Username equals the database
  private var user: InteractiveUser = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    val conn = DriverManager.getConnection(container.jdbcUrl, container.username, container.password);
    val resource = Source.fromResource("example.sql")
    val sql =
      try { resource.mkString }
      finally { resource.close() }

    val stmt = conn.createStatement()
    stmt.execute(sql)

    user = InteractiveUser(RawUid(container.databaseName), "fdw user", "email", Seq.empty)

    jdbcUrl = {
      val dbPort = container.mappedPort(5432).toString
      val dbName = container.databaseName
      val user = container.username
      val password = container.password
      s"jdbc:postgresql://localhost:$dbPort/$dbName?user=$user&password=$password"
    }

    compilerService = {
      new SqlCompilerService()
    }
  }

  override def afterAll(): Unit = {
    if (compilerService != null) {
      compilerService.stop()
      compilerService = null
    }
    container.stop()
    super.afterAll()
  }

  private def asJson(params: Map[String, RawValue] = Map.empty, scopes: Set[String] = Set.empty): ProgramEnvironment = {
    ProgramEnvironment(
      user,
      if (params.isEmpty) None else Some(params.toArray),
      scopes,
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map("output-format" -> "json"),
      jdbcUrl = Some(jdbcUrl)
    )
  }
  private def asCsv(params: Map[String, RawValue], scopes: Set[String] = Set.empty): ProgramEnvironment = {
    ProgramEnvironment(
      user,
      if (params.isEmpty) None else Some(params.toArray),
      scopes,
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map("output-format" -> "csv"),
      jdbcUrl = Some(jdbcUrl)
    )
  }

  test("""SELECT COUNT(*) FROM example.airports
    |WHERE :city::json IS NULL
    |   OR :city::integer != 3
    |   OR :city::xml IS NULL""".stripMargin) { t =>
    val v = compilerService.validate(t.q, asJson())
    assert(v.messages.size == 2)
    assert(v.messages(0).positions(0).begin.line == 2) // first error is about json (one position, the :city::json)
    assert(v.messages(1).positions(0).begin.line == 4) // second error is about xml (one position, the :city::xml)
  // :city::integer is valid
  }

  test("""-- @type v double precisionw
    |SELECT :v FROM example.airports where city = :city""".stripMargin) { t =>
    val hover = compilerService.hover(t.q, asJson(), Pos(2, 48))
    // the typo in type declaration doesn't block hover info about a correct one
    assert(hover.completion.contains(TypeCompletion("city", "varchar")))

  }
  ignore("""select * from public."B """.stripMargin) { t => }

  // Quoted value
  test("""select * from exam""".stripMargin) { t =>
    val completion = compilerService.wordAutoComplete(t.q, asJson(), "c", Pos(1, 19))
    assert(
      completion.completions.toSet === Set(LetBindCompletion("example", "schema"))
    )
  }

  // Quoted value
  ignore("""do something to see if a schema has the same name as a column and it still works""") { t =>
    val completion = compilerService.wordAutoComplete(t.q, asJson(), "c", Pos(1, 19)) // right after 'm'
    assert(
      completion.completions.toSet === Set(LetBindCompletion("example", "schema"))
    )
  }

  // Quoted value
  test("""select * from "example"."airp""".stripMargin) { t =>
    val completion = compilerService.wordAutoComplete(t.q, asJson(), "c", Pos(1, 30))
    assert(
      completion.completions.toSet === Set(LetBindCompletion("airports", "table"))
    )
  }

  test("""SELECT * FROM example.airports
    |WHERE airports.c
    |""".stripMargin) { t =>
    val completion = compilerService.wordAutoComplete(t.q, asJson(), "c", Pos(2, 17))
    assert(
      completion.completions.toSet === Set(
        LetBindCompletion("city", "character varying"),
        LetBindCompletion("country", "character varying")
      )
    )
  }

  // Quoted value
  test("""SELECT * FROM example.airports
    |WHERE airports."c
    |""".stripMargin) { t =>
    val completion = compilerService.wordAutoComplete(t.q, asJson(), "c", Pos(2, 18))
    assert(
      completion.completions.toSet === Set(
        LetBindCompletion("city", "character varying"),
        LetBindCompletion("country", "character varying")
      )
    )
  }

  // it parses AND as a field of ai.ANDairports
  test("""SELECT * FROM example.airports
    |WHERE ai.
    |AND   airports.
    |""".stripMargin) { t =>
    val hover = compilerService.hover(t.q, asJson(), Pos(1, 16))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    val completion = compilerService.wordAutoComplete(t.q, asJson(), "", Pos(2, 9))
    assert(
      completion.completions.toSet === Set(
        LetBindCompletion("airport_id", "integer"),
        LetBindCompletion("airports", "table")
      )
    )

    // The calls to the dotAutoComplete have to point to the place before the dot
    val dotCompletion = compilerService.dotAutoComplete(t.q, asJson(), Pos(3, 15))
    assert(
      dotCompletion.completions.toSet === airportColumns
    )
  }

  test("""SELECT * FROM example.airports
    |WHERE ai.
    |""".stripMargin) { t =>
    val hover = compilerService.hover(t.q, asJson(), Pos(1, 16))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    val completion = compilerService.wordAutoComplete(t.q, asJson(), "", Pos(2, 9))
    assert(
      completion.completions.toSet === Set(
        LetBindCompletion("airport_id", "integer"),
        LetBindCompletion("airports", "table")
      )
    )

  }

  test("""SELECT * FROM example.airports
    |WHERE airports.
    |""".stripMargin) { t =>
    val hover = compilerService.hover(t.q, asJson(), Pos(1, 16))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    // The calls to the dotAutoComplete have to point to the place before the dot
    val dotCompletion = compilerService.dotAutoComplete(t.q, asJson(), Pos(2, 15))
    assert(
      dotCompletion.completions.toSet === airportColumns
    )
  }

  // dot completion with three items
  test("""SELECT * FROM example.airports
    |WHERE example.airports.
    |""".stripMargin) { t =>
    val hover = compilerService.hover(t.q, asJson(), Pos(1, 16))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    val completion = compilerService.wordAutoComplete(t.q, asJson(), "", Pos(2, 17))
    assert(
      completion.completions.toSet === Set(
        LetBindCompletion("airports", "table")
      )
    )

    // The calls to the dotAutoComplete have to point to the place before the dot
    val dotCompletion = compilerService.dotAutoComplete(t.q, asJson(), Pos(2, 23))
    assert(
      dotCompletion.completions.toSet === airportColumns
    )
  }

  // Quoted identifiers
  test("""SELECT * FROM "example"."airports"
    |WHERE "ai
    |""".stripMargin) { t =>
    val hover = compilerService.hover(t.q, asJson(), Pos(1, 17))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    val completion = compilerService.wordAutoComplete(t.q, asJson(), "", Pos(2, 10))
    assert(
      completion.completions.toSet === Set(
        LetBindCompletion("airport_id", "integer"),
        LetBindCompletion("airports", "table")
      )
    )
  }

  // Quoted identifiers
  test("""SELECT * FROM "example"."airports"
    |WHERE "airports".
    |""".stripMargin) { t =>
    val hover = compilerService.hover(t.q, asJson(), Pos(1, 17))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    // The calls to the dotAutoComplete have to point to the place before the dot
    val dotCompletion = compilerService.dotAutoComplete(t.q, asJson(), Pos(2, 17))
    assert(
      dotCompletion.completions.toSet === Set(
        LetBindCompletion("icao", "character varying"),
        LetBindCompletion("tz", "character varying"),
        LetBindCompletion("latitude", "numeric"),
        LetBindCompletion("altitude", "numeric"),
        LetBindCompletion("airport_id", "integer"),
        LetBindCompletion("country", "character varying"),
        LetBindCompletion("timezone", "integer"),
        LetBindCompletion("iata_faa", "character varying"),
        LetBindCompletion("name", "character varying"),
        LetBindCompletion("dst", "character varying"),
        LetBindCompletion("longitude", "numeric"),
        LetBindCompletion("city", "character varying")
      )
    )
  }

  // Upper case
  test("""SELECT * FROM EXAMPLE.AIRPORTS
    |WHERE AIRPORTS.
    |""".stripMargin) { t =>
    val hover = compilerService.hover(t.q, asJson(), Pos(1, 16))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    // The calls to the dotAutoComplete have to point to the place before the dot
    val dotCompletion = compilerService.dotAutoComplete(t.q, asJson(), Pos(2, 15))
    assert(
      dotCompletion.completions.toSet === Set(
        LetBindCompletion("icao", "character varying"),
        LetBindCompletion("tz", "character varying"),
        LetBindCompletion("latitude", "numeric"),
        LetBindCompletion("altitude", "numeric"),
        LetBindCompletion("airport_id", "integer"),
        LetBindCompletion("country", "character varying"),
        LetBindCompletion("timezone", "integer"),
        LetBindCompletion("iata_faa", "character varying"),
        LetBindCompletion("name", "character varying"),
        LetBindCompletion("dst", "character varying"),
        LetBindCompletion("longitude", "numeric"),
        LetBindCompletion("city", "character varying")
      )
    )
  }

  // Upper case
  test("""SELECT * FROM EXAMPLE.AIRPORTS
    |WHERE AI.
    |""".stripMargin) { t =>
    val hover = compilerService.hover(t.q, asJson(), Pos(1, 16))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    val completion = compilerService.wordAutoComplete(t.q, asJson(), "", Pos(2, 9))
    assert(
      completion.completions.toSet === Set(
        LetBindCompletion("airport_id", "integer"),
        LetBindCompletion("airports", "table")
      )
    )

  }

  // Wrong case with quotes (should not find a match)
  test("""SELECT * FROM "EXAMPLE"."AIRPORTS"
    |WHERE "AIRPORTS".
    |""".stripMargin) { t =>
    val hover = compilerService.hover(t.q, asJson(), Pos(1, 16))
    assert(hover.completion.isEmpty)
    val completion = compilerService.wordAutoComplete(t.q, asJson(), "", Pos(2, 9))
    assert(completion.completions.isEmpty)

    // The calls to the dotAutoComplete have to point to the place before the dot
    val dotCompletion = compilerService.dotAutoComplete(t.q, asJson(), Pos(2, 17))
    assert(dotCompletion.completions.isEmpty)
  }

  test("""SELECT * FROM example.
    |WHERE airports.city = 'Porto'
    |AND airports.country = 'Portugal'
    |""".stripMargin) { t =>
    val hover = compilerService.hover(t.q, asJson(), Pos(1, 16))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    val completion = compilerService.wordAutoComplete(t.q, asJson(), "exa", Pos(1, 18))
    assert(completion.completions sameElements Array(LetBindCompletion("example", "schema")))
    // The calls to the dotAutoComplete have to point to the place before the dot
    val dotCompletion = compilerService.dotAutoComplete(t.q, asJson(), Pos(1, 22))
    assert(
      dotCompletion.completions.toSet === Set(
        LetBindCompletion("airports", "table"),
        LetBindCompletion("trips", "table"),
        LetBindCompletion("machines", "table")
      )
    )
  }

  test("SELECT * FROM example.airports WHERE city = 'Braganca'") { t =>
    val v = compilerService.validate(t.q, asJson())
    assert(v.messages.isEmpty)
    val GetProgramDescriptionSuccess(description) = compilerService.getProgramDescription(t.q, asJson())
    val Some(main) = description.maybeRunnable
    assert(
      main.outType.get == airportType
    )
    assert(main.params.contains(Vector.empty))
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        t.q,
        asJson(),
        None,
        baos
      ) == ExecutionSuccess(true)
    )
    assert(
      baos.toString() ==
        """[
          |  {
          |    "airport_id": 1618,
          |    "name": "Braganca",
          |    "city": "Braganca",
          |    "country": "Portugal",
          |    "iata_faa": "BGC",
          |    "icao": "LPBG",
          |    "latitude": 41.857800,
          |    "longitude": -6.707125,
          |    "altitude": 2241.000,
          |    "timezone": 0,
          |    "dst": "E",
          |    "tz": "Europe/Lisbon"
          |  }
          |]""".stripMargin.replaceAll("\\s+", "")
    )
  }

  test("SELECT * FROM example.airports WHERE city = :city") { t =>
    val environment = asCsv(Map("city" -> RawString("Braganca")))
    val v = compilerService.validate(t.q, environment)
    assert(v.messages.isEmpty)
    val GetProgramDescriptionSuccess(description) = compilerService.getProgramDescription(t.q, environment)
    assert(description.decls.isEmpty)
    val Some(main) = description.maybeRunnable
    assert(
      main.outType.get == airportType
    )
    val Some(Vector(param)) = main.params
    assert(param.required) // no default was provided
    assert(param.idn == "city")
    assert(param.tipe.get == RawStringType(true, false))
    assert(param.defaultValue.isEmpty)
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        t.q,
        environment,
        None,
        baos
      ) == ExecutionSuccess(true)
    )
    assert(
      baos.toString() ==
        """airport_id,name,city,country,iata_faa,icao,latitude,longitude,altitude,timezone,dst,tz
          |1618,Braganca,Braganca,Portugal,BGC,LPBG,41.857800,-6.707125,2241.000,0,E,Europe/Lisbon
          |""".stripMargin
    )
  }

  test("""-- a query with no default, called without the parameter
    |SELECT * FROM example.airports WHERE city = :city""".stripMargin) { t =>
    val environment = asJson(Map("city" -> RawNull()))
    val v = compilerService.validate(t.q, environment)
    assert(v.messages.isEmpty)
    val GetProgramDescriptionSuccess(description) = compilerService.getProgramDescription(t.q, environment)
    val Some(main) = description.maybeRunnable
    assert(
      main.outType.get == airportType
    )
    val Some(Vector(param)) = main.params
    assert(param.required)
    assert(param.idn == "city")
    assert(param.tipe.get == RawStringType(true, false))
    assert(param.defaultValue.isEmpty)
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        t.q,
        environment,
        None,
        baos
      ) == ExecutionSuccess(true)
    )
    assert(baos.toString() == "[]")
  }

  test("""-- a query with a default, called without the parameter
    |-- @default city 'Athens'
    |SELECT COUNT(*) AS n FROM example.airports WHERE city = :city""".stripMargin) { t =>
    val environment = asJson()
    val v = compilerService.validate(t.q, environment)
    assert(v.messages.isEmpty)
    val GetProgramDescriptionSuccess(description) = compilerService.getProgramDescription(t.q, environment)
    assert(description.decls.isEmpty)
    val Some(main) = description.maybeRunnable
    assert(
      main.outType.contains(
        RawIterableType(
          RawRecordType(Vector(RawAttrType("n", RawLongType(true, false))), false, false),
          false,
          false
        )
      )
    )
    val Some(Vector(param)) = main.params
    assert(!param.required) // default value implies it's not mandatory
    assert(param.idn == "city")
    assert(param.tipe.contains(RawStringType(true, false)))
    assert(param.defaultValue.contains(RawString("Athens")))
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        t.q,
        environment,
        None,
        baos
      ) == ExecutionSuccess(true)
    )
    assert(baos.toString() == """[{"n":6}]""")
  }

  test("""-- @type age intger
    |-- @default age 'tralala
    |-- @param whatever an unknown parameter
    |SELECT COUNT(*) FROM example.airports WHERE :city = city""".stripMargin) { t =>
    val environment = asJson(Map.empty)
    val v = compilerService.validate(t.q, environment)
    val expectedErrors = List(
      1 -> "unsupported type intger",
      2 -> "Unterminated string literal",
      3 -> "cannot guess parameter type" // whatever isn't typed
    )
    expectedErrors.foreach {
      case (line, error) => assert(
          v.messages.exists(e =>
            e.message.contains(error)
              && e.positions.exists(_.end.line == line)
          )
        )
    }
  }

  test("""-- @type age integer
    |-- @default age 'tralala'
    |-- @param whatever an unknown parameter
    |SELECT COUNT(*) FROM example.airports WHERE :age = city""".stripMargin) { t =>
    val environment = asJson(Map.empty)

    val v = compilerService.validate(t.q, environment)
    val expectedErrors = List(
      2 -> "invalid input syntax for type integer",
      3 -> "cannot guess parameter type", // 'whatever' isn't typed
      4 -> "operator does not exist: integer = character varying"
    )
    expectedErrors.foreach {
      case (line, error) => assert(
          v.messages.exists(e =>
            e.message.contains(error)
              && e.positions.exists(_.end.line == line)
          )
        )
    }
  }

  test("SELECT * FROM example.airports WHERE city = :param and airport_id = :param") { t =>
    val environment = asCsv(Map("param" -> RawString("Braganca")))
    val v = compilerService.validate(t.q, environment)
    assert(v.messages.nonEmpty)
    val GetProgramDescriptionFailure(errors) = compilerService.getProgramDescription(t.q, environment)
    assert(errors.size === 1)
    assert(errors.head.message === "a parameter cannot be both varchar and integer")
    assert(errors.head.positions(0).begin === ErrorPosition(1, 45))
    assert(errors.head.positions(0).end === ErrorPosition(1, 51)) // a, r, a, m  = 4 chars + 1
    assert(errors.head.positions(1).begin === ErrorPosition(1, 69))
    assert(errors.head.positions(1).end === ErrorPosition(1, 75)) //
  }

  test("""SELECT *
    |FROM""".stripMargin) { t =>
    val expectedError = "syntax error at end of input"
    val validation = compilerService.validate(t.q, asJson())
    assert(validation.messages.exists(_.message.contains(expectedError)))
    val GetProgramDescriptionFailure(descriptionErrors) = compilerService.getProgramDescription(t.q, asJson())
    assert(descriptionErrors.exists(_.message.contains(expectedError)))
    val baos = new ByteArrayOutputStream()
    val ExecutionValidationFailure(executionErrors) = compilerService.execute(t.q, asJson(), None, baos)
    assert(executionErrors.exists(_.message.contains(expectedError)))
  }

  test("SELECT * FROM inexistent-table") { t =>
    val expectedError = "syntax error at or near \"-\""
    val v = compilerService.validate(t.q, asJson())
    assert(v.messages.exists(_.message.contains(expectedError)))
    val GetProgramDescriptionFailure(descriptionErrors) = compilerService.getProgramDescription(t.q, asJson())
    assert(descriptionErrors.exists(_.message.contains(expectedError)))
    val baos = new ByteArrayOutputStream()
    val ExecutionValidationFailure(executionErrors) = compilerService.execute(t.q, asJson(), None, baos)
    assert(executionErrors.exists(_.message.contains(expectedError)))
  }

  test("SELECT * FROM inexistent_table") { t =>
    val expectedErrors = Set("relation \"inexistent_table\" does not exist", "Did you forget to add credentials?")
    val v = compilerService.validate(t.q, asJson())
    val failures = v.messages.collect { case errorMessage: ErrorMessage => errorMessage }
    assert(failures.exists(failure => expectedErrors.forall(failure.message.contains)))
    val GetProgramDescriptionFailure(descriptionErrors) = compilerService.getProgramDescription(t.q, asJson())
    assert(descriptionErrors.exists(error => expectedErrors.forall(error.message.contains)))
    val baos = new ByteArrayOutputStream()
    val ExecutionValidationFailure(executionErrors) = compilerService.execute(t.q, asJson(), None, baos)
    assert(executionErrors.exists(error => expectedErrors.forall(error.message.contains)))
  }

  test("""-- @default a 1234
    |-- @type a date
    |SELECT LENGTH(:a)""".stripMargin) { t =>
    val expectedErrors = Set("cannot cast type integer to date", "function length(date) does not exist")
    val v = compilerService.validate(t.q, asJson())
    val failures = v.messages.collect { case errorMessage: ErrorMessage => errorMessage }
    assert(failures.forall(err => expectedErrors.exists(err.message.contains)))
    val GetProgramDescriptionFailure(descriptionErrors) = compilerService.getProgramDescription(t.q, asJson())
    assert(descriptionErrors.forall(err => expectedErrors.exists(err.message.contains)))
    val baos = new ByteArrayOutputStream()
    val ExecutionValidationFailure(executionErrors) = compilerService.execute(t.q, asJson(), None, baos)
    assert(executionErrors.forall(err => expectedErrors.exists(err.message.contains)))
  }

  test("""/* @default a 1 + 1 */
    |SELECT :a + :a AS v""".stripMargin) { t =>
    val v = compilerService.validate(t.q, asJson())
    assert(v.messages.isEmpty, v.messages.mkString(","))
    val GetProgramDescriptionSuccess(description) = compilerService.getProgramDescription(t.q, asJson())
    assert(!description.maybeRunnable.get.params.get.head.required)
    assert(description.maybeRunnable.get.params.get.head.defaultValue.contains(RawInt(2)))
    val baos = new ByteArrayOutputStream()
    assert(compilerService.execute(t.q, asJson(), None, baos) == ExecutionSuccess(true))
    assert(baos.toString() == """[{"v":4}]""")
  }

  test("SELECT * FROM wrong.relation") { t =>
    val expectedErrors = Set("relation \"wrong.relation\" does not exist", "Did you forget to add credentials?")
    val v = compilerService.validate(t.q, asJson())
    val failures = v.messages.collect { case errorMessage: ErrorMessage => errorMessage }
    assert(failures.exists(failure => expectedErrors.forall(failure.message.contains)))
    val GetProgramDescriptionFailure(descriptionErrors) = compilerService.getProgramDescription(t.q, asJson())
    assert(descriptionErrors.exists(error => expectedErrors.forall(error.message.contains)))
    val baos = new ByteArrayOutputStream()
    val ExecutionValidationFailure(executionErrors) = compilerService.execute(t.q, asJson(), None, baos)
    assert(executionErrors.exists(error => expectedErrors.forall(error.message.contains)))
  }

  private val airportType = RawIterableType(
    RawRecordType(
      Vector(
        RawAttrType("airport_id", RawIntType(true, false)),
        RawAttrType("name", RawStringType(true, false)),
        RawAttrType("city", RawStringType(true, false)),
        RawAttrType("country", RawStringType(true, false)),
        RawAttrType("iata_faa", RawStringType(true, false)),
        RawAttrType("icao", RawStringType(true, false)),
        RawAttrType("latitude", RawDecimalType(true, false)),
        RawAttrType("longitude", RawDecimalType(true, false)),
        RawAttrType("altitude", RawDecimalType(true, false)),
        RawAttrType("timezone", RawIntType(true, false)),
        RawAttrType("dst", RawStringType(true, false)),
        RawAttrType("tz", RawStringType(true, false))
      ),
      false,
      false
    ),
    false,
    false
  )

  test("""
    |SELECT COUNT(*) FROM example.airports
    |WHERE city = :name OR country = :name""".stripMargin) { t =>
    val withCity = asJson(Map("name" -> RawString("Braganca")))
    val withCountry = asJson(Map("name" -> RawString("Portugal")))
    val withNull = asJson(Map("name" -> RawNull()))
    val v = compilerService.validate(t.q, withCity)
    assert(v.messages.isEmpty)
    val GetProgramDescriptionSuccess(description) = compilerService.getProgramDescription(t.q, withCity)
    assert(description.decls.isEmpty)
    val Some(main) = description.maybeRunnable
    assert(
      main.outType.get == RawIterableType(
        RawRecordType(Vector(RawAttrType("count", RawLongType(true, false))), false, false),
        false,
        false
      )
    )
    val Some(Vector(param)) = main.params
    assert(param.required)
    assert(param.idn == "name")
    assert(param.tipe.get == RawStringType(true, false))
    assert(param.defaultValue.isEmpty)
    val baos = new ByteArrayOutputStream()
    baos.reset()
    assert(compilerService.execute(t.q, withCity, None, baos) == ExecutionSuccess(true))
    assert(baos.toString() == """[{"count":1}]""")
    baos.reset()
    assert(compilerService.execute(t.q, withNull, None, baos) == ExecutionSuccess(true))
    assert(baos.toString() == """[{"count":0}]""")
    baos.reset()
    assert(compilerService.execute(t.q, withCountry, None, baos) == ExecutionSuccess(true))
    assert(baos.toString() == """[{"count":39}]""")
  }

  test("""
    |SELECT COUNT(*) FROM example.airports
    |WHERE city = COALESCE(:name, 'Lyon')""".stripMargin) { t =>
    val withCity = asJson(Map("name" -> RawString("Braganca")))
    val withNull = asJson(Map("name" -> RawNull()))
    val v = compilerService.validate(t.q, withCity)
    assert(v.messages.isEmpty)
    val GetProgramDescriptionSuccess(description) = compilerService.getProgramDescription(t.q, withCity)
    assert(description.decls.isEmpty)
    val Some(main) = description.maybeRunnable
    assert(
      main.outType.get == RawIterableType(
        RawRecordType(Vector(RawAttrType("count", RawLongType(true, false))), false, false),
        false,
        false
      )
    )
    val Some(Vector(param)) = main.params
    assert(param.required)
    assert(param.idn == "name")
    assert(param.tipe.get == RawStringType(true, false))
    assert(param.defaultValue.isEmpty)
    val baos = new ByteArrayOutputStream()
    baos.reset()
    assert(compilerService.execute(t.q, withCity, None, baos) == ExecutionSuccess(true))
    assert(baos.toString() == """[{"count":1}]""")
    baos.reset()
    assert(compilerService.execute(t.q, withNull, None, baos) == ExecutionSuccess(true))
    assert(baos.toString() == """[{"count":3}]""")
  }

  test("""-- @param s just an int
    |SELECT DATE '2002-01-01' - :s::int AS x -- RD-10538""".stripMargin) { t =>
    val v = compilerService.validate(t.q, asJson())
    assert(v.messages.isEmpty)
    val GetProgramDescriptionSuccess(description) = compilerService.getProgramDescription(t.q, asJson())
    assert(description.decls.isEmpty)
    val Some(main) = description.maybeRunnable
    assert(
      main.outType.get == RawIterableType(
        RawRecordType(
          Vector(
            RawAttrType("x", RawDateType(true, false))
          ),
          false,
          false
        ),
        false,
        false
      )
    )
    assert(
      main.params.contains(
        Vector(ParamDescription("s", Some(RawIntType(true, false)), None, Some("just an int"), required = true))
      )
    )
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        t.q,
        asJson(),
        None,
        baos
      ) == ExecutionRuntimeFailure("no value was specified for s")
    )
  }

  test("""-- @default s CAST(null AS INTEGER)
    |SELECT DATE '2002-01-01' - :s AS x -- RD-10538""".stripMargin) { t =>
    val v = compilerService.validate(t.q, asJson())
    assert(v.messages.isEmpty)
    val GetProgramDescriptionSuccess(description) = compilerService.getProgramDescription(t.q, asJson())
    val Some(main) = description.maybeRunnable
    assert(
      main.outType.contains(
        RawIterableType(
          RawRecordType(
            Vector(
              RawAttrType("x", RawDateType(true, false))
            ),
            false,
            false
          ),
          false,
          false
        )
      )
    )
    assert(
      main.params.contains(
        Vector(ParamDescription("s", Some(RawIntType(true, false)), Some(RawNull()), comment = None, required = false))
      )
    )
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        t.q,
        asJson(),
        None,
        baos
      ) == ExecutionSuccess(true)
    )
    assert(
      baos.toString() ==
        """[
          |  {
          |    "x": null
          |  }
          |]""".stripMargin.replaceAll("\\s+", "")
    )
  }

  private val airportColumns = Set(
    LetBindCompletion("icao", "character varying"),
    LetBindCompletion("tz", "character varying"),
    LetBindCompletion("latitude", "numeric"),
    LetBindCompletion("altitude", "numeric"),
    LetBindCompletion("airport_id", "integer"),
    LetBindCompletion("country", "character varying"),
    LetBindCompletion("timezone", "integer"),
    LetBindCompletion("iata_faa", "character varying"),
    LetBindCompletion("name", "character varying"),
    LetBindCompletion("dst", "character varying"),
    LetBindCompletion("longitude", "numeric"),
    LetBindCompletion("city", "character varying")
  )

  test("""SELECT COUNT(*) FROM example.airports;""") { t =>
    val baos = new ByteArrayOutputStream()
    baos.reset()
    val noParam = asJson()
    assert(compilerService.execute(t.q, noParam, None, baos) == ExecutionSuccess(true))
    assert(baos.toString() == """[{"count":8107}]""")
  }

  test( // RD-10505
    """SELECT COUNT(*) FROM example.airports;
      |-- nothing here
      |""".stripMargin
  ) { t =>
    val baos = new ByteArrayOutputStream()
    baos.reset()
    val noParam = asJson()
    assert(compilerService.execute(t.q, noParam, None, baos) == ExecutionSuccess(true))
    assert(baos.toString() == """[{"count":8107}]""")
  }

  test( // RD-10505
    """
      |SELECT COUNT(*) FROM example.airports;
      |
      |-- @param name Name of the client.
      |""".stripMargin
  ) { t =>
    val baos = new ByteArrayOutputStream()
    baos.reset()
    val noParam = asJson()
    assert(compilerService.execute(t.q, noParam, None, baos) == ExecutionSuccess(true))
    assert(baos.toString() == """[{"count":8107}]""")
  }

  // #RD-10612: hovering on a parameter name doesn't return the parameter type + fails internally
  // The problem is because we are at the limit of the token and we returned an empty list.
  // However, because of the function SqlCodeUtils.identifiers right now it returns an identifier with an empty string.
  // The state machine in that function bails out because it finds the ':' at the start of the string.
  test("SELECT :v > 12 AS column") { t =>
    val hover = compilerService.hover(t.q, asJson(), Pos(1, 10))
    assert(hover.completion.contains(TypeCompletion("v", "integer"))) // postgres type

  }

  test("SELECT :city > 12, city FROM example.airports WHERE airport_id = :city LIMIT 2 ") { t =>
    val hover1 = compilerService.hover(t.q, asJson(), Pos(1, 11))
    assert(hover1.completion.contains(TypeCompletion("city", "integer"))) // has to be the Postgres type
    val hover2 = compilerService.hover(t.q, asJson(), Pos(1, 20))
    assert(hover2.completion.contains(TypeCompletion("city", "character varying")))
  }

  // RD-10865 (mistakenly passing snapi code)
  test("""[{a: 12}, null]""".stripMargin) { t =>
    val v = compilerService.validate(t.q, asJson())
    assert(v.messages.exists(_.message contains "the input does not form a valid statement or expression"))
  }

  test("""RD-10948+10961""") { _ =>
    val q = """:
      |""".stripMargin
    val ValidateResponse(errors) = compilerService.validate(q, asJson())
    assert(errors.nonEmpty)
  }

  test("""RD-10948""") { _ =>
    val q = """SELECT * FROM
      |(VALUES
      |  (1, 'janedoe', 'janedoe@raw-labs.com', '123'),
      |  (2, 'janedoe', 'janedoe@raw-labs.com', '123')
      |) as i(id, first_name, email, password)
      |WHERE email = :email AND password:
      |""".stripMargin
    val ValidateResponse(errors) = compilerService.validate(q, asJson())
    assert(errors.nonEmpty)
  }

  test("""RD-10961""") { _ =>
    val q = """-- @default id 1
      |
      |SELECT * FROM
      |(VALUES
      |  (1, 'John', 'Doe', DATE '2023-01-02'),
      |  (2, 'Jane', 'Doe', DATE '2024-01-03')
      |) as i(id, first_name, last_name, birthday)
      |WHERE id = :id && id = :
      |""".stripMargin
    val ValidateResponse(errors) = compilerService.validate(q, asJson())
    assert(errors.nonEmpty)
  }

  test("""scopes work""") { _ =>
    val baos = new ByteArrayOutputStream()
    def runWith(q: String, scopes: Set[String]): String = {
      val env = asJson(scopes = scopes)
      assert(compilerService.validate(q, env).messages.isEmpty)
      val GetProgramDescriptionSuccess(_) = compilerService.getProgramDescription(q, env)
      baos.reset()
      assert(compilerService.execute(q, env, None, baos) == ExecutionSuccess(true))
      baos.toString
    }
//    assert(runWith("SELECT e.airport_id FROM example.airports e", Set.empty) == """[]""")
    assert(runWith("SELECT token\nFROM scopes", Set.empty) == """[]""")
    assert(runWith("SELECT * FROM scopes value ORDER by value", Set.empty) == """[]""")
    assert(runWith("SELECT * FROM scopes AS value ORDER by value", Set("ADMIN")) == """[{"token":"ADMIN"}]""")
    assert(
      runWith(
        "SELECT token FROM scopes value ORDER by value",
        Set("ADMIN", "SALES", "DEV")
      ) == """[{"token":"ADMIN"},{"token":"DEV"},{"token":"SALES"}]"""
    )
    assert(
      runWith(
        """SELECT 'DEV' IN (SELECT * FROM scopes) AS isDev,
          |       'ADMIN' IN (SELECT token FROM scopes) AS isAdmin""".stripMargin,
        Set("ADMIN")
      ) == """[{"isdev":false,"isadmin":true}]"""
    )
    // demo CASE WHEN to hide a certain field
    val q = """SELECT
      |    CASE WHEN 'DEV' IN (SELECT * FROM scopes) THEN trip_id END AS trip_id, -- "AS trip_id" to name it normally
      |    departure_date,
      |    arrival_date
      |FROM example.trips
      |WHERE reason = 'Holidays' AND departure_date = DATE '2016-02-27'""".stripMargin
    assert(
      runWith(q, Set("ADMIN"))
        == """[{"trip_id":null,"departure_date":"2016-02-27","arrival_date":"2016-03-06"}]"""
    )
    assert(
      runWith(q, Set("DEV"))
        == """[{"trip_id":0,"departure_date":"2016-02-27","arrival_date":"2016-03-06"}]"""
    )
  }

  test("""-- @param p
    |-- @type p integer
    |-- SELECT :p + 10;
    |""".stripMargin) { t =>
    val v = compilerService.validate(t.q, asJson())
    assert(v.messages.size == 1)
    assert(v.messages(0).message == "non-executable code")
  }

  test("""CREATE TABLE Persons (
    |    ID int NOT NULL,
    |    LastName varchar(255) NOT NULL,
    |    FirstName varchar(255),
    |    Age int,
    |    PRIMARY KEY (ID)
    |);""".stripMargin) { t =>
    val v = compilerService.validate(t.q, asJson())
    assert(v.messages.size == 1)
    assert(v.messages(0).message == "non-executable code")
  }

  test("""select
    |  airport_id,
    |  city,
    |  country.name 'cname'
    |from
    |  example.airports
    |limit 10;
    |""".stripMargin) { t =>
    val v = compilerService.validate(t.q, asJson())
    assert(v.messages.size == 1)
    assert(v.messages(0).message == "schema \"country\" does not exist")
  }
}
