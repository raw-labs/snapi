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
import org.bitbucket.inkytonik.kiama.util.Positions
import org.testcontainers.utility.DockerImageName
import raw.client.api.{RawInt, RawString}
import raw.client.sql.antlr4.RawSqlSyntaxAnalyzer
import raw.utils._

class TestNamedParametersStatement
    extends RawTestSuite
    with ForAllTestContainer
    with SettingsTestContext
    with TrainingWheelsContext {

  override val container: PostgreSQLContainer = PostgreSQLContainer(
    dockerImageNameOverride = DockerImageName.parse("postgres:15-alpine")
  )
  private var connectionPool: SqlConnectionPool = _
  private var jdbcUrl: String = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    val dbPort = container.mappedPort(5432).toString
    val dbName = container.databaseName
    val user = container.username
    val password = container.password
    connectionPool = new SqlConnectionPool()
    jdbcUrl = s"jdbc:postgresql://localhost:$dbPort/$dbName?user=$user&password=$password"
  }

  private def mkPreparedStatement(code: String) =
    new NamedParametersPreparedStatement(connectionPool.getConnection(jdbcUrl), parse(code))

  override def afterAll(): Unit = {
    if (connectionPool != null) {
      connectionPool.stop()
      connectionPool = null
    }
    super.afterAll()
  }

  test("single parameter") { _ =>
    val code = "SELECT :v1 as arg"

    val statement = mkPreparedStatement(code)
    val rs = statement.executeWith(Seq("v1" -> RawString("Hello!"))).right.get
    rs.next()
    assert(rs.getString("arg") == "Hello!")
  }

  test("SELECT :v::varchar AS greeting;") { _ =>
    val code = "SELECT :v::varchar AS greeting;"
    val statement = mkPreparedStatement(code)
    val rs = statement.executeWith(Seq("v" -> RawString("Hello!"))).right.get

    rs.next()
    assert(rs.getString("greeting") == "Hello!")

  }

  test("several parameters") { _ =>
    val code = "SELECT :v1::varchar,:v2::int,:v1"
    val statement = mkPreparedStatement(code)
    val metadata = statement.queryMetadata.right.get
    assert(metadata.parameters.keys == Set("v1", "v2"))

    val rs = statement.executeWith(Seq("v1" -> RawString("Lisbon"), "v2" -> RawInt(1))).right.get
    rs.next()
    assert(rs.getString(1) == "Lisbon")
    assert(rs.getInt(2) == 1)
    assert(rs.getString(3) == "Lisbon")
  }

  test("skip parameters in comments") { _ =>
    val code = """/* this should not be a parameter
      | :foo
      |*/
      |SELECT :v1 as arg  -- neither this one :bar """.stripMargin
    val statement = mkPreparedStatement(code)
    val rs = statement.executeWith(Seq("v1" -> RawString("Hello!"))).right.get

    rs.next()
    assert(rs.getString("arg") == "Hello!")
  }

  test("skip parameter in string") { _ =>
    val code = """SELECT ':foo' as v1, :bar as v2""".stripMargin
    val statement = mkPreparedStatement(code)
    val metadata = statement.queryMetadata.right.get
    assert(metadata.parameters.keys == Set("bar"))
    val rs = statement.executeWith(Seq("bar" -> RawString("Hello!"))).right.get

    rs.next()
    assert(rs.getString("v1") == ":foo")
    assert(rs.getString("v2") == "Hello!")
  }

  test("RD-10681 SQL fails to validate string with json ") { _ =>
    val code = """ SELECT '[1, 2, "3", {"a": "Hello"}]' as arg""".stripMargin
    val statement = mkPreparedStatement(code)
    val metadata = statement.queryMetadata
    assert(metadata.isRight)
    assert(metadata.right.get.parameters.isEmpty)
    val rs = statement.executeWith(Seq.empty).right.get

    rs.next()
    assert(rs.getString("arg") == """[1, 2, "3", {"a": "Hello"}]""")
  }

  private def parse(sourceCode: String) = {
    val positions = new Positions
    val syntaxAnalyzer = new RawSqlSyntaxAnalyzer(positions)
    syntaxAnalyzer.parse(sourceCode)
  }
}
