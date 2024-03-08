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

package raw.rest.client

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.scala.JavaTypeable
import com.google.common.base.Stopwatch
import com.typesafe.scalalogging.StrictLogging
import org.apache.commons.io.IOUtils
import org.apache.hc.client5.http.classic.methods._
import org.apache.hc.client5.http.config._
import org.apache.hc.client5.http.impl.classic.{CloseableHttpResponse, HttpClientBuilder}
import org.apache.hc.client5.http.impl.io._
import org.apache.hc.core5.http._
import org.apache.hc.core5.http.io.entity.StringEntity
import org.apache.hc.core5.http.io.SocketConfig
import org.apache.hc.core5.net.URIBuilder
import org.apache.hc.core5.util.Timeout
import raw.auth.api.{ForbiddenException, GenericAuthException, TokenProvider, UnauthorizedException}
import raw.utils.RawSettings
import raw.rest.common._

import java.io.{IOException, InterruptedIOException}
import java.net.{URI, UnknownHostException}
import java.nio.charset.Charset
import java.time.Duration
import java.util.concurrent.TimeUnit
import scala.concurrent.CancellationException

object RestClient {
  val X_RAW_CLIENT = "X-RAW-Client"
  val X_RAW_CLIENT_VALUE = "Scala RAW REST Client/0.1"

  val mapper = new RestJsonMapper()
  val restErrorReader = mapper.readerFor[GenericRestError]
}

/**
 * REST client used for RAW services.
 */
