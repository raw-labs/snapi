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

package com.rawlabs.utils.sources.filesystem.dropbox

trait DropboxTestContext {

  val publicationsHjsonGzipDropboxDirectory = "dropbox:/publications/publications-hjson-gzip"
  val publicationsHjsonBzip2DropboxDirectory = "dropbox:/publications/publications-hjson-bzip2"
  val publicationsHjsonLz4DropboxDirectory = "dropbox:/publications/publications-hjson-lz4"
  val publicationsHjsonDeflateDropboxDirectory = "dropbox:/publications/publications-hjson-deflate"
  val publicationsHjsonSnappyDropboxDirectory = "dropbox:/publications/publications-hjson-snappy"
  val publicationsHjsonBzip2DropboxFile = "dropbox:/publications/publications.hjson.bz2"
  val publicationsHjsonGzDropboxFile = "dropbox:/publications/publications.hjson.gz"

}
