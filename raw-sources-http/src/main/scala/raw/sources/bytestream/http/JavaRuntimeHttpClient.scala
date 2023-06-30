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

package raw.sources.bytestream.http

import com.typesafe.scalalogging.StrictLogging
import org.apache.hc.core5.net.URIBuilder
import raw.config._
import raw.sources.LocationException
import raw.sources.bytestream._

import java.io.{IOException, InputStream}
import java.net._
import java.net.http.HttpClient.Version
import java.net.http.HttpRequest.{BodyPublisher, BodyPublishers}
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
import scala.collection.mutable

// TODO (msb): The existence of this is weird; also for the config settings it implies...
object JavaRuntimeHttpClient {

  // TODO: Transform this into dependency injection. Note that is requires 'settings'.
  private val initLock = new Object
  private var httpClient: HttpClient = _

  protected[http] def buildHttpClient(settings: RawSettings): HttpClient = {
    initLock.synchronized {
      if (httpClient == null) {
        val connectTimeout = settings.getDuration("raw.sources.bytestream.http.connect-timeout", TimeUnit.MILLISECONDS)
        this.httpClient = java.net.http.HttpClient.newBuilder
          .version(Version.HTTP_1_1)
          .connectTimeout(Duration.ofMillis(connectTimeout))
          .followRedirects(HttpClient.Redirect.NORMAL)
          .build
      }
    }
    httpClient
  }
}

class JavaRuntimeHttpClient(
    method: String,
    args: Array[(String, String)],
    headers: Array[(String, String)],
    body: Option[Array[Byte]],
    expectedStatus: Seq[Int]
)(implicit settings: RawSettings)
    extends InputStreamClient
    with RuntimeHttpClient
    with StrictLogging {

  private val httpClient = JavaRuntimeHttpClient.buildHttpClient(settings)
  private val httpReadTimeoutMillis =
    settings.getDuration("raw.sources.bytestream.http.read-timeout", TimeUnit.MILLISECONDS)

  private val requestBuilderTemplate = HttpRequest
    .newBuilder()
    .timeout(Duration.ofMillis(httpReadTimeoutMillis))

  // This method is called by our runtime in a read so expects the status code to be 200
  override def getInputStream(url: String): InputStream = {
    val response = openHTTPConnection(url)
    val responseCode = response.statusCode()

    if (expectedStatus.contains(responseCode)) {
      response.body()
    } else {
      val is = response.body()
      val bodyContents =
        try {
          readOutputBounded(is)
        } finally {
          is.close()
        }
      if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
        throw new HttpClientException(s"authorization error accessing $url: ($responseCode)\n$bodyContents")
      } else {
        throw new HttpClientException(
          s"could not read (HTTP ${method.toUpperCase}) from $url: ($responseCode)\n$bodyContents"
        )
      }
    }
  }

  // This method is called by our 'http_request' intrinsic so does not check for the status code.
  def getInputStreamWithStatus(url: String): HttpResult = {
    val response = openHTTPConnection(url)
    val responseCode = response.statusCode()

    // In some cases, like a 204, there is no content, so creating an empty input-stream
    val is =
      if (response.body() != null) {
        response.body()
      } else {
        InputStream.nullInputStream()
      }
    val headersBuffer = new mutable.ArrayBuffer[(String, String)]()
    response
      .headers()
      .map()
      .forEach((key, values) => headersBuffer += (key -> values.stream().collect(Collectors.joining(","))))
    val headersSeq = headersBuffer.toArray
    HttpResult(responseCode, is, headersSeq)
  }

  override def getSeekableInputStream(url: String): SeekableInputStream = {
    val skipableInputStream = new GenericSkippableInputStream(() => getInputStream(url))
    new DelegatingSeekableInputStream(skipableInputStream) {
      override def getPos: Long = skipableInputStream.getPos

      override def seek(newPos: Long): Unit = skipableInputStream.seek(newPos)
    }
  }

  private def createBodyPublisher(): BodyPublisher = {
    body
      .map(s =>
        method.toLowerCase match {
          case "post" | "put" | "patch" => return BodyPublishers.ofByteArray(s)
          case _ => throw new LocationException("only 'POST', 'PUT' and 'PATCH' HTTP requests can have a body")
        }
      )
      .getOrElse(BodyPublishers.noBody())
  }

  private def openHTTPConnection(url: String): HttpResponse[InputStream] = {
    val requestBuilder = requestBuilderTemplate.copy()
    try {
      val uriBuilder = new URIBuilder(url)
      args.foreach(x => uriBuilder.addParameter(x._1, x._2))
      requestBuilder.uri(uriBuilder.build())
    } catch {
      case ex: IllegalArgumentException => throw new LocationException(s"invalid HTTP URL: ${ex.getMessage}", ex)
      case ex: MalformedURLException => throw new LocationException(s"invalid HTTP URL: ${ex.getMessage}", ex)
      case ex: URISyntaxException => throw new LocationException(s"invalid HTTP URL: ${ex.getMessage}", ex)
    }
    method.toLowerCase match {
      case "get" => requestBuilder.GET()
      case "post" => requestBuilder.POST(createBodyPublisher())
      case "put" => requestBuilder.PUT(createBodyPublisher())
      case "delete" => requestBuilder.DELETE()
      case "head" => requestBuilder.method("HEAD", BodyPublishers.noBody())
      case "patch" => requestBuilder.method("PATCH", createBodyPublisher())
      case "options" => requestBuilder.method("OPTIONS", BodyPublishers.noBody())
      case _ => throw new LocationException(s"invalid HTTP method: $method")
    }

    headers.foreach {
      case (k, v) =>
        try { requestBuilder.setHeader(k, v) }
        catch {
          case ex: IllegalArgumentException =>
            // .setHeader docs says `IllegalArgumentException` can be thrown if the header is restricted (e.g. "Host")
            // RD-6871
            throw new HttpClientException(ex.getMessage, ex)
        }
    }

    val request = requestBuilder.build()
    logger.debug(s"Sending request: $request")
    try {
      httpClient.send(request, BodyHandlers.ofInputStream())
    } catch {
      case ex: java.net.ConnectException => throw new HttpClientException(s"host not found for $url", ex)
      case ex: IOException => throw new HttpClientException(s"unexpected error accessing $url", ex)
    }
  }
}
