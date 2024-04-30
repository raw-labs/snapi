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

package raw.creds.oauth2.shopify

import raw.creds.oauth2.api.OAuth2Client
import raw.utils.RawSettings

class ShopifyOAuth2Client(implicit settings: RawSettings) extends OAuth2Client {

  override def customHeader: Option[String] = Some("X-Shopify-Access-Token")
}
