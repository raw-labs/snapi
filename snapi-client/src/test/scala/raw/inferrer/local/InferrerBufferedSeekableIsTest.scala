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

package raw.inferrer.local

import org.scalatest.funsuite.AnyFunSuite
import raw.inferrer.local.auto.InferrerBufferedSeekableIS
import raw.sources.bytestream.api.SeekableInputStream

import java.nio.ByteBuffer

class InferrerBufferedSeekableIsTest extends AnyFunSuite {

  class SeekableByteArrayIs(bytes: Array[Byte]) extends SeekableInputStream {
    var pos = 0;

    override def getPos: Long = pos

    override def read(): Int = {
      if (pos >= bytes.length) return -1
      val v = bytes(pos)
      pos += 1
      v & 0xff
    }

    override def seek(newPos: Long): Unit = {
      pos = newPos.toInt
    }

    override def readFully(bytes: Array[Byte]): Unit = ???

    override def readFully(bytes: Array[Byte], start: Int, len: Int): Unit = ???

    override def read(buf: ByteBuffer): Int = ???

    override def readFully(buf: ByteBuffer): Unit = ???
  }

  // test buffer which will be read over and over
  val testBytes = (0 until 256).map(x => (x & 0xff).toByte).toArray

  // will read from testBytes back and forth
  def testIs(bufferSize: Int): Unit = {

    val is = new InferrerBufferedSeekableIS(new SeekableByteArrayIs(testBytes), bufferSize)

    def assertRead(length: Int): Unit = {
      val bytes = new Array[Byte](length)
      val expectedLength = scala.math.min(length, testBytes.length - is.getPos.toInt)

      if (expectedLength == 0 && length > 0) {
        // we are past the original inputStream
        assert(is.read(bytes) == -1)
      } else {
        val expected = new Array[Byte](expectedLength)
        Array.copy(testBytes, is.getPos.toInt, expected, 0, expectedLength)
        assert(is.read(bytes) == expectedLength)
        // comparing both arrays
        (0 until expectedLength).foreach(i => assert(bytes(i) == expected(i)))
      }
    }

    is.seek(0)
    for (n <- 0 until testBytes.length) {
      assert(is.read().toByte == testBytes(n))
    }
    assert(is.read() == -1)

    is.seek(0)
    // reading the full thing
    assertRead(testBytes.length)
    assert(is.read() == -1)

    // full thing + 1
    is.seek(0)
    assertRead(testBytes.length + 1)
    assert(is.read() == -1)

    // reading the data in 3 chunks
    is.seek(0)
    (0 until 3).foreach(_ => assertRead((testBytes.length / 3) + 1))
    assert(is.read() == -1)

    // this will read part from the buffer part for the original IS
    is.seek(0)
    assertRead(bufferSize / 2)
    assertRead(bufferSize)

    // reading 0 bytes
    val bytes = new Array[Byte](10)
    is.seek(0)
    assert(is.read(bytes, 0, 0) == 0)

    // reading more than the available bytes
    assertRead(2 * testBytes.length)
    // this IS is empty
    assert(is.read() == -1)
    assert(is.read(bytes) == -1)
    // reading 0 bytes at the end
    assert(is.read(bytes, 0, 0) == 0)

  }

  test("data fits exactly in the buffer") {
    testIs(testBytes.length)
  }

  test("the data is bigger than the buffer") {
    testIs(testBytes.length / 2)
  }

  test("the data smaller than the buffer") {
    testIs(2 * testBytes.length)
  }

  test("no buffer") {
    testIs(0)
  }
}
