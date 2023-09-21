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

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.typesafe.scalalogging.StrictLogging
import org.apache.commons.lang3.StringUtils
import org.apache.hc.core5.http.HttpHeaders
import org.apache.hc.core5.net.URIBuilder
import raw.creds.CredentialsException

import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpRequest, HttpResponse}
import java.nio.charset.StandardCharsets
import java.time.Instant

object Auth0OAuth2Client {
  // Required options that must be provided by the user
  val AUDIENCE_KEY = "audience"
  val BASE_URL_KEY = "base_url"

  @JsonNaming(classOf[PropertyNamingStrategy.SnakeCaseStrategy])
  private case class Auth0TokenResponse(
      accessToken: String,
      expiresIn: Int,
      scope: String,
      tokenType: String,
      refreshToken: Option[String]
  )

  @JsonNaming(classOf[PropertyNamingStrategy.SnakeCaseStrategy])
  case class Auth0ErrorResponse(error: String, errorDescription: String)
}

class Auth0OAuth2Client extends OAuth2Client with StrictLogging {

  import Auth0OAuth2Client._
  import OAuth2Client._

  logger.debug("Creating new Auth0 OAuth2 client")

  def newAccessTokenFromClientCredentials(
      clientId: String,
      clientSecret: String,
      options: Map[String, String]
  ): RenewedAccessToken = {
    logger.debug(s"newAccessTokenFromClientCredentials: $clientId, $clientSecret")

    val audience =
      options.get(AUDIENCE_KEY).getOrElse(throw new CredentialsException(s"""Missing "$AUDIENCE_KEY" in options"""))
    val apiBaseUrl =
      options.get(BASE_URL_KEY).getOrElse(throw new CredentialsException(s"""Missing "$BASE_URL_KEY" in options"""))

    val requestBody = mapper.writeValueAsString(
      Map(
        "grant_type" -> "client_credentials",
        "client_id" -> clientId,
        "client_secret" -> clientSecret,
        "audience" -> audience
      )
    )
    val uri = new URIBuilder(apiBaseUrl).appendPath("/oauth/token").normalizeSyntax().build()
    val request = HttpRequest
      .newBuilder()
      .timeout(readTimeout)
      .uri(uri)
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .POST(BodyPublishers.ofString(requestBody))
      .build()
    logger.debug(s"Executing Auth0 client credentials flow, HTTP request $request")

    val response = httpClient.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8))

    logger.debug(s"Response from Auth0: $response")

    // https://auth0.com/docs/api/authentication?http#post-oauth-access_token
    handleErrorResponse(response)
    if (response.statusCode() == 200) {
      val auth0Token = mapper.readValue[Auth0TokenResponse](response.body())
      RenewedAccessToken(
        auth0Token.accessToken,
        Instant.now().plusSeconds(auth0Token.expiresIn),
        StringUtils.split(auth0Token.scope),
        auth0Token.refreshToken
      )
    } else {
      throw new CredentialsException(s"Unexpected response: ${response.statusCode()}. Error: ${response.body()}")
    }
  }

  private def handleErrorResponse(response: HttpResponse[String]): Unit = {
    response.statusCode() match {
      case 200 | 201 | 202 | 204 => // Ok
      case 400 =>
        val body = mapper.readValue[Auth0ErrorResponse](response.body())
        throw new CredentialsException(
          s"Error calling Auth0 REST API: (${response.statusCode()}) ${body.error}: ${body.errorDescription}"
        )

      case 401 | 403 =>
        val errorResponse = mapper.readValue[Auth0ErrorResponse](response.body())
        throw new CredentialsException(
          s"Authorization error calling Auth0 REST API: (${response.statusCode()}) ${errorResponse.error}: ${errorResponse.errorDescription}"
        )

      case _ =>
        logger.warn(s"Unexpected error calling REST API: (${response.statusCode()}) ${response.body()}")
        throw new CredentialsException(
          s"Unexpected error calling REST API. Status code: ${response.statusCode()}"
        )
    }
  }
  def newAccessTokenFromRefreshToken(refreshToken: String, options: Map[String, String]): RenewedAccessToken =
    throw new UnsupportedOperationException("The refresh token flow with Auth0 is not supported.")
}
