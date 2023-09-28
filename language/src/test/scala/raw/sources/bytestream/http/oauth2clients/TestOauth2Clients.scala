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

package raw.sources.bytestream.http.oauth2clients

import com.typesafe.scalalogging.StrictLogging
import org.scalatest.funsuite.AnyFunSuite
import raw.creds.api.{ClientCredentialsCredential, NewHttpCredential, TokenCredential}
import raw.creds.http.OAuth2TestCreds

class TestOauth2Clients extends AnyFunSuite with StrictLogging with OAuth2TestCreds {

  def testTokenFromClient(client: OAuth2Client, credential: NewHttpCredential) = {
    val token = credential match {
      case ClientCredentialsCredential(_, clientId, clientSecret, options, _, _, _) =>
        client.newAccessTokenFromClientCredentials(clientId, clientSecret, options)
      case TokenCredential(_, _, _, _, Some(refreshToken), options) =>
        client.newAccessTokenFromRefreshToken(refreshToken, options)
      case TokenCredential(_, _, _, _, None, _) =>
        throw new AssertionError("refresh token has to be defined in token credential")
      case _ => throw new AssertionError(s"unsupported credential type $credential")
    }
    logger.debug(s"token: ${token.accessToken}")

    assert(token.accessToken != "")
  }

  test("auth0 client credentials") {
    testTokenFromClient(new Auth0OAuth2Client(), auth0ClientCreds)
  }

  test("linkedin renew token") {
    testTokenFromClient(new LinkedInOauth2Client(), linkedInTokenCredentials)
  }

  test("twitter client credentials get token") {
    testTokenFromClient(new TwitterOauth2Client(), twitterClientCredential)
  }

  test("zoho refresh token") {
    testTokenFromClient(new ZohoOauth2Client, zohoTokenCredentials)
  }

  test("google api credentials") {
    testTokenFromClient(new GoogleApiKeyOAuth2Client, googleApiClientCredentials)
  }
}
