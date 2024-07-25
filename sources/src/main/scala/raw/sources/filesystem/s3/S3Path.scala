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

package raw.sources.filesystem.s3

import raw.sources.bytestream.api.{ByteStreamException, SeekableInputStream}
import raw.sources.filesystem.api._
import raw.sources.api.OptionValue

import java.io.InputStream
import java.nio.file.Path

class S3Path(cli: S3FileSystem, protected val path: String, options: Map[String, OptionValue])
    extends FileSystemLocation {

  // TODO (msb): Shouldn't we sanitize path?
  override val rawUri: String = {
    val sep = if (path.startsWith("/")) "" else "/"
    s"s3://${cli.bucket}$sep$path"
  }

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
    throw new ByteStreamException("currently not supported for S3")
  }

  override def metadata(): FileSystemMetadata = {
    cli.metadata(path)
  }

  override protected def doLs(): Iterator[FileSystemLocation] = {
    cli
      .listContents(path)
      .map(npath => new S3Path(cli, npath, options))
  }

  override protected def doLsWithMetadata(): Iterator[(FileSystemLocation, FileSystemMetadata)] = {
    cli.listContentsWithMetadata(path).map { case (npath, meta) => (new S3Path(cli, npath, options), meta) }
  }

}