class RestClient(
    serverHttpAddress: URI,
    maybeTokenProvider: Option[TokenProvider],
    name: String,
    maybeImpersonateUser: Option[String] = None,
    retryOnAccepted: Option[String] =
      Some("/1/public/pending-request") // Legacy mode to automatically retry ACCEPTED responses in the following URL.
)(implicit settings: RawSettings)
    extends StrictLogging {

  import RestClient._

  // Remove any trailing /, as the URI class interprets a trailing slash as a segment of the path
  private val cleanedServerHttpAddress = {
    val uriStr = serverHttpAddress.toString
    if (uriStr.endsWith("/")) {
      new URI(uriStr.substring(0, uriStr.length - 1))
    } else {
      serverHttpAddress
    }
  }

  logger.debug(s"[$name] Creating REST Client ($serverHttpAddress)")
  private val asyncRequestRetries = settings.getInt("raw.rest.client.async-request-retries")

  private val serviceNotAvailableRetries = settings.getInt("raw.rest.client.service-not-available-retries")
  private val serviceNotAvailableRetryIntervalMillis =
    settings.getDuration("raw.rest.client.service-not-available-retry-interval", TimeUnit.MILLISECONDS)

  private val socketTimeout = settings.getDuration("raw.rest.client.socket-timeout", TimeUnit.MILLISECONDS).toInt
  private val socketConfig = SocketConfig
    .custom()
    .setSoTimeout(Timeout.ofMilliseconds(socketTimeout))
    .build()

  private val connectTimeout = settings.getDuration("raw.rest.client.connect-timeout", TimeUnit.MILLISECONDS).toInt

  private val connectionConfig = ConnectionConfig
    .custom()
    .setConnectTimeout(Timeout.ofMilliseconds(connectTimeout))
    .build()

  private val maxConnPerRoute = settings.getIntOpt("raw.rest.client.max-conn-per-route").getOrElse(20)
  private val maxConnTotal = settings.getIntOpt("raw.rest.client.max-conn-total").getOrElse(100)

  private val connectionManager = PoolingHttpClientConnectionManagerBuilder
    .create()
    .setMaxConnPerRoute(maxConnPerRoute)
    .setMaxConnTotal(maxConnTotal)
    .setDefaultSocketConfig(socketConfig)
    .setDefaultConnectionConfig(connectionConfig)
    .build()

  private def checkAvailableConnections(): Unit = {
    val stats = connectionManager.getTotalStats
    if (stats.getPending > 0) {
      logger.warn(
        s"[$name] Some requests are blocked waiting for connections to become available: $stats"
      )
    }
  }

  private val requestConfig = RequestConfig
    .custom()
    .setResponseTimeout(Timeout.ofMilliseconds(socketTimeout))
    .build()

  private val httpClient = HttpClientBuilder
    .create()
    .setConnectionManager(connectionManager)
    .setDefaultRequestConfig(requestConfig)
    .build()

  def this(serverHttpAddress: URI, tokenProvider: TokenProvider, name: String)(implicit settings: RawSettings) =
    this(serverHttpAddress, Some(tokenProvider), name)

  def this(serverHttpAddress: URI, name: String)(implicit settings: RawSettings) = this(serverHttpAddress, None, name)

  private def executeRequest(request: HttpUriRequest): CloseableHttpResponse = {

    def doRequest(): CloseableHttpResponse = {
      logger.trace(s"[$name] Sending request: ${request.getMethod} ${request.getUri}")
      val start = Stopwatch.createStarted()
      var response: CloseableHttpResponse = null
      try {
        checkAvailableConnections()
        response = httpClient.execute(request)
        response
      } catch {
        case ex @ (_: InterruptedIOException | _: CancellationException) =>
          logger.warn(s"[$name] Interrupted while waiting for response.", ex)
          // If the I/O operation is interrupted in-flight because the thread doing it itself is interrupted, we get either
          // InterruptedIOException or CancellationException (Apache HTTP Client). We convert it to InterruptedException so
          // the rest of the system handles it as a normal interruption.
          if (Thread.interrupted()) {
            throw new InterruptedException()
          } else {
            throw new ServerNotAvailableException(s"error contacting $name", ex)
          }
        case ex: UnknownHostException =>
          throw new ServerNotAvailableException(s"unknown host while contacting $name", ex)
        case ex: IOException => throw new ServerNotAvailableException(s"error contacting $name", ex)
      } finally {
        logger.trace(
          s"[$name] Request completed: ${request.getMethod} ${request.getUri}: " +
            s"${if (response != null) response.getCode else "Failed"}. " +
            s"Duration: ${start.elapsed(TimeUnit.MILLISECONDS)}ms"
        )
      }
    }

    var response = doRequest()
    // Retry while response is 503...
    var retriesLeft = serviceNotAvailableRetries
    while (retriesLeft > 0 && response.getCode == HttpStatus.SC_SERVICE_UNAVAILABLE) {
      response.close()
      logger.debug(
        s"[$name] Service temporarily unavailable. Retrying in $serviceNotAvailableRetryIntervalMillis milliseconds. Retries left: $retriesLeft."
      )
      Thread.sleep(serviceNotAvailableRetryIntervalMillis)
      retriesLeft -= 1
      response = doRequest()
    }
    // ... if we run out of retries, throw a RequestTimeoutException.
    if (response.getCode == HttpStatus.SC_SERVICE_UNAVAILABLE) {
      throw new RequestTimeoutException()
    }

    // Finally we have a response; let's validate and return it or throw an exception.
    validateResponseOrThrow(response)
  }

  // This method parses the status code+response. If ok, returns it. Otherwise, it throws a REST client exception,
  // which inherit from APIException.
  // (msb): For "historical" reasons, we parse the full body response in most all cases. This was done historically to
  // ensure protocol consistency even on errors. I'm not sure it's worth doing it anymore, but the code remains for now.
  def validateResponseOrThrow[T <: ClassicHttpResponse](response: T): T = {
    response.getCode match {
      case statusCode @ HttpStatus.SC_BAD_REQUEST =>
        try {
          readBody(response) match {
            case Some(body) =>
              try {
                val restError = restErrorReader.readValue[GenericRestError](body)
                throw new ClientAPIException(restError)
              } catch {
                case ex: JsonProcessingException => throw new InvalidBodyException(ex)
              }
            case None => throw new InvalidBodyException()
          }
        } finally {
          response.close()
        }
      case HttpStatus.SC_UNAUTHORIZED =>
        try {
          readBody(response) match {
            case Some(_) =>
              // We parse the body to validate it, but disregard it in the exception message.
              throw new UnauthorizedException()
            case None => throw new InvalidBodyException()
          }
        } finally {
          response.close()
        }
      case HttpStatus.SC_FORBIDDEN =>
        try {
          readBody(response) match {
            case Some(_) =>
              // We parse the body to validate it, but disregard it in the exception message.
              throw new ForbiddenException()
            case None => throw new InvalidBodyException()
          }
        } finally {
          response.close()
        }
      case statusCode if statusCode >= 500 =>
        // (msb) For 500 errors, we send back UnexpectedErrorException.
        // This message has a public description that is static, e.g. "internal error".
        // This public description does not include any details (e.g. the message in the body describing the server-side
        // crash), because UnexpectedErrorException is still a RawException, and our definition is that RawException messages are
        // always publicly visible.
        // Therefore, not to lose the body info (which may be useful for debugging purposes), we wrap it inside a
        // fake exception because we want to carry it somewhere for debugging purposes (so that it prints in the logs),
        // and the only reliable way for exceptions to carry extra info besides the message is in a cause/inner exception.
        // The alternative would be to declare a field called 'body' in UnexpectedErrorException, but for that to print
        // we would need to override toString, and the moment we do that and someone composes two errors with
        // string interpolators, we risk leaking that body info out to the user...
        try {
          readBody(response) match {
            case Some(body) => throw new UnexpectedErrorException(new Exception(body))
            case None => throw new UnexpectedErrorException()
          }
        } finally {
          response.close()
        }
      case _ =>
        // Other responses are considered valid by the client and to be handled by the caller methods, so we accept them.
        response
    }
  }

  private def readBody(response: ClassicHttpResponse): Option[String] = {
    if (response.getEntity == null) {
      None
    } else {
      val resp =
        try {
          IOUtils.toString(response.getEntity.getContent, Charset.forName("UTF-8")).trim
        } catch {
          case ex: IOException =>
            // TODO (msb): Questionable choice of exception. Use BadResponseException instead?
            throw new ServerNotAvailableException(s"error reading response body from $name", ex)
        }
      if (resp.isEmpty) {
        None
      } else {
        Some(resp)
      }
    }
  }

  private def setJsonPayload(request: HttpUriRequestBase, payload: Any): Unit = {
    request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType)
    val reqBody = mapper.writeValueAsString(payload)
    request.setEntity(new StringEntity(reqBody, ContentType.APPLICATION_JSON))
  }

  private def setPlainTextPayload(request: HttpUriRequestBase, payload: String): Unit = {
    request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_PLAIN.getMimeType)
    request.setEntity(new StringEntity(payload))
  }

  private def doJsonRequestWithEmptyResponse(
      request: HttpUriRequestBase,
      payload: Any,
      expectedStatus: Int,
      queryHeaders: Seq[(String, String)]
  ): Unit = {
    setJsonPayload(request, payload)
    val response = doRequest(request, queryHeaders)
    val statusCode = response.getCode
    try {
      if (statusCode != expectedStatus) {
        throw new UnexpectedStatusCodeException(expectedStatus, statusCode)
      }
      val maybeBody = readBody(response)
      if (maybeBody.isDefined) {
        throw new InvalidBodyException()
      }
    } finally {
      response.close()
    }
  }

  private def doJsonRequestWithResponse[T](
      request: HttpUriRequestBase,
      payload: Any,
      expectedStatus: Int,
      queryHeaders: Seq[(String, String)]
  )(
      implicit classTag: JavaTypeable[T]
  ): T = {
    setJsonPayload(request, payload)
    val response = doRequest(request, queryHeaders)
    val statusCode = response.getCode
    try {
      if (statusCode != expectedStatus) {
        throw new UnexpectedStatusCodeException(expectedStatus, statusCode)
      }
      val body = readBody(response).getOrElse(throw new InvalidBodyException())
      mapper.readValue[T](body)
    } finally {
      response.close()
    }
  }

  def newGet(path: String, queryParams: Map[String, Any] = Map.empty, withAuth: Boolean = true): HttpGet = {
    val uri = buildUri(path, queryParams)
    val httpGet = new HttpGet(uri)
    configureRequest(httpGet, withAuth)
    httpGet
  }

   def newPost(path: String, queryParams: Map[String, Any] = Map.empty, withAuth: Boolean = true): HttpPost = {
    val uri = buildUri(path, queryParams)
    val httpPost = new HttpPost(uri)
    configureRequest(httpPost, withAuth)
    httpPost
  }

   def newPut(path: String, queryParams: Map[String, Any] = Map.empty, withAuth: Boolean = true): HttpPut = {
    val uri = buildUri(path, queryParams)
    val httpPut = new HttpPut(uri)
    configureRequest(httpPut, withAuth)
    httpPut
  }

   def newDelete(
      path: String,
      queryParams: Map[String, Any] = Map.empty,
      withAuth: Boolean = true
  ): HttpDelete = {
    val uri = buildUri(path, queryParams)
    val httpDelete = new HttpDelete(uri)
    configureRequest(httpDelete, withAuth)
    httpDelete
  }

  private def buildUri(path: String, queryParams: Map[String, Any]): URI = {
    val uriBuilder = new URIBuilder(cleanedServerHttpAddress)
      .appendPath(path.replaceAllLiterally("|", "%7C"))
    queryParams.foreach {
      case (k, v) => v match {
          case s: String => uriBuilder.addParameter(k, s)
          case i: Int => uriBuilder.addParameter(k, i.toString)
          case l: Long => uriBuilder.addParameter(k, l.toString)
          case d: Duration => uriBuilder.addParameter(k, d.toString) // java.time.Duration converts to ISO-8601 Duration
          case b: Boolean => uriBuilder.addParameter(k, b.toString)
          case Some(s: String) => uriBuilder.addParameter(k, s)
          case None =>
            // (ctm) Another option here would be to not add the parameter.
            uriBuilder.addParameter(k, null)
          case l: List[_] =>
            // TODO (msb): This only works for List[String]. (BTW, erasure absolutely sucks...)
            uriBuilder.addParameter(k, l.map(_.toString).mkString(","))
        }
    }
    uriBuilder.build
  }

  private def configureRequest(req: HttpUriRequestBase, withAuth: Boolean): Unit = {
    req.setHeader(RestClient.X_RAW_CLIENT, RestClient.X_RAW_CLIENT_VALUE)
    if (withAuth) {
      maybeTokenProvider
        .map { tokenProvider =>
          val accessToken =
            try {
              tokenProvider.token.accessToken
            } catch {
              case ex: GenericAuthException =>
                throw new ServerNotAvailableException("cannot retrieve access token from token provider", ex)
            }
          req.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        }
        .getOrElse {
          throw new ServerNotAvailableException("no token provider specified but authentication required")
        }
      maybeImpersonateUser.foreach(uid => req.setHeader(RawHttpHeaders.X_IMPERSONATE_UID, uid))
    }
  }

  /** General purpose */

  def doRequest(req: HttpUriRequestBase, queryHeaders: Seq[(String, String)] = Seq.empty): CloseableHttpResponse = {
    queryHeaders.foreach { case (k, v) => req.setHeader(k, v) }
    var response = executeRequest(req)
    retryOnAccepted match {
      case Some(urlToRetry) =>
        // Legacy mode where requests were accepted and this REST Client would automatically retry them transparently
        // on 202. Kept for backwards compatibility but not necessary anymore.
        var retriesLeft = asyncRequestRetries
        while (retriesLeft > 0 && response.getCode == HttpStatus.SC_ACCEPTED) {
          val requestUuid = readBody(response) match {
            case Some(str) => str
            case None =>
              logger.error(s"[$name] Received a 202 response without a request ID")
              throw new InvalidBodyException()
          }
          response.close()
          logger.debug(
            s"[$name] Request did not complete before timeout. Retrying with request id: $requestUuid. Retries left: $retriesLeft."
          )
          val retryRequest = newPost(urlToRetry)
          setPlainTextPayload(retryRequest, requestUuid)
          retriesLeft -= 1
          response = executeRequest(retryRequest)
        }
      case None =>
    }
    if (response.getCode == HttpStatus.SC_ACCEPTED) {
      throw new RequestTimeoutException()
    }
    response
  }

  /** GET */

  def doGet[T](
      path: String,
      expectedStatus: Int = HttpStatus.SC_OK,
      queryHeaders: Seq[(String, String)] = Seq.empty,
      queryParams: Map[String, Any] = Map.empty,
      withAuth: Boolean = true
  )(
      implicit classTag: JavaTypeable[T]
  ): T = {
    val request = newGet(path, queryParams, withAuth)
    val response = doRequest(request, queryHeaders)
    val statusCode = response.getCode
    try {
      if (statusCode != expectedStatus) {
        throw new UnexpectedStatusCodeException(expectedStatus, statusCode)
      }
      val body = readBody(response).getOrElse(throw new InvalidBodyException())
      mapper.readValue[T](body)
    } finally {
      response.close()
    }
  }

  def doGetWithPlainTextResponse(
      path: String,
      expectedStatus: Int = HttpStatus.SC_OK,
      queryHeaders: Seq[(String, String)] = Seq.empty,
      queryParams: Map[String, Any] = Map.empty,
      withAuth: Boolean = true
  ): String = {
    val request = newGet(path, queryParams, withAuth)
    val response = doRequest(request, queryHeaders)
    val statusCode = response.getCode
    try {
      if (statusCode != expectedStatus) {
        throw new UnexpectedStatusCodeException(expectedStatus, statusCode)
      }
      readBody(response).getOrElse(throw new InvalidBodyException())
    } finally {
      response.close()
    }
  }

  def doGetWithEmptyResponse(
      path: String,
      expectedStatus: Int = HttpStatus.SC_OK,
      queryHeaders: Seq[(String, String)] = Seq.empty,
      queryParams: Map[String, Any] = Map.empty,
      withAuth: Boolean = true
  ): Unit = {
    val request = newGet(path, queryParams, withAuth)
    val response = doRequest(request, queryHeaders)
    val statusCode = response.getCode
    try {
      if (statusCode != expectedStatus) {
        throw new UnexpectedStatusCodeException(expectedStatus, statusCode)
      }
      val maybeBody = readBody(response)
      if (maybeBody.isDefined) {
        throw new InvalidBodyException()
      }
    } finally {
      response.close()
    }
  }

  /** POST */

  def doPlainTextPost[T](
      path: String,
      payload: String,
      expectedStatus: Int = HttpStatus.SC_OK,
      queryHeaders: Seq[(String, String)] = Seq.empty,
      queryParams: Map[String, Any] = Map.empty,
      withAuth: Boolean = true
  )(
      implicit classTag: JavaTypeable[T]
  ): T = {
    val request = newPost(path, queryParams, withAuth)
    setPlainTextPayload(request, payload)
    val response = doRequest(request, queryHeaders)
    val statusCode = response.getCode
    try {
      if (statusCode != expectedStatus) {
        throw new UnexpectedStatusCodeException(expectedStatus, statusCode)
      }
      val body = readBody(response).getOrElse(throw new InvalidBodyException())
      mapper.readValue[T](body)
    } finally {
      response.close()
    }
  }

  // Used to pass response back to consumer, who may close it later.
  def doPlainTextPostWithOpenResponse(
      path: String,
      maybePayload: Option[String] = None,
      expectedStatus: Int = HttpStatus.SC_OK,
      queryHeaders: Seq[(String, String)] = Seq.empty,
      queryParams: Map[String, Any] = Map.empty,
      withAuth: Boolean = true
  ): CloseableHttpResponse = {
    val request = newPost(path, queryParams, withAuth)
    maybePayload.foreach(payload => setPlainTextPayload(request, payload))
    val response = doRequest(request, queryHeaders)
    val statusCode = response.getCode
    if (statusCode != expectedStatus) {
      response.close()
      throw new UnexpectedStatusCodeException(expectedStatus, statusCode)
    }
    response
  }

  def doJsonPostWithEmptyResponse(
      path: String,
      payload: Any,
      expectedStatus: Int = HttpStatus.SC_OK,
      queryHeaders: Seq[(String, String)] = Seq.empty,
      queryParams: Map[String, Any] = Map.empty,
      withAuth: Boolean = true
  ): Unit = {
    val post = newPost(path, queryParams, withAuth)
    doJsonRequestWithEmptyResponse(post, payload, expectedStatus, queryHeaders)
  }

  def doJsonPost[T](
      path: String,
      payload: Any,
      expectedStatus: Int = HttpStatus.SC_OK,
      queryHeaders: Seq[(String, String)] = Seq.empty,
      queryParams: Map[String, Any] = Map.empty,
      withAuth: Boolean = true
  )(
      implicit classTag: JavaTypeable[T]
  ): T = {
    val post = newPost(path, queryParams, withAuth)
    doJsonRequestWithResponse(post, payload, expectedStatus, queryHeaders)
  }

  /** PUT */

  def doJsonPut[T](
      path: String,
      payload: Any,
      expectedStatus: Int = HttpStatus.SC_OK,
      queryHeaders: Seq[(String, String)] = Seq.empty,
      queryParams: Map[String, Any] = Map.empty,
      withAuth: Boolean = true
  )(
      implicit classTag: JavaTypeable[T]
  ): T = {
    val req = newPut(path, queryParams, withAuth)
    doJsonRequestWithResponse(req, payload, expectedStatus, queryHeaders)
  }

  def doPlainTextPut[T](
      path: String,
      payload: String,
      expectedStatus: Int = HttpStatus.SC_OK,
      queryHeaders: Seq[(String, String)] = Seq.empty,
      queryParams: Map[String, Any] = Map.empty,
      withAuth: Boolean = true
  )(
      implicit classTag: JavaTypeable[T]
  ): T = {
    val request = newPut(path, queryParams, withAuth)
    setPlainTextPayload(request, payload)
    val response = doRequest(request, queryHeaders)
    val statusCode = response.getCode
    try {
      if (statusCode != expectedStatus) {
        throw new UnexpectedStatusCodeException(expectedStatus, statusCode)
      }
      val body = readBody(response).getOrElse(throw new InvalidBodyException())
      mapper.readValue[T](body)
    } finally {
      response.close()
    }
  }

  // TODO (msb): expectedStatus should be SC_NO_CONTENT for empty responses?
  def doPlainTextPutWithEmptyResponse(
      path: String,
      payload: String,
      expectedStatus: Int = HttpStatus.SC_OK,
      queryHeaders: Seq[(String, String)] = Seq.empty,
      queryParams: Map[String, Any] = Map.empty,
      withAuth: Boolean = true
  ): Unit = {
    val request = newPut(path, queryParams, withAuth)
    setPlainTextPayload(request, payload)
    val response = doRequest(request, queryHeaders)
    val statusCode = response.getCode
    try {
      if (statusCode != expectedStatus) {
        throw new UnexpectedStatusCodeException(expectedStatus, statusCode)
      }
      val maybeBody = readBody(response)
      if (maybeBody.isDefined) {
        throw new InvalidBodyException()
      }
    } finally {
      response.close()
    }
  }

  // TODO (msb): expectedStatus sohuld be SC_NO_CONTENT for empty responses?
  def doJsonPutWithEmptyResponse(
      path: String,
      payload: Any,
      expectedStatus: Int = HttpStatus.SC_OK,
      queryHeaders: Seq[(String, String)] = Seq.empty,
      queryParams: Map[String, Any] = Map.empty,
      withAuth: Boolean = true
  ): Unit = {
    val request = newPut(path, queryParams, withAuth)
    doJsonRequestWithEmptyResponse(request, payload, expectedStatus, queryHeaders)
  }

  /** DELETE */

  def doDeleteWithEmptyResponse(
      path: String,
      expectedStatus: Int = HttpStatus.SC_NO_CONTENT,
      queryHeaders: Seq[(String, String)] = Seq.empty,
      queryParams: Map[String, Any] = Map.empty,
      withAuth: Boolean = true
  ): Unit = {
    val request = newDelete(path, queryParams, withAuth)
    val response = doRequest(request, queryHeaders)
    val statusCode = response.getCode
    try {
      if (statusCode != expectedStatus) {
        throw new UnexpectedStatusCodeException(expectedStatus, statusCode)
      }
      val maybeBody = readBody(response)
      if (maybeBody.isDefined) {
        throw new InvalidBodyException()
      }
    } finally {
      response.close()
    }
  }

  /** Service Calls */

  def version(): String = {
    doGetWithPlainTextResponse("version", expectedStatus = HttpStatus.SC_OK, withAuth = false)
  }

  def health(): Unit = {
    doGetWithEmptyResponse("health", expectedStatus = HttpStatus.SC_NO_CONTENT, withAuth = false)
  }

  def close(): Unit = {
    logger.debug(s"[$name] Closing REST Client ($serverHttpAddress)")
    httpClient.close()
    connectionManager.close()
  }

}
