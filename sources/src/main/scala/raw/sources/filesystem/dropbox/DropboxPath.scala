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

import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import raw.sources.bytestream.api.{ByteStreamException, SeekableInputStream}
import raw.sources.filesystem.api._
import raw.utils.RawSettings

import java.io.InputStream
import java.nio.file.Path

object DropboxPath {
  val DROPBOX_CLIENT_ID = "raw.sources.dropbox.clientId"
}

class DropboxPath private (dbxClientV2: DbxClientV2, path: String)(implicit settings: RawSettings)
    extends FileSystemLocation {

  private val cli = new DropboxFileSystem(dbxClientV2)

  def this(config: DropboxAccessTokenConfig)(implicit settings: RawSettings) = this(
    new DbxClientV2(
      DbxRequestConfig.newBuilder(settings.getString(DropboxPath.DROPBOX_CLIENT_ID)).build(),
      new DbxCredential(config.accessToken)
    ),
    config.path
  )

  def this(config: DropboxUsernamePasswordConfig)(implicit settings: RawSettings) = this(
    new DbxClientV2(
      DbxRequestConfig.newBuilder(settings.getString(DropboxPath.DROPBOX_CLIENT_ID)).build(),
      new DbxCredential(null, null, null, config.username, config.password)
    ),
    config.path
  )

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
      .map(npath => new DropboxPath(dbxClientV2, npath))
  }

  override protected def doLsWithMetadata(): Iterator[(FileSystemLocation, FileSystemMetadata)] = {
    cli.listContentsWithMetadata(path).map { case (npath, meta) => (new DropboxPath(dbxClientV2, npath), meta) }
  }

}
