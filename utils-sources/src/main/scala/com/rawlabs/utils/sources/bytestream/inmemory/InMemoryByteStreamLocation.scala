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

package com.rawlabs.utils.sources.bytestream.inmemory

import com.rawlabs.utils.sources.api.LocationException
import com.rawlabs.utils.sources.bytestream.api.{
  ByteStreamLocation,
  DelegatingSeekableInputStream,
  GenericSkippableInputStream,
  SeekableInputStream
}

import java.io.{ByteArrayInputStream, InputStream}
import java.nio.file.Path

class InMemoryByteStreamLocation(val data: Array[Byte]) extends ByteStreamLocation {

  def this(data: String) = this(data.getBytes("UTF-8"))

  override protected def doGetInputStream(): InputStream = {
    new ByteArrayInputStream(data)
  }

  override protected def doGetSeekableInputStream(): SeekableInputStream = {
    val genSings = new GenericSkippableInputStream(() => doGetInputStream())
    new DelegatingSeekableInputStream(genSings) {
      override def getPos: Long = genSings.getPos
      override def seek(newPos: Long): Unit = genSings.seek(newPos)
    }
  }

  override def getLocalPath(): Path = throw new LocationException("not supported for in-memory location")

  override def testAccess(): Unit = {}

}
