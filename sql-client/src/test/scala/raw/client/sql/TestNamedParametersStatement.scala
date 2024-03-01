package raw.client.sql

import raw.utils._

class TestNamedParametersStatement extends RawTestSuite with SettingsTestContext with TrainingWheelsContext {
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

  private val connectionPool = new SqlConnectionPool(settings)
  private val con = connectionPool.getConnection(user)

  override def afterAll(): Unit = {
    con.close()
    super.afterAll()
  }

  test("single parameter") { _ =>
    assume(password != "")

    val code = "SELECT :v1 as arg"

    val statement = new NamedParametersPreparedStatement(con, code)
    statement.setString("v1", "Hello!")
    val rs = statement.executeQuery()

    rs.next()
    assert(rs.getString("arg")== "Hello!")
  }

  test("several parameters") { _ =>
    assume(password != "")

    val code = "SELECT :v1,:v2, city FROM example.airports WHERE city = :v1"
    val con = connectionPool.getConnection(user)
    val statement = new NamedParametersPreparedStatement(con, code)

    statement.setString("v1", "Lisbon")
    statement.setInt("v2", 1)
    val rs = statement.executeQuery()

    rs.next()
    assert(rs.getString(1) == "Lisbon")
    assert(rs.getInt(2) == 1)
    assert(rs.getString(3) == "Lisbon")
    con.close()
  }

  test("skip parameters in comments") { _ =>
    assume(password != "")

    val code =
      """/* this should not be a parameter
        | :foo
        |*/
        |SELECT :v1 as arg  -- neither this one :bar """.stripMargin
    val statement = new NamedParametersPreparedStatement(con, code)
    statement.setString("v1", "Hello!")
    val rs = statement.executeQuery()

    rs.next()
    assert(rs.getString("arg") == "Hello!")
  }

  // RD-10681
  test("skip parameters in strings") { _ =>
    assume(password != "")

    val code =
      """ SELECT '[1, 2, "3", {"a": "Hello"}]' as arg""".stripMargin
    val statement = new NamedParametersPreparedStatement(con, code)
    val metadata = statement.queryMetadata.right.get

    assert(metadata.parameters.isEmpty)
    val rs = statement.executeQuery()

    rs.next()
    assert(rs.getString("arg") == """[1, 2, "3", {"a": "Hello"}]""")
  }
}
