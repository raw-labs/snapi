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

import com.rawlabs.utils.sources.bytestream.api.{ByteStreamException, ByteStreamLocation, SeekableInputStream}
import com.rawlabs.utils.core.RawSettings

import java.io.InputStream
import java.net.{HttpURLConnection, MalformedURLException, URI, URISyntaxException}
import java.nio.file.Path

final case class HttpResult(status: Int, is: InputStream, headers: Seq[(String, String)])

class HttpByteStreamLocation(
    val url: String,
    val method: String = "GET",
    val args: Array[(String, String)] = Array.empty,
    val headers: Array[(String, String)] = Array.empty,
    val maybeBody: Option[Array[Byte]] = None,
    val expectedStatus: Array[Int] = Array(
      HttpURLConnection.HTTP_OK,
      HttpURLConnection.HTTP_ACCEPTED,
      HttpURLConnection.HTTP_CREATED,
      HttpURLConnection.HTTP_PARTIAL
    )
)(implicit settings: RawSettings)
    extends ByteStreamLocation {

  private val httpClient =
    try {
      new HttpByteStreamClient(method, args, headers, maybeBody, expectedStatus)(
        settings
      )
    } catch {
      case ex: MalformedURLException => throw new HttpByteStreamException(s"invalid HTTP URL: ${ex.getMessage}", ex)
      case ex: URISyntaxException => throw new HttpByteStreamException(s"invalid HTTP URL: ${ex.getMessage}", ex)
    }

  private val safeUrl: String = {
    new URI(url).normalize().toString
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
