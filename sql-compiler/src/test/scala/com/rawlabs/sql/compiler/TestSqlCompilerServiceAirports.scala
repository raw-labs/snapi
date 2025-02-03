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
import com.rawlabs.compiler.{
  CompilerService,
  ErrorMessage,
  ErrorPosition,
  ExecutionError,
  ExecutionSuccess,
  LetBindCompletion,
  ParamDescription,
  Pos,
  ProgramEnvironment,
  RawAttrType,
  RawDate,
  RawDateType,
  RawDecimalType,
  RawInt,
  RawIntType,
  RawIterableType,
  RawLongType,
  RawNull,
  RawRecordType,
  RawString,
  RawStringType,
  RawValue,
  TypeCompletion,
  ValidateResponse
}
import org.testcontainers.utility.DockerImageName
import com.rawlabs.utils.core._

import java.io.ByteArrayOutputStream
import java.sql.DriverManager
import java.time.LocalDate
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
  private var user: RawUid = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    val conn = DriverManager.getConnection(container.jdbcUrl, container.username, container.password);
    val resource = Source.fromResource("example.sql")
    val sql =
      try { resource.mkString }
      finally { resource.close() }

    val stmt = conn.createStatement()
    stmt.execute(sql)

    user = RawUid(container.databaseName)

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
      Map("output-format" -> "json"),
      jdbcUrl = Some(jdbcUrl)
    )
  }

  private def asCsv(params: Map[String, RawValue] = Map.empty, scopes: Set[String] = Set.empty): ProgramEnvironment = {
    ProgramEnvironment(
      user,
      if (params.isEmpty) None else Some(params.toArray),
      scopes,
      Map.empty,
      Map.empty,
      Map("output-format" -> "csv"),
      jdbcUrl = Some(jdbcUrl)
    )
  }

  // ARRAY types
  test("""SELECT
    |    INTERVAL '1 day' AS interval,
    |    ARRAY[CAST(1 AS SMALLINT), CAST(2 AS SMALLINT), CAST(3 AS SMALLINT), CAST(4 AS SMALLINT)] AS short_array,
    |    ARRAY[1, 2, 3, 4] AS integer_array,
    |    ARRAY[CAST(1.1 AS REAL), CAST(2.2 AS REAL), CAST(3.3 AS REAL), CAST(4.4 AS REAL)] AS float_array,
    |    ARRAY[CAST(1.1 AS DOUBLE PRECISION), CAST(2.2 AS DOUBLE PRECISION), CAST(3.3 AS DOUBLE PRECISION), CAST(4.4 AS DOUBLE PRECISION)] AS double_array,
    |    ARRAY[1.1::numeric, 2.2::numeric, 3.3::numeric, 4.4::numeric] AS decimal_array,
    |    ARRAY[true, false, true, false] AS boolean_array,
    |    ARRAY[DATE '2021-01-01', DATE '2021-01-02', DATE '2021-01-03', DATE '2021-01-04'] AS date_array,
    |    ARRAY[TIME '12:00:00', TIME '13:00:00', TIME '14:00:00', TIME '15:00:00'] AS time_array,
    |    ARRAY[TIMESTAMP '2021-01-01 12:00:00', TIMESTAMP '2021-01-02 13:00:00', TIMESTAMP '2021-01-03 14:00:00', TIMESTAMP '2021-01-04 15:00:00'] AS timestamp_array,
    |    ARRAY[INTERVAL '1 day', INTERVAL '2 days', INTERVAL '3 days', INTERVAL '4 days'] AS interval_array,
    |    ARRAY['{"a": 2}'::json, '{"b": 3}'::json, '{"c": 4}'::json, '{"d": 5}'::json] AS json_array,
    |    ARRAY['{"a": 2}'::jsonb, '{"b": 3}'::jsonb, '{"c": 4}'::jsonb, '{"d": 5}'::jsonb] AS jsonb_array,
    |    ARRAY['"a" => "2", "b" => "3"'::hstore, '"c" => "4", "d" => "5"'::hstore] AS hstore_array,
    |    ARRAY['apple', 'banana', 'cherry'] AS text_array;""".stripMargin) { t =>
    val v = compilerService.validate(t.q, asJson())
    assert(v.messages.isEmpty)
    val Right(description) = compilerService.getProgramDescription(t.q, asJson())
    val Some(main) = description.maybeRunnable
    assert(main.params.contains(Vector.empty))
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        t.q,
        asJson(),
        None,
        baos
      ) == Right(ExecutionSuccess(true))
    )
    assert(
      baos.toString() ==
        """[
          |  {
          |    "interval": "P1D",
          |    "short_array": [
          |      1,
          |      2,
          |      3,
          |      4
          |    ],
          |    "integer_array": [
          |      1,
          |      2,
          |      3,
          |      4
          |    ],
          |    "float_array": [
          |      1.1,
          |      2.2,
          |      3.3,
          |      4.4
          |    ],
          |    "double_array": [
          |      1.1,
          |      2.2,
          |      3.3,
          |      4.4
          |    ],
          |    "decimal_array": [
          |      1.1,
          |      2.2,
          |      3.3,
          |      4.4
          |    ],
          |    "boolean_array": [
          |      true,
          |      false,
          |      true,
          |      false
          |    ],
          |    "date_array": [
          |      "2021-01-01",
          |      "2021-01-02",
          |      "2021-01-03",
          |      "2021-01-04"
          |    ],
          |    "time_array": [
          |      "12:00:00.000",
          |      "13:00:00.000",
          |      "14:00:00.000",
          |      "15:00:00.000"
          |    ],
          |    "timestamp_array": [
          |      "2021-01-01T12:00:00.000",
          |      "2021-01-02T13:00:00.000",
          |      "2021-01-03T14:00:00.000",
          |      "2021-01-04T15:00:00.000"
          |    ],
          |    "interval_array": [
          |      "P1D",
          |      "P2D",
          |      "P3D",
          |      "P4D"
          |    ],
          |    "json_array": [
          |      {
          |        "a": 2.0
          |      },
          |      {
          |        "b": 3.0
          |      },
          |      {
          |        "c": 4.0
          |      },
          |      {
          |        "d": 5.0
          |      }
          |    ],
          |    "jsonb_array": [
          |      {
          |        "a": 2.0
          |      },
          |      {
          |        "b": 3.0
          |      },
          |      {
          |        "c": 4.0
          |      },
          |      {
          |        "d": 5.0
          |      }
          |    ],
          |    "hstore_array": [
          |      {
          |        "a": "2",
          |        "b": "3"
          |      },
          |      {
          |        "c": "4",
          |        "d": "5"
          |      }
          |    ],
          |    "text_array": [
          |      "apple",
          |      "banana",
          |      "cherry"
          |    ]
          |  }
          |]""".stripMargin.replaceAll("\\s+", "")
    )
  }

  // To be sure our offset checks aren't fooled by internal postgres parameters called $1, $2, ..., $10 (with several digits)
  test("""-- @type n integer
    |SELECT :n + 1
    |WHERE :n > 0
    |AND :n > 0
    |AND :n > 0
    |AND :n > 0
    |AND :n > 0
    |AND :n > 0
    |AND :n > 0
    |AND :n > 0
    |AND :n > 0
    |AND :n > 0
    |AND :n > 0,
    |AND :n > 0
    |AND :n > 0
    |AND :n > 0
    |AND :n > 0""".stripMargin) { t =>
    val Left(errors) = compilerService.getProgramDescription(t.q, asJson())
    assert(errors.size == 1)
    val error = errors.head
    assert(error.message.contains("syntax error at or near \",\""))
    assert(error.positions.head.begin.line == 13)
    assert(error.positions.head.begin.column == 11)
    assert(error.positions.head.end.line == 13)
    assert(error.positions.head.end.column == 12)
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
    val Right(description) = compilerService.getProgramDescription(t.q, asJson())
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
      ) == Right(ExecutionSuccess(true))
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
    val Right(description) = compilerService.getProgramDescription(t.q, environment)
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
      ) == Right(ExecutionSuccess(true))
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
    val Right(description) = compilerService.getProgramDescription(t.q, environment)
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
      ) == Right(ExecutionSuccess(true))
    )
    assert(baos.toString() == "[]")
  }

  test("""-- a query with a default, called without the parameter
    |-- @default city 'Athens'
    |SELECT COUNT(*) AS n FROM example.airports WHERE city = :city""".stripMargin) { t =>
    val environment = asJson()
    val v = compilerService.validate(t.q, environment)
    assert(v.messages.isEmpty)
    val Right(description) = compilerService.getProgramDescription(t.q, environment)
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
      ) == Right(ExecutionSuccess(true))
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
    val Left(errors) = compilerService.getProgramDescription(t.q, environment)
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
    val Left(descriptionErrors) = compilerService.getProgramDescription(t.q, asJson())
    assert(descriptionErrors.exists(_.message.contains(expectedError)))
    val baos = new ByteArrayOutputStream()
    val Left(ExecutionError.ValidationError(executionErrors)) = compilerService.execute(t.q, asJson(), None, baos)
    assert(executionErrors.exists(_.message.contains(expectedError)))
  }

  test("SELECT * FROM inexistent-table") { t =>
    val expectedError = "syntax error at or near \"-\""
    val v = compilerService.validate(t.q, asJson())
    assert(v.messages.exists(_.message.contains(expectedError)))
    val Left(descriptionErrors) = compilerService.getProgramDescription(t.q, asJson())
    assert(descriptionErrors.exists(_.message.contains(expectedError)))
    val baos = new ByteArrayOutputStream()
    val Left(ExecutionError.ValidationError(executionErrors)) = compilerService.execute(t.q, asJson(), None, baos)
    assert(executionErrors.exists(_.message.contains(expectedError)))
  }

  test("SELECT * FROM inexistent_table") { t =>
    val expectedErrors = Set("relation \"inexistent_table\" does not exist", "Did you forget to add credentials?")
    val v = compilerService.validate(t.q, asJson())
    val failures = v.messages.collect { case errorMessage: ErrorMessage => errorMessage }
    assert(failures.exists(failure => expectedErrors.forall(failure.message.contains)))
    val Left(descriptionErrors) = compilerService.getProgramDescription(t.q, asJson())
    assert(descriptionErrors.exists(error => expectedErrors.forall(error.message.contains)))
    val baos = new ByteArrayOutputStream()
    val Left(ExecutionError.ValidationError(executionErrors)) = compilerService.execute(t.q, asJson(), None, baos)
    assert(executionErrors.exists(error => expectedErrors.forall(error.message.contains)))
  }

  test("""-- @default a 1234
    |-- @type a date
    |SELECT LENGTH(:a)""".stripMargin) { t =>
    val expectedErrors = Set("cannot cast type integer to date", "function length(date) does not exist")
    val v = compilerService.validate(t.q, asJson())
    val failures = v.messages.collect { case errorMessage: ErrorMessage => errorMessage }
    assert(failures.forall(err => expectedErrors.exists(err.message.contains)))
    val Left(descriptionErrors) = compilerService.getProgramDescription(t.q, asJson())
    assert(descriptionErrors.forall(err => expectedErrors.exists(err.message.contains)))
    val baos = new ByteArrayOutputStream()
    val Left(ExecutionError.ValidationError(executionErrors)) = compilerService.execute(t.q, asJson(), None, baos)
    assert(executionErrors.forall(err => expectedErrors.exists(err.message.contains)))
  }

  test("""/* @default a 1 + 1 */
    |SELECT :a + :a AS v""".stripMargin) { t =>
    val v = compilerService.validate(t.q, asJson())
    assert(v.messages.isEmpty, v.messages.mkString(","))
    val Right(description) = compilerService.getProgramDescription(t.q, asJson())
    assert(!description.maybeRunnable.get.params.get.head.required)
    assert(description.maybeRunnable.get.params.get.head.defaultValue.contains(RawInt(2)))
    val baos = new ByteArrayOutputStream()
    assert(compilerService.execute(t.q, asJson(), None, baos) == Right(ExecutionSuccess(true)))
    assert(baos.toString() == """[{"v":4}]""")
  }

  test("SELECT * FROM wrong.relation") { t =>
    val expectedErrors = Set("relation \"wrong.relation\" does not exist", "Did you forget to add credentials?")
    val v = compilerService.validate(t.q, asJson())
    val failures = v.messages.collect { case errorMessage: ErrorMessage => errorMessage }
    assert(failures.exists(failure => expectedErrors.forall(failure.message.contains)))
    val Left(descriptionErrors) = compilerService.getProgramDescription(t.q, asJson())
    assert(descriptionErrors.exists(error => expectedErrors.forall(error.message.contains)))
    val baos = new ByteArrayOutputStream()
    val Left(ExecutionError.ValidationError(executionErrors)) = compilerService.execute(t.q, asJson(), None, baos)
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
    val Right(description) = compilerService.getProgramDescription(t.q, withCity)
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
    assert(compilerService.execute(t.q, withCity, None, baos) == Right(ExecutionSuccess(true)))
    assert(baos.toString() == """[{"count":1}]""")
    baos.reset()
    assert(compilerService.execute(t.q, withNull, None, baos) == Right(ExecutionSuccess(true)))
    assert(baos.toString() == """[{"count":0}]""")
    baos.reset()
    assert(compilerService.execute(t.q, withCountry, None, baos) == Right(ExecutionSuccess(true)))
    assert(baos.toString() == """[{"count":39}]""")
  }

  test("""
    |SELECT COUNT(*) FROM example.airports
    |WHERE city = COALESCE(:name, 'Lyon')""".stripMargin) { t =>
    val withCity = asJson(Map("name" -> RawString("Braganca")))
    val withNull = asJson(Map("name" -> RawNull()))
    val v = compilerService.validate(t.q, withCity)
    assert(v.messages.isEmpty)
    val Right(description) = compilerService.getProgramDescription(t.q, withCity)
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
    assert(compilerService.execute(t.q, withCity, None, baos) == Right(ExecutionSuccess(true)))
    assert(baos.toString() == """[{"count":1}]""")
    baos.reset()
    assert(compilerService.execute(t.q, withNull, None, baos) == Right(ExecutionSuccess(true)))
    assert(baos.toString() == """[{"count":3}]""")
  }

  test("""-- @param s just an int
    |SELECT DATE '2002-01-01' - :s::int AS x -- RD-10538""".stripMargin) { t =>
    val v = compilerService.validate(t.q, asJson())
    assert(v.messages.isEmpty)
    val Right(description) = compilerService.getProgramDescription(t.q, asJson())
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
      ) == Left(
        ExecutionError.ValidationError(List(ErrorMessage("no value was specified for s", List(), "sqlError", List())))
      )
    )
  }

  test("""-- @default s CAST(null AS INTEGER)
    |SELECT DATE '2002-01-01' - :s AS x -- RD-10538""".stripMargin) { t =>
    val v = compilerService.validate(t.q, asJson())
    assert(v.messages.isEmpty)
    val Right(description) = compilerService.getProgramDescription(t.q, asJson())
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
      ) == Right(ExecutionSuccess(true))
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
    assert(compilerService.execute(t.q, noParam, None, baos) == Right(ExecutionSuccess(true)))
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
    assert(compilerService.execute(t.q, noParam, None, baos) == Right(ExecutionSuccess(true)))
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
    assert(compilerService.execute(t.q, noParam, None, baos) == Right(ExecutionSuccess(true)))
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
      val Right(_) = compilerService.getProgramDescription(q, env)
      baos.reset()
      assert(compilerService.execute(q, env, None, baos) == Right(ExecutionSuccess(true)))
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
    assert(v.messages.isEmpty)
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        t.q,
        asJson(Map("p" -> RawInt(5))),
        None,
        baos
      ) == Right(ExecutionSuccess(true))
    )
    // The code does nothing, but we don't get an error when running it in Postgres.
    assert(
      baos.toString() ===
        """[{"update_count":0}]""".stripMargin
    )
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

  test("""SELECT pg_typeof(NOW())""".stripMargin) { t =>
    val ValidateResponse(errors) = compilerService.validate(t.q, asJson())
    assert(errors.isEmpty)
    val Left(errors2) = compilerService.getProgramDescription(t.q, asJson())
    errors2.map(_.message).contains("unsupported type: regtype")
  }

  test("""SELECT CAST(pg_typeof(NOW()) AS VARCHAR)""".stripMargin) { t =>
    val ValidateResponse(errors) = compilerService.validate(t.q, asJson())
    assert(errors.isEmpty)
    val Right(_) = compilerService.getProgramDescription(t.q, asJson())
    val baos = new ByteArrayOutputStream()
    baos.reset()
    assert(compilerService.execute(t.q, asJson(), None, baos) == Right(ExecutionSuccess(true)))
    assert(baos.toString() == """[{"pg_typeof":"timestamp with time zone"}]""")

  }

  test("""SELECT NOW()""".stripMargin) { t =>
    // NOW() is a timestamp with timezone. The one of the SQL connection. This test is to make sure
    // it works (we cannot assert on the result).
    val ValidateResponse(errors) = compilerService.validate(t.q, asJson())
    assert(errors.isEmpty)
    val Right(description) = compilerService.getProgramDescription(t.q, asJson())
    val baos = new ByteArrayOutputStream()
    baos.reset()
    assert(compilerService.execute(t.q, asJson(), None, baos) == Right(ExecutionSuccess(true)))
  }

  test("""SELECT TIMESTAMP '2001-07-01 12:13:14.567' AS t""".stripMargin) { t =>
    val ValidateResponse(errors) = compilerService.validate(t.q, asJson())
    assert(errors.isEmpty)
    val Right(_) = compilerService.getProgramDescription(t.q, asJson())
    val baos = new ByteArrayOutputStream()
    for (env <- Seq(asJson(), asCsv())) {
      baos.reset()
      assert(compilerService.execute(t.q, env, None, baos) == Right(ExecutionSuccess(true)))
      assert(baos.toString().contains("12:13:14.567"))
    }
  }

  test("""SELECT TIMESTAMP '2001-07-01 12:13:14' AS t""".stripMargin) { t =>
    val ValidateResponse(errors) = compilerService.validate(t.q, asJson())
    assert(errors.isEmpty)
    val Right(_) = compilerService.getProgramDescription(t.q, asJson())
    val baos = new ByteArrayOutputStream()
    baos.reset()
    assert(compilerService.execute(t.q, asCsv(), None, baos) == Right(ExecutionSuccess(true)))
    assert(baos.toString().contains("12:13:14"))
    assert(!baos.toString().contains("12:13:14.000"))
  }

  test("""SELECT TIME '12:13:14.567' AS t""".stripMargin) { t =>
    val ValidateResponse(errors) = compilerService.validate(t.q, asJson())
    assert(errors.isEmpty)
    val Right(_) = compilerService.getProgramDescription(t.q, asJson())
    val baos = new ByteArrayOutputStream()
    for (env <- Seq(asJson(), asCsv())) {
      baos.reset()
      assert(compilerService.execute(t.q, env, None, baos) == Right(ExecutionSuccess(true)))
      assert(baos.toString().contains("12:13:14.567"))
    }
  }

  test("""SELECT TIME '12:13:14' AS t""".stripMargin) { t =>
    val ValidateResponse(errors) = compilerService.validate(t.q, asJson())
    assert(errors.isEmpty)
    val Right(_) = compilerService.getProgramDescription(t.q, asJson())
    val baos = new ByteArrayOutputStream()
    baos.reset()
    assert(compilerService.execute(t.q, asCsv(), None, baos) == Right(ExecutionSuccess(true)))
    assert(baos.toString().contains("12:13:14"))
    assert(!baos.toString().contains("12:13:14.000"))
  }

  test("""-- @default t TIME '12:13:14.567'
    |SELECT :t AS t""".stripMargin) { t =>
    val ValidateResponse(errors) = compilerService.validate(t.q, asJson())
    assert(errors.isEmpty)
    val Right(_) = compilerService.getProgramDescription(t.q, asJson())
    val baos = new ByteArrayOutputStream()
    baos.reset()
    for (env <- Seq(asJson(), asCsv())) {
      baos.reset()
      assert(compilerService.execute(t.q, env, None, baos) == Right(ExecutionSuccess(true)))
      assert(baos.toString().contains("12:13:14.567"))
    }
  }

  test("""-- @type x integer
    |-- @default x null
    |SELECT :x AS x""".stripMargin) { t =>
    val baos = new ByteArrayOutputStream()
    baos.reset()
    assert(compilerService.execute(t.q, asJson(), None, baos) == Right(ExecutionSuccess(true)))
    assert(baos.toString() === """[{"x":null}]""")
    baos.reset()
    assert(compilerService.execute(t.q, asJson(Map("x" -> RawInt(12))), None, baos) == Right(ExecutionSuccess(true)))
    assert(baos.toString() === """[{"x":12}]""")
  }

  test("""-- @type x varchar
    |-- @default x null
    |SELECT :x AS x""".stripMargin) { t =>
    val baos = new ByteArrayOutputStream()
    assert(compilerService.execute(t.q, asJson(), None, baos) == Right(ExecutionSuccess(true)))
    assert(baos.toString() === """[{"x":null}]""")
    baos.reset()
    assert(
      compilerService.execute(t.q, asJson(Map("x" -> RawString("tralala"))), None, baos) == Right(
        ExecutionSuccess(true)
      )
    )
    assert(baos.toString() === """[{"x":"tralala"}]""")
  }

  test("""-- @type x date
    |-- @default x null
    |SELECT :x AS x""".stripMargin) { t =>
    val baos = new ByteArrayOutputStream()
    assert(compilerService.execute(t.q, asJson(), None, baos) == Right(ExecutionSuccess(true)))
    assert(baos.toString() === """[{"x":null}]""")
    baos.reset()
    assert(
      compilerService.execute(
        t.q,
        asJson(Map("x" -> RawDate(LocalDate.of(2008, 9, 29)))),
        None,
        baos
      ) == Right(ExecutionSuccess(true))
    )
    assert(baos.toString() === """[{"x":"2008-09-29"}]""")
  }

  test("""RD-14898""".stripMargin) { _ =>
    {
      // A query with a single statement spread over two lines.
      val t = TestData("""
        |SELECT airport_id FROM example.airports WHERE city = :location
        |                                    AND airport_id = :iata
        |""".stripMargin)
      // hover ':location' returns the type of location (varchar)
      val v1 = compilerService.hover(t.q, asJson(), Pos(2, 57))
      assert(v1.completion.contains(TypeCompletion("location", "varchar")))
      // hover ':iata' returns the type of iata (integer)
      val v2 = compilerService.hover(t.q, asJson(), Pos(3, 57))
      assert(v2.completion.contains(TypeCompletion("iata", "integer")))
    }
    {
      // ALMOST THE SAME code as before, there's a typo: a semicolon in the end of the first line.
      val t = TestData("""
        |SELECT airport_id FROM example.airports WHERE city = :location;
        |                                    AND airport_id = :iata
        |""".stripMargin)
      // hover ':location' still returns the type of location (varchar)
      val v1 = compilerService.hover(t.q, asJson(), Pos(2, 57))
      assert(v1.completion.contains(TypeCompletion("location", "varchar")))
      // hover ':iata' doesn't return anything since it's ignored.
      val v2 = compilerService.hover(t.q, asJson(), Pos(3, 57))
      assert(v2.completion.isEmpty)
    }
  }

  test("SELECT 'a=>1,b=>tralala'::hstore AS r -- JSON") { t =>
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        t.q,
        asJson(),
        None,
        baos
      ) == Right(ExecutionSuccess(true))
    )
    assert(baos.toString() === """[{"r":{"a":"1","b":"tralala"}}]""")
  }

  test("SELECT NULL::hstore AS r -- JSON") { t =>
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        t.q,
        asJson(),
        None,
        baos
      ) == Right(ExecutionSuccess(true))
    )
    assert(baos.toString() === """[{"r":null}]""")
  }

  // TODO What do we do in CSV?
  ignore("SELECT 'a=>1,b=>tralala'::hstore AS r -- CSV") { t =>
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        t.q,
        asCsv(),
        None,
        baos
      ) == Right(ExecutionSuccess(true))
    )
    assert(
      baos.toString() ===
        """r
          |{"a":"1","b":"tralala"}
          |""".stripMargin
    )
  }

  ignore("SELECT NULL::hstore AS r -- CSV") { t =>
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        t.q,
        asCsv(),
        None,
        baos
      ) == Right(ExecutionSuccess(true))
    )
    assert(baos.toString() === """[{"r":{"a":"1","b":"tralala"}}]""")
  }

  test("SELECT a.* FROM example.airports a ORDER BY airport_id LIMIT 1") { t =>
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        t.q,
        asCsv(),
        None,
        baos
      ) == Right(ExecutionSuccess(true))
    )
    assert(baos.toString() === """airport_id,name,city,country,iata_faa,icao,latitude,longitude,altitude,timezone,dst,tz
      |1,Goroka,Goroka,Papua New Guinea,GKA,AYGA,-6.081689,145.391881,5282.000,10,U,Pacific/Port_Moresby
      |""".stripMargin)

  }

  test("INSERT") { _ =>
    // An SQL INSERT statement adding a fake city to the airports table.
    val q =
      """INSERT INTO example.airports (airport_id, name, city, country, iata_faa, icao, latitude, longitude, altitude, timezone, dst, tz)
        |VALUES (8108, :airport, :city, :country, 'FC', 'FC', 0.0, 0.0, 0.0, 0, 'U', 'UTC')
        |""".stripMargin
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        q,
        asCsv(params =
          Map("airport" -> RawString("FAKE"), "city" -> RawString("Fake City"), "country" -> RawString("Fake Country"))
        ),
        None,
        baos
      ) == Right(ExecutionSuccess(true))
    )
    assert(
      baos.toString() ===
        """update_count
          |1
          |""".stripMargin
    )
    baos.reset()
    assert(
      compilerService.execute(
        "SELECT city, country FROM example.airports WHERE name = :a",
        asCsv(params = Map("a" -> RawString("FAKE"))),
        None,
        baos
      ) == Right(ExecutionSuccess(true))
    )
    assert(
      baos.toString() ===
        """city,country
          |Fake City,Fake Country
          |""".stripMargin
    )

  }

  test("UPDATE (CSV output)") { _ =>
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        "UPDATE example.airports SET city = :newName WHERE country = :c",
        asCsv(params = Map("newName" -> RawString("La Roche sur Foron"), "c" -> RawString("Portugal"))),
        None,
        baos
      ) == Right(ExecutionSuccess(true))
    )
    assert(
      baos.toString() ===
        """update_count
          |39
          |""".stripMargin
    )
    baos.reset()
    assert(
      compilerService.execute(
        "SELECT DISTINCT city FROM example.airports WHERE country = :c",
        asCsv(params = Map("c" -> RawString("Portugal"))),
        None,
        baos
      ) == Right(ExecutionSuccess(true))
    )
    assert(
      baos.toString() ===
        """city
          |La Roche sur Foron
          |""".stripMargin
    )
  }

  test("UPDATE (Json output)") { _ =>
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        "UPDATE example.airports SET city = :newName WHERE country = :c",
        asJson(params = Map("newName" -> RawString("Lausanne"), "c" -> RawString("Portugal"))),
        None,
        baos
      ) == Right(ExecutionSuccess(true))
    )
    assert(
      baos.toString() ===
        """[{"update_count":39}]""".stripMargin
    )
    baos.reset()
    assert(
      compilerService.execute(
        "SELECT DISTINCT city FROM example.airports WHERE country = :c",
        asJson(params = Map("c" -> RawString("Portugal"))),
        None,
        baos
      ) == Right(ExecutionSuccess(true))
    )
    assert(
      baos.toString() ===
        """[{"city":"Lausanne"}]"""
    )
  }
}
