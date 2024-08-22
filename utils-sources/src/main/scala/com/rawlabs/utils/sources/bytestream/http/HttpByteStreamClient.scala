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

import com.typesafe.scalalogging.StrictLogging
import org.apache.hc.core5.net.URIBuilder
import com.rawlabs.utils.sources.api.LocationException
import com.rawlabs.utils.sources.bytestream.api._
import com.rawlabs.utils.core.RawSettings

import java.io.{IOException, InputStream}
import java.net._
import java.net.http.HttpClient.Version
import java.net.http.HttpRequest.{BodyPublisher, BodyPublishers}
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
import scala.collection.mutable

object HttpByteStreamClient {

  private val HTTP_CONNECT_TIMEOUT = "raw.utils.sources.bytestream.http.connect-timeout"
  private val HTTP_READ_TIMEOUT = "raw.utils.sources.bytestream.http.read-timeout"

  private val ERROR_RESPONSE_MAX_OUTPUT_SIZE = 2048

  private val httpClientLock = new Object
  private var httpClient: HttpClient = _

  def buildHttpClient(settings: RawSettings): HttpClient = {
    httpClientLock.synchronized {
      if (httpClient == null) {
        val connectTimeout = settings.getDuration(HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
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

class HttpByteStreamClient(
    method: String = "GET",
    args: Array[(String, String)] = Array.empty,
    headers: Array[(String, String)] = Array.empty,
    maybeBody: Option[Array[Byte]] = None,
    expectedStatus: Array[Int] = Array(
      HttpURLConnection.HTTP_OK,
      HttpURLConnection.HTTP_ACCEPTED,
      HttpURLConnection.HTTP_CREATED,
      HttpURLConnection.HTTP_PARTIAL
    )
)(implicit settings: RawSettings)
    extends InputStreamClient
    with StrictLogging {

  import HttpByteStreamClient._

  private val httpClient = HttpByteStreamClient.buildHttpClient(settings)
  private val httpReadTimeoutMillis = settings.getDuration(HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)

  private val requestBuilderTemplate = HttpRequest
    .newBuilder()
    .timeout(Duration.ofMillis(httpReadTimeoutMillis))

  protected def readOutputBounded(is: InputStream): String = {
    val data = is.readNBytes(ERROR_RESPONSE_MAX_OUTPUT_SIZE)
    val result = new String(data, StandardCharsets.UTF_8)
    if (data.length == ERROR_RESPONSE_MAX_OUTPUT_SIZE && is.read() != -1) {
      result + "..."
    } else {
      result
    }
  }

  // This method expects the response status code to be 200.
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
        throw new HttpByteStreamException(s"authorization error accessing $url: ($responseCode)\n$bodyContents")
      } else {
        throw new HttpByteStreamException(
          s"could not read (HTTP ${method.toUpperCase}) from $url: ($responseCode)\n$bodyContents"
        )
      }
    }
  }

  // This method does not check the response status code.
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

  def getSeekableInputStream(url: String): SeekableInputStream = {
    val skippableInputStream = new GenericSkippableInputStream(() => getInputStream(url))
    new DelegatingSeekableInputStream(skippableInputStream) {
      override def getPos: Long = skippableInputStream.getPos

      override def seek(newPos: Long): Unit = skippableInputStream.seek(newPos)
    }
  }

  private def createBodyPublisher(): BodyPublisher = {
    maybeBody
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
            throw new HttpByteStreamException(ex.getMessage, ex)
        }
    }

    val request = requestBuilder.build()
    logger.debug(s"Sending request: $request")
    try {
      httpClient.send(request, BodyHandlers.ofInputStream())
    } catch {
      case ex: java.net.ConnectException => throw new HttpByteStreamException(s"host not found for $url", ex)
      case ex: IOException => throw new HttpByteStreamException(s"unexpected error accessing $url", ex)
    }
  }
}
