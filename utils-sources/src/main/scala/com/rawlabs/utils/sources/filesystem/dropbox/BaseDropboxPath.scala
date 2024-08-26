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

import com.dropbox.core.v2.DbxClientV2
import com.rawlabs.utils.sources.bytestream.api.{ByteStreamException, SeekableInputStream}
import com.rawlabs.utils.sources.filesystem.api._

import java.io.InputStream
import java.nio.file.Path

object BaseDropboxPath {
  val DROPBOX_CLIENT_ID = "raw.utils.sources.dropbox.clientId"
}

abstract class BaseDropboxPath(dbxClientV2: DbxClientV2, path: String) extends FileSystemLocation {

  protected val cli = new DropboxFileSystem(dbxClientV2)

  override def testAccess(): Unit = {
    cli.testAccess(path)
  }

  override protected def doGetInputStream(): InputStream = {
    cli.getInputStream(path)
  }

  override protected def doGetSeekableInputStream(): SeekableInputStream = {
    cli.getSeekableInputStream(path)
  }

  override def getLocalPath(): Path = {
    throw new ByteStreamException("currently not supported for Dropbox")
  }

  override def metadata(): FileSystemMetadata = {
    cli.metadata(path)
  }

}
