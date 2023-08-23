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

package raw.sources.bytestream

import com.typesafe.scalalogging.StrictLogging
import raw.sources.LocationDescription
import raw.utils._

import java.io.{ByteArrayOutputStream, InputStream}

/**
 * Wrapper that caches the data returned by an input stream and when end of stream is reached,
 * caches the contents.
 *
 * @param source The source input stream
 * @param locationDescription Description of the location, used as a cache key.
 * @param cacheManager The cache manager that is used to store the contents of the stream at the end.
 */
private class ByteStreamCacheInputStream(
    source: InputStream,
    locationDescription: LocationDescription,
    cacheManager: EhCacheByteStreamCache
) extends InputStream
    with StrictLogging {

  // Caches the data while it is being retrieved from the source input stream
  private var buffer = new ByteArrayOutputStream()
  // Did we read from the source stream more than the maximum stream size that we are allowed to cache?
  private var exceededMaxSize = false
  // Did we reach the end of the source input stream?
  private var reachedEOF = false

  override def read(): Int = {
    val ch = source.read()
    if (ch == -1) {
      // End of the source stream
      endOfStreamReached()
    } else {
      // Tries to cache the data read from the source stream
      if (canWriteNMoreBytes(1)) {
        buffer.write(ch)
      }
    }
    ch
  }

  override def read(b: Array[Byte], off: Int, len: Int): Int = {
    val bytesRead = source.read(b, off, len)
    if (bytesRead == -1) {
      endOfStreamReached()
    } else {
      if (canWriteNMoreBytes(bytesRead)) {
        buffer.write(b, off, bytesRead)
      }
    }
    bytesRead
  }

  override def close(): Unit = {
    if (!exceededMaxSize) {
      withSuppressNonFatalException {
        // Try to read until EOF to trigger the caching. We need to be sure that the reader using this stream has read
        // everything before we cache, we do not cache partially read streams.
        // If there are only a few characters left, like a carriage return and empty lines at the end of the file,
        // the first read might return those characters. So read twice, the second read should return -1 if we are close to EOF
        val buffer = new Array[Byte](32)
        val bytesRead = read(buffer)
        if (bytesRead != -1) {
          read(buffer)
        }
      }
    }
    source.close()
  }

  private def endOfStreamReached(): Unit = {
    if (!reachedEOF) {
      reachedEOF = true
      if (exceededMaxSize) {
        logger.debug(s"EOF reached. exceeded max size.")
      } else {
        cacheManager.cacheCompleteStream(locationDescription, buffer)
      }
    }
  }

  private def canWriteNMoreBytes(bytesToWrite: Int): Boolean = {
    if (exceededMaxSize) {
      false
    } else {
      logger.trace(s"Trying to write $bytesToWrite. Buffer: ${buffer.size()}/${cacheManager.maxEntrySizeBytes}")
      if (buffer.size() + bytesToWrite <= cacheManager.maxEntrySizeBytes) {
        // There is still space for writing `bytesToWrite` bytes
        true
      } else {
        logger.debug(
          s"Exceeded maximum buffer size ${cacheManager.maxEntrySizeBytes}. Giving up on caching this input stream."
        )
        // Not enough space to write the next block of data. Clear any temporarily cached data and stop trying to cache
        exceededMaxSize = true
        buffer = null // Release the buffer, to quickly free up the memory
        false
      }
    }
  }
}
