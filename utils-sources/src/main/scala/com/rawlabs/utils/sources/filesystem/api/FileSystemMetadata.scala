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

import java.time.Instant

sealed trait FileSystemMetadata

final case class DirectoryMetadata(modifiedInstant: Option[Instant]) extends FileSystemMetadata

final case class FileBlock(hosts: Array[String], offset: Long, length: Long)

final case class FileMetadata(modifiedInstant: Option[Instant], size: Option[Long], blocks: Array[FileBlock])
    extends FileSystemMetadata
