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

package raw.compiler.rql2.tests.builtin
import com.sun.net.httpserver.{BasicAuthenticator, HttpExchange, HttpServer}
import com.typesafe.scalalogging.StrictLogging
import org.scalatest.BeforeAndAfterAll
import raw.creds.dropbox.DropboxTestCreds
import raw.compiler.rql2.tests.CompilerTestContext

import java.net.InetSocketAddress
import scala.collection.JavaConverters._

trait HttpPackageTest extends CompilerTestContext with DropboxTestCreds with BeforeAndAfterAll {

  val expectedUser = "user"
  val expectedPassword = "passwd"

  val testServer: TestHttpServer = new TestHttpServer
  val testPort: Int = testServer.address.getPort
  val wrongPort: Int = testServer.address.getPort + 10

  class TestHttpServer() extends StrictLogging {

    val simpleAuthenticator = new BasicAuthenticator("raw-tests") {
      override def checkCredentials(user: String, pwd: String): Boolean = {
        logger.debug(s"Checking credentials: $user, $pwd")
        user == expectedUser && pwd == expectedPassword
      }
    }

    private val server = HttpServer.create(new InetSocketAddress("localhost", 0), 5)
    server.createContext(
      "/hello",
      (exchange: HttpExchange) => {
        logger.debug(s" /hello Received request: $exchange")
        val bodyToReturn = "Hello World"
        exchange.sendResponseHeaders(200, bodyToReturn.length)
        val os = exchange.getResponseBody
        os.write(bodyToReturn.getBytes)
        os.close()
      }
    )
    server.createContext(
      "/csv",
      (exchange: HttpExchange) => {
        logger.debug(s" /csv Received request: $exchange")
        val bodyToReturn = {
          val provided = exchange.getRequestBody.readAllBytes()
          if (provided.nonEmpty) provided
          else "name|team\nLebron James|Lakers\nReggie Miller|Pacers".getBytes("UTF-8")
        }
        exchange.sendResponseHeaders(200, bodyToReturn.length)
        val os = exchange.getResponseBody
        os.write(bodyToReturn)
        os.close()
      }
    )

    server.createContext(
      "/return-headers",
      (exchange: HttpExchange) => {
        logger.debug(s"/return-headers Received request: $exchange")

        // These headers are added automatically, so removing them
        val toRemove = Seq("Host", "User-agent", "Content-length")
        val headers = exchange.getRequestHeaders.asScala.filter(x => !toRemove.contains(x._1))
        val str = headers.map(x => x._1 + ":" + x._2.asScala.mkString(",")).mkString("\n")
        exchange.sendResponseHeaders(200, str.length)
        val os = exchange.getResponseBody
        try os.write(str.getBytes())
        finally os.close()
      }
    )

    val returnBodyCtx = server.createContext(
      "/return-body",
      (exchange: HttpExchange) => {
        logger.debug(s"/return-body Received request: $exchange")
        val is = exchange.getRequestBody
        val bytes =
          try is.readAllBytes()
          finally is.close()

        exchange.sendResponseHeaders(200, bytes.length)
        val os = exchange.getResponseBody
        try os.write(bytes)
        finally os.close()
      }
    )
    returnBodyCtx.setAuthenticator(simpleAuthenticator)
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

  test(s"""
    |String.Read(
    |  Http.Get("http://localhost:$testPort/hello")
    |)""".stripMargin) { it =>
    it should evaluateTo(s"""String.Read(
      |    Location.Build(
      |        "http://localhost:$testPort/hello",
      |        http_method = "GET"
      |    )
      |)""".stripMargin)
    it should evaluateTo(""" "Hello World" """)
  }

  test(s"""
    |String.Read(
    |  Http.Post("http://localhost:$testPort/hello")
    |)""".stripMargin) { it =>
    it should evaluateTo(s"""String.Read(
      |    Location.Build(
      |        "http://localhost:$testPort/hello",
      |        http_method = "POST"
      |    )
      |)""".stripMargin)
    it should evaluateTo(""" "Hello World" """)
  }

  test(s"""
    |String.Read(
    |  Http.Put("http://localhost:$testPort/hello")
    |)""".stripMargin) { it =>
    it should evaluateTo(s"""String.Read(
      |    Location.Build(
      |        "http://localhost:$testPort/hello",
      |        http_method = "PUT"
      |    )
      |)""".stripMargin)
    it should evaluateTo(""" "Hello World" """)
  }

