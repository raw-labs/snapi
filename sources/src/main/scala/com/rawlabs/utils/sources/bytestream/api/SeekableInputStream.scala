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
import java.nio.ByteBuffer

// Same as org.apache.parquet.IO.SeekableInputStream, see the docs on that class
trait SeekableInputStream extends InputStream {
  @throws[IOException]
  def getPos: Long

  @throws[IOException]
  def seek(newPos: Long): Unit

  @throws[IOException]
  def readFully(bytes: Array[Byte]): Unit

  @throws[IOException]
  def readFully(bytes: Array[Byte], start: Int, len: Int): Unit

  @throws[IOException]
  def read(buf: ByteBuffer): Int

  @throws[IOException]
  def readFully(buf: ByteBuffer): Unit

  @throws[IOException]
  override def close(): Unit = super.close()

}
