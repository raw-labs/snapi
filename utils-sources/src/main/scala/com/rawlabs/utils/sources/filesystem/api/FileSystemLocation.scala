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

import com.rawlabs.utils.sources.bytestream.api.ByteStreamLocation

trait FileSystemLocation extends ByteStreamLocation {

  def metadata(): FileSystemMetadata

  // This call uses the retry mechanism.
  final def ls(): Iterator[FileSystemLocation] = {
    doLs()
  }

  protected def doLs(): Iterator[FileSystemLocation]

  // (msb): Instead of this call, we could use listFiles and then for each entry do getMetadata.
  //        To be efficient, this would require the builders to retrieve and store the metadata in memory as
  //        part of the listFiles call.
  // This call uses the retry mechanism.
  final def lsWithMetadata(): Iterator[(FileSystemLocation, FileSystemMetadata)] = {
    doLsWithMetadata()
  }

  protected def doLsWithMetadata(): Iterator[(FileSystemLocation, FileSystemMetadata)]

}
