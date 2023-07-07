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

package raw.creds

import java.time.{LocalDateTime, ZoneOffset}
import scala.util.Random

trait OAuth2TestCreds {

  // RawTest, raw-test2 tenant
  val auth0ClientId = sys.env("RAW_AUTH0_TEST_CLIENT_ID")
  val auth0ClientSecret = sys.env("RAW_AUTH0_TEST_CLIENT_SECRET")
  val auth0Domain = sys.env("RAW_AUTH0_TEST_DOMAIN")
  val auth0MgmtApi = s"https://$auth0Domain/api/v2/"

  val auth0ClientCreds = ClientCredentialsCredential(
    OAuth2Provider.Auth0,
    auth0ClientId,
    auth0ClientSecret,
    Map("audience" -> auth0MgmtApi, "base_url" -> s"https://$auth0Domain")
  )

  //replace random char by another random char
  val auth0BadClientId = auth0ClientId.updated(Random.nextInt(auth0ClientId.length), (Random.nextInt(26) + 'a').toChar)
  val auth0BadClientIdCreds = ClientCredentialsCredential(
    OAuth2Provider.Auth0,
    auth0BadClientId,
    auth0ClientSecret,
    Map("audience" -> auth0MgmtApi, "base_url" -> s"https://$auth0Domain")
  )

  //replace random char by another random char
  val auth0BadClientSecret =
    auth0ClientSecret.updated(Random.nextInt(auth0ClientSecret.length), (Random.nextInt(26) + 'a').toChar)
  val auth0BadClientSecretCreds = ClientCredentialsCredential(
    OAuth2Provider.Auth0,
    auth0ClientId,
    auth0BadClientSecret,
    Map("audience" -> auth0MgmtApi, "base_url" -> s"https://$auth0Domain")
  )

  val auth0AccessToken = TokenCredential(OAuth2Provider.Auth0, "not-supported", None, None, None, Map.empty)

  val shopifyAccessToken = sys.env("RAW_SHOPIFY_TEST_ACCESS_TOKEN")
  val shopifyTokenCredential = TokenCredential(OAuth2Provider.Shopify, shopifyAccessToken, None, None, None, Map.empty)

  //replace random char by another random char
  val shopifyBadAccessToken =
    shopifyAccessToken.updated(Random.nextInt(shopifyAccessToken.length), (Random.nextInt(26) + 'a').toChar)
  val shopifyBadTokenCredential =
    TokenCredential(OAuth2Provider.Shopify, shopifyBadAccessToken, None, None, None, Map.empty)

  val shopifyClientCredential = ClientCredentialsCredential(
    OAuth2Provider.Shopify,
    "client-id-not-supported",
    "client-secret-not-supported",
    Map("key" -> "value-not-supported")
  )

  val twitterApiKey = sys.env("RAW_TWITTER_TEST_API_KEY")
  val twiterApiSecret = sys.env("RAW_TWITTER_TEST_API_SECRET")

  val twitterClientCredential = ClientCredentialsCredential(
    OAuth2Provider.Twitter,
    twitterApiKey,
    twiterApiSecret,
    Map.empty
  )

  // Created on: 2022.06.02 (1654180772 UTC)
  // Token expires: in 2 months (1659364775 UTC)
  // Refresh token expires: in 12 months (1685716777 UTC)
  val linkedInAccessToken = sys.env("RAW_LINKEDIN_TEST_ACCESS_TOKEN")
  val linkedInRefreshToken = sys.env("RAW_LINKEDIN_TEST_REFRESH_TOKEN")
  val linkedInClientId = sys.env("RAW_LINKEDIN_TEST_CLIENT_ID")
  val linkedInClientSecret = sys.env("RAW_LINKEDIN_TEST_CLIENT_SECRET")
  val linkedInTokenCredentials = TokenCredential(
    OAuth2Provider.LinkedIn,
    linkedInAccessToken,
    Some(LocalDateTime.parse("2019-10-25T12:15:30").toInstant(ZoneOffset.UTC)),
    None,
    Some(linkedInRefreshToken),
    Map(
      "client_id" -> linkedInClientId,
      "client_secret" -> linkedInClientSecret
    )
  )
  // Non browser application created in zoho api console.
  // flow: https://www.zoho.com/accounts/protocol/oauth/devices/client-protocol-flow.html
  // validation: https://www.zoho.com/accounts/protocol/oauth/devices/initiation-request.html
  val zohoAccessToken = sys.env("RAW_ZOHO_TEST_ACCESS_TOKEN")
  val zohoRefreshToken = sys.env("RAW_ZOHO_TEST_REFRESH_TOKEN")
  val zohoClientID = sys.env("RAW_ZOHO_TEST_CLIENT_ID")
  val zohoClientSecret = sys.env("RAW_ZOHO_TEST_CLIENT_SECRET")
  val zohoAccountsUrl = "https://accounts.zoho.com"

  val zohoTokenCredentials = TokenCredential(
    OAuth2Provider.Zoho,
    zohoAccessToken,
    Some(LocalDateTime.parse("2019-10-25T12:15:30").toInstant(ZoneOffset.UTC)),
    None,
    Some(zohoRefreshToken),
    Map(
      "accounts_url" -> zohoAccountsUrl,
      "client_id" -> zohoClientID,
      "client_secret" -> zohoClientSecret
    )
  )

  val zohoBadAccessToken = TokenCredential(
    OAuth2Provider.Zoho,
    "bad-token",
    Some(LocalDateTime.parse("2019-10-25T12:15:30").toInstant(ZoneOffset.UTC)),
    None,
    Some("bad-refresh-token"),
    Map(
      "accounts_url" -> zohoAccountsUrl,
      "client_id" -> zohoClientID,
      "client_secret" -> zohoClientSecret
    )
  )

  // Zoho client-Id/client-secret credentials are not supported
  val zohoClientCredentials = ClientCredentialsCredential(
    OAuth2Provider.Zoho,
    clientId = zohoClientID,
    clientSecret = zohoClientSecret,
    options = Map("accounts_url" -> "https://accounts.zoho.com")
  )

  lazy val googleApiSecretKey = sys.env(RAW_GOOGLE_TEST_API_SECRET_KEY).stripMargin

  lazy val googleApiClientCredentials = ClientCredentialsCredential(
    OAuth2Provider.GoogleApi,
    clientId = sys.env(RAW_GOOGLE_TEST_API_CLIENT_ID),
    clientSecret = googleApiSecretKey,
    options = Map(
      "scope" -> "https://www.googleapis.com/auth/analytics.readonly",
      "client_email" -> "raw-labs-analytics-serrvice@raw-labs-analytics.iam.gserviceaccount.com",
      "token_uri" -> "https://oauth2.googleapis.com/token"
    )
  )
}
