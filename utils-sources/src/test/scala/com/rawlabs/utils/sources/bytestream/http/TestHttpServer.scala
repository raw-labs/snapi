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

package com.rawlabs.utils.sources.bytestream.http

import com.sun.net.httpserver.{Authenticator, BasicAuthenticator, HttpExchange, HttpHandler, HttpServer}
import com.typesafe.scalalogging.StrictLogging

import java.io.IOException
import java.net.InetSocketAddress

class DefaultHandler(bodyToReturn: String) extends HttpHandler with StrictLogging {
  @throws[IOException]
  def handle(exchange: HttpExchange): Unit = {
    logger.debug(s"Received request: $exchange")
    exchange.sendResponseHeaders(200, bodyToReturn.length)
    val os = exchange.getResponseBody
    os.write(bodyToReturn.getBytes)
    os.close()
  }
}

class SimpleAuthenticator(expectedUsername: String, expectedPassword: String)
    extends BasicAuthenticator("raw-tests")
    with StrictLogging {
  override def checkCredentials(user: String, pwd: String): Boolean = {
    logger.debug(s"Checking credentials: $user, $pwd")
    user == expectedUsername && pwd == expectedPassword
  }
}

class TestHttpServer(authenticator: Authenticator, handler: HttpHandler) extends StrictLogging {

  private val server = HttpServer.create(new InetSocketAddress("localhost", 0), 5)

  private val ctx = server.createContext("/get", handler)
  ctx.setAuthenticator(authenticator)
  server.setExecutor(null)
  logger.info(s"Starting server at address: ${server.getAddress}")
  server.start()

  def address = server.getAddress

  def stop(): Unit = {
    server.stop(0)
  }
}
