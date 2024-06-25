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

///*
// * Copyright 2023 RAW Labs S.A.
// *
// * Use of this software is governed by the Business Source License
// * included in the file licenses/BSL.txt.
// *
// * As of the Change Date specified in that file, in accordance with
// * the Business Source License, use of this software will be governed
// * by the Apache License, Version 2.0, included in the file
// * licenses/APL.txt.
// */
//
//package raw.sources.bytestream.http
//
//import com.typesafe.scalalogging.StrictLogging
//import org.apache.hc.client5.http.classic.methods._
//import org.apache.hc.client5.http.config.RequestConfig
//import org.apache.hc.client5.http.impl.classic.{CloseableHttpClient, CloseableHttpResponse, HttpClients}
//import org.apache.hc.client5.http.impl.io.{
//  PoolingHttpClientConnectionManager,
//  PoolingHttpClientConnectionManagerBuilder
//}
//import org.apache.hc.core5.http.ContentType
//import org.apache.hc.core5.http.io.SocketConfig
//import org.apache.hc.core5.http.io.entity.ByteArrayEntity
//import org.apache.hc.core5.net.URIBuilder
//import org.apache.hc.core5.util.{TimeValue, Timeout}
//import raw.sources.api.LocationException
//import raw.sources.bytestream.api._
//import raw.utils._
//
//import java.io.{ByteArrayInputStream, IOException, InputStream, OutputStream}
//import java.net._
//import java.util.concurrent.{Executors, TimeUnit}
//
//final class HttpClientException(message: String, cause: Throwable = null)
//    extends ByteStreamException(s"http error: $message", cause)
//
//object HttpClientSettings {
//  val HTTP_READ_TIMEOUT = "raw.sources.bytestream.http.read-timeout"
//  val HTTP_CONNECT_TIMEOUT = "raw.sources.bytestream.http.connect-timeout"
//}
//
//object ApacheRuntimeHttpClient {
//
//  import HttpClientSettings._
//
//  private val HTTP_CONN_POOL_MAX_TOTAL = "raw.sources.bytestream.http.conn-pool-max-total"
//  private val HTTP_CONN_POOL_MAX_PER_ROUTE = "raw.sources.bytestream.http.conn-pool-max-per-route"
//
//  private var apacheHttpClient: ApacheHttpClientHolder = _
//  private val apacheHttpClientInitLock = new Object
//
//  protected[http] def buildApacheHttpClient(settings: RawSettings): ApacheHttpClientHolder = synchronized {
//    apacheHttpClientInitLock.synchronized {
//      // Global Apache Http Client for all instances of HttpClient
//      if (apacheHttpClient == null) {
//        val httpReadTimeout = settings.getDuration(HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
//        val socketConfig = SocketConfig
//          .custom()
//          .setSoTimeout(Timeout.ofMilliseconds(httpReadTimeout))
//          .build()
//
//        val pool = PoolingHttpClientConnectionManagerBuilder
//          .create()
//          .setDefaultSocketConfig(socketConfig)
//          .setMaxConnTotal(settings.getInt(HTTP_CONN_POOL_MAX_TOTAL))
//          .setMaxConnPerRoute(settings.getInt(HTTP_CONN_POOL_MAX_PER_ROUTE))
//          .build()
//
//        val client = HttpClients
//          .custom()
//          .setConnectionManager(pool)
//          // setConnectionManagerShared(true) is needed to avoid a bug where the connection manager was closed when an
//          // unexpected exception occurred.
//          .setConnectionManagerShared(true)
//          .build()
//        apacheHttpClient = new ApacheHttpClientHolder(client, pool)
//
//        Executors
//          .newSingleThreadScheduledExecutor(RawUtils.newThreadFactory("http-client-idle-connections-cleanup"))
//          .scheduleAtFixedRate(() => { apacheHttpClient.closeIdleConnections() }, 1, 1, TimeUnit.MINUTES)
//      }
//    }
//    apacheHttpClient
//  }
//}
//
//private[http] class ApacheHttpClientHolder(
//    val httpClient: CloseableHttpClient,
//    connPool: PoolingHttpClientConnectionManager
//) {
//
//  def closeIdleConnections(): Unit = {
//    connPool.closeIdle(TimeValue.ofMinutes(30))
//    connPool.closeExpired()
//  }
//}
//
//class ApacheRuntimeHttpClient(
//    method: String,
//    args: Array[(String, String)],
//    headers: Array[(String, String)],
//    body: Option[Array[Byte]]
//)(implicit settings: RawSettings)
//    extends InputStreamClient
//    with StrictLogging
//    with RuntimeHttpClient {
//
//  import HttpClientSettings._
//
//  private val apacheHttpClient = ApacheRuntimeHttpClient.buildApacheHttpClient(settings)
//
//  private val requestConfig = {
//    val httpConnectTimeout = settings.getDuration(HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
//    RequestConfig
//      .custom()
//      .setConnectTimeout(Timeout.ofMilliseconds(httpConnectTimeout.toInt))
//      .build()
//  }
//
//  // This method is called by our runtime in a read so expects the status code to be 200
//  override def getInputStream(url: String): InputStream = {
//    val (response, request) = openHTTPConnection(url)
//    try {
//      val responseCode = response.getCode
//      if (responseCode != HttpURLConnection.HTTP_OK) {
//        val is = response.getEntity.getContent
//        val body =
//          try {
//            readOutputBounded(response.getEntity.getContent)
//          } finally {
//            is.close()
//          }
//        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
//          throw new HttpClientException(s"authorization error accessing $url: ($responseCode)\n$body")
//        } else {
//          throw new HttpClientException(
//            s"could not read (HTTP ${method.toUpperCase}) from $url: ($responseCode)\n$body"
//          )
//        }
//      }
//      // The response is wrapped into a "custom input stream". The purpose of this input stream is to close the response
//      // when the inputstream is itself closed. Closing the response is required to ensure that the resources are released
//      // back to the underlying pool. Otherwise, when doing a close(), the close() would stay hanging until the connection
//      // times out.
//      new HttpInputStream(response, request)
//    } catch {
//      case ex: Exception =>
//        response.close()
//        ex match {
//          case ex: java.net.UnknownHostException => throw new HttpClientException(s"host not found for $url", ex)
//          case ex: IOException => throw new HttpClientException(s"unexpected error accessing $url", ex)
//          case _ => throw ex
//        }
//    }
//  }
//
//  // This method is called by our 'http_request' intrinsic so does not check for the status code.
//  def getInputStreamWithStatus(url: String): HttpResult = {
//    val (response, request) = openHTTPConnection(url)
//    try {
//      val responseCode = response.getCode
//
//      // In some cases, like a 204, there is no content, so creating an empty input-stream
//      val is = {
//        if (response.getEntity != null) new HttpInputStream(response, request)
//        else new ByteArrayInputStream(Array.empty)
//      }
//      val headers = response.getHeaders().map(x => x.getName -> x.getValue)
//      HttpResult(responseCode, is, headers)
//    } catch {
//      case ex: Exception =>
//        response.close()
//        ex match {
//          case ex: java.net.UnknownHostException => throw new HttpClientException(s"host not found for $url", ex)
//          case ex: IOException => throw new HttpClientException(s"unexpected error accessing $url", ex)
//          case _ => throw ex
//        }
//    }
//  }
//
//  override def getSeekableInputStream(url: String): SeekableInputStream = {
//    val skipableInputStream = new GenericSkippableInputStream(() => getInputStream(url))
//    new DelegatingSeekableInputStream(skipableInputStream) {
//      override def getPos: Long = skipableInputStream.getPos
//
//      override def seek(newPos: Long): Unit = skipableInputStream.seek(newPos)
//    }
//  }
//
//  private def openHTTPConnection(url: String): (CloseableHttpResponse, HttpUriRequestBase) = {
//    val request =
//      try {
//        val uriBuilder = new URIBuilder(url)
//        args.foreach(x => uriBuilder.addParameter(x._1, x._2))
//        val uri = uriBuilder.build()
//        method.toLowerCase match {
//          case "get" => new HttpGet(uri)
//          case "post" => new HttpPost(uri)
//          case "put" => new HttpPut(uri)
//          case "delete" => new HttpDelete(uri)
//          case "head" => new HttpHead(uri)
//          case "patch" => new HttpPatch(uri)
//          case "options" => new HttpOptions(uri)
//        }
//      } catch {
//        case ex: MalformedURLException => throw new LocationException(s"invalid HTTP URL: ${ex.getMessage}", ex)
//        case ex: URISyntaxException => throw new LocationException(s"invalid HTTP URL: ${ex.getMessage}", ex)
//      }
//
//    headers.foreach { case (k, v) => request.setHeader(k, v) }
//
//    body.foreach(s =>
//      request match {
//        case _: HttpPost | _: HttpPut | _: HttpPatch =>
//          request.setEntity(new ByteArrayEntity(s, ContentType.APPLICATION_OCTET_STREAM))
//        case _ => throw new LocationException("only 'POST', 'PUT' and 'PATCH' HTTP requests can have a body")
//      }
//    )
//    request.setConfig(requestConfig)
//    try {
//      (apacheHttpClient.httpClient.execute(request), request)
//    } catch {
//      case ex: java.net.UnknownHostException => throw new HttpClientException(s"host not found for $url", ex)
//      case ex: IOException => throw new HttpClientException(s"unexpected error accessing $url", ex)
//    }
//  }
//}
//
///**
// * InputStream that closes response on close, to ensure resources are released even if responses not consumed.
// * If we just closed the inputstream with is.close() - and not response.close() -, then it would hang until a timeout
// * is reached.
// */
//class HttpInputStream(response: CloseableHttpResponse, request: HttpUriRequestBase)
//    extends InputStream
//    with StrictLogging {
//
//  private val is = response.getEntity.getContent
//
//  override def readAllBytes(): Array[Byte] = super.readAllBytes()
//
//  override def readNBytes(b: Array[Byte], off: Int, len: Int): Int = is.readNBytes(b, off, len)
//
//  override def readNBytes(len: Int): Array[Byte] = is.readNBytes(len)
//
//  override def skipNBytes(n: Long): Unit = is.skipNBytes(n)
//
//  override def transferTo(out: OutputStream): Long = is.transferTo(out)
//
//  override def read(b: Array[Byte]): Int = is.read(b)
//
//  override def read(b: Array[Byte], off: Int, len: Int): Int = is.read(b, off, len)
//
//  override def read(): Int = is.read()
//
//  override def available(): Int = is.available()
//
//  override def mark(readlimit: Int): Unit = is.mark(readlimit)
//
//  override def markSupported(): Boolean = is.markSupported()
//
//  override def reset(): Unit = is.reset()
//
//  override def skip(n: Long): Long = is.skip(n)
//
//  override def close(): Unit = {
//    // (CTM) response.close() was hanging until downloading all the file
//    // So following example here:
//    // https://github.com/apache/httpcomponents-client/blob/5.1.x/httpclient5/src/test/java/org/apache/hc/client5/http/examples/ClientAbortMethod.java
//    // calling response.close() after request.cancel() was throwing a SocketException with a message "Socket closed"
//    // So assuming the request.cancel also closes the response
//    request.cancel()
//  }
//}
