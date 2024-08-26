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

/**
 * BaseFileSystem provides default implementations for some FileSystem methods.
 * If a new file system is implemented in terms of BaseFileSystem, then the implementor needs to implement fewer methods.
 * However, the performance will likely be worse: e.g. `listContentsWithMetadata` calls `listContents` and then for each
 * calls `metadata`. A native implementation that does both simultaneously, if available, would be faster.
 */
trait BaseFileSystem extends FileSystem with StrictLogging {

  override def listContentsWithMetadata(path: String): Iterator[(String, FileSystemMetadata)] =
    listContents(path).map(f => (f, metadata(f)))

  override def hasGlob(path: String): Boolean = path.contains("*") || path.contains("?")

  override def isDirectory(path: String): Boolean = metadata(path).isInstanceOf[DirectoryMetadata]

}
