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

import com.dropbox.core.v2.DbxClientV2
import raw.sources.bytestream.api.{ByteStreamException, SeekableInputStream}
import raw.sources.filesystem.api._

import java.io.InputStream
import java.nio.file.Path

object BaseDropboxPath {
  val DROPBOX_CLIENT_ID = "raw.sources.dropbox.clientId"
}

class BaseDropboxPath(dbxClientV2: DbxClientV2, path: String) extends FileSystemLocation {

  private val cli = new DropboxFileSystem(dbxClientV2)

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

  override protected def doLs(): Iterator[FileSystemLocation] = {
    cli
      .listContents(path)
      .map(npath => new BaseDropboxPath(dbxClientV2, npath))
  }

  override protected def doLsWithMetadata(): Iterator[(FileSystemLocation, FileSystemMetadata)] = {
    cli.listContentsWithMetadata(path).map { case (npath, meta) => (new BaseDropboxPath(dbxClientV2, npath), meta) }
  }

}
