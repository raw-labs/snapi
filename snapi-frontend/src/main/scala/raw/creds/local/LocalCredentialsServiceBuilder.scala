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

package raw.creds.local

import raw.creds.api.CredentialsService
import raw.utils.RawSettings

class LocalCredentialsServiceBuilder extends raw.creds.api.CredentialsServiceBuilder {

  override val name: String = "local"

  override def build(
      maybeClassLoader: Option[ClassLoader] = None
  )(implicit settings: RawSettings): CredentialsService = {
    new LocalCredentialsService
  }

}
