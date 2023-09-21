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

package raw.cli

import com.typesafe.scalalogging.StrictLogging
import raw.config.RawSettings
import raw.creds.CredentialsServiceBuilder

class LocalCredentialsServiceBuilder extends CredentialsServiceBuilder with StrictLogging {

  override val name: String = "local"

  override def build(implicit settings: RawSettings): LocalCredentialsService = {
    new LocalCredentialsService
  }

}
