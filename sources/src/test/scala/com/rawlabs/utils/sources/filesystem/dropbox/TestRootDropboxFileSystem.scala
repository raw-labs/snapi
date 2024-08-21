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

import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import com.typesafe.scalalogging.StrictLogging
import com.rawlabs.utils.core.{RawTestSuite, SettingsTestContext}

class TestRootDropboxFileSystem extends RawTestSuite with SettingsTestContext with StrictLogging {

  test("list /") { _ =>
    val fs = new DropboxFileSystem(
      new DbxClientV2(
        DbxRequestConfig.newBuilder(settings.getString(BaseDropboxPath.DROPBOX_CLIENT_ID)).build(),
        new DbxCredential(sys.env("RAW_DROPBOX_TEST_LONG_LIVED_ACCESS_TOKEN"))
      )
    )
    logger.debug("Result: " + fs.listContents("/").toList)
    assert(fs.listContents("/").nonEmpty)
  }

}
