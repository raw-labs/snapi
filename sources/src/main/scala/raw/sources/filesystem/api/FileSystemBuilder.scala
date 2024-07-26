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

package raw.sources.filesystem.api

import raw.sources.filesystem.dropbox.{DropboxAccessTokenConfig, DropboxPath, DropboxUsernamePasswordConfig}
import raw.sources.filesystem.local.{LocalConfig, LocalPath}
import raw.sources.filesystem.mock.{MockConfig, MockPath}
import raw.sources.filesystem.s3.{S3Config, S3Path}
import raw.utils.RawSettings

object FileSystemBuilder {

  def build(config: FileSystemConfig)(implicit settings: RawSettings): FileSystemLocation = {
    config match {
      case c: LocalConfig => new LocalPath(c)
      case c: MockConfig => new MockPath(c)
      case c: DropboxAccessTokenConfig => new DropboxPath(c)
      case c: DropboxUsernamePasswordConfig => new DropboxPath(c)
      case c: S3Config => new S3Path(c)
    }
  }

}
