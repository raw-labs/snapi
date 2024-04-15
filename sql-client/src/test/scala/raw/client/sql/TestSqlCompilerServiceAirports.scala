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

/* we probably need to make parameters optional and replace them by null when not specified. No default value
   where does one get the credentials from?
   let's try to find a decent library that deals with parameters?
   since we run on Postgres we can use the Postgres JDBC driver exceptions and be more specific with error handling
   - some messages show line and column numbers, we could use that to provide more precise error messages
   - but it will be messed up if we don't account for question marks
   one should return correct runtime/validation failures
 */

package raw.client.sql

import raw.client.api._
import raw.creds.api.CredentialsTestContext
import raw.creds.local.LocalCredentialsTestContext
import raw.utils._

import java.io.ByteArrayOutputStream

class TestSqlCompilerServiceAirports
    extends RawTestSuite
    with SettingsTestContext
    with TrainingWheelsContext
    with CredentialsTestContext
    with LocalCredentialsTestContext {

  private var compilerService: CompilerService = _

  private val database = sys.env.getOrElse("FDW_DATABASE", "raw")
  private val hostname = sys.env.getOrElse("FDW_HOSTNAME", "localhost")
  private val port = sys.env.getOrElse("FDW_HOSTNAME", "5432")
  private val username = sys.env.getOrElse("FDW_USERNAME", "newbie")
  private val password = sys.env.getOrElse("FDW_PASSWORD", "")

  property("raw.creds.jdbc.fdw.host", hostname)
  property("raw.creds.jdbc.fdw.port", port)
  property("raw.creds.jdbc.fdw.user", username)
  property("raw.creds.jdbc.fdw.password", password)

  // Username equals the database
  private val user = InteractiveUser(Uid(database), "fdw user", "email", Seq.empty)

  override def beforeAll(): Unit = {
    super.beforeAll()
    compilerService = new SqlCompilerService(None)
  }

  override def afterAll(): Unit = {
    if (compilerService != null) {
      compilerService.stop()
      compilerService = null
    }
    super.afterAll()
  }

  test("""SELECT COUNT(*) FROM example.airports
    |WHERE :city::json IS NULL
    |   OR :city::integer != 3
    |   OR :city::xml IS NULL""".stripMargin) { t =>
    assume(password != "")

    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val v = compilerService.validate(t.q, environment)
    assert(v.messages.size == 2)
    assert(v.messages(0).positions(0).begin.line == 2) // first error is about json (one position, the :city::json)
    assert(v.messages(1).positions(0).begin.line == 4) // second error is about xml (one position, the :city::xml)
  // :city::integer is valid
  }

  test("""-- @type v double precisionw
    |SELECT :v FROM example.airports where city = :city""".stripMargin) { t =>
    assume(password != "")

    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val hover = compilerService.hover(t.q, environment, Pos(2, 48))
    // the typo in type declaration doesn't block hover info about a correct one
    assert(hover.completion.contains(TypeCompletion("city", "varchar")))

  }
  ignore("""select * from public."B """.stripMargin) { t => }

  // Quoted value
  test("""select * from exam""".stripMargin) { t =>
    assume(password != "")
    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val completion = compilerService.wordAutoComplete(t.q, environment, "c", Pos(1, 19))
    assert(
      completion.completions.toSet === Set(LetBindCompletion("example", "schema"))
    )
  }

  // Quoted value
  ignore("""do something to see if a schema has the same name as a column and it still works""") { t =>
    assume(password != "")
    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val completion = compilerService.wordAutoComplete(t.q, environment, "c", Pos(1, 19)) // right after 'm'
    assert(
      completion.completions.toSet === Set(LetBindCompletion("example", "schema"))
    )
  }

  // Quoted value
  test("""select * from "example"."airp""".stripMargin) { t =>
    assume(password != "")
    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val completion = compilerService.wordAutoComplete(t.q, environment, "c", Pos(1, 30))
    assert(
      completion.completions.toSet === Set(LetBindCompletion("airports", "table"))
    )
  }

  test("""SELECT * FROM example.airports
    |WHERE airports.c
    |""".stripMargin) { t =>
    assume(password != "")
    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val completion = compilerService.wordAutoComplete(t.q, environment, "c", Pos(2, 17))
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
    assume(password != "")
    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val completion = compilerService.wordAutoComplete(t.q, environment, "c", Pos(2, 18))
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
    assume(password != "")

    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val hover = compilerService.hover(t.q, environment, Pos(1, 16))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    val completion = compilerService.wordAutoComplete(t.q, environment, "", Pos(2, 9))
    assert(
      completion.completions.toSet === Set(
        LetBindCompletion("airport_id", "integer"),
        LetBindCompletion("airports", "table")
      )
    )

    // The calls to the dotAutoComplete have to point to the place before the dot
    val dotCompletion = compilerService.dotAutoComplete(t.q, environment, Pos(3, 15))
    assert(
      dotCompletion.completions.toSet === airportColumns
    )
  }

  test("""SELECT * FROM example.airports
    |WHERE ai.
    |""".stripMargin) { t =>
    assume(password != "")

    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val hover = compilerService.hover(t.q, environment, Pos(1, 16))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    val completion = compilerService.wordAutoComplete(t.q, environment, "", Pos(2, 9))
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
    assume(password != "")

    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val hover = compilerService.hover(t.q, environment, Pos(1, 16))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    // The calls to the dotAutoComplete have to point to the place before the dot
    val dotCompletion = compilerService.dotAutoComplete(t.q, environment, Pos(2, 15))
    assert(
      dotCompletion.completions.toSet === airportColumns
    )
  }

  // dot completion with three items
  test("""SELECT * FROM example.airports
    |WHERE example.airports.
    |""".stripMargin) { t =>
    assume(password != "")

    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val hover = compilerService.hover(t.q, environment, Pos(1, 16))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    val completion = compilerService.wordAutoComplete(t.q, environment, "", Pos(2, 17))
    assert(
      completion.completions.toSet === Set(
        LetBindCompletion("airports", "table")
      )
    )

    // The calls to the dotAutoComplete have to point to the place before the dot
    val dotCompletion = compilerService.dotAutoComplete(t.q, environment, Pos(2, 23))
    assert(
      dotCompletion.completions.toSet === airportColumns
    )
  }

  // Quoted identifiers
  test("""SELECT * FROM "example"."airports"
    |WHERE "ai
    |""".stripMargin) { t =>
    assume(password != "")

    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val hover = compilerService.hover(t.q, environment, Pos(1, 17))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    val completion = compilerService.wordAutoComplete(t.q, environment, "", Pos(2, 10))
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
    assume(password != "")

    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val hover = compilerService.hover(t.q, environment, Pos(1, 17))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    // The calls to the dotAutoComplete have to point to the place before the dot
    val dotCompletion = compilerService.dotAutoComplete(t.q, environment, Pos(2, 17))
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
    assume(password != "")

    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val hover = compilerService.hover(t.q, environment, Pos(1, 16))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    // The calls to the dotAutoComplete have to point to the place before the dot
    val dotCompletion = compilerService.dotAutoComplete(t.q, environment, Pos(2, 15))
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
    assume(password != "")

    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val hover = compilerService.hover(t.q, environment, Pos(1, 16))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    val completion = compilerService.wordAutoComplete(t.q, environment, "", Pos(2, 9))
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
    assume(password != "")

    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val hover = compilerService.hover(t.q, environment, Pos(1, 16))
    assert(hover.completion.isEmpty)
    val completion = compilerService.wordAutoComplete(t.q, environment, "", Pos(2, 9))
    assert(completion.completions.isEmpty)

    // The calls to the dotAutoComplete have to point to the place before the dot
    val dotCompletion = compilerService.dotAutoComplete(t.q, environment, Pos(2, 17))
    assert(dotCompletion.completions.isEmpty)
  }

  test("""SELECT * FROM example.
    |WHERE airports.city = 'Porto'
    |AND airports.country = 'Portugal'
    |""".stripMargin) { t =>
    assume(password != "")

    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val hover = compilerService.hover(t.q, environment, Pos(1, 16))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    val completion = compilerService.wordAutoComplete(t.q, environment, "exa", Pos(1, 18))
    assert(completion.completions sameElements Array(LetBindCompletion("example", "schema")))
    // The calls to the dotAutoComplete have to point to the place before the dot
    val dotCompletion = compilerService.dotAutoComplete(t.q, environment, Pos(1, 22))
    assert(
      dotCompletion.completions.toSet === Set(
        LetBindCompletion("airports", "table"),
        LetBindCompletion("trips", "table"),
        LetBindCompletion("machines", "table")
      )
    )
  }

  test("SELECT * FROM example.airports WHERE city = 'Braganca'") { t =>
    assume(password != "")

    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val v = compilerService.validate(t.q, environment)
    assert(v.messages.isEmpty)
    val GetProgramDescriptionSuccess(description) = compilerService.getProgramDescription(t.q, environment)
    assert(description.decls.isEmpty)
    val Some(main) = description.maybeRunnable
    assert(
      main.outType.get == airportType
    )
    assert(main.params.contains(Vector.empty))
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        t.q,
        environment,
        None,
        baos
      ) == ExecutionSuccess
    )
    assert(baos.toString() == """[
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
      |]""".stripMargin.replaceAll("\\s+", ""))
  }

  test("SELECT * FROM example.airports WHERE city = :city") { t =>
    assume(password != "")

    val environment = ProgramEnvironment(
      user,
      Some(Array(("city", RawString("Braganca")))),
      Set.empty,
      Map("output-format" -> "csv")
    )
    val v = compilerService.validate(t.q, environment)
    assert(v.messages.isEmpty)
    val GetProgramDescriptionSuccess(description) = compilerService.getProgramDescription(t.q, environment)
    assert(description.decls.isEmpty)
    val Some(main) = description.maybeRunnable
    assert(
      main.outType.get == airportType
    )
    val Some(Vector(param)) = main.params
    assert(!param.required)
    assert(param.idn == "city")
    assert(param.tipe.get == RawStringType(true, false))
    assert(param.defaultValue.contains(RawNull()))
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        t.q,
        environment,
        None,
        baos
      ) == ExecutionSuccess
    )
    assert(
      baos.toString() ==
        """airport_id,name,city,country,iata_faa,icao,latitude,longitude,altitude,timezone,dst,tz
          |1618,Braganca,Braganca,Portugal,BGC,LPBG,41.857800,-6.707125,2241.000,0,E,Europe/Lisbon
          |""".stripMargin
    )
  }

  test("SELECT * FROM example.airports WHERE city = :city -- with null") { t =>
    assume(password != "")

    val environment = ProgramEnvironment(
      user,
      Some(Array(("city", RawNull()))),
      Set.empty,
      Map("output-format" -> "json")
    )
    val v = compilerService.validate(t.q, environment)
    assert(v.messages.isEmpty)
    val GetProgramDescriptionSuccess(description) = compilerService.getProgramDescription(t.q, environment)
    assert(description.decls.isEmpty)
    val Some(main) = description.maybeRunnable
    assert(
      main.outType.get == airportType
    )
    val Some(Vector(param)) = main.params
    assert(!param.required)
    assert(param.idn == "city")
    assert(param.tipe.get == RawStringType(true, false))
    assert(param.defaultValue.contains(RawNull()))
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        t.q,
        environment,
        None,
        baos
      ) == ExecutionSuccess
    )
    assert(baos.toString() == "[]")
  }

  test("SELECT * FROM example.airports WHERE city = :param and airport_id = :param") { t =>
    assume(password != "")

    val environment = ProgramEnvironment(
      user,
      Some(Array(("param", RawString("Braganca")))),
      Set.empty,
      Map("output-format" -> "csv")
    )
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
    assume(password != "")

    val expectedError = "syntax error at end of input"
    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val validation = compilerService.validate(t.q, environment)
    assert(validation.messages.exists(_.message.contains(expectedError)))
    val GetProgramDescriptionFailure(descriptionErrors) = compilerService.getProgramDescription(t.q, environment)
    assert(descriptionErrors.exists(_.message.contains(expectedError)))
    val baos = new ByteArrayOutputStream()
    val ExecutionValidationFailure(executionErrors) = compilerService.execute(t.q, environment, None, baos)
    assert(executionErrors.exists(_.message.contains(expectedError)))
  }

  test("SELECT * FROM inexistent-table") { t =>
    assume(password != "")

    val expectedError = "syntax error at or near \"-\""
    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val v = compilerService.validate(t.q, environment)
    assert(v.messages.exists(_.message.contains(expectedError)))
    val GetProgramDescriptionFailure(descriptionErrors) = compilerService.getProgramDescription(t.q, environment)
    assert(descriptionErrors.exists(_.message.contains(expectedError)))
    val baos = new ByteArrayOutputStream()
    val ExecutionValidationFailure(executionErrors) = compilerService.execute(t.q, environment, None, baos)
    assert(executionErrors.exists(_.message.contains(expectedError)))
  }

  test("SELECT * FROM inexistent_table") { t =>
    assume(password != "")

    val expectedErrors = Set("relation \"inexistent_table\" does not exist", "Did you forget to add credentials?")
    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val v = compilerService.validate(t.q, environment)
    val failures = v.messages.collect { case errorMessage: ErrorMessage => errorMessage }
    assert(failures.exists(failure => expectedErrors.forall(failure.message.contains)))
    val GetProgramDescriptionFailure(descriptionErrors) = compilerService.getProgramDescription(t.q, environment)
    assert(descriptionErrors.exists(error => expectedErrors.forall(error.message.contains)))
    val baos = new ByteArrayOutputStream()
    val ExecutionValidationFailure(executionErrors) = compilerService.execute(t.q, environment, None, baos)
    assert(executionErrors.exists(error => expectedErrors.forall(error.message.contains)))
  }

  test("SELECT * FROM wrong.relation") { t =>
    assume(password != "")

    val expectedErrors = Set("relation \"wrong.relation\" does not exist", "Did you forget to add credentials?")
    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val v = compilerService.validate(t.q, environment)
    val failures = v.messages.collect { case errorMessage: ErrorMessage => errorMessage }
    assert(failures.exists(failure => expectedErrors.forall(failure.message.contains)))
    val GetProgramDescriptionFailure(descriptionErrors) = compilerService.getProgramDescription(t.q, environment)
    assert(descriptionErrors.exists(error => expectedErrors.forall(error.message.contains)))
    val baos = new ByteArrayOutputStream()
    val ExecutionValidationFailure(executionErrors) = compilerService.execute(t.q, environment, None, baos)
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
    assume(password != "")

    val withCity = ProgramEnvironment(
      user,
      Some(Array(("name", RawString("Braganca")))),
      Set.empty,
      Map("output-format" -> "json")
    )
    val withCountry = ProgramEnvironment(
      user,
      Some(Array(("name", RawString("Portugal")))),
      Set.empty,
      Map("output-format" -> "json")
    )
    val withNull = ProgramEnvironment(
      user,
      Some(Array(("name", RawNull()))),
      Set.empty,
      Map("output-format" -> "json")
    )
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
    assert(!param.required)
    assert(param.idn == "name")
    assert(param.tipe.get == RawStringType(true, false))
    assert(param.defaultValue.contains(RawNull()))
    val baos = new ByteArrayOutputStream()
    baos.reset()
    assert(compilerService.execute(t.q, withCity, None, baos) == ExecutionSuccess)
    assert(baos.toString() == """[{"count":1}]""")
    baos.reset()
    assert(compilerService.execute(t.q, withNull, None, baos) == ExecutionSuccess)
    assert(baos.toString() == """[{"count":0}]""")
    baos.reset()
    assert(compilerService.execute(t.q, withCountry, None, baos) == ExecutionSuccess)
    assert(baos.toString() == """[{"count":39}]""")
  }

  test("""
    |SELECT COUNT(*) FROM example.airports
    |WHERE city = COALESCE(:name, 'Lyon')""".stripMargin) { t =>
    assume(password != "")

    val withCity = ProgramEnvironment(
      user,
      Some(Array(("name", RawString("Braganca")))),
      Set.empty,
      Map("output-format" -> "json")
    )
    val withNull = ProgramEnvironment(
      user,
      Some(Array(("name", RawNull()))),
      Set.empty,
      Map("output-format" -> "json")
    )
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
    assert(!param.required)
    assert(param.idn == "name")
    assert(param.tipe.get == RawStringType(true, false))
    assert(param.defaultValue.contains(RawNull()))
    val baos = new ByteArrayOutputStream()
    baos.reset()
    assert(compilerService.execute(t.q, withCity, None, baos) == ExecutionSuccess)
    assert(baos.toString() == """[{"count":1}]""")
    baos.reset()
    assert(compilerService.execute(t.q, withNull, None, baos) == ExecutionSuccess)
    assert(baos.toString() == """[{"count":3}]""")
  }

  test("""-- @param s just an int
    |SELECT DATE '2002-01-01' - :s::int AS x -- RD-10538""".stripMargin) { t =>
    assume(password != "")

    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val v = compilerService.validate(t.q, environment)
    assert(v.messages.isEmpty)
    val GetProgramDescriptionSuccess(description) = compilerService.getProgramDescription(t.q, environment)
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
        Vector(ParamDescription("s", Some(RawIntType(true, false)), Some(RawNull()), Some("just an int"), false))
      )
    )
    val baos = new ByteArrayOutputStream()
    assert(
      compilerService.execute(
        t.q,
        environment,
        None,
        baos
      ) == ExecutionSuccess
    )
    assert(baos.toString() == """[
      |  {
      |    "x": null
      |  }
      |]""".stripMargin.replaceAll("\\s+", ""))
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
    assume(password != "")
    val baos = new ByteArrayOutputStream()
    baos.reset()
    val noParam = ProgramEnvironment(
      user,
      None,
      Set.empty,
      Map("output-format" -> "json")
    )
    assert(compilerService.execute(t.q, noParam, None, baos) == ExecutionSuccess)
    assert(baos.toString() == """[{"count":8107}]""")
  }

  test( // RD-10505
    """SELECT COUNT(*) FROM example.airports;
      |-- nothing here
      |""".stripMargin
  ) { t =>
    assume(password != "")
    val baos = new ByteArrayOutputStream()
    baos.reset()
    val noParam = ProgramEnvironment(
      user,
      None,
      Set.empty,
      Map("output-format" -> "json")
    )
    assert(compilerService.execute(t.q, noParam, None, baos) == ExecutionSuccess)
    assert(baos.toString() == """[{"count":8107}]""")
  }

  test( // RD-10505
    """
      |SELECT COUNT(*) FROM example.airports;
      |
      |-- @param name Name of the client.
      |""".stripMargin
  ) { t =>
    assume(password != "")
    val baos = new ByteArrayOutputStream()
    baos.reset()
    val noParam = ProgramEnvironment(
      user,
      None,
      Set.empty,
      Map("output-format" -> "json")
    )
    assert(compilerService.execute(t.q, noParam, None, baos) == ExecutionSuccess)
    assert(baos.toString() == """[{"count":8107}]""")
  }

  // #RD-10612: hovering on a parameter name doesn't return the parameter type + fails internally
  // The problem is because we are at the limit of the token and we returned an empty list.
  // However, because of the function SqlCodeUtils.identifiers right now it returns an identifier with an empty string.
  // The state machine in that function bails out because it finds the ':' at the start of the string.
  test("SELECT :v > 12 AS column") { t =>
    assume(password != "")

    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val hover = compilerService.hover(t.q, environment, Pos(1, 10))
    assert(hover.completion.contains(TypeCompletion("v", "integer"))) // postgres type

  }

  test("SELECT :city > 12, city FROM example.airports WHERE airport_id = :city LIMIT 2 ") { t =>
    assume(password != "")
    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val hover1 = compilerService.hover(t.q, environment, Pos(1, 11))
    assert(hover1.completion.contains(TypeCompletion("city", "integer"))) // has to be the Postgres type
    val hover2 = compilerService.hover(t.q, environment, Pos(1, 20))
    assert(hover2.completion.contains(TypeCompletion("city", "character varying")))
  }
}
