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

package raw.creds.api

import raw.creds.client.ClientCredentialsService
import raw.creds.local.LocalCredentialsService
import raw.utils.RawSettings

object CredentialsServiceProvider {

  private val CREDS_IMPL = "raw.creds.impl"

  def apply()(implicit settings: RawSettings): CredentialsService = {
    settings.getStringOpt(CREDS_IMPL) match {
      case Some("client") => new ClientCredentialsService
      case Some("local") => new LocalCredentialsService
      case Some(impl) => throw new CredentialsException(s"cannot find credentials service: $impl")
      case None => throw new CredentialsException("no credentials service defined")
    }
  }

}
