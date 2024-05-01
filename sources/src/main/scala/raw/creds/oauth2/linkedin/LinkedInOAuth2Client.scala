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

package raw.creds.oauth2.linkedin

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.typesafe.scalalogging.StrictLogging
import org.apache.hc.core5.http.HttpHeaders
import org.apache.hc.core5.net.URIBuilder
import raw.creds.api.CredentialsException
import raw.creds.oauth2.api.{OAuth2Client, RenewedAccessToken}
import raw.utils.RawSettings

import java.io.IOException
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpRequest, HttpResponse}
import java.nio.charset.StandardCharsets
import java.time.Instant

object LinkedInOAuth2Client {
  private val CLIENT_ID_KEY = "client_id"
  private val CLIENT_SECRET_KEY = "client_secret"

  @JsonNaming(classOf[PropertyNamingStrategy.SnakeCaseStrategy])
  private case class LinkedInAuth2TokenResponse(
      accessToken: String,
      expiresIn: Int,
      refreshToken: Option[String],
      refreshTokenExpiresIn: Option[Int]
  )

}

class LinkedInOAuth2Client(implicit settings: RawSettings) extends OAuth2Client with StrictLogging {

  import LinkedInOAuth2Client._

  override def supportsRefreshToken: Boolean = true

  override def supportsClientCredentials: Boolean = false

  /**
   * Executes the OAuth2 refresh token flow to obtain a new access token. Implementation is specific to the provider.
   */
  override def newAccessTokenFromRefreshToken(
      refreshToken: String,
      options: Map[String, String]
  ): RenewedAccessToken = {

    // https://docs.microsoft.com/en-us/linkedin/shared/authentication/programmatic-refresh-tokens
    val clientId =
      options.getOrElse(CLIENT_ID_KEY, throw new CredentialsException(s"""missing "$CLIENT_ID_KEY" in options"""))
    val clientSecret = options.getOrElse(
      CLIENT_SECRET_KEY,
      throw new CredentialsException(s"""missing "$CLIENT_SECRET_KEY" in options""")
    )
    try {
      val uri = new URIBuilder("https://www.linkedin.com/oauth/v2/accessToken").build()

      val formData = Map(
        "grant_type" -> "refresh_token",
        "refresh_token" -> refreshToken,
        "client_id" -> clientId,
        "client_secret" -> clientSecret
      )
      val request = HttpRequest
        .newBuilder()
        .timeout(readTimeout)
        .uri(uri)
        .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
        .POST(ofFormData(formData))
        .build()

      logger.debug(s"Sending request to ${request.uri()}")
      val response: HttpResponse[String] = httpClient.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8))
      logger.debug(s"Response status: ${response.statusCode()}")

      if (response.statusCode() == 200) {
        val newToken = mapper.readValue[LinkedInAuth2TokenResponse](response.body())

        RenewedAccessToken(
          newToken.accessToken,
          Instant.now().plusSeconds(newToken.expiresIn),
          Seq.empty,
          newToken.refreshToken
        )
      } else {
        throw new CredentialsException(s"unexpected response: ${response.statusCode()}. Error: ${response.body()}")
      }
    } catch {
      case ex: JsonProcessingException =>
        throw new CredentialsException(s"error processing json response while refreshing LinkedIn token", ex)
      case ex: IOException => throw new CredentialsException(s"error refreshing LinkedIn token", ex)
    }

  }

}
