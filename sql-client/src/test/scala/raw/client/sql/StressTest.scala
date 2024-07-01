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

import com.dimafeng.testcontainers.PostgreSQLContainer
import eu.rekawek.toxiproxy.model.ToxicDirection
import eu.rekawek.toxiproxy.{Proxy, ToxiproxyClient}
import org.apache.commons.io.output.NullOutputStream
import org.testcontainers.containers.{Network, ToxiproxyContainer}
import org.testcontainers.utility.DockerImageName
import raw.client.api._
import raw.utils._

import java.sql.DriverManager
import scala.io.Source

class StressTest extends RawTestSuite with SettingsTestContext with TrainingWheelsContext {

  private var container: PostgreSQLContainer = _
  Class.forName("org.postgresql.Driver")

  private var network: Network = _
  private var toxiproxy: ToxiproxyContainer = _
  private var proxy: Proxy = _

  private var compilerService: CompilerService = _
  private var jdbcUrl: String = _

  // Username equals the database
  private val user = InteractiveUser(Uid("blah"), "fdw user", "email", Seq.empty)


  override def beforeAll(): Unit = {
    super.beforeAll()
    network = Network.newNetwork()
    container = PostgreSQLContainer(
      dockerImageNameOverride = DockerImageName.parse("postgres:15-alpine")
    ).configure(_.withNetwork(network)).configure(_.withNetworkAliases("pgpg"))

    container.start()

    val conn = DriverManager.getConnection(container.jdbcUrl, container.username, container.password);
    val resource = Source.fromResource("example.sql")
    val sql =
      try { resource.mkString }
      finally { resource.close() }

    val stmt = conn.createStatement()
    stmt.execute(sql)

    toxiproxy = new ToxiproxyContainer("ghcr.io/shopify/toxiproxy:2.5.0").withNetwork(network)
    toxiproxy.start()
    val toxiproxyClient = new ToxiproxyClient(toxiproxy.getHost, toxiproxy.getControlPort)
    proxy = toxiproxyClient.createProxy("db", "0.0.0.0:8666", s"pgpg:5432")
    jdbcUrl = {
      val dbPort = toxiproxy.getMappedPort(8666).toString
      val dbName = container.databaseName
      val user = container.username
      val password = container.password
      s"jdbc:postgresql://${toxiproxy.getHost}:$dbPort/$dbName?user=$user&password=$password"
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
    Option(container).foreach(_.stop)
    Option(toxiproxy).foreach(_.stop)
    Option(network).foreach(_.close)
    Option(proxy).foreach(_.delete)
    container = null
    toxiproxy = null
    network = null
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
    for (i <- 1 to 100000) {
      val r = compilerService.getProgramDescription(code, env)
      if (i == 50) proxy.toxics().timeout("test", ToxicDirection.UPSTREAM, 1000)
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
