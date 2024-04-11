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

package raw.creds.client

import com.typesafe.scalalogging.StrictLogging
import raw.client.utils.RawSettings
import raw.creds.api.CredentialsServiceBuilder

class ClientCredentialsServiceBuilder extends CredentialsServiceBuilder with StrictLogging {

  override val name: String = "client"

  override def build(implicit settings: RawSettings): ClientCredentialsService = {
    new ClientCredentialsService
  }

}
