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

import com.rawlabs.utils.sources.bytestream.api.ByteStreamException

class FileSystemException(message: String, cause: Throwable = null)
    extends ByteStreamException(s"file system error: $message", cause)

class PathInvalidException(path: String, cause: Throwable) extends FileSystemException(s"path invalid: $path", cause)

class PathUnauthorizedException(path: String, cause: Throwable)
    extends FileSystemException(s"path not authorized: $path", cause)

class FileSystemUnavailableException(path: String, cause: Throwable)
    extends FileSystemException(s"file system unavailable: $path", cause)

class FileSystemTimeoutException(path: Option[String], cause: Throwable)
    extends FileSystemException(s"file system timeout${path.map(url => s": $url").getOrElse("")}", cause)

class PathAlreadyExistsException(path: String, cause: Throwable)
    extends FileSystemException(s"path already exists: $path", cause)

class PathNotFoundException(path: String, cause: Throwable) extends FileSystemException(s"path not found: $path", cause)

class HostNotFoundException(host: String, cause: Throwable) extends FileSystemException(s"host not found: $host", cause)

class NotAFileException(path: String, cause: Throwable = null)
    extends FileSystemException(s"path is not a file: $path", cause)

class UnexpectedFileSystemErrorException(message: String, cause: Throwable)
    extends FileSystemException(if (message.isEmpty) "unexpected error" else s"unexpected error: $message", cause) {
  def this(cause: Throwable) = this("", cause)
  def this(message: String) = this(message, null)
}
