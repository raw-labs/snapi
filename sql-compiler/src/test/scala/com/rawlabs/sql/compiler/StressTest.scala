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
import org.scalatest.matchers.must.Matchers.{be, noException}
import org.testcontainers.containers.{Network, ToxiproxyContainer}
import org.testcontainers.utility.DockerImageName
import raw.client.api._
import raw.utils._

import java.io.{ByteArrayOutputStream, IOException}
import java.sql.DriverManager
import java.util.concurrent.Executors
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
      try {
        resource.mkString
      } finally {
        resource.close()
      }

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
    env = ProgramEnvironment(
      user,
      Some(Array("city" -> RawString("Lyon"))),
      Set.empty,
      Map("output-format" -> "json"),
      None,
      Some(jdbcUrl)
    )
  }

  override def afterAll(): Unit = {
    if (compilerService != null) {
      compilerService.stop()
      compilerService = null
    }
    Option(proxy).foreach(_.delete)
    proxy = null
    Option(toxiproxy).foreach(_.stop)
    toxiproxy = null
    Option(container).foreach(_.stop)
    container = null
    Option(network).foreach(_.close)
    network = null
    super.afterAll()
  }

  private def withFailures(what: () => Unit): Unit = {
    val executor = Executors.newSingleThreadExecutor()
    val bugFunctions = Seq(
      () => proxy.toxics().timeout("bug", ToxicDirection.UPSTREAM, 0),
      () => proxy.toxics().timeout("bug", ToxicDirection.DOWNSTREAM, 0),
      () => proxy.toxics().resetPeer("bug", ToxicDirection.UPSTREAM, 0),
      () => proxy.toxics().resetPeer("bug", ToxicDirection.DOWNSTREAM, 0)
    )
    val failures = executor.submit(() => {
      for (i <- 1 to 2; installBug <- bugFunctions) {
        Thread.sleep(2000)
        installBug()
        logger.warn("enable bug")
        Thread.sleep(3000)
        proxy.toxics().get("bug").remove()
        logger.warn("disable bug")
      }
      12
    })
    try {
      for (i <- 1 to 500) {
        logger.debug(s"#$i")
        noException should be thrownBy what()
      }
    } finally {
      failures.cancel(true)
      try {
        proxy.toxics().get("bug").remove()
      } catch {
        case _: IOException => // normal if not existing
      }
      executor.close()
    }
  }

  private var env: ProgramEnvironment = _
  private val code = "SELECT COUNT(*) FROM example.airports WHERE city = :city"

  test("getProgramDescription") { _ =>
    withFailures { () =>
      val GetProgramDescriptionSuccess(_) = compilerService.getProgramDescription(code, env)
      Thread.sleep(10)
    }
  }

  test("dotAutoComplete") { _ =>
    withFailures { () =>
      val AutoCompleteResponse(r) = compilerService.dotAutoComplete(code, env, Pos(1, 30))
      assert(r.nonEmpty) // example.airports columns
      Thread.sleep(10)
    }
  }

  test("wordAutoComplete") { _ =>
    withFailures { () =>
      val AutoCompleteResponse(r) = compilerService.wordAutoComplete(code, env, "ex", Pos(1, 24))
      assert(r.nonEmpty) // example.airports columns
      Thread.sleep(10)
    }
  }

  test("hover") { _ =>
    withFailures { () =>
      val HoverResponse(r) = compilerService.hover(code, env, Pos(1, 24))
      assert(r.nonEmpty)
      Thread.sleep(10)
    }
  }

  test("validate") { _ =>
    withFailures { () =>
      val ValidateResponse(r) = compilerService.validate(code, env)
      assert(r.isEmpty)
      Thread.sleep(10)
    }
  }

  ignore("execute") { _ =>
    val outputStream = new ByteArrayOutputStream()
    try {
      withFailures { () =>
        val r = compilerService.execute(code, env, None, outputStream, None)
        assert(r.isInstanceOf[ExecutionSuccess])
        Thread.sleep(10)
        outputStream.reset()
      }
    } finally {
      outputStream.close()
    }
  }

}
