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

package raw.sources.filesystem.dropbox

import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import raw.utils.RawSettings

class DropboxAccessTokenPath(val accessToken: String, val path: String)(implicit settings: RawSettings)
    extends BaseDropboxPath(
      new DbxClientV2(
        DbxRequestConfig.newBuilder(settings.getString(BaseDropboxPath.DROPBOX_CLIENT_ID)).build(),
        new DbxCredential(accessToken)
      ),
      path
    )
