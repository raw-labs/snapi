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

package com.rawlabs.utils.sources.filesystem.api

import com.typesafe.scalalogging.StrictLogging
import org.scalatest.BeforeAndAfterAll
import com.rawlabs.utils.core.{RawTestSuite, SettingsTestContext}

trait TestFileSystems extends SettingsTestContext with BeforeAndAfterAll with StrictLogging {
  this: RawTestSuite =>

  def basePath: String

  def buildPath(fs: FileSystem, relativePath: String): String = basePath + fs.fileSeparator + relativePath

  def newFileSystem: FileSystem

  def writeTestFile(fs: FileSystem, parts: String*): Unit

  private var fileSeparator: String = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    val fs = newFileSystem
    fileSeparator = fs.fileSeparator
    writeTestFile(fs, "file1.csv")
    writeTestFile(fs, "file2.csv")
    writeTestFile(fs, "file1.json")
    writeTestFile(fs, "jsons", "file1.json")
    writeTestFile(fs, "jsons", "file2.json")
    writeTestFile(fs, "csvs", "01", "data1.csv")
    writeTestFile(fs, "csvs", "01", "data2.csv")
    writeTestFile(fs, "csvs", "02", "data1.csv")
  }

  // Some implementations have hidden files that must be filtered for comparisons to work properly.
  // This method allows thsee implementations to override it and have the comparison still perform as expected.
  protected def filterResults(path: String): Boolean = true

  private def runListTest(path: String, expectedResults: Int) = {
    val fs = newFileSystem
    val fullPath = buildPath(fs, path)
    val results = fs.listContents(fullPath).filter(filterResults).toList
    logger.debug("Result: " + results)
    assert(results.size == expectedResults)
  }

  test("list /file1.csv")(_ => runListTest("file1.csv", 1))

  test("list /*")(_ => runListTest("*", 5))

  test("list /*csv")(_ => runListTest("*csv", 2))

  test("list /*.csv")(_ => runListTest("*.csv", 2))

  test("list /*.json")(_ => runListTest("*.json", 1))

  test("list /jsons")(_ => runListTest("jsons", 2))

  test("list /jsons/")(_ => runListTest("jsons" + fileSeparator, 2))

  test("list /jsons/*")(_ => runListTest("jsons" + fileSeparator + "*", 2))

  test("list /csvs")(_ => runListTest("csvs", 2))

  test("list /csvs/")(_ => runListTest("csvs" + fileSeparator, 2))

  test("list /csvs/*")(_ => runListTest("csvs" + fileSeparator + "*", 2))

  test("list /**")(_ => runListTest("**", 13))

  test("list /**/*json")(_ => runListTest("**" + fileSeparator + "*json", 3))

  test("list /**/*.json")(_ => runListTest("**" + fileSeparator + "*.json", 3))

  test("list /csvs/**")(_ => runListTest("csvs" + fileSeparator + "**", 6))

  test("list /csvs/**/*.csv") { _ =>
    runListTest("csvs" + fileSeparator + "**" + fileSeparator + "*.csv", 3)
  // TODO: Add ? version
  }

  test("list /") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "*")
    logger.debug("Result: " + fs.listContents(basePath).toList)
    assert(fs.listContents(basePath).toSet === fs.listContents(p).toSet)
  }

  test("list with metadata /") { _ =>
    val fs = newFileSystem
    logger.debug("Result: " + fs.listContentsWithMetadata(basePath).toList)
    assert(fs.listContentsWithMetadata(basePath).map(_._1).count(filterResults) === 5)
    assert(fs.listContentsWithMetadata(basePath).count {
      case (f, metadata) => filterResults(f) && metadata.isInstanceOf[DirectoryMetadata]
    } === 2)
  }

  test("list with metadata /*") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "*")
    logger.debug("Result: " + fs.listContentsWithMetadata(p).toList)
    assert(fs.listContentsWithMetadata(p).map(_._1).toSet === fs.listContentsWithMetadata(basePath).map(_._1).toSet)
  }

  test("testAccess /") { _ =>
    val fs = newFileSystem
    fs.testAccess(basePath)
  }

  test("testAccess /file1.csv") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "file1.csv")
    fs.testAccess(p)
  }

  // S3 had a bug where files were "found" even though only "prefix" matched
  test("testAccess /file1.cs") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "file1.cs")
    intercept[FileSystemException] {
      fs.testAccess(p)
    }
  }

  // This is a variant of the S3 bug described above that would accidentally match two files
  test("testAccess /file") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "file")
    intercept[FileSystemException] {
      fs.testAccess(p)
    }
  }

  test("testAccess /jsons") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "jsons")
    fs.testAccess(p)
  }

  test("testAccess /jsons/") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "jsons" + fs.fileSeparator)
    fs.testAccess(p)
  }

  test("testAccess /jsons/file1.json") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "jsons" + fs.fileSeparator + "file1.json")
    fs.testAccess(p)
  }

  test("testAccess /doesnt-exist") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "doesnt-exist")
    intercept[FileSystemException] {
      fs.testAccess(p)
    }
  }

  test("isDirectory /") { _ =>
    val fs = newFileSystem
    assert(fs.isDirectory(basePath))
  }

  test("isDirectory /csvs") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "csvs")
    assert(fs.isDirectory(p))
  }

  test("isDirectory /csvs/") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "csvs" + fs.fileSeparator)
    assert(fs.isDirectory(p))
  }

  test("isDirectory /csvs/01") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "csvs" + fs.fileSeparator + "01")
    assert(fs.isDirectory(p))
  }

  test("isDirectory /csvs/01/") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "csvs" + fs.fileSeparator + "01" + fs.fileSeparator)
    assert(fs.isDirectory(p))
  }

  test("isDirectory /jsons/file1.json") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "jsons" + fs.fileSeparator + "file1.json")
    assert(!fs.isDirectory(p))
  }

  test("isDirectory /bad") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "bad")
    intercept[PathNotFoundException] {
      fs.isDirectory(p)
    }
  }

  test("getInputStream on directory") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "jsons")
    intercept[NotAFileException] {
      fs.getInputStream(p)
    }
  }

  test("getInputStream on file") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "jsons" + fs.fileSeparator + "file1.json")
    val is = fs.getInputStream(p)
    try {
      // most of files created are empty, this mostly checking that no exception is being thrown
      assert(is.available() >= 0)
    } finally {
      is.close()
    }
  }

  test("getSeekableInputStream on file") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "jsons" + fs.fileSeparator + "file1.json")
    val is = fs.getSeekableInputStream(p)
    try {
      // most of files created are empty, this mostly checking that no exception is being thrown
      assert(is.available() >= 0)
    } finally {
      is.close()
    }
  }

  test("test access does_not_exist") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "does_not_exist.csv")
    intercept[PathNotFoundException] {
      try {
        fs.testAccess(p)
      } catch {
        case e: NotImplementedError =>
          logger.warn(s"file system ${this.getClass} with testAccess not implemented")
          throw new PathNotFoundException(p, e)
      }
    }
  }

  test("test access folder") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "jsons")
    fs.testAccess(p)
  }

  test("test access file") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "jsons" + fileSeparator + "file1.json")
    fs.testAccess(p)
  }

  test("get metadata file") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "file1.csv")
    val m = fs.metadata(p)
    assert(m.isInstanceOf[FileMetadata])
  }

  test("get metadata folder") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "jsons")
    val m = fs.metadata(p)

    assert(m.isInstanceOf[DirectoryMetadata])
  }

  test("getInputStream on non existent") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "does_not_exist")
    intercept[PathNotFoundException] {
      fs.getInputStream(p)
    }
  }

  test("exception get metadata  on wildcard") { _ =>
    val fs = newFileSystem
    val p = buildPath(fs, "jsons" + fs.fileSeparator + "*")
    intercept[FileSystemException] {
      fs.metadata(p)
    }
  }

}
