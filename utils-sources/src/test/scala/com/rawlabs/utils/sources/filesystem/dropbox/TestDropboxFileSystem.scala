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

package com.rawlabs.utils.sources.filesystem.dropbox

import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import com.rawlabs.utils.core.RawTestSuite
import com.rawlabs.utils.sources.filesystem.api.{FileSystem, TestFileSystems}

import java.io.ByteArrayInputStream
import scala.util.Try

class TestDropboxFileSystem extends RawTestSuite with TestFileSystems {

  override val basePath = "/dropbox-test"

  val dropboxClient = new DropboxFileSystem(
    new DbxClientV2(
      DbxRequestConfig.newBuilder(settings.getString(BaseDropboxPath.DROPBOX_CLIENT_ID)).build(),
      new DbxCredential(sys.env("RAW_DROPBOX_TEST_LONG_LIVED_ACCESS_TOKEN"))
    )
  )

  override def newFileSystem: FileSystem = dropboxClient

  override def writeTestFile(fs: FileSystem, parts: String*): Unit = {
    Try(dropboxClient.client.files().createFolderV2(basePath))
    val in = new ByteArrayInputStream(Array[Byte]())
    Try(dropboxClient.client.files().uploadBuilder(buildPath(fs, parts.mkString(fs.fileSeparator))).uploadAndFinish(in))
  }

}