  test(s"""
    |String.Read(
    |  Http.Delete("http://localhost:$testPort/hello")
    |)""".stripMargin) { it =>
    it should evaluateTo(s"""String.Read(
      |    Location.Build(
      |        "http://localhost:$testPort/hello",
      |        http_method = "DELETE"
      |    )
      |)""".stripMargin)
    it should evaluateTo(""" "Hello World" """)
  }

  // Head returns an empty body so not checking the return value
  test(s"""
    |String.Read(
    |  Http.Head("http://localhost:$testPort/hello")
    |)""".stripMargin) { it =>
    it should evaluateTo(s"""String.Read(
      |    Location.Build(
      |        "http://localhost:$testPort/hello",
      |        http_method = "HEAD"
      |    )
      |)""".stripMargin)
  }

  test(s"""
    |String.Read(
    |  Http.Options("http://localhost:$testPort/hello")
    |)""".stripMargin) { it =>
    it should evaluateTo(s"""String.Read(
      |    Location.Build(
      |        "http://localhost:$testPort/hello",
      |        http_method = "OPTIONS"
      |    )
      |)""".stripMargin)
    it should evaluateTo(""" "Hello World" """)
  }

  test(s"""
    |String.Read(
    |  Http.Patch("http://localhost:$testPort/hello")
    |)""".stripMargin) { it =>
    it should evaluateTo(s"""String.Read(
      |    Location.Build(
      |        "http://localhost:$testPort/hello",
      |        http_method = "PATCH"
      |    )
      |)""".stripMargin)
    it should evaluateTo(""" "Hello World" """)
  }

  test(s"""
    |String.Read(
    |  Http.Post(
    |    "http://localhost:$testPort/return-body",
    |    username = "$expectedUser",
    |    password = "$expectedPassword",
    |    bodyString = "Hello World"
    |  )
    |)""".stripMargin) { it =>
    it should evaluateTo(s"""String.Read(
      |    Location.Build(
      |        "http://localhost:$testPort/return-body",
      |        http_method = "Post",
      |        http_user_name = "$expectedUser",
      |        http_password = "$expectedPassword",
      |        http_body_string = "Hello World"
      |    )
      |)""".stripMargin)
    it should evaluateTo(""" "Hello World" """)
  }

  test(s"""
    |String.Read(
    |  Http.Put(
    |    "http://localhost:$testPort/return-body",
    |    username = "$expectedUser",
    |    password = "$expectedPassword",
    |    bodyString = "Hello World"
    |  )
    |)""".stripMargin) { it =>
    it should evaluateTo(s"""String.Read(
      |    Location.Build(
      |        "http://localhost:$testPort/return-body",
      |        http_method = "Post",
      |        http_user_name = "$expectedUser",
      |        http_password = "$expectedPassword",
      |        http_body_string = "Hello World"
      |    )
      |)""".stripMargin)
    it should evaluateTo(""" "Hello World" """)
  }

  test(s"""
    |let
    |   request = Http.Read("http://localhost:$testPort/hello")
    |in
    |   String.Decode(request.data, "utf-8")
    |""".stripMargin)(it => it should evaluateTo(""" "Hello World" """))

  test(s"""Http.Read(
    |  "http://localhost:$testPort/hello",
    |  expectedStatus = List.Build(201)
    |)""".stripMargin)(it => it should runErrorAs("HTTP GET failed, got 200, expected 201"))

  test(s"""
    |let
    |  request = Http.Read(
    |    Http.Post(
    |      "http://localhost:$testPort/return-body",
    |      bodyString = "Hello World",
    |      username = "$expectedUser",
    |      password = "$expectedPassword"
    |    )
    |  )
    |
    |in
    |  String.Decode(request.data, "utf-8")
    |""".stripMargin)(it => it should evaluateTo(""" "Hello World" """))

  // Error handling
  test(s"""
    |// HTTP GET against several URLs, that succeed or fail
    |let urls = List.Build("http://localhost:$wrongPort/hello", "http://localhost:$testPort/hello",
    |"http://localhost:$testPort/darkness")
    |in List.Transform(urls, u -> Record.Build(url=u, r=(Http.Read(u)).status))
    |""".stripMargin)(it => it should evaluateTo(s"""
    |List.Build(
    | Record.Build(url="http://localhost:$wrongPort/hello",
    |              r=Error.Build("http error: host not found for http://localhost:$wrongPort/hello")),
    | Record.Build(url="http://localhost:$testPort/hello", r=200),
    | Record.Build(url="http://localhost:$testPort/darkness", r=404)
    |)""".stripMargin))

