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

package raw.sources.filesystem.dropbox

import com.typesafe.scalalogging.StrictLogging
import raw.creds.dropbox.DropboxTestCreds
import raw.utils.{RawTestSuite, SettingsTestContext}

class TestRootDropboxFileSystem extends RawTestSuite with DropboxTestCreds with SettingsTestContext with StrictLogging {

  test("list /") { _ =>
    val fs = new DropboxFileSystem(bearerToken)
    logger.debug("Result: " + fs.listContents("/").toList)
    assert(fs.listContents("/").nonEmpty)
  }

}
