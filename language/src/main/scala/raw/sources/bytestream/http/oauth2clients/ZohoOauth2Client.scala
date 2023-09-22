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

import java.io.IOException
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpRequest, HttpResponse}
import java.nio.charset.StandardCharsets
import java.time.Instant

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.typesafe.scalalogging.StrictLogging
import org.apache.hc.core5.net.URIBuilder
import raw.creds.api.CredentialsException
import raw.sources.bytestream.http.oauth2clients.OAuth2Client.{httpClient, mapper, readTimeout}

@JsonNaming(classOf[PropertyNamingStrategy.SnakeCaseStrategy])
case class ZohoAuth2TokenResponse(
    accessToken: String,
    expiresIn: Int,
    apiDomain: String,
    tokenType: String,
    refreshToken: Option[String],
    error: Option[String]
)

@JsonNaming(classOf[PropertyNamingStrategy.SnakeCaseStrategy])
case class ZohoDeviceCodeResponse(
    userCode: String,
    deviceCode: String,
    verificationUriComplete: String,
    verificationUrl: String,
    interval: Int,
    expiresIn: Int,
    error: Option[String]
)

/**
 * Zoho oauth client
 *  To use this class first create non-browser application in zoho api console.
 *  Then validated it with the client-protocol-flow as seen in zoho documentation:
 *  flow: https://www.zoho.com/accounts/protocol/oauth/devices/client-protocol-flow.html
 *  validation: https://www.zoho.com/accounts/protocol/oauth/devices/initiation-request.html
 */
class ZohoOauth2Client extends OAuth2Client with StrictLogging {
  val ACCOUNTS_URL = "accounts_url"
  val CLIENT_ID_KEY = "client_id"
  val CLIENT_SECRET_KEY = "client_secret"

  /**
   * Executes the OAuth2 refresh token flow to obtain a new access token. Implementation is specific to the provider.
   */
  override def newAccessTokenFromRefreshToken(
      refreshToken: String,
      options: Map[String, String]
  ): RenewedAccessToken = {
    // https://www.zoho.com/crm/developer/docs/api/v2/refresh.html
    val accountsUrl =
      options.getOrElse(ACCOUNTS_URL, throw new CredentialsException(s"""missing "$ACCOUNTS_URL" in options"""))
    val clientId =
      options.getOrElse(CLIENT_ID_KEY, throw new CredentialsException(s"""missing "$CLIENT_ID_KEY" in options"""))
    val clientSecret = options.getOrElse(
      CLIENT_SECRET_KEY,
      throw new CredentialsException(s"""missing "$CLIENT_SECRET_KEY" in options""")
    )

    try {
      val uri = new URIBuilder(accountsUrl)
        .appendPath("/oauth/v2/token")
        .addParameter("grant_type", "refresh_token")
        .addParameter("refresh_token", refreshToken)
        .addParameter("client_id", clientId)
        .addParameter("client_secret", clientSecret)
        .build()

      val request = HttpRequest
        .newBuilder()
        .timeout(readTimeout)
        .uri(uri)
        .POST(HttpRequest.BodyPublishers.noBody())
        .build()

      logger.debug(s"Sending request to ${request.uri()}")
      val response: HttpResponse[String] = httpClient.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8))
      logger.debug(s"Response status: ${response.statusCode()}")

      if (response.statusCode() == 200) {
        val newToken = mapper.readValue[ZohoAuth2TokenResponse](response.body())

        // Zoho will still return a 200  sometimes even though there was an error
        //  for example: '{"error": "invalid_client"}'
        newToken.error.foreach(msg =>
          throw new CredentialsException(
            s"error while trying to refresh token from ${accountsUrl + "/oauth/v2/token"}: $msg"
          )
        )

        RenewedAccessToken(
          newToken.accessToken,
          Instant.now().plusSeconds(newToken.expiresIn),
          Seq.empty,
          Some(refreshToken)
        )
      } else {
        throw new CredentialsException(s"unexpected response: ${response.statusCode()}. Error: ${response.body()}")
      }
    } catch {
      case ex: JsonProcessingException => throw new CredentialsException(
          s"error processing json response while refreshing zoho token from $accountsUrl",
          ex
        )
      case ex: IOException => throw new CredentialsException(s"error refreshing zoho token from $accountsUrl", ex)
    }
  }

  /**
   * Executes the OAuth2 client credentials flow to obtain a new access token. Implementation is specific to the provider.
   */
  override def newAccessTokenFromClientCredentials(
      clientId: String,
      clientSecret: String,
      options: Map[String, String]
  ): RenewedAccessToken = {
    throw new UnsupportedOperationException("Zoho does not support client credentials flow.")
  }

  def getVerificationUrl(
      clientId: String,
      scope: String,
      accountsUrl: String
  ): ZohoDeviceCodeResponse = {
    val uri = new URIBuilder(accountsUrl)
      .appendPath("/oauth/v3/device/code")
      .addParameter("client_id", clientId)
      .addParameter("scope", scope)
      .addParameter("grant_type", "device_request")
      .addParameter("access_type", "offline")
      .build()

    val request = HttpRequest
      .newBuilder()
      .timeout(readTimeout)
      .uri(uri)
      .POST(HttpRequest.BodyPublishers.noBody())
      .build()

    val response: HttpResponse[String] = httpClient.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8))

    if (response.statusCode() != 200) throw new CredentialsException(
      s"unexpected response: ${response.statusCode()} while trying to get zoho authorization url"
    )
    try {
      val codeResponse = mapper.readValue[ZohoDeviceCodeResponse](response.body())
      codeResponse.error.foreach(msg =>
        throw new CredentialsException(
          s"error getting verification url from ${accountsUrl + "/oauth/v3/device/code"}: $msg"
        )
      )
      codeResponse
    } catch {
      case ex: JsonProcessingException => throw new CredentialsException(
          s"error processing request from $accountsUrl: ${response.body()}",
          ex
        )
    }
  }

  def exchangeCodeForRefreshToken(
      clientId: String,
      clientSecret: String,
      accountsUrl: String,
      code: String
  ): RenewedAccessToken = {
    val uri = new URIBuilder(accountsUrl)
      .appendPath("/oauth/v3/device/token")
      .addParameter("client_id", clientId)
      .addParameter("client_secret", clientSecret)
      .addParameter("code", code)
      .addParameter("grant_type", "device_token")
      .addParameter("access_type", "offline")
      .build()

    val request = HttpRequest
      .newBuilder()
      .timeout(readTimeout)
      .uri(uri)
      .POST(HttpRequest.BodyPublishers.noBody())
      .build()

    val response: HttpResponse[String] = httpClient.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8))

    if (response.statusCode() != 200) throw new CredentialsException(
      s"unexpected response: ${response.statusCode()} while trying to get zoho authorization url"
    )
    try {
      val token = mapper.readValue[ZohoAuth2TokenResponse](response.body())
      token.error.foreach(msg =>
        throw new CredentialsException(
          s"error exchanging code for refresh token from  ${accountsUrl + "/oauth/v3/device/token"}: $msg"
        )
      )
      RenewedAccessToken(
        token.accessToken,
        Instant.now().plusSeconds(token.expiresIn),
        Seq.empty,
        token.refreshToken
      )
    } catch {
      case ex: JsonProcessingException => throw new CredentialsException(
          s"error processing request from $accountsUrl: ${response.body()}",
          ex
        )
    }

  }
}
