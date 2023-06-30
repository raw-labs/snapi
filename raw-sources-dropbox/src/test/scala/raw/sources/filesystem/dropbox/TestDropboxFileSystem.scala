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

package raw.sources.filesystem.dropbox

import raw.RawTestSuite
import raw.creds.DropboxTestCreds
import raw.sources.filesystem.{FileSystem, TestFileSystems}

import java.io.ByteArrayInputStream
import scala.util.Try

class TestDropboxFileSystem extends RawTestSuite with TestFileSystems with DropboxTestCreds {

  override val basePath = "/dropbox-test"

  val dropboxClient = DropboxFileSystem.buildDbxClientV2(bearerToken)

  override def newFileSystem: FileSystem = new DropboxFileSystem(bearerToken)

  override def writeTestFile(fs: FileSystem, parts: String*): Unit = {
    Try(dropboxClient.files().createFolderV2(basePath))
    val in = new ByteArrayInputStream(Array[Byte]())
    Try(dropboxClient.files().uploadBuilder(buildPath(fs, parts.mkString(fs.fileSeparator))).uploadAndFinish(in))
  }

}
