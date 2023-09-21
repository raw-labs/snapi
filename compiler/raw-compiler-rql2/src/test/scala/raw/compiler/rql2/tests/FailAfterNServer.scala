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

package raw.compiler.rql2.tests

import com.sun.net.httpserver.{HttpExchange, HttpServer}
import com.typesafe.scalalogging.StrictLogging
import org.scalatest.{BeforeAndAfterAll, Suite}

import java.io.{OutputStreamWriter, StringReader}
import java.net.InetSocketAddress
import scala.util.control.NonFatal

trait FailAfterNServer extends Suite with BeforeAndAfterAll {

  val testServer: TestHttpServer = new TestHttpServer
  val testServerUrl = s"http://localhost:${testServer.address.getPort}"

  case class FailAfter(path: String, nBytes: Int, data: String)

  protected def failServices: Seq[FailAfter] = Seq.empty

  class TestHttpServer() extends StrictLogging {

    private val server = HttpServer.create(new InetSocketAddress("localhost", 0), 5)

    for (s <- failServices) {
      server.createContext(
        s.path,
        (exchange: HttpExchange) => {

          exchange.sendResponseHeaders(200, s.data.length)
          val reader = new StringReader(s.data)
          val writer = new OutputStreamWriter(exchange.getResponseBody)

          try {
            var nBytes = 0
            val buff = new Array[Char](10)
            var read = reader.read(buff)

            while (read >= 0) {
              writer.write(buff, 0, read)
              nBytes += read
              if (nBytes > s.nBytes) throw new AssertionError(s"${s.path} reached the limit of ${s.nBytes}")
              read = reader.read(buff)
            }
          } catch {
            case NonFatal(ex) =>
              logger.warn(s"Error running service ${s.path}", ex)
              throw ex
          } finally {
            writer.close()
          }

        }
      )
    }
    server.setExecutor(null)
    logger.info(s"Starting server at address: ${server.getAddress}")
    server.start()

    def address = server.getAddress

    def stop(): Unit = {
      server.stop(0)
    }
  }

  override def afterAll(): Unit = {
    testServer.stop()
    super.afterAll()
  }
}
