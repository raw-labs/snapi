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
import com.rawlabs.compiler.{RawInt, RawString}
import com.rawlabs.sql.compiler.antlr4.SqlSyntaxAnalyzer
import org.bitbucket.inkytonik.kiama.util.Positions
import org.testcontainers.utility.DockerImageName
import com.rawlabs.utils.core._

import java.sql.{Connection, ResultSet}

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
    connectionPool = new SqlConnectionPool
    jdbcUrl = s"jdbc:postgresql://localhost:$dbPort/$dbName?user=$user&password=$password"
  }

  override def afterAll(): Unit = {
    if (connectionPool != null) {
      connectionPool.stop()
      connectionPool = null
    }
    super.afterAll()
  }

  test("single parameter") { _ =>
    val code = "SELECT :v1 as arg"

    val conn = connectionPool.getConnection(jdbcUrl)
    var statement: NamedParametersPreparedStatement = null
    var rs: ResultSet = null
    try {
      statement = mkPreparedStatement(conn, code)
      rs = statement
        .executeWith(Seq("v1" -> RawString("Hello!")))
        .right
        .get
        .asInstanceOf[NamedParametersPreparedStatementResultSet]
        .rs
      rs.next()
      assert(rs.getString("arg") == "Hello!")
    } finally {
      if (rs != null) rs.close()
      if (statement != null) statement.close()
      conn.close()
    }
  }

  test("SELECT :v::varchar AS greeting;") { _ =>
    val code = "SELECT :v::varchar AS greeting;"
    val conn = connectionPool.getConnection(jdbcUrl)
    var statement: NamedParametersPreparedStatement = null
    var rs: ResultSet = null
    try {
      statement = mkPreparedStatement(conn, code)
      rs = statement
        .executeWith(Seq("v" -> RawString("Hello!")))
        .right
        .get
        .asInstanceOf[NamedParametersPreparedStatementResultSet]
        .rs

      rs.next()
      assert(rs.getString("greeting") == "Hello!")
    } finally {
      if (rs != null) rs.close()
      if (statement != null) statement.close()
      conn.close()
    }
  }

  test("several parameters") { _ =>
    val code = "SELECT :v1::varchar,:v2::int,:v1"
    val conn = connectionPool.getConnection(jdbcUrl)
    var statement: NamedParametersPreparedStatement = null
    var rs: ResultSet = null
    try {
      statement = mkPreparedStatement(conn, code)
      val metadata = statement.queryMetadata.right.get
      assert(metadata.parameters.keys == Set("v1", "v2"))

      rs = statement
        .executeWith(Seq("v1" -> RawString("Lisbon"), "v2" -> RawInt(1)))
        .right
        .get
        .asInstanceOf[NamedParametersPreparedStatementResultSet]
        .rs
      rs.next()
      assert(rs.getString(1) == "Lisbon")
      assert(rs.getInt(2) == 1)
      assert(rs.getString(3) == "Lisbon")
    } finally {
      if (rs != null) rs.close()
      if (statement != null) statement.close()
      conn.close()
    }
  }

  test("skip parameters in comments") { _ =>
    val code = """/* this should not be a parameter
      | :foo
      |*/
      |SELECT :v1 as arg  -- neither this one :bar """.stripMargin
    val conn = connectionPool.getConnection(jdbcUrl)
    var statement: NamedParametersPreparedStatement = null
    var rs: ResultSet = null
    try {
      statement = mkPreparedStatement(conn, code)
      rs = statement
        .executeWith(Seq("v1" -> RawString("Hello!")))
        .right
        .get
        .asInstanceOf[NamedParametersPreparedStatementResultSet]
        .rs
      rs.next()
      assert(rs.getString("arg") == "Hello!")
    } finally {
      if (rs != null) rs.close()
      if (statement != null) statement.close()
      conn.close()
    }
  }

  test("skip parameter in string") { _ =>
    val code = """SELECT ':foo' as v1, :bar as v2""".stripMargin
    val conn = connectionPool.getConnection(jdbcUrl)
    var statement: NamedParametersPreparedStatement = null
    var rs: ResultSet = null
    try {
      statement = mkPreparedStatement(conn, code)
      val metadata = statement.queryMetadata.right.get
      assert(metadata.parameters.keys == Set("bar"))
      rs = statement
        .executeWith(Seq("bar" -> RawString("Hello!")))
        .right
        .get
        .asInstanceOf[NamedParametersPreparedStatementResultSet]
        .rs

      rs.next()
      assert(rs.getString("v1") == ":foo")
      assert(rs.getString("v2") == "Hello!")
    } finally {
      if (rs != null) rs.close()
      if (statement != null) statement.close()
      conn.close()
    }
  }

  test("RD-10681 SQL fails to validate string with json ") { _ =>
    val code = """ SELECT '[1, 2, "3", {"a": "Hello"}]' as arg""".stripMargin
    val conn = connectionPool.getConnection(jdbcUrl)
    var statement: NamedParametersPreparedStatement = null
    var rs: ResultSet = null
    try {
      statement = mkPreparedStatement(conn, code)
      val metadata = statement.queryMetadata
      assert(metadata.isRight)
      assert(metadata.right.get.parameters.isEmpty)
      rs = statement.executeWith(Seq.empty).right.get.asInstanceOf[NamedParametersPreparedStatementResultSet].rs

      rs.next()
      assert(rs.getString("arg") == """[1, 2, "3", {"a": "Hello"}]""")
    } finally {
      if (rs != null) rs.close()
      if (statement != null) statement.close()
      conn.close()
    }
  }

  private def mkPreparedStatement(conn: Connection, code: String) = {
    new NamedParametersPreparedStatement(conn, parse(code))
  }

  private def parse(sourceCode: String) = {
    val positions = new Positions
    val syntaxAnalyzer = new SqlSyntaxAnalyzer(positions)
    syntaxAnalyzer.parse(sourceCode)
  }

}