  test(s"""
    |// HTTP GET against several URLs, that succeed or fail
    |let urls = List.Build("http://localhost:$wrongPort/hello", "http://localhost:$testPort/hello",
    |"http://localhost:$testPort/darkness")
    |in List.Transform(urls, u -> Try.IsError(String.Read(Http.Get(u))))
    |""".stripMargin)(it => it should evaluateTo("List.Build(true, false, true)"))

  test(s"""
    |// HTTP GET against a given URL but different expected status
    |let codesToTry = List.Build(200, 201)
    |in List.Transform(codesToTry, code ->
    |       let r = Http.Read("http://localhost:$testPort/hello", expectedStatus=List.Build(code))
    |       in Record.Build(
    |          status=code,
    |          r=String.Decode(r.data, "utf-8")
    |       )
    |   )
    |""".stripMargin)(it => it should evaluateTo(s"""
    |List.Build(
    | Record.Build(status=200, r="Hello World"),
    | Record.Build(status=201, r=Error.Build("HTTP GET failed, got 200, expected 201"))
    |)""".stripMargin))

  val triple = "\"\"\""
  test(s"""String.Read(
    |  Http.Get(
    |      "http://localhost:$testPort/return-headers",
    |      headers = [
    |       {"Header1", "value1"},
    |       {"Header2", "value2"}
    |     ]
    |  )
    |)""".stripMargin)(it => it should evaluateTo(s""" ${triple}Header1:value1
    |Header2:value2$triple """.stripMargin))

  test(s"""String.Read(
    |  Location.Build(
    |      "http://localhost:$testPort/return-headers",
    |      http_headers = [
    |       {"Header1", "value1"},
    |       {"Header2", "value2"}
    |     ]
    |  )
    |)""".stripMargin)(it =>
    it should evaluateTo(
      s""" ${triple}Header1:value1
        |Header2:value2$triple """.stripMargin
    )
  )

  // Restricted headers are properly leading to an error (RD-6871)
  test(s"""String.Read(
    |  Location.Build(
    |      "http://localhost:$testPort/return-headers",
    |      http_headers = [
    |       {"Host", "value1"},
    |       {"Header2", "value2"}
    |     ]
    |  )
    |)""".stripMargin)(it => it should runErrorAs("http error: restricted header name: \"Host\""))

  test(s"""let
    |  query = $triple SELECT DISTINCT (?country as ?wikidata_country) ?countryLabel ?code
    |    WHERE
    |    {
    |        ?country wdt:P31 wd:Q3624078 .
    |        # part of the G20
    |        FILTER EXISTS {?country wdt:P463 wd:Q19771}
    |        #not a former country
    |        FILTER NOT EXISTS {?country wdt:P31 wd:Q3024240}
    |       OPTIONAL { ?country wdt:P901 ?code } .
    |
    |       SERVICE wikibase:label { bd:serviceParam wikibase:language "en" }
    |    }
    |  $triple,
    |  data = Csv.Read(
    |    Http.Get(
    |        "https://query.wikidata.org/bigdata/namespace/wdq/sparql",
    |        args = [{"query", query}],
    |        headers = [{"Accept", "text/csv"}]
    |    ),
    |    type collection(record(wikidata_country: string, countryLabel: string, code: string))
    |  )
    |in
    |  Collection.Filter(data, x -> x.code == "UK")""".stripMargin)(
    _ should evaluateTo("""[{
      |  wikidata_country: "http://www.wikidata.org/entity/Q145",
      |  countryLabel: "United Kingdom",
      |  code: "UK"
      |}] """.stripMargin)
  )

  test("""Http.UrlEncode("http://www.tralala.com/ploum+ploum")""")(
    _ should evaluateTo(""" "http%3A%2F%2Fwww.tralala.com%2Fploum%2Bploum" """)
  )

  test("""Http.UrlDecode("http%3A%2F%2Fwww.tralala.com%2Fploum%2Bploum")""")(
    _ should evaluateTo(""" "http://www.tralala.com/ploum+ploum" """)
  )

  // tests with InferAndRead generating the Value object of the Http.Location

  test(s"""Csv.InferAndRead(Http.Get(
    |  "http://localhost:$testPort/csv",
    |  expectedStatus = List.Build(201)
    |))""".stripMargin)(it => it should runErrorAs("inference error")) // because it fails to read during inference

  test(s"""Csv.InferAndRead(Http.Post(
    |  "http://localhost:$testPort/csv",
    |  bodyBinary = Binary.FromString("a|b|c\\n1|2|3\\n4|5|6")
    |))""".stripMargin)(it => it should evaluateTo("""[{a: 1, b: 2, c: 3}, {a: 4, b: 5, c: 6}]"""))
}
