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
import raw.sources.bytestream.SeekableInputStream

import java.io.InputStream
import java.nio.charset.StandardCharsets

trait RuntimeHttpClient {

  // This method is called by our runtime in a read so expects the status code to be 200
  def getInputStream(url: String): InputStream

  // This method is called by our 'http_request' intrinsic so does not check for the status code.
  def getInputStreamWithStatus(url: String): HttpResult

  def getSeekableInputStream(url: String): SeekableInputStream

  private val ErrorResponseOutputMaxSize = 2048

  protected def readOutputBounded(is: InputStream): String = {
    val data = is.readNBytes(ErrorResponseOutputMaxSize)
    val result = new String(data, StandardCharsets.UTF_8)
    if (data.length == ErrorResponseOutputMaxSize && is.read() != -1) {
      result + "..."
    } else {
      result
    }
  }
}
