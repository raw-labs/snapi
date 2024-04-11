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

package raw.creds.dropbox

import raw.client.utils.Uid
import scala.util.Random
import raw.creds.api._

trait DropboxTestCreds {

  // For Raw Labs app
  // Access token for "dropbox://Apps/Raw Test/"
  val dropboxAccessToken = sys.env("RAW_DROPBOX_TEST_ACCESS_TOKEN")
  val dropboxTokenUID = sys.env("RAW_DROPBOX_TEST_TOKEN_UID")
  val dropboxToken = DropboxToken(dropboxAccessToken, "bearer", Uid(dropboxTokenUID))
  val dropboxClientId = sys.env("RAW_DROPBOX_TEST_CLIENT_ID")

  // The refresh token does not expire, but can be revoked
  // https://www.dropbox.com/developers/documentation/http/documentation#auth-token-revoke
  val expiredAccessToken = sys.env("RAW_EXPIRED_TEST_ACCESS_TOKEN")
  val validUntil = "2022-02-08T14:45:35.959994891Z"
  val scopes = Seq("scope1", "scope2")
  val refreshToken = sys.env("RAW_REFRESH_TEST_TOKEN")

  // RAW Test Dropbox App
  val dropboxRawTestAppClientId = sys.env("RAW_DROPBOX_RAW_TEST_APP_CLIENT_ID")
  val dropboxRawTestAppClientSecret = sys.env("RAW_DROPBOX_RAW_TEST_APP_CLIENT_SECRET")

  // This token is expired.
  val rawTestAccessToken = sys.env(
    "RAW_DROPBOX_RAW_TEST_ACCESS_TOKEN"
  )

  // The refresh token should be valid indefinitely
  val dropboxRefreshToken = sys.env("RAW_DROPBOX_TEST_REFRESH_TOKEN")
  val dropboxBadRefreshToken =
    dropboxRefreshToken.updated(Random.nextInt(dropboxRefreshToken.length), (Random.nextInt(26) + 'a').toChar)
  val rawTestTokenUid = dropboxTokenUID
  val rawTestTokenAccountId = sys.env("RAW_DROPBOX_TEST_ACCOUNT_ID")

  // The access token is expired, but the refresh token is valid forever
  val dropboxRefreshTokenCredential = TokenCredential(
    OAuth2Provider.Dropbox,
    rawTestAccessToken,
    None,
    Some(scopes),
    Some(dropboxRefreshToken),
    Map("key1" -> "value1", "key2" -> "value2")
  )

  val dropboxBadRefreshTokenCredential = TokenCredential(
    OAuth2Provider.Dropbox,
    rawTestAccessToken,
    None,
    Some(scopes),
    Some(dropboxBadRefreshToken),
    Map("key1" -> "value1", "key2" -> "value2")
  )

  val dropboxLongLivedAccessToken = sys.env(
    "RAW_DROPBOX_TEST_LONG_LIVED_ACCESS_TOKEN"
  )
  // Long lived access token for "dropbox://Apps/Raw Test/"
  // https://www.dropbox.com/developers/apps/info/97wyik3vzi5s5im
  val dropboxAccessTokenCredential = TokenCredential(
    OAuth2Provider.Dropbox,
    dropboxLongLivedAccessToken,
    None,
    Some(scopes),
    None,
    Map("key1" -> "value1", "key2" -> "value2")
  )

  val dropboxBadLongLivedAccessToken = dropboxLongLivedAccessToken.updated(
    Random.nextInt(dropboxLongLivedAccessToken.length),
    (Random.nextInt(26) + 'a').toChar
  )
  val dropboxBadAccessTokenCredential = TokenCredential(
    OAuth2Provider.Dropbox,
    dropboxBadLongLivedAccessToken,
    None,
    Some(scopes),
    None,
    Map("key1" -> "value1", "key2" -> "value2")
  )

  val bearerToken = BearerToken(dropboxAccessTokenCredential.accessToken, Map("key1" -> "value1", "key2" -> "value2"))
}
