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

package raw.inferrer.local.auto

import com.rawlabs.utils.sources.bytestream.api.SeekableInputStream

import java.nio.ByteBuffer

/**
 * Seekable input-stream to be used in the auto-inferrer
 * It wraps another SeekableInputStream buffering the first bytes and can only seek to 0.
 * Like this we avoid downloading the same S3 or http file all the time.
 * Attention: this works only with text formats (csv, json, xml ...)
 */
class InferrerBufferedSeekableIS(other: SeekableInputStream, bufferSize: Int = 8 * 1024 * 1024)
    extends SeekableInputStream {
  val buffer = new Array[Byte](bufferSize)
  var readPos = 0
  var pos = 0

  override def getPos: Long = {
    if (pos >= bufferSize) {
      other.getPos
    } else {
      pos
    }
  }

  override def seek(newPos: Long): Unit = synchronized {
    if (newPos != 0) throw new Exception(s"This input-stream can only seek to 0")
    other.seek(readPos)
    pos = 0
  }

  override def read(): Int = synchronized {
    if (pos >= bufferSize) {
      other.read()
    } else if (pos >= readPos) {
      val v = other.read()
      // if other ended do not put this value (-1) in the buffer
      if (v >= 0) {
        buffer(readPos) = (v & 0xff).toByte
        readPos += 1
        pos += 1
      }
      v
    } else {
      val v = buffer(pos)
      pos += 1
      // the bytes might be negative transforming them back to unsigned bytes
      (v & 0xff).toInt
    }
  }

  override def read(bytes: Array[Byte], offset: Int, length: Int): Int = synchronized {
    if (pos >= bufferSize) {
      // data is outside of the buffer
      other.read(bytes, offset, length)
    } else if (pos + length <= readPos) {
      // data is completely in the buffer
      Array.copy(buffer, pos, bytes, offset, length)
      pos += length
      length

    } else {
      //data is part in the buffer part in the stream
      // first reads into the buffer what still fits
      val bytesToBuffer = scala.math.min(pos + length - readPos, bufferSize - readPos)
      val bytesRead = other.read(buffer, readPos, bytesToBuffer)
      // if the other is at the end, result will be -1, so this check has to be made
      if (bytesRead >= 0) readPos += bytesRead

      val bytesFromBuffer = readPos - pos
      Array.copy(buffer, pos, bytes, offset, bytesFromBuffer)
      pos = readPos

      // other was not empty and we we are passed our buffer-size so read the rest from the other
      if (bytesRead >= 0 && readPos >= bufferSize) {
        val bytesFromOther = other.read(bytes, offset + bytesFromBuffer, length - bytesFromBuffer)
        // same -1 check basically
        val out =
          if (bytesFromOther >= 0) bytesFromBuffer + bytesFromOther
          else bytesFromBuffer
        out
      } else if (bytesFromBuffer > 0) {
        // the other was empty but there are still bytes in the buffer
        bytesFromBuffer
      } else {
        // the other was empty and nothing to read from buffer
        -1
      }
    }
  }

  override def close(): Unit = {
    other.close()
  }

  // The auto-inferrer only uses the basic input-stream methods, so these can stay unimplemented
  override def readFully(bytes: Array[Byte]): Unit = ???

  override def readFully(bytes: Array[Byte], start: Int, len: Int): Unit = ???

  override def read(buf: ByteBuffer): Int = ???

  override def readFully(buf: ByteBuffer): Unit = ???
}
