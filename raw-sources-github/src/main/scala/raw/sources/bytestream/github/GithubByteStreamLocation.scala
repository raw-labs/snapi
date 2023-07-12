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

package raw.sources.bytestream.github

import java.io.InputStream
import java.nio.file.Path
import com.typesafe.scalalogging.StrictLogging
import raw.sources.{CacheStrategy, RetryStrategy}
import raw.sources.bytestream.http.HttpByteStreamLocation
import raw.sources.bytestream.{ByteStreamLocation, SeekableInputStream}

class GithubByteStreamLocation(
    http: HttpByteStreamLocation,
    url: String,
    override val cacheStrategy: CacheStrategy,
    override val retryStrategy: RetryStrategy
) extends ByteStreamLocation
    with StrictLogging {

  override protected def doGetInputStream(): InputStream = http.getInputStream

  override protected def doGetSeekableInputStream(): SeekableInputStream = http.getSeekableInputStream

  override def getLocalPath(): Path = http.getLocalPath()

  override def rawUri: String = url

  override def testAccess(): Unit = {
    http.testAccess()
  }
}
