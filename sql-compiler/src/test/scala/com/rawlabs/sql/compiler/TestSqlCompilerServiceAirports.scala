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
import com.rawlabs.compiler._
import com.rawlabs.protocol.raw.{Type, Value}
import com.rawlabs.utils.core._
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.testcontainers.utility.DockerImageName

import java.io.ByteArrayOutputStream
import java.sql.DriverManager
import java.time.LocalDate
import scala.collection.JavaConverters._
import scala.io.Source

class TestSqlCompilerServiceAirports
    extends RawTestSuite
    with EitherValues
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
    val EvalSuccess.IteratorValue(tipe, it) = compilerService.eval(t.q, asJson(), None).value
    assert(tipe.hasRecord)
    val colTypes = tipe.getRecord.getAttsList
    val colValues = it.next().getRecord.getFieldsList
    it.hasNext shouldBe false
    colTypes.size shouldBe 15
    colValues.size shouldBe colTypes.size
    // Col 0
    colTypes.get(0).getIdn shouldBe "interval"
    colTypes.get(0).getTipe.hasInterval shouldBe true
    val intervalCol = colValues.get(0).getValue.getInterval
    intervalCol.getYears shouldBe 0
    intervalCol.getMonths shouldBe 0
    intervalCol.getDays shouldBe 1
    intervalCol.getHours shouldBe 0
    intervalCol.getMinutes shouldBe 0
    intervalCol.getSeconds shouldBe 0
    intervalCol.getMillis shouldBe 0
    // Col 1
    colTypes.get(1).getIdn shouldBe "short_array"
    colTypes.get(1).getTipe.hasList shouldBe true
    colTypes.get(1).getTipe.getList.getInnerType.hasShort shouldBe true
    val shortArrayCol = colValues.get(1).getValue.getList.getValuesList.asScala.map(v => v.getShort.getV)
    shortArrayCol shouldBe Seq(1, 2, 3, 4)
    // Col 2
    colTypes.get(2).getIdn shouldBe "integer_array"
    colTypes.get(2).getTipe.hasList shouldBe true
    colTypes.get(2).getTipe.getList.getInnerType.hasInt shouldBe true
    val integerArrayCol = colValues.get(2).getValue.getList.getValuesList.asScala.map(v => v.getInt.getV)
    integerArrayCol shouldBe Seq(1, 2, 3, 4)
    // Col 3
    colTypes.get(3).getIdn shouldBe "float_array"
    colTypes.get(3).getTipe.hasList shouldBe true
    colTypes.get(3).getTipe.getList.getInnerType.hasFloat shouldBe true
    val floatArrayCol = colValues.get(3).getValue.getList.getValuesList.asScala.map(v => v.getFloat.getV)
    floatArrayCol shouldBe Seq(1.1f, 2.2f, 3.3f, 4.4f)
    // Col 4
    colTypes.get(4).getIdn shouldBe "double_array"
    colTypes.get(4).getTipe.hasList shouldBe true
    colTypes.get(4).getTipe.getList.getInnerType.hasDouble shouldBe true
    val doubleArrayCol = colValues.get(4).getValue.getList.getValuesList.asScala.map(v => v.getDouble.getV)
    doubleArrayCol shouldBe Seq(1.1, 2.2, 3.3, 4.4)
    // Col 5
    colTypes.get(5).getIdn shouldBe "decimal_array"
    colTypes.get(5).getTipe.hasList shouldBe true
    colTypes.get(5).getTipe.getList.getInnerType.hasDecimal shouldBe true
    val decimalArrayCol = colValues.get(5).getValue.getList.getValuesList.asScala.map(v => v.getDecimal.getV)
    decimalArrayCol shouldBe Seq("1.1", "2.2", "3.3", "4.4")
    // Col 6
    colTypes.get(6).getIdn shouldBe "boolean_array"
    colTypes.get(6).getTipe.hasList shouldBe true
    colTypes.get(6).getTipe.getList.getInnerType.hasBool shouldBe true
    val booleanArrayCol = colValues.get(6).getValue.getList.getValuesList.asScala.map(v => v.getBool.getV)
    booleanArrayCol shouldBe Seq(true, false, true, false)
    // Col 7
    colTypes.get(7).getIdn shouldBe "date_array"
    colTypes.get(7).getTipe.hasList shouldBe true
    colTypes.get(7).getTipe.getList.getInnerType.hasDate shouldBe true
    val dateArrayCol = colValues
      .get(7)
      .getValue
      .getList
      .getValuesList
      .asScala
      .map(v => v.getDate)
      .map(d => (d.getYear, d.getMonth, d.getDay))
    dateArrayCol shouldBe Seq(
      (2021, 1, 1),
      (2021, 1, 2),
      (2021, 1, 3),
      (2021, 1, 4)
    )
    // Col 8
    colTypes.get(8).getIdn shouldBe "time_array"
    colTypes.get(8).getTipe.hasList shouldBe true
    colTypes.get(8).getTipe.getList.getInnerType.hasTime shouldBe true
    val timeArrayCol = colValues
      .get(8)
      .getValue
      .getList
      .getValuesList
      .asScala
      .map(v => v.getTime)
      .map(v => (v.getHour, v.getMinute, v.getSecond, v.getNano))
    timeArrayCol shouldBe Seq(
      (12, 0, 0, 0),
      (13, 0, 0, 0),
      (14, 0, 0, 0),
      (15, 0, 0, 0)
    )
    // Col 9
    colTypes.get(9).getIdn shouldBe "timestamp_array"
    colTypes.get(9).getTipe.hasList shouldBe true
    colTypes.get(9).getTipe.getList.getInnerType.hasTimestamp shouldBe true
    val timestampArrayCol = colValues
      .get(9)
      .getValue
      .getList
      .getValuesList
      .asScala
      .map(v => v.getTimestamp)
      .map(v => (v.getYear, v.getMonth, v.getDay, v.getHour, v.getMinute, v.getSecond, v.getNano))
    timestampArrayCol shouldBe Seq(
      (2021, 1, 1, 12, 0, 0, 0),
      (2021, 1, 2, 13, 0, 0, 0),
      (2021, 1, 3, 14, 0, 0, 0),
      (2021, 1, 4, 15, 0, 0, 0)
    )
    // Col 10
    colTypes.get(10).getIdn shouldBe "interval_array"
    colTypes.get(10).getTipe.hasList shouldBe true
    colTypes.get(10).getTipe.getList.getInnerType.hasInterval shouldBe true
    val intervalArrayCol = colValues.get(10).getValue.getList.getValuesList.asScala.map(v => v.getInterval)
    intervalArrayCol.map(i =>
      (i.getYears, i.getMonths, i.getDays, i.getHours, i.getMinutes, i.getSeconds, i.getMillis)
    ) shouldBe Seq(
      (0, 0, 1, 0, 0, 0, 0),
      (0, 0, 2, 0, 0, 0, 0),
      (0, 0, 3, 0, 0, 0, 0),
      (0, 0, 4, 0, 0, 0, 0)
    )
    // Col 11
    colTypes.get(11).getIdn shouldBe "json_array"
    colTypes.get(11).getTipe.hasList shouldBe true
    colTypes.get(11).getTipe.getList.getInnerType.hasAny shouldBe true
    val jsonArrayCol = colValues
      .get(11)
      .getValue
      .getList
      .getValuesList
      .asScala
      .map(v => v.getRecord)
      .map(r => r.getFieldsList.asScala.map(f => f.getName -> f.getValue.getLong.getV).toMap)
    jsonArrayCol shouldBe Seq(
      Map("a" -> 2L),
      Map("b" -> 3L),
      Map("c" -> 4L),
      Map("d" -> 5L)
    )
    // Col 12
    colTypes.get(12).getIdn shouldBe "jsonb_array"
    colTypes.get(12).getTipe.hasList shouldBe true
    colTypes.get(12).getTipe.getList.getInnerType.hasAny shouldBe true
    val jsonbArrayCol = colValues
      .get(12)
      .getValue
      .getList
      .getValuesList
      .asScala
      .map(v => v.getRecord)
      .map(r => r.getFieldsList.asScala.map(f => f.getName -> f.getValue.getLong.getV).toMap)
    jsonbArrayCol shouldBe Seq(
      Map("a" -> 2L),
      Map("b" -> 3L),
      Map("c" -> 4L),
      Map("d" -> 5L)
    )
    // Col 13
    colTypes.get(13).getIdn shouldBe "hstore_array"
    colTypes.get(13).getTipe.hasList shouldBe true
    colTypes.get(13).getTipe.getList.getInnerType.hasAny shouldBe true
    val hstoreArrayCol = colValues
      .get(13)
      .getValue
      .getList
      .getValuesList
      .asScala
      .map(v => v.getRecord.getFieldsList.asScala.map(f => f.getName -> f.getValue.getString.getV).toMap)
    hstoreArrayCol shouldBe Seq(
      Map("a" -> "2", "b" -> "3"),
      Map("c" -> "4", "d" -> "5")
    )
    // Col 14
    colTypes.get(14).getIdn shouldBe "text_array"
    colTypes.get(14).getTipe.hasList shouldBe true
    colTypes.get(14).getTipe.getList.getInnerType.hasString shouldBe true
    val textArrayCol = colValues.get(14).getValue.getList.getValuesList.asScala.map(v => v.getString.getV)
    textArrayCol shouldBe Seq("apple", "banana", "cherry")
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
    val EvalSuccess.IteratorValue(tipe, it) = compilerService.eval(t.q, asJson(), None).value
    val row = it.next()
    it.hasNext shouldBe false
    it.close()
    val columns = row.getRecord.getFieldsList
    val colTypes = tipe.getRecord.getAttsList.asScala.map(_.getTipe)
    colTypes(0).hasInt shouldBe true
    columns.get(0).getName shouldBe "airport_id"
    columns.get(0).getValue.getInt.getV shouldBe 1618
    colTypes(1).hasString shouldBe true
    columns.get(1).getName shouldBe "name"
    columns.get(1).getValue.getString.getV shouldBe "Braganca"
    colTypes(2).hasString shouldBe true
    columns.get(2).getName shouldBe "city"
    columns.get(2).getValue.getString.getV shouldBe "Braganca"
    colTypes(3).hasString shouldBe true
    columns.get(3).getName shouldBe "country"
    columns.get(3).getValue.getString.getV shouldBe "Portugal"
    colTypes(4).hasString shouldBe true
    columns.get(4).getName shouldBe "iata_faa"
    columns.get(4).getValue.getString.getV shouldBe "BGC"
    colTypes(5).hasString shouldBe true
    columns.get(5).getName shouldBe "icao"
    columns.get(5).getValue.getString.getV shouldBe "LPBG"
    colTypes(6).hasDecimal shouldBe true
    columns.get(6).getName shouldBe "latitude"
    columns.get(6).getValue.getDecimal.getV shouldBe "41.857800"
    colTypes(7).hasDecimal shouldBe true
    columns.get(7).getName shouldBe "longitude"
    columns.get(7).getValue.getDecimal.getV shouldBe "-6.707125"
    colTypes(8).hasDecimal shouldBe true
    columns.get(8).getName shouldBe "altitude"
    columns.get(8).getValue.getDecimal.getV shouldBe "2241.000"
    colTypes(9).hasInt shouldBe true
    columns.get(9).getName shouldBe "timezone"
    columns.get(9).getValue.getDouble.getV shouldBe 0
    colTypes(10).hasString shouldBe true
    columns.get(10).getName shouldBe "dst"
    columns.get(10).getValue.getString.getV shouldBe "E"
    colTypes(11).hasString shouldBe true
    columns.get(11).getName shouldBe "tz"
    columns.get(11).getValue.getString.getV shouldBe "Europe/Lisbon"
  }

  test("SELECT * FROM example.airports WHERE city = :city") { t =>
    val environment = asJson(Map("city" -> RawString("Braganca")))
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
    val EvalSuccess.IteratorValue(tipe, it) = compilerService.eval(t.q, environment, None).value
    val row = it.next()
    it.hasNext shouldBe false
    it.close()
    val columns = row.getRecord.getFieldsList
    val colTypes = tipe.getRecord.getAttsList.asScala.map(_.getTipe)
    colTypes(0).hasInt shouldBe true
    columns.get(0).getName shouldBe "airport_id"
    columns.get(0).getValue.getInt.getV shouldBe 1618
    colTypes(1).hasString shouldBe true
    columns.get(1).getName shouldBe "name"
    columns.get(1).getValue.getString.getV shouldBe "Braganca"
    colTypes(2).hasString shouldBe true
    columns.get(2).getName shouldBe "city"
    columns.get(2).getValue.getString.getV shouldBe "Braganca"
    colTypes(3).hasString shouldBe true
    columns.get(3).getName shouldBe "country"
    columns.get(3).getValue.getString.getV shouldBe "Portugal"
    colTypes(4).hasString shouldBe true
    columns.get(4).getName shouldBe "iata_faa"
    columns.get(4).getValue.getString.getV shouldBe "BGC"
    colTypes(5).hasString shouldBe true
    columns.get(5).getName shouldBe "icao"
    columns.get(5).getValue.getString.getV shouldBe "LPBG"
    colTypes(6).hasDecimal shouldBe true
    columns.get(6).getName shouldBe "latitude"
    columns.get(6).getValue.getDecimal.getV shouldBe "41.857800"
    colTypes(7).hasDecimal shouldBe true
    columns.get(7).getName shouldBe "longitude"
    columns.get(7).getValue.getDecimal.getV shouldBe "-6.707125"
    colTypes(8).hasDecimal shouldBe true
    columns.get(8).getName shouldBe "altitude"
    columns.get(8).getValue.getDecimal.getV shouldBe "2241.000"
    colTypes(9).hasInt shouldBe true
    columns.get(9).getName shouldBe "timezone"
    columns.get(9).getValue.getDouble.getV shouldBe 0
    colTypes(10).hasString shouldBe true
    columns.get(10).getName shouldBe "dst"
    columns.get(10).getValue.getString.getV shouldBe "E"
    colTypes(11).hasString shouldBe true
    columns.get(11).getName shouldBe "tz"
    columns.get(11).getValue.getString.getV shouldBe "Europe/Lisbon"
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
    val EvalSuccess.IteratorValue(tipe, it) = compilerService.eval(t.q, environment, None).value
    it.hasNext shouldBe false
    it.close()
    val colTypes = tipe.getRecord.getAttsList.asScala.map(_.getTipe)
    colTypes(0).hasInt shouldBe true
    colTypes(1).hasString shouldBe true
    colTypes(2).hasString shouldBe true
    colTypes(3).hasString shouldBe true
    colTypes(4).hasString shouldBe true
    colTypes(5).hasString shouldBe true
    colTypes(6).hasDecimal shouldBe true
    colTypes(7).hasDecimal shouldBe true
    colTypes(8).hasDecimal shouldBe true
    colTypes(9).hasInt shouldBe true
    colTypes(10).hasString shouldBe true
    colTypes(11).hasString shouldBe true
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
    val EvalSuccess.IteratorValue(tipe, it) = compilerService.eval(t.q, environment, None).value
    val row = it.next()
    it.hasNext shouldBe false
    it.close()
    val columns = row.getRecord.getFieldsList
    columns.size shouldBe 1
    val colTypes = tipe.getRecord.getAttsList.asScala.map(_.getTipe)
    colTypes.size shouldBe 1
    colTypes(0).hasLong shouldBe true
    columns.get(0).getName shouldBe "n"
    columns.get(0).getValue.getLong.getV shouldBe 6
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
    val environment = asJson(Map("param" -> RawString("Braganca")))
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
    val Left(ExecutionError.ValidationError(errorMessages)) = compilerService.eval(t.q, asJson(), None)
    assert(errorMessages.exists(_.message.contains(expectedError)))
  }

  test("SELECT * FROM inexistent-table") { t =>
    val expectedError = "syntax error at or near \"-\""
    val v = compilerService.validate(t.q, asJson())
    assert(v.messages.exists(_.message.contains(expectedError)))
    val Left(descriptionErrors) = compilerService.getProgramDescription(t.q, asJson())
    assert(descriptionErrors.exists(_.message.contains(expectedError)))
    val Left(ExecutionError.ValidationError(errorMessages)) = compilerService.eval(t.q, asJson(), None)
    assert(errorMessages.exists(_.message.contains(expectedError)))
  }

  test("SELECT * FROM inexistent_table") { t =>
    val expectedErrors = Set("relation \"inexistent_table\" does not exist", "Did you forget to add credentials?")
    val v = compilerService.validate(t.q, asJson())
    val failures = v.messages.collect { case errorMessage: ErrorMessage => errorMessage }
    assert(failures.exists(failure => expectedErrors.forall(failure.message.contains)))
    val Left(descriptionErrors) = compilerService.getProgramDescription(t.q, asJson())
    assert(descriptionErrors.exists(error => expectedErrors.forall(error.message.contains)))
    val Left(ExecutionError.ValidationError(errorMessages)) = compilerService.eval(t.q, asJson(), None)
    assert(errorMessages.exists(error => expectedErrors.forall(error.message.contains)))
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
    val Left(ExecutionError.ValidationError(errorMessages)) = compilerService.eval(t.q, asJson(), None)
    assert(errorMessages.forall(err => expectedErrors.exists(err.message.contains)))
  }

  test("""/* @default a 1 + 1 */
    |SELECT :a + :a AS v""".stripMargin) { t =>
    val v = compilerService.validate(t.q, asJson())
    assert(v.messages.isEmpty, v.messages.mkString(","))
    val Right(description) = compilerService.getProgramDescription(t.q, asJson())
    assert(!description.maybeRunnable.get.params.get.head.required)
    assert(description.maybeRunnable.get.params.get.head.defaultValue.contains(RawInt(2)))
    val EvalSuccess.IteratorValue(tipe, it) = compilerService.eval(t.q, asJson(), None).value
    val row = it.next()
    it.hasNext shouldBe false
    it.close()
    val columns = row.getRecord.getFieldsList
    columns.size shouldBe 1
    val colTypes = tipe.getRecord.getAttsList.asScala.map(_.getTipe)
    colTypes.size shouldBe 1
    colTypes(0).hasInt shouldBe true
    columns.get(0).getName shouldBe "v"
    columns.get(0).getValue.getInt.getV shouldBe 4
  }

  test("SELECT * FROM wrong.relation") { t =>
    val expectedErrors = Set("relation \"wrong.relation\" does not exist", "Did you forget to add credentials?")
    val v = compilerService.validate(t.q, asJson())
    val failures = v.messages.collect { case errorMessage: ErrorMessage => errorMessage }
    assert(failures.exists(failure => expectedErrors.forall(failure.message.contains)))
    val Left(descriptionErrors) = compilerService.getProgramDescription(t.q, asJson())
    assert(descriptionErrors.exists(error => expectedErrors.forall(error.message.contains)))
    val Left(ExecutionError.ValidationError(errorMessages)) = compilerService.eval(t.q, asJson(), None)
    assert(errorMessages.forall(err => expectedErrors.exists(err.message.contains)))
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

  private def fetchOneRowResult(q: String, environment: ProgramEnvironment): Seq[(Type, Value)] = {
    val EvalSuccess.IteratorValue(tipe, it) = compilerService.eval(q, environment, None).value
    val row = it.next()
    it.hasNext shouldBe false
    it.close()
    val columns = row.getRecord.getFieldsList.asScala
    val atts = tipe.getRecord.getAttsList.asScala
    columns.size shouldBe atts.size
    atts.zip(columns).foreach { case (a, v) => v.getName shouldBe a.getIdn }
    atts.zip(columns).map { case (t, v) => (t.getTipe, v.getValue) }
  }

  private def fetchStatementResult(q: String, environment: ProgramEnvironment): Int = {
    val EvalSuccess.ResultValue(tipe, v) = compilerService.eval(q, environment, None).value
    val atts = tipe.getRecord.getAttsList.asScala
    atts.size shouldBe 1
    tipe.getRecord.getAtts(0).getIdn shouldBe "update_count"
    v.getRecord.getFields(0).getValue.getInt.getV
  }

  private def fetchCountQueryResult(q: String, environment: ProgramEnvironment) = {
    val res = fetchOneRowResult(q, environment)
    res.size shouldBe 1
    res.head._1.hasLong shouldBe true
    res.head._2.hasLong shouldBe true
    res.head._2.getLong.getV
  }

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

    fetchCountQueryResult(t.q, withCity) shouldBe 1
    fetchCountQueryResult(t.q, withNull) shouldBe 0
    fetchCountQueryResult(t.q, withCountry) shouldBe 39

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
    fetchCountQueryResult(t.q, withCity) shouldBe 1
    fetchCountQueryResult(t.q, withNull) shouldBe 3
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
    val Left(ExecutionError.ValidationError(errorMessages)) = compilerService.eval(t.q, asJson(), None)
    errorMessages.size shouldBe 1
    errorMessages.head shouldBe ErrorMessage("no value was specified for s", List(), "sqlError", List())
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
    val EvalSuccess.IteratorValue(tipe, it) = compilerService.eval(t.q, asJson(), None).value
    val row = it.next()
    it.hasNext shouldBe false
    it.close()
    val columns = row.getRecord.getFieldsList
    columns.size shouldBe 1
    val colTypes = tipe.getRecord.getAttsList.asScala.map(_.getTipe)
    colTypes.size shouldBe 1
    colTypes(0).hasDate shouldBe true
    columns.get(0).getName shouldBe "x"
    columns.get(0).getValue.hasNull shouldBe true
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
    fetchCountQueryResult(t.q, noParam) shouldBe 8107
  }

  test( // RD-10505
    """SELECT COUNT(*) FROM example.airports;
      |-- nothing here
      |""".stripMargin
  ) { t =>
    val baos = new ByteArrayOutputStream()
    baos.reset()
    val noParam = asJson()
    fetchCountQueryResult(t.q, noParam) shouldBe 8107
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
    fetchCountQueryResult(t.q, noParam) shouldBe 8107
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
    def runWith(q: String, scopes: Set[String]): Seq[(String, String)] = {
      val env = asJson(scopes = scopes)
      assert(compilerService.validate(q, env).messages.isEmpty)
      val Right(_) = compilerService.getProgramDescription(q, env)
      val EvalSuccess.IteratorValue(_, it) = compilerService
        .eval(q, env, None)
        .value
      val r = it.map { v =>
        val fields = v.getRecord.getFieldsList
        (fields.get(0).getName, fields.get(0).getValue.getString.getV)
      }.toList
      it.close()
      r
    }
    runWith("SELECT token\nFROM scopes", Set.empty) shouldBe Seq.empty
    runWith("SELECT * FROM scopes value ORDER by value", Set.empty) shouldBe Seq.empty
    runWith("SELECT * FROM scopes AS value ORDER by value", Set("ADMIN")) shouldBe Seq(("token", "ADMIN"))
    runWith(
      "SELECT token FROM scopes value ORDER by value",
      Set("ADMIN", "SALES", "DEV")
    ) shouldBe Seq(("token", "ADMIN"), ("token", "DEV"), ("token", "SALES"))
    // more complex query
    val r1 = fetchOneRowResult(
      """SELECT 'DEV' IN (SELECT * FROM scopes) AS isDev,
        |       'ADMIN' IN (SELECT token FROM scopes) AS isAdmin""".stripMargin,
      asJson(scopes = Set("ADMIN"))
    )
    r1.size shouldBe 2
    r1(0)._1.hasBool shouldBe true
    r1(0)._2.getBool.getV shouldBe false
    r1(1)._1.hasBool shouldBe true
    r1(1)._2.getBool.getV shouldBe true
    // demo CASE WHEN to hide a certain field
    val r2 = fetchOneRowResult(
      """SELECT
        |    CASE WHEN 'DEV' IN (SELECT * FROM scopes) THEN trip_id END AS trip_id, -- "AS trip_id" to name it normally
        |    departure_date,
        |    arrival_date
        |FROM example.trips
        |WHERE reason = 'Holidays' AND departure_date = DATE '2016-02-27'""".stripMargin,
      asJson(scopes = Set("ADMIN"))
    )
    r2.size shouldBe 3 // Three columns
    r2(0)._1.hasInt shouldBe true
    r2(0)._2.hasNull shouldBe true
    r2(1)._1.hasDate shouldBe true
    r2(1)._2.getDate.getYear shouldBe 2016
    r2(1)._2.getDate.getMonth shouldBe 2
    r2(1)._2.getDate.getDay shouldBe 27
    r2(2)._2.getDate.getYear shouldBe 2016
    r2(2)._2.getDate.getMonth shouldBe 3
    r2(2)._2.getDate.getDay shouldBe 6
  }

  test("""-- @param p
    |-- @type p integer
    |-- SELECT :p + 10;
    |""".stripMargin) { t =>
    val v = compilerService.validate(t.q, asJson())
    assert(v.messages.isEmpty)
    fetchStatementResult(t.q, asJson(Map("p" -> RawInt(5)))) shouldBe 0
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
    val row = fetchOneRowResult(t.q, asJson())
    row.size shouldBe 1
    row.head._1.hasString shouldBe true
    row.head._2.getString.getV shouldBe "timestamp with time zone"
  }

  test("""SELECT NOW()""".stripMargin) { t =>
    // NOW() is a timestamp with timezone. The one of the SQL connection. This test is to make sure
    // it works (we cannot assert on the result).
    val ValidateResponse(errors) = compilerService.validate(t.q, asJson())
    assert(errors.isEmpty)
    val Right(description) = compilerService.getProgramDescription(t.q, asJson())
    val row = fetchOneRowResult(t.q, asJson())
    row.size shouldBe 1
  }

  test("""SELECT TIMESTAMP '2001-07-01 12:13:14.567' AS t""".stripMargin) { t =>
    val ValidateResponse(errors) = compilerService.validate(t.q, asJson())
    assert(errors.isEmpty)
    val Right(_) = compilerService.getProgramDescription(t.q, asJson())
    val row = fetchOneRowResult(t.q, asJson())
    row.size shouldBe 1
    val ts = row.head._2.getTimestamp
    ts.getYear shouldBe 2001
    ts.getMonth shouldBe 7
    ts.getDay shouldBe 1
    ts.getHour shouldBe 12
    ts.getMinute shouldBe 13
    ts.getSecond shouldBe 14
    ts.getNano shouldBe 567000000
  }

  test("""SELECT TIME '12:13:14.567' AS t""".stripMargin) { t =>
    val ValidateResponse(errors) = compilerService.validate(t.q, asJson())
    assert(errors.isEmpty)
    val Right(_) = compilerService.getProgramDescription(t.q, asJson())
    val row = fetchOneRowResult(t.q, asJson())
    row.size shouldBe 1
    val ts = row.head._2.getTime
    ts.getHour shouldBe 12
    ts.getMinute shouldBe 13
    ts.getSecond shouldBe 14
    ts.getNano shouldBe 567000000
  }

  test("""-- @default t TIME '12:13:14.567'
    |SELECT :t AS t""".stripMargin) { t =>
    val ValidateResponse(errors) = compilerService.validate(t.q, asJson())
    assert(errors.isEmpty)
    val Right(_) = compilerService.getProgramDescription(t.q, asJson())
    val baos = new ByteArrayOutputStream()
    baos.reset()
    val row = fetchOneRowResult(t.q, asJson())
    row.size shouldBe 1
    val ts = row.head._2.getTime
    ts.getHour shouldBe 12
    ts.getMinute shouldBe 13
    ts.getSecond shouldBe 14
    ts.getNano shouldBe 567000000
  }

  test("""-- @type x integer
    |-- @default x null
    |SELECT :x AS x""".stripMargin) { t =>
    val rowWithDefault = fetchOneRowResult(t.q, asJson())
    rowWithDefault.size shouldBe 1
    rowWithDefault.head._2.hasNull shouldBe true
    val rowWith12 = fetchOneRowResult(t.q, asJson(Map("x" -> RawInt(12))))
    rowWith12.size shouldBe 1
    rowWith12.head._2.getInt.getV shouldBe 12
  }

  test("""-- @type x varchar
    |-- @default x null
    |SELECT :x AS x""".stripMargin) { t =>
    val rowWithDefault = fetchOneRowResult(t.q, asJson())
    rowWithDefault.size shouldBe 1
    rowWithDefault.head._2.hasNull shouldBe true
    val rowWithTralala = fetchOneRowResult(t.q, asJson(Map("x" -> RawString("tralala"))))
    rowWithTralala.size shouldBe 1
    rowWithTralala.head._2.getString.getV shouldBe "tralala"
  }

  test("""-- @type x date
    |-- @default x null
    |SELECT :x AS x""".stripMargin) { t =>
    val rowWithDefault = fetchOneRowResult(t.q, asJson())
    rowWithDefault.size shouldBe 1
    rowWithDefault.head._2.hasNull shouldBe true
    val rowWithDate = fetchOneRowResult(t.q, asJson(Map("x" -> RawDate(LocalDate.of(2008, 9, 29)))))
    rowWithDate.size shouldBe 1
    val d = rowWithDate.head._2.getDate
    d.getYear shouldBe 2008
    d.getMonth shouldBe 9
    d.getDay shouldBe 29
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

  // Checking nested records with hstore
  test("SELECT 'a=>1,b=>tralala'::hstore AS r") { t =>
    val Seq((_, v)) = fetchOneRowResult(t.q, asJson())
    v.getRecord.getFields(0).getName shouldBe "a"
    v.getRecord.getFields(0).getValue.getString.getV shouldBe "1"
    v.getRecord.getFields(1).getName shouldBe "b"
    v.getRecord.getFields(1).getValue.getString.getV shouldBe "tralala"
  }

  test("SELECT NULL::hstore AS r") { t =>
    val Seq((_, v)) = fetchOneRowResult(t.q, asJson())
    v.hasNull shouldBe true
  }

  test("INSERT") { _ =>
    // An SQL INSERT statement adding a fake city to the airports table.
    val q =
      """INSERT INTO example.airports (airport_id, name, city, country, iata_faa, icao, latitude, longitude, altitude, timezone, dst, tz)
        |VALUES (8108, :airport, :city, :country, 'FC', 'FC', 0.0, 0.0, 0.0, 0, 'U', 'UTC')
        |""".stripMargin
    fetchStatementResult(
      q,
      asJson(
        Map("airport" -> RawString("FAKE"), "city" -> RawString("Fake City"), "country" -> RawString("Fake Country"))
      )
    ) shouldBe 1
    val columns = fetchOneRowResult(
      "SELECT city, country FROM example.airports WHERE name = :a",
      asJson(params = Map("a" -> RawString("FAKE")))
    )
    columns.size shouldBe 2
    columns(0)._2.getString.getV shouldBe "Fake City"
    columns(1)._2.getString.getV shouldBe "Fake Country"
  }

  test("UPDATE") { _ =>
    fetchStatementResult(
      "UPDATE example.airports SET city = :newName WHERE country = :c",
      asJson(params = Map("newName" -> RawString("La Roche sur Foron"), "c" -> RawString("Portugal")))
    ) shouldBe 39
    val columns = fetchOneRowResult(
      "SELECT DISTINCT city FROM example.airports WHERE country = :c",
      asJson(params = Map("c" -> RawString("Portugal")))
    )
    columns.size shouldBe 1
    columns.head._2.getString.getV shouldBe "La Roche sur Foron"
  }

}
