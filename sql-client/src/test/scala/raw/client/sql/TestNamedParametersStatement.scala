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

package raw.client.sql

import org.bitbucket.inkytonik.kiama.util.Positions
import raw.client.sql.antlr4.RawSqlSyntaxAnalyzer
import raw.creds.api.CredentialsTestContext
import raw.creds.local.LocalCredentialsTestContext
import raw.utils._

class TestNamedParametersStatement
    extends RawTestSuite
    with SettingsTestContext
    with TrainingWheelsContext
    with CredentialsTestContext
    with LocalCredentialsTestContext {

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

  private var con: java.sql.Connection = _

  override def beforeAll(): Unit = {
    if (password != "") {
      val connectionPool = new SqlConnectionPool(credentials)
      con = connectionPool.getConnection(user)
    }
    super.beforeAll()
  }

  override def afterAll(): Unit = {
    if (con != null) con.close()
    super.afterAll()
  }

  test("bug") { _ =>
    assume(password != "")

    val code = """-- @type aeroport_id smallint
      |SELECT :city::json FROM example.airports WHERE  airport_id = :aeroport_id""".stripMargin

    val statement = new NamedParametersPreparedStatement(con, parse(code))
    val metadata = statement.queryMetadata
    assert(metadata.isLeft)
    statement.setString("v1", "Hello!")
    val rs = statement.executeQuery()

    rs.next()
    assert(rs.getString("arg") == "Hello!")
  }

  test("single parameter") { _ =>
    assume(password != "")

    val code = "SELECT :v1 as arg"

    val statement = new NamedParametersPreparedStatement(con, parse(code))
    statement.setString("v1", "Hello!")
    val rs = statement.executeQuery()

    rs.next()
    assert(rs.getString("arg") == "Hello!")
  }

  test("SELECT :v::varchar AS greeting;") { _ =>
    assume(password != "")

    val code = "SELECT :v::varchar AS greeting;"
    val statement = new NamedParametersPreparedStatement(con, parse(code))
    statement.setString("v", "Hello!")
    val rs = statement.executeQuery()

    rs.next()
    assert(rs.getString("greeting") == "Hello!")

  }

  test("several parameters") { _ =>
    assume(password != "")

    val code = "SELECT :v1,:v2, city FROM example.airports WHERE city = :v1"
    val statement = new NamedParametersPreparedStatement(con, parse(code))
    val metadata = statement.queryMetadata.right.get
    assert(metadata.parameters.keys == Set("v1", "v2"))

    statement.setString("v1", "Lisbon")
    statement.setInt("v2", 1)
    val rs = statement.executeQuery()
    rs.next()
    assert(rs.getString(1) == "Lisbon")
    assert(rs.getInt(2) == 1)
    assert(rs.getString(3) == "Lisbon")
  }

  test("skip parameters in comments") { _ =>
    assume(password != "")

    val code = """/* this should not be a parameter
      | :foo
      |*/
      |SELECT :v1 as arg  -- neither this one :bar """.stripMargin
    val statement = new NamedParametersPreparedStatement(con, parse(code))
    statement.setString("v1", "Hello!")
    val rs = statement.executeQuery()

    rs.next()
    assert(rs.getString("arg") == "Hello!")
  }

  test("skip parameter in string") { _ =>
    assume(password != "")

    val code = """SELECT ':foo' as v1, :bar as v2""".stripMargin
    val statement = new NamedParametersPreparedStatement(con, parse(code))
    val metadata = statement.queryMetadata.right.get
    assert(metadata.parameters.keys == Set("bar"))
    statement.setString("bar", "Hello!")
    val rs = statement.executeQuery()

    rs.next()
    assert(rs.getString("v1") == ":foo")
    assert(rs.getString("v2") == "Hello!")
  }

  test("RD-10681 SQL fails to validate string with json ") { _ =>
    assume(password != "")

    val code = """ SELECT '[1, 2, "3", {"a": "Hello"}]' as arg""".stripMargin
    val statement = new NamedParametersPreparedStatement(con, parse(code))
    val metadata = statement.queryMetadata
    assert(metadata.isRight)
    assert(metadata.right.get.parameters.isEmpty)
    val rs = statement.executeQuery()

    rs.next()
    assert(rs.getString("arg") == """[1, 2, "3", {"a": "Hello"}]""")
  }

  private def parse(sourceCode: String) = {
    val positions = new Positions
    val syntaxAnalyzer = new RawSqlSyntaxAnalyzer(positions)
    syntaxAnalyzer.parse(sourceCode)
  }
}
