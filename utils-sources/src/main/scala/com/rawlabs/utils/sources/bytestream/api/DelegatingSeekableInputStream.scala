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

import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * Implements read methods required by [[SeekableInputStream]] for generic input streams.
 *
 * Implementations must implement [[com.rawlabs.utils.sources.bytestream.DelegatingSeekableInputStream.getPos()]]
 * and [[com.rawlabs.utils.sources.bytestream.DelegatingSeekableInputStream.seek(long)]] and may optionally
 * implement other read methods to improve performance.
 */
private object DelegatingSeekableInputStream {
  // (ns) Copied from parquet-mr library
  @throws[IOException]
  private def readFully(f: InputStream, bytes: Array[Byte], start: Int, len: Int): Unit = {
    var offset = start
    var remaining = len
    while (remaining > 0) {
      val bytesRead = f.read(bytes, offset, remaining)
      if (bytesRead < 0) throw new EOFException("Reached the end of stream with " + remaining + " bytes left to read")
      remaining -= bytesRead
      offset += bytesRead
    }
  }

  @throws[IOException]
  private def readHeapBuffer(f: InputStream, buf: ByteBuffer): Int = {
    val bytesRead = f.read(buf.array, buf.arrayOffset + buf.position(), buf.remaining)
    if (bytesRead < 0) { // if this resulted in EOF, don't update position
      bytesRead
    } else {
      buf.position(buf.position() + bytesRead)
      bytesRead
    }
  }

  @throws[IOException]
  private def readFullyHeapBuffer(f: InputStream, buf: ByteBuffer): Unit = {
    readFully(f, buf.array, buf.arrayOffset + buf.position(), buf.remaining)
    buf.position(buf.limit)
  }

  @throws[IOException]
  private def readDirectBuffer(f: InputStream, buf: ByteBuffer, temp: Array[Byte]): Int = { // copy all the bytes that return immediately, stopping at the first
    // read that doesn't return a full buffer.
    var nextReadLength = Math.min(buf.remaining, temp.length)
    var totalBytesRead = 0
    var bytesRead = temp.length
    while (bytesRead == temp.length) {
      bytesRead = f.read(temp, 0, nextReadLength)
      if (bytesRead == temp.length) {
        buf.put(temp)
        totalBytesRead += bytesRead
        nextReadLength = Math.min(buf.remaining, temp.length)
      }
    }
    if (bytesRead < 0) { // return -1 if nothing was read
      if (totalBytesRead == 0) -1 else totalBytesRead
    } else { // copy the last partial buffer
      buf.put(temp, 0, bytesRead)
      totalBytesRead += bytesRead
      totalBytesRead
    }
  }

  @throws[IOException]
  private def readFullyDirectBuffer(f: InputStream, buf: ByteBuffer, temp: Array[Byte]): Unit = {
    var nextReadLength = Math.min(buf.remaining, temp.length)
    var bytesRead = 0
    while (nextReadLength > 0 && bytesRead >= 0) {
      bytesRead = f.read(temp, 0, nextReadLength)
      if (bytesRead >= 0) {
        buf.put(temp, 0, bytesRead)
        nextReadLength = Math.min(buf.remaining, temp.length)
      }
    }
    if (bytesRead < 0 && buf.remaining > 0) {
      throw new EOFException("Reached the end of stream with " + buf.remaining + " bytes left to read")
    }
  }
}

abstract class DelegatingSeekableInputStream(val stream: InputStream) extends SeekableInputStream {
  final private val COPY_BUFFER_SIZE = 8192
  final private val temp = new Array[Byte](COPY_BUFFER_SIZE)

  def getStream: InputStream = stream

  @throws[IOException]
  override def close(): Unit = {
    stream.close()
  }

  @throws[IOException]
  override def getPos: Long

  @throws[IOException]
  override def seek(newPos: Long): Unit

  @throws[IOException]
  override def read(): Int = stream.read

  @throws[IOException]
  override def readFully(bytes: Array[Byte]): Unit = {
    DelegatingSeekableInputStream.readFully(stream, bytes, 0, bytes.length)
  }

  @throws[IOException]
  override def readFully(bytes: Array[Byte], start: Int, len: Int): Unit = {
    DelegatingSeekableInputStream.readFully(stream, bytes, start, len)
  }

  @throws[IOException]
  override def read(b: Array[Byte], off: Int, len: Int): Int = {
    stream.read(b, off, len)
  }

  @throws[IOException]
  override def read(buf: ByteBuffer): Int = {
    if (buf.hasArray) {
      DelegatingSeekableInputStream.readHeapBuffer(stream, buf)
    } else {
      DelegatingSeekableInputStream.readDirectBuffer(stream, buf, temp)
    }
  }

  @throws[IOException]
  override def readFully(buf: ByteBuffer): Unit = {
    if (buf.hasArray) {
      DelegatingSeekableInputStream.readFullyHeapBuffer(stream, buf)
    } else {
      DelegatingSeekableInputStream.readFullyDirectBuffer(stream, buf, temp)
    }
  }
}
