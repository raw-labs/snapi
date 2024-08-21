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

package com.rawlabs.utils.sources.bytestream.api

import java.io.{IOException, InputStream}

// This is very inefficient for seeking, as it reads the full stream until reaching the destination.
// But a better implementation needs to use specific information about the source, so this is used
// just as a last resort when we don't have anything better than tha generic input stream.
class GenericSkippableInputStream(inputStreamProvider: () => InputStream) extends InputStream {
  private var pos: Long = 0
  private var is: InputStream = inputStreamProvider()
  private var closed = false

  def getPos: Long = pos

  def seek(newPos: Long): Unit = synchronized {
    if (newPos < pos) {
      // Poor man's lseek (but maybe not so bad in practice?).
      // Close file and reopen it.
      if (closed) throw new IOException("Stream is closed")
      is.close()
      is = inputStreamProvider()
      pos = 0
      seek(newPos)
    } else {
      assert(newPos >= pos)
      var continue = true
      while (continue && pos < newPos) {
        val n = skip(newPos - pos) // pos is updated in skip
        continue = n > 0
      }
    }
  }

  override def read(): Int = synchronized {
    val c = is.read()
    if (c >= 0) {
      pos += 1
    }
    c
  }

  override def read(b: Array[Byte], off: Int, len: Int): Int = synchronized {
    val n = is.read(b, off, len)
    if (n > 0) {
      pos += n
    }
    n
  }

  override def available(): Int = synchronized {
    is.available()
  }

  override def markSupported(): Boolean = false

  override def mark(i: Int): Unit = {
    throw new IOException("Mark / reset is not supported")
  }

  override def reset(): Unit = {
    throw new IOException("Mark / reset is not supported")
  }

  override def skip(n: Long): Long = synchronized {
    val nread = is.skip(n)
    pos += nread
    nread
  }

  override def close(): Unit = synchronized {
    closed = true
    is.close()
  }
}
