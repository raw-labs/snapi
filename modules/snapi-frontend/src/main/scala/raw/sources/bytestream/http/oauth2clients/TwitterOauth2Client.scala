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

import com.fasterxml.jackson.core.JsonProcessingException

import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpRequest, HttpResponse}
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Base64
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.typesafe.scalalogging.StrictLogging
import org.apache.hc.core5.net.URIBuilder
import raw.creds.api.CredentialsException
import raw.sources.bytestream.http.HttpClientException
import raw.sources.bytestream.http.oauth2clients.OAuth2Client.{httpClient, mapper, readTimeout}

import java.io.IOException

// Tokens requested using the PKCE flow can have the expiresIn, scope and refreshToken
@JsonNaming(classOf[PropertyNamingStrategy.SnakeCaseStrategy])
case class TwitterAuth2TokenResponse(
    accessToken: String,
    tokenType: String,
    expiresIn: Option[Long],
    scope: Option[String],
    refreshToken: Option[String]
)

class TwitterOauth2Client extends OAuth2Client with StrictLogging {

  /**
   * Executes the OAuth2 refresh token flow to obtain a new access token. Implementation is specific to the provider.
   */
  override def newAccessTokenFromRefreshToken(
      refreshToken: String,
      options: Map[String, String]
  ): RenewedAccessToken = {
    throw new UnsupportedOperationException("Twitter does not support refresh tokens flow.")
  }

  /**
   * Executes the OAuth2 client credentials flow to obtain a new access token. Implementation is specific to the provider.
   */
  override def newAccessTokenFromClientCredentials(
      clientId: String,
      clientSecret: String,
      options: Map[String, String]
  ): RenewedAccessToken = {

    try {
      // https://developer.twitter.com/en/docs/authentication/api-reference/token
      val uri = new URIBuilder("https://api.twitter.com/oauth2/token")
        .addParameter("grant_type", "client_credentials")
        .build()

      // twitter passes the client-id client-secret as basic auth
      val strEncode = Base64.getEncoder.encodeToString(s"$clientId:$clientSecret".getBytes)
      val request = HttpRequest
        .newBuilder()
        .timeout(readTimeout)
        .uri(uri)
        .header("Authorization", s"Basic $strEncode")
        .POST(HttpRequest.BodyPublishers.noBody())
        .build()

      val response: HttpResponse[String] = httpClient.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8))
      logger.debug(s"Response status: ${response.statusCode()}")

      if (response.statusCode() == 200) {
        val newToken = mapper.readValue[TwitterAuth2TokenResponse](response.body())
        // when passing the api-key/api-secret I always got the same token, which corresponds to the one given to the app
        // which lasts forever, so if no expiration is defined then assume a long time (1 day)
        val expiration: Instant = Instant.now().plusSeconds(newToken.expiresIn.getOrElse(24 * 3600))
        val scopes: Seq[String] = newToken.scope.map(_.split(" ")).getOrElse(Array.empty).toSeq
        RenewedAccessToken(newToken.accessToken, expiration, scopes, newToken.refreshToken)
      } else {
        throw new CredentialsException(s"unexpected response: ${response.statusCode()}. Error: ${response.body()}")
      }
    } catch {
      case ex: IOException => throw new HttpClientException("HTTP error obtaining token from twitter", ex)
      case ex: JsonProcessingException => throw new HttpClientException(
          "error processing json response while getting token from twitter",
          ex
        )
    }
  }
}
