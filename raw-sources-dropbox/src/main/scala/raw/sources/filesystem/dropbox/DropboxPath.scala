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

import raw.sources._
import raw.sources.bytestream.{ByteStreamCache, ByteStreamException, SeekableInputStream}
import raw.sources.filesystem._

import java.io.InputStream
import java.nio.file.Path

class DropboxPath(
    cli: DropboxFileSystem,
    path: String,
    override val cacheStrategy: CacheStrategy,
    override val retryStrategy: RetryStrategy,
    locationDescription: LocationDescription,
    cacheManager: ByteStreamCache
) extends FileSystemLocation {

  override def rawUri: String = s"dropbox://${cli.name}$path"

  override def sparkUri: String = throw new ByteStreamException("spark url not supported for Dropbox")

  override def testAccess(): Unit = {
    cli.testAccess(path)
  }

  override protected def doGetInputStream(): InputStream = {
    cacheManager.getInputStream(path, locationDescription) {
      cli.getInputStream(path)
    }
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
      .map(npath => new DropboxPath(cli, npath, cacheStrategy, retryStrategy, locationDescription, cacheManager))
  }

  override protected def doLsWithMetadata(): Iterator[(FileSystemLocation, FileSystemMetadata)] = {
    cli.listContentsWithMetadata(path).map {
      case (npath, meta) =>
        (new DropboxPath(cli, npath, cacheStrategy, retryStrategy, locationDescription, cacheManager), meta)
    }
  }

}
