/*
 * Copyright 2024 RAW Labs S.A.
 *
 *   Use of this software is governed by the Business Source License
 *   included in the file licenses/BSL.txt.
 *
 *   As of the Change Date specified in that file, in accordance with
 *   the Business Source License, use of this software will be governed
 *   by the Apache License, Version 2.0, included in the file
 *   licenses/APL.txt.
 */

package raw.client.jinja.sql

import org.scalatest.matchers.must.Matchers.be
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.matchers.{MatchResult, Matcher}
import raw.client.api.{CompilerService, ExecutionRuntimeFailure, ExecutionSuccess, ExecutionValidationFailure, GetProgramDescriptionResponse, GetProgramDescriptionSuccess, ProgramEnvironment, RawDate, RawDateType, RawInt, RawIntType, RawString, RawValue}
import raw.creds.api.CredentialsTestContext
import raw.creds.local.LocalCredentialsTestContext
import raw.utils.{InteractiveUser, RawTestSuite, SettingsTestContext, TrainingWheelsContext, Uid}

import java.io.ByteArrayOutputStream
import java.time.LocalDate

class PreprocessingTest
    extends RawTestSuite
    with SettingsTestContext
    with TrainingWheelsContext
    with CredentialsTestContext
    with LocalCredentialsTestContext {

  private val database = sys.env.getOrElse("FDW_DATABASE", "unittest")
  private val hostname = sys.env.getOrElse("FDW_HOSTNAME", "localhost")
  private val port = sys.env.getOrElse("FDW_HOSTNAME", "5432")
  private val username = sys.env.getOrElse("FDW_USERNAME", "postgres")
  private val password = sys.env.getOrElse("FDW_PASSWORD", "1234")

  property("raw.creds.jdbc.fdw.host", hostname)
  property("raw.creds.jdbc.fdw.port", port)
  property("raw.creds.jdbc.fdw.user", username)
  property("raw.creds.jdbc.fdw.password", password)

  private case class Q(code: String, args: Map[String, Any] = Map.empty) {
    def withArgs(values: Map[String, Any]): Q = Q(code, values)
    def withArg(arg: (String, Any)): Q = withArgs(Map(arg))

    def description(): GetProgramDescriptionResponse = {
      compilerService.getProgramDescription(code, environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json")))
    }

  }

  private class Give(jsonString: String) extends Matcher[Q] {
    override def apply(left: Q): MatchResult = {
      val rawArgs: Array[(String, RawValue)] = left.args.mapValues {
        case s: String => RawString(s)
        case i: Int => RawInt(i)
        case d: LocalDate => RawDate(d)
      }.toArray
      val env = ProgramEnvironment(user, Some(rawArgs), Set.empty, Map("output-format" -> "json"))
      val baos = new ByteArrayOutputStream()
      val r = compilerService.execute(left.code, env, None, baos)
      r match {
        case ExecutionSuccess =>
          val result = baos.toString
          MatchResult(result == jsonString, s"Actual: $result\nExpected: $jsonString", "ok")
        case ExecutionRuntimeFailure(error) => MatchResult(false, error, "ok")
        case ExecutionValidationFailure(errors) => MatchResult(false, errors.map(_.message).mkString(","), "ok")
      }
    }
  }

  private class FailWith(errorMessage: String) extends Matcher[Q] {
    override def apply(left: Q): MatchResult = {
      val rawArgs: Array[(String, RawValue)] = left.args.mapValues {
        case s: String => RawString(s)
        case i: Int => RawInt(i)
      }.toArray
      val env = ProgramEnvironment(user, Some(rawArgs), Set.empty, Map("output-format" -> "json"))
      val baos = new ByteArrayOutputStream()
      val r = compilerService.execute(left.code, env, None, baos)
      r match {
        case ExecutionRuntimeFailure(error) => MatchResult(error.contains(errorMessage), error, "ok")
        case ExecutionValidationFailure(errors) => MatchResult(errors.exists(_.message.contains(errorMessage)), errors.map(_.message).mkString(","), "ok")
        case ExecutionSuccess => MatchResult(false, "didn't fail", "ok")
      }
    }
  }

  private def give(jsonContent: String) = new Give(jsonContent)
  private def failWith(error: String) = new FailWith(error)

  test("safe vs. unsafe") { _ =>
    // without '|safe' the string variable is turned into a quoted string
    val code = Q("SELECT MIN({{ column }}) FROM example.airports WHERE country = {{ country }}")
    code withArgs Map("column" -> "latitude", "country" -> "France") should give("""[{"min":"latitude"}]""")
    code withArgs Map("column" -> "longitude", "country" -> "France") should give("""[{"min":"longitude"}]""")

    // with '|safe' the string variable is pasted untouched/unquoted (e.g. here, it's used as a column name)
    val code2 = Q("SELECT MAX({{ column|safe }}) FROM example.airports WHERE country = {{ country }}")
    code2 withArgs Map("column" -> "latitude", "country" -> "France") should give("""[{"max":50.967536}]""")
    code2 withArgs Map("column" -> "longitude", "country" -> "France") should give("""[{"max":9.483731}]""")

    // '|safe' used when computing a local variable, which is then used as safe even though it's not flagged directly,
    val code3 = Q(
      """{% set col = column|safe %}
        |SELECT MAX({{ col }}) FROM example.airports WHERE country = {{ country }}""".stripMargin)
    code3 withArgs Map("column" -> "latitude", "country" -> "France") should give("""[{"max":50.967536}]""")
    code3 withArgs Map("column" -> "longitude", "country" -> "France") should give("""[{"max":9.483731}]""")

  }

  test("escape") { _ =>
    Q("SELECT {{ v }} AS v") withArg("v" -> "<p/>") should give("""[{"v":"<p/>"}]""")
    Q("SELECT '{{ v|safe }}' AS v") withArg("v" -> "<p/>") should give("""[{"v":"<p/>"}]""")
    Q("SELECT {{ v }} AS v") withArg("v" -> "'<p'/>'") should give("""[{"v":"'<p'/>'"}]""")
    Q("SELECT '{{ v }}' AS v") withArg("v" -> "<p/>") should failWith("column \"p\" does not exist")
    Q("SELECT {{ v }} AS v") withArg("v" -> "\"<p/>\"") should give("""[{"v":"\"<p/>\""}]""")
    Q("SELECT {{ v }} AS v") withArg("v" -> "1 + 2") should give("""[{"v":"1 + 2"}]""") // quoted by default
    Q("SELECT {{ v|safe }} AS v") withArg("v" -> "1 + 2") should give("""[{"v":3}]""") // pasted straight => interpreted
  }

  /*
  -- @param v the date
  --          of the event
  SELECT 1;
   */

  test("""integer parameter""") { _ =>
    val code = Q(s"""
         |{# @type v integer #}
         |{# @default v 12 #}
         |{# @param v a random number
         |            here to test something #}
         |SELECT {{ v }} * 10 AS r
         |""".stripMargin)
    val GetProgramDescriptionSuccess(d) = code.description()
    d.decls.size should be(0)
    val Vector(param) = d.maybeRunnable.get.params.get
    param.idn should be("v")
    param.required should be(false) // because we have a default
    param.tipe.get should be (RawIntType(true, false)) // null is always OK
    param.comment.get should be ("a random number here to test something")
    code withArg "v" -> 22 should give("""[{"r":220}]""")
    code withArg "v" -> "tralala" should failWith("invalid input syntax for type integer")
  }

  test("""date parameter""") { _ =>
    val code = Q(s"""
                    |{# @type v date #}
                    |{# @default v '2001-01-01' #}
                    |{# @param v a random date
                    |    here to test something #}
                    |SELECT YEAR({{ v }}) AS r
                    |""".stripMargin)
    val GetProgramDescriptionSuccess(d) = code.description()
    d.decls.size should be(0)
    val Vector(param) = d.maybeRunnable.get.params.get
    param.idn should be("v")
    param.required should be(false) // because we have a default
    param.tipe.get should be (RawDateType(true, false)) // null is always OK
    param.comment.get should be ("a random date here to test something")
    code withArg "v" -> LocalDate.of(2024,1, 1) should give("""[{"r":220}]""")
    code withArg "v" -> "tralala" should failWith("invalid input syntax for type integer")
  }

  test("""SELECT {{ column }}, COUNT(*)
    |FROM example.airports
    |GROUP BY {{ column }}
    |ORDER BY COUNT(*) DESC
    |LIMIT 3
    |""".stripMargin)(t => Q(t.q) withArg ("column" -> "city") should give("[]"))

  ignore("""SELECT {{ key }},
    |       COUNT(*),
    |       SUM({% raise "error, unknown key: " + key %})
    |FROM example.airports
    |GROUP BY {{ key }}
    |""".stripMargin) { q =>
    val v = compilerService.validate(q.q, asJson())
    assert(v != null)
    val baos = new ByteArrayOutputStream()
    val r = compilerService.execute(q.q, asJson(Map("key" -> RawString("city"))), None, baos)
    assert(r == ExecutionSuccess)
  }

  ignore("""SELECT {{ key }},
    |       COUNT(*),
    |       SUM({{ raw:fail() }})
    |FROM example.airports
    |GROUP BY {{ key }}
    |""".stripMargin) { q =>
    val v = compilerService.validate(q.q, asJson())
    assert(v != null)
    val baos = new ByteArrayOutputStream()
    val r = compilerService.execute(q.q, asJson(Map("key" -> RawString("city"))), None, baos)
    assert(r == ExecutionSuccess)
  }

  test("""SELECT {{ 1 - 2 }}
    |""".stripMargin) { q =>
    val v = compilerService.getProgramDescription(q.q, asJson())
    assert(v != null)
    val baos = new ByteArrayOutputStream()
    val r = compilerService.execute(q.q, asJson(), None, baos)
    assert(r == ExecutionSuccess)
  }

  test("""SELECT {{ a }} + {{ b }}
    |""".stripMargin) { q =>
    val v = compilerService.getProgramDescription(q.q, asJson())
    assert(v != null)
  }

  test("""SELECT {{ a + 1 - b }}
    |""".stripMargin) { q =>
    val v = compilerService.getProgramDescription(q.q, asJson())
    assert(v != null)
  }

  test("""SELECT {{ 1 +> 2 }}
    |""".stripMargin) { q =>
    val v = compilerService.validate(q.q, asJson())
    assert(v != null)
    val baos = new ByteArrayOutputStream()
    val r = compilerService.execute(q.q, asJson(), None, baos)
    assert(r == ExecutionSuccess)
  }

  test("""{% include '/etc/passwd' %} """.stripMargin) { q =>
    val v = compilerService.validate(q.q, asJson())
    assert(v != null)
  }

  test(s"""
    |{% set v = val == "latitude" ? "latitude" : "longitude" %}
    |SELECT {{ key }}, MAX({{ v }}), MIN({{ v }})
    |FROM example.airports GROUP BY {{ key }}
    |ORDER BY COUNT(*) {{ order }}
    |LIMIT 3
    |""".stripMargin) { q =>
    val g = compilerService.getProgramDescription(q.q, asJson())
    assert(g != null)
    val baos = new ByteArrayOutputStream()
    val r = compilerService.execute(
      q.q,
      asJson(Map("key" -> RawString("country"), "val" -> RawString("latitude"), "order" -> RawString("DESC"))),
      None,
      baos
    )
    assert(r == ExecutionSuccess)

  }

  test(s"""
    |{% set v = "latitude" if val == "latitude" else "longitude" %}
    |SELECT {{ key }}, MAX({{ v }}), MIN({{ v }})
    |FROM example.airports GROUP BY {{ key }}
    |ORDER BY COUNT(*) {{ order }}
    |LIMIT 3
    |""".stripMargin) { q =>
    val g = compilerService.getProgramDescription(q.q, asJson())
    assert(g != null)
    val baos = new ByteArrayOutputStream()
    val r = compilerService.execute(
      q.q,
      asJson(Map("key" -> RawString("country"), "val" -> RawString("latitude"), "order" -> RawString("DESC"))),
      None,
      baos
    )
    assert(r == ExecutionSuccess)

  }

  test(s"""
    |SELECT {{key}},
    |       COUNT(*),
    |       SUM(
    |           {%if sumRow == "Quantity" %} quantity
    |           {%elif sumRow == "NetAmount" %} netamount
    |           {%else %} {% raise "unknown sum: " + sumRow %}
    |           {%endif %}
    |       )
    |       {% if moreColumns == true } , MAX(latitude) {% endif %}
    |FROM lokad_orders
    |GROUP BY
    |    {%if key == "year" %} YEAR(date)
    |    {%elif key == "month" %} MONTH(date)
    |    {%elif key == "Quantity" %} quantity
    |    {%elif key == "Currency" %} currency
    |    {%else %} {% raise "unknown key:" + key  %}
    |    {%endif %} AS {{key}}
    |""".stripMargin) { q =>
    val v = compilerService.validate(q.q, asJson())
    assert(v != null)
  }

  test("SELECT airport_id, {{ c }} FROM {{ }} ") { q =>
    val v = compilerService.validate(q.q, asJson())
    assert(v != null)
  }

  test("SELECT airport_id, {{ colName }} FROM {{ table }}") { q =>
    val v = compilerService.validate(q.q, asJson())
    assert(v != null)
    val d = compilerService.getProgramDescription(q.q, asJson())
    assert(d != null)
    val baos = new ByteArrayOutputStream()
    val r = compilerService.execute(
      q.q,
      asJson(Map("colName" -> RawString("city"), "table" -> RawString("example.airports"))),
      None,
      baos
    )
    assert(r == ExecutionSuccess)
  }

  test("SELECT airport_id, {{ colNames }} FROM {{ table }}") { q =>
    val baos = new ByteArrayOutputStream()
    val r = compilerService.execute(
      q.q,
      asJson(Map("colNames" -> RawString("city, country"), "table" -> RawString("example.airports"))),
      None,
      baos
    )
    assert(r == ExecutionSuccess)
    assert(baos.toString() == "")
  }

  test("SELECT * FROM example.airports") { q =>
    val baos = new ByteArrayOutputStream()
    val r = compilerService.execute(q.q, asJson(), None, baos)
    assert(r == ExecutionSuccess)
  }

  // a typo
  test("SELECT '{{ name }'") { q =>
    val baos = new ByteArrayOutputStream()
    val r = compilerService.execute(q.q, asJson(), None, baos)
    assert(r == ExecutionSuccess)
  }

  test("SELECT '{{ 12 + name }}'") { q =>
    val baos = new ByteArrayOutputStream()
    val r = compilerService.execute(q.q, asJson(), None, baos)
    assert(r == ExecutionSuccess)
  }

  private var compilerService: CompilerService = _

  private val user = InteractiveUser(Uid(database), "fdw user", "email", Seq.empty)

  private def asJson(params: Map[String, RawValue] = Map.empty): ProgramEnvironment = {
    if (params.isEmpty) ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    else ProgramEnvironment(user, Some(params.toArray), Set.empty, Map("output-format" -> "json"))
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    compilerService = new JinjaSqlCompilerService(None)
  }

  override def afterAll(): Unit = {
    if (compilerService != null) {
      compilerService.stop()
      compilerService = null
    }
  }

}
