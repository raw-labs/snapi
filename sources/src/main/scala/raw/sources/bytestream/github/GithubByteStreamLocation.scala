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
import raw.sources.bytestream.http.HttpByteStreamLocation
import raw.sources.bytestream.api.{ByteStreamLocation, SeekableInputStream}

class GithubByteStreamLocation(
    http: HttpByteStreamLocation,
    username: String,
    repo: String,
    file: String,
    branch: String
) extends ByteStreamLocation
    with StrictLogging {

  override def rawUri: String = s"github://$username/$repo/$file[$branch]"

  override protected def doGetInputStream(): InputStream = http.getInputStream

  override protected def doGetSeekableInputStream(): SeekableInputStream = http.getSeekableInputStream

  override def getLocalPath(): Path = http.getLocalPath()

  override def testAccess(): Unit = {
    http.testAccess()
  }
}
