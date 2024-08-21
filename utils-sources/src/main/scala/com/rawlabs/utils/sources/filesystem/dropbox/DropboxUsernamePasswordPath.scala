/*
 * Copyright 2024 RAW Labs S.A.
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

import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import com.rawlabs.utils.sources.filesystem.api.{FileSystemLocation, FileSystemMetadata}
import com.rawlabs.utils.core.RawSettings

class DropboxUsernamePasswordPath(
    val username: String,
    val password: String,
    val path: String,
    dbxClientV2: DbxClientV2
) extends BaseDropboxPath(dbxClientV2, path) {

  def this(username: String, password: String, path: String)(
      implicit settings: RawSettings
  ) = this(
    username,
    password,
    path,
    new DbxClientV2(
      DbxRequestConfig.newBuilder(settings.getString(BaseDropboxPath.DROPBOX_CLIENT_ID)).build(),
      new DbxCredential(null, null, null, username, password)
    )
  )

  override protected def doLs(): Iterator[FileSystemLocation] = {
    cli
      .listContents(path)
      .map(npath => new DropboxUsernamePasswordPath(username, password, npath, dbxClientV2))
  }

  override protected def doLsWithMetadata(): Iterator[(FileSystemLocation, FileSystemMetadata)] = {
    cli.listContentsWithMetadata(path).map {
      case (npath, meta) => (new DropboxUsernamePasswordPath(username, password, npath, dbxClientV2), meta)
    }
  }

}
