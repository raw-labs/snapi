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

package raw.sources

import java.io.File
import raw.RawTestSuite
import raw.sources.filesystem.local.LocalFileSystem
import raw.sources.filesystem.{FileSystem, TestFileSystems}

import java.nio.file.Files

class TestLocalFileSystem extends RawTestSuite with TestFileSystems {

  override val basePath: String = Files.createTempDirectory("test-local").toFile.getAbsolutePath

  override def newFileSystem: FileSystem = new LocalFileSystem

  override def writeTestFile(fs: FileSystem, parts: String*): Unit = {
    val f = new File(buildPath(fs, parts.mkString(fs.fileSeparator)))
    f.getParentFile.mkdirs()
    f.createNewFile()
  }

}