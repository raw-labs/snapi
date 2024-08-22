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

import java.io.InputStream

import com.rawlabs.utils.sources.bytestream.api.InputStreamClient

/**
 * Path conventions:
 * Only absolute paths allowed: e.g. "/dir/file".
 * Relative paths NOT allowed: e.g. "foo.csv" is invalid.
 * Wildcards allowed are *, ** and ?.
 * File Systems should implement paths as flexibly as possible, e.g.:
 * "/tmp", "/tmp/" and "/tmp/\*" should all be handled in equivalent ways.
 */
trait FileSystem extends InputStreamClient {

  // Used for testing (to generate the right test code).
  private[sources] def fileSeparator: String

  /**
   * Test access to the file system.
   * If any error occurs, an exception is thrown.
   */
  def testAccess(path: String): Unit

  def metadata(path: String): FileSystemMetadata

  def getInputStream(file: String): InputStream

  def listContents(path: String): Iterator[String]

  def listContentsWithMetadata(path: String): Iterator[(String, FileSystemMetadata)]

  def isDirectory(path: String): Boolean

  def hasGlob(path: String): Boolean

}
