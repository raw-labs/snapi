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

package raw.sources.bytestream.in_memory

import raw.sources._
import raw.sources.bytestream.{
  ByteStreamLocation,
  DelegatingSeekableInputStream,
  GenericSkippableInputStream,
  SeekableInputStream
}

import java.io.{ByteArrayInputStream, InputStream}
import java.nio.file.Path

object InMemoryByteStreamLocation {
  val schema = "in-memory"
  val schemaWithColon = s"$schema:"
  val codeDataKey = "code-data"
}

class InMemoryByteStreamLocation(
    locationDescription: LocationDescription
) extends ByteStreamLocation {

  override protected def doGetInputStream(): InputStream = {
    assert(locationDescription.settings.contains(LocationSettingKey(InMemoryByteStreamLocation.codeDataKey)))

    val LocationBinarySetting(v) =
      locationDescription.settings(LocationSettingKey(InMemoryByteStreamLocation.codeDataKey))

    new ByteArrayInputStream(v.toArray)
  }

  override protected def doGetSeekableInputStream(): SeekableInputStream = {
    val genSings = new GenericSkippableInputStream(() => doGetInputStream())
    new DelegatingSeekableInputStream(genSings) {
      override def getPos: Long = genSings.getPos
      override def seek(newPos: Long): Unit = genSings.seek(newPos)
    }
  }

  override def getLocalPath(): Path = throw new AssertionError("Calling path on in memory location")

  override def cacheStrategy: CacheStrategy = locationDescription.cacheStrategy

  override def retryStrategy: RetryStrategy = locationDescription.retryStrategy

  override def rawUri: String = InMemoryByteStreamLocation.schemaWithColon

  override def testAccess(): Unit = {}
}
