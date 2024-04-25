import raw.client.api.{CompilerService, ExecutionSuccess, ProgramEnvironment, RawString, RawValue}
import raw.client.jinja.sql.JinjaSqlCompilerService
import raw.creds.api.CredentialsTestContext
import raw.creds.local.LocalCredentialsTestContext
import raw.utils.{InteractiveUser, RawTestSuite, SettingsTestContext, TrainingWheelsContext, Uid}

import java.io.ByteArrayOutputStream

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
    assert(baos.toString == "")

  }

  test("""SELECT {{key}},
    |       COUNT(*),
    |       SUM(
    |           {%if sumRow == "Quantity" %} quantity
    |           {%elif key == "NetAmount" %} netamount
    |           {%else %} {% raise "unknown sum: " + sumRow %}
    |           {%endif %}
    |       )
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
  }

  test("SELECT * FROM example.airports") { q =>
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
