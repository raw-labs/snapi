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

import java.util.Locale

object OAuth2Provider extends Enumeration {
  type OAuth2Provider = Value

  val Dropbox, Auth0, Shopify, Zoho, LinkedIn, Twitter, GoogleApi, Generic = Value

  def apply(name: String): Value = {
    val sanitizedName = name.toLowerCase(Locale.ROOT).replace("-", "")
    values
      .find(_.toString.toLowerCase(Locale.ROOT) == sanitizedName)
      .getOrElse(throw new CredentialsException(s"invalid credential type: $name"))
  }
}
