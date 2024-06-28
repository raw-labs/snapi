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
import org.apache.commons.io.output.NullOutputStream
import org.testcontainers.utility.DockerImageName
import raw.client.api._
import raw.utils._

import java.sql.DriverManager
import scala.io.Source

class StressTest extends RawTestSuite with ForAllTestContainer with SettingsTestContext with TrainingWheelsContext {

  override val container: PostgreSQLContainer = PostgreSQLContainer(
    dockerImageNameOverride = DockerImageName.parse("postgres:15-alpine")
  )
  Class.forName("org.postgresql.Driver")

  private var compilerService: CompilerService = _
  private var jdbcUrl: String = _

  // Username equals the database
  private var user: InteractiveUser = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    val conn = DriverManager.getConnection(container.jdbcUrl, container.username, container.password);
    val resource = Source.fromResource("example.sql")
    val sql =
      try { resource.mkString }
      finally { resource.close() }

    val stmt = conn.createStatement()
    stmt.execute(sql)

    user = InteractiveUser(Uid(container.databaseName), "fdw user", "email", Seq.empty)

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

  test("getProgramDescription loop") { _ =>
    val env = ProgramEnvironment(
      user,
      Some(Array("city" -> RawString("Lyon"))),
      Set.empty,
      Map("output-format" -> "json"),
      None,
      Some(jdbcUrl)
    )
    val code = "SELECT COUNT(*) FROM example.airports WHERE city = :city"
    for (i <- 1 to 10000) {
      Thread.sleep(500)
      val r = compilerService.getProgramDescription(code, env)
      assert(r.isInstanceOf[GetProgramDescriptionSuccess])
    }
  }

 ignore("execute loop") { _ =>
    val env = ProgramEnvironment(
      user,
      Some(Array("city" -> RawString("Lyon"))),
      Set.empty,
      Map("output-format" -> "json"),
      None,
      Some(jdbcUrl)
    )
    val code = "SELECT COUNT(*) FROM example.airports WHERE city = :city"
    val outputStream = NullOutputStream.NULL_OUTPUT_STREAM
    for (i <- 1 to 10000) {
      val r = compilerService.execute(code, env, None, outputStream)
      assert(r == ExecutionSuccess)
    }
  }

}
