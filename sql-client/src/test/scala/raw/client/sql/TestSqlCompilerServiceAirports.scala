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
import raw.utils._

import java.io.ByteArrayOutputStream

class TestSqlCompilerServiceAirports extends RawTestSuite with SettingsTestContext with TrainingWheelsContext {

  private var compilerService: CompilerService = _
  private val user = InteractiveUser(Uid("bgaidioz"), "Benjamin Gaidioz", "email", Seq.empty)

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

  /* Tests are ignored since we can't really run such tests against FDW. They pass if the JDBC credentials
     are set to the example database straightaway.
   */

  ignore("""SELECT * FROM example.airports
    |WHERE airports.c
    |""".stripMargin) { t =>
    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val completion = compilerService.wordAutoComplete(t.q, environment, "c", Pos(2, 16))
    assert(
      completion.completions.toSet === Set(
        LetBindCompletion("city", "character varying"),
        LetBindCompletion("country", "character varying")
      )
    )
  }

  ignore("""SELECT * FROM example.airports
    |WHERE airports.
    |""".stripMargin) { t =>
    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val hover = compilerService.hover(t.q, environment, Pos(1, 16))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    val completion = compilerService.wordAutoComplete(t.q, environment, "air", Pos(2, 9))
    assert(
      completion.completions.toSet === Set(
        LetBindCompletion("airport_id", "integer"),
        LetBindCompletion("airports", "table")
      )
    )
    val dotCompletion = compilerService.dotAutoComplete(t.q, environment, Pos(2, 14))
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

  ignore("""SELECT * FROM example.
    |WHERE airports.city = 'Porto'
    |AND airports.country = 'Portugal'
    |""".stripMargin) { t =>
    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val hover = compilerService.hover(t.q, environment, Pos(1, 16))
    assert(hover.completion.contains(TypeCompletion("example", "schema")))
    val completion = compilerService.wordAutoComplete(t.q, environment, "exa", Pos(1, 17))
    assert(completion.completions sameElements Array(LetBindCompletion("example", "schema")))
    val dotCompletion = compilerService.dotAutoComplete(t.q, environment, Pos(1, 21))
    assert(
      dotCompletion.completions.toSet === Set(
        LetBindCompletion("airports", "table"),
        LetBindCompletion("trips", "table"),
        LetBindCompletion("machines", "table")
      )
    )
  }

  ignore("SELECT * FROM example.airports WHERE city = 'Braganca'") { t =>
    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val v = compilerService.validate(t.q, environment)
    assert(v.errors.isEmpty)
    val GetProgramDescriptionSuccess(description) = compilerService.getProgramDescription(t.q, environment)
    assert(description.maybeType.isEmpty)
    val List(main) = description.decls("main")
    assert(
      main.outType == airportType
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
      |    "latitude": "41.857800",
      |    "longitude": "-6.707125",
      |    "altitude": "2241.000",
      |    "timezone": 0,
      |    "dst": "E",
      |    "tz": "Europe/Lisbon"
      |  }
      |]""".stripMargin.replaceAll("\\s+", ""))
  }

  ignore("SELECT * FROM example.airports WHERE city = :city") { t =>
    val environment = ProgramEnvironment(
      user,
      Some(Array(("city", RawString("Braganca")))),
      Set.empty,
      Map("output-format" -> "csv")
    )
    val v = compilerService.validate(t.q, environment)
    assert(v.errors.isEmpty)
    val GetProgramDescriptionSuccess(description) = compilerService.getProgramDescription(t.q, environment)
    assert(description.maybeType.isEmpty)
    val List(main) = description.decls("main")
    assert(
      main.outType == airportType
    )
    val Some(Vector(param)) = main.params
    assert(!param.required)
    assert(param.idn == "city")
    assert(param.tipe == RawStringType(true, false))
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

  ignore("SELECT * FROM example.airports WHERE city = :city -- with null") { t =>
    val environment = ProgramEnvironment(
      user,
      Some(Array(("city", RawNull()))),
      Set.empty,
      Map("output-format" -> "json")
    )
    val v = compilerService.validate(t.q, environment)
    assert(v.errors.isEmpty)
    val GetProgramDescriptionSuccess(description) = compilerService.getProgramDescription(t.q, environment)
    assert(description.maybeType.isEmpty)
    val List(main) = description.decls("main")
    assert(
      main.outType == airportType
    )
    val Some(Vector(param)) = main.params
    assert(!param.required)
    assert(param.idn == "city")
    assert(param.tipe == RawStringType(true, false))
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

  ignore("SELECT * FROM example.airports WHERE city = :param and airport_id = :param") { t =>
    val environment = ProgramEnvironment(
      user,
      Some(Array(("param", RawString("Braganca")))),
      Set.empty,
      Map("output-format" -> "csv")
    )
    val v = compilerService.validate(t.q, environment)
    assert(v.errors.nonEmpty)
    val GetProgramDescriptionFailure(errors) = compilerService.getProgramDescription(t.q, environment)
    assert(errors.size === 1)
    assert(errors.head.message === "a parameter cannot be both string and int")
    assert(errors.head.positions(0).begin === ErrorPosition(1, 45 + 1)) // after the colon, on 'p'
    assert(errors.head.positions(0).end === ErrorPosition(1, 51)) // a, r, a, m  = 4 chars + 1
    assert(errors.head.positions(1).begin === ErrorPosition(1, 69 + 1)) // after the colon, on 'p'
    assert(errors.head.positions(1).end === ErrorPosition(1, 75)) //
  }

  ignore("""SELECT *
    |FROM""".stripMargin) { t =>
    val expectedError = "syntax error at end of input"
    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val validation = compilerService.validate(t.q, environment)
    assert(validation.errors.exists(_.message.contains(expectedError)))
    val GetProgramDescriptionFailure(descriptionErrors) = compilerService.getProgramDescription(t.q, environment)
    assert(descriptionErrors.exists(_.message.contains(expectedError)))
    val baos = new ByteArrayOutputStream()
    val ExecutionValidationFailure(executionErrors) = compilerService.execute(t.q, environment, None, baos)
    assert(executionErrors.exists(_.message.contains(expectedError)))
  }

  ignore("SELECT * FROM inexistent-table") { t =>
    val expectedError = "syntax error at or near \"-\""
    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val v = compilerService.validate(t.q, environment)
    assert(v.errors.exists(_.message.contains(expectedError)))
    val GetProgramDescriptionFailure(descriptionErrors) = compilerService.getProgramDescription(t.q, environment)
    assert(descriptionErrors.exists(_.message.contains(expectedError)))
    val baos = new ByteArrayOutputStream()
    val ExecutionValidationFailure(executionErrors) = compilerService.execute(t.q, environment, None, baos)
    assert(executionErrors.exists(_.message.contains(expectedError)))
  }

  ignore("SELECT * FROM inexistent_table") { t =>
    val expectedError = "relation \"inexistent_table\" does not exist"
    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val v = compilerService.validate(t.q, environment)
    assert(v.errors.exists(_.message.contains(expectedError)))
    val GetProgramDescriptionFailure(descriptionErrors) = compilerService.getProgramDescription(t.q, environment)
    assert(descriptionErrors.exists(_.message.contains(expectedError)))
    val baos = new ByteArrayOutputStream()
    val ExecutionValidationFailure(executionErrors) = compilerService.execute(t.q, environment, None, baos)
    assert(executionErrors.exists(_.message.contains(expectedError)))
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

  ignore("""
    |SELECT COUNT(*) FROM example.airports
    |WHERE city = :name OR country = :name""".stripMargin) { t =>
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
    assert(v.errors.isEmpty)
    val GetProgramDescriptionSuccess(description) = compilerService.getProgramDescription(t.q, withCity)
    assert(description.maybeType.isEmpty)
    val List(main) = description.decls("main")
    assert(
      main.outType == RawIterableType(
        RawRecordType(Vector(RawAttrType("count", RawLongType(false, false))), false, false),
        false,
        false
      )
    )
    val Some(Vector(param)) = main.params
    assert(!param.required)
    assert(param.idn == "name")
    assert(param.tipe == RawStringType(true, false))
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

  ignore("""
    |SELECT COUNT(*) FROM example.airports
    |WHERE city = COALESCE(:name, 'Lyon')""".stripMargin) { t =>
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
    assert(v.errors.isEmpty)
    val GetProgramDescriptionSuccess(description) = compilerService.getProgramDescription(t.q, withCity)
    assert(description.maybeType.isEmpty)
    val List(main) = description.decls("main")
    assert(
      main.outType == RawIterableType(
        RawRecordType(Vector(RawAttrType("count", RawLongType(false, false))), false, false),
        false,
        false
      )
    )
    val Some(Vector(param)) = main.params
    assert(!param.required)
    assert(param.idn == "name")
    assert(param.tipe == RawStringType(true, false))
    assert(param.defaultValue.contains(RawNull()))
    val baos = new ByteArrayOutputStream()
    baos.reset()
    assert(compilerService.execute(t.q, withCity, None, baos) == ExecutionSuccess)
    assert(baos.toString() == """[{"count":1}]""")
    baos.reset()
    assert(compilerService.execute(t.q, withNull, None, baos) == ExecutionSuccess)
    assert(baos.toString() == """[{"count":3}]""")
  }
}
