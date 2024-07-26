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

import raw.sources.bytestream.api.{ByteStreamException, ByteStreamLocation, SeekableInputStream}
import raw.utils.RawSettings

import java.io.InputStream
import java.net.{MalformedURLException, URI, URISyntaxException}
import java.nio.file.Path
import java.util.Base64
import scala.collection.mutable

final case class HttpResult(status: Int, is: InputStream, headers: Seq[(String, String)])

class HttpByteStreamLocation(config: HttpByteStreamConfig)(implicit settings: RawSettings) extends ByteStreamLocation {

  private val allHeaders = mutable.ArrayBuilder.make[(String, String)]
  allHeaders ++= config.headers

  // Add the Authentication Header
  config.basicAuth.foreach {
    case (username, password) => allHeaders += (
        (
          "Authorization",
          s"Basic ${Base64.getEncoder.encodeToString(s"$username:$password".getBytes)}"
        )
      )
  }

  private val httpClient =
    try {
      new HttpByteStreamClient(config.method, config.args, allHeaders.result, config.maybeBody, config.expectedStatus)(
        settings
      )
    } catch {
      case ex: MalformedURLException => throw new HttpByteStreamException(s"invalid HTTP URL: ${ex.getMessage}", ex)
      case ex: URISyntaxException => throw new HttpByteStreamException(s"invalid HTTP URL: ${ex.getMessage}", ex)
    }

  private val safeUrl: String = {
    new URI(config.url).normalize().toString
  }

  override def testAccess(): Unit = {
    // We are reading a single byte to ensure the connection is valid.
    // By reading a single byte, we are actually doing a connection and authentication.
    val is = httpClient.getInputStream(safeUrl)
    try {
      is.read()
    } finally {
      is.close()
    }
  }

  override protected def doGetInputStream(): InputStream = {
    httpClient.getInputStream(safeUrl)
  }

  override protected def doGetSeekableInputStream(): SeekableInputStream = {
    httpClient.getSeekableInputStream(safeUrl)
  }

  override def getLocalPath(): Path = {
    throw new ByteStreamException("currently not supported for HTTP get")
  }

  def getHttpResult(): HttpResult = {
    httpClient.getInputStreamWithStatus(safeUrl)
  }

}
