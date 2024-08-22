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

package com.rawlabs.utils.sources.filesystem.s3

import com.rawlabs.utils.sources.bytestream.api.{ByteStreamException, SeekableInputStream}
import com.rawlabs.utils.sources.filesystem.api.{FileSystemLocation, FileSystemMetadata}
import com.rawlabs.utils.core.RawSettings

import java.io.InputStream
import java.nio.file.Path

class S3Path private (cli: S3FileSystem, val path: String)(implicit settings: RawSettings) extends FileSystemLocation {

  val bucket: String = cli.bucket

  val region: Option[String] = cli.maybeRegion

  val maybeAccessKey: Option[String] = cli.maybeAccessKey

  val maybeSecretKey: Option[String] = cli.maybeSecretKey

  def this(
      bucket: String,
      maybeRegion: Option[String],
      maybeAccessKey: Option[String],
      maybeSecretKey: Option[String],
      path: String
  )(implicit settings: RawSettings) = {
    this(
      new S3FileSystem(bucket, maybeRegion, maybeAccessKey, maybeSecretKey),
      path
    )
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
      .map(npath => new S3Path(cli, npath))
  }

  override protected def doLsWithMetadata(): Iterator[(FileSystemLocation, FileSystemMetadata)] = {
    cli.listContentsWithMetadata(path).map { case (npath, meta) => (new S3Path(cli, npath), meta) }
  }

}
