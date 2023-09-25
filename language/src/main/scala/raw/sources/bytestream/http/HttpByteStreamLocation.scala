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
import raw.sources.api.LocationDescription

import java.io.InputStream
import java.net.URI
import java.nio.file.Path

final case class HttpResult(status: Int, is: InputStream, headers: Seq[(String, String)])

class HttpByteStreamLocation(
    cli: RuntimeHttpClient,
    url: String,
    locationDescription: LocationDescription
) extends ByteStreamLocation {

  override val rawUri: String = {
    // normalizing the URL removes consecutive occurrences of /, which would make the Apache HTTP client fail. This is a
    // convenience feature so that URLs like http://host:port//path will work correctly, mainly useful for URLs generated by
    // the concatenation of several segments.
    new URI(url).normalize().toString
  }

  override def testAccess(): Unit = {
    // We are reading a single byte to ensure the connection is valid.
    // By reading a single byte, we are actually doing a connection and authentication
    // this was done to fix a bug where http sources where not retrying
    val is = cli.getInputStream(rawUri)
    try {
      is.read()
    } finally {
      is.close()
    }
  }

  override protected def doGetInputStream(): InputStream = {
    cli.getInputStream(rawUri)
  }

  override protected def doGetSeekableInputStream(): SeekableInputStream = {
    cli.getSeekableInputStream(rawUri)
  }

  override def getLocalPath(): Path = {
    throw new ByteStreamException("currently not supported for HTTP get")
  }

  def getHttpResult(): HttpResult = {
    cli.getInputStreamWithStatus(rawUri)
  }
}
