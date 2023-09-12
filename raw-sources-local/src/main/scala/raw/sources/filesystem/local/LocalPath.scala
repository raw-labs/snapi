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

package raw.sources.filesystem.local

import java.io.InputStream
import java.nio.file.{Path, Paths}
import raw.sources.bytestream.SeekableInputStream
import raw.sources.filesystem._

// TODO (msb): Remove dependency on LocalFileSystem in the few places where it's still needed.
class LocalPath(pathName: String)
    extends FileSystemLocation {

  def this(path: Path) =
    this(path.toAbsolutePath.toString)

  override def rawUri: String = s"file:$pathName"

  protected def path: Path = Paths.get(pathName)

  override def testAccess(): Unit = {
    LocalFileSystem.testAccess(pathName)
  }

  override protected def doGetInputStream(): InputStream = {
    LocalFileSystem.getInputStream(pathName)
  }

  override protected def doGetSeekableInputStream(): SeekableInputStream = {
    LocalFileSystem.getSeekableInputStream(pathName)
  }

  override def getLocalPath(): Path = {
    Paths.get(pathName)
  }

  override def metadata(): FileSystemMetadata = {
    LocalFileSystem.metadata(pathName)
  }

  override protected def doLs(): Iterator[FileSystemLocation] = {
    LocalFileSystem.listContents(pathName).map(npath => new LocalPath(npath))
  }

  override protected def doLsWithMetadata(): Iterator[(FileSystemLocation, FileSystemMetadata)] = {
    LocalFileSystem.listContentsWithMetadata(pathName).map {
      case (npath, meta) => (new LocalPath(npath), meta)
    }
  }

}
