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
import raw.creds.api.CredentialsException
import raw.client.utils.RawSettings

import java.net.URI
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpRequest, HttpResponse}
import java.nio.charset.StandardCharsets
import java.time.Instant
import scala.compat.java8.OptionConverters.RichOptionalGeneric

@JsonNaming(classOf[PropertyNamingStrategy.SnakeCaseStrategy])
case class DropboxAuth2TokenResponse(
    accessToken: String,
    expiresIn: Int,
    scopes: String,
    refreshToken: Option[String]
)

object DropboxOAuth2Client {
  // TODO (msb): This could be given as an option to the credential and therefore removed from here!
  private val DROPBOX_CLIENT_ID = "raw.sources.dropbox.clientId"
}

class DropboxOAuth2Client(implicit settings: RawSettings) extends OAuth2Client with StrictLogging {
  import DropboxOAuth2Client._
  import OAuth2Client._

  // https://www.dropbox.com/developers/documentation/http/documentation#oauth2-token
  logger.debug("Creating new Dropbox OAuth2 client")

  // TODO (ns): This could be given as an option to the credential
  private val clientId = settings.getString(DROPBOX_CLIENT_ID)

  private val tokenUri = new URI("https://api.dropbox.com/oauth2/token")
  private val testAccessUri = new URI("https://api.dropboxapi.com/2/users/get_space_usage")
  // https://www.dropbox.com/developers/documentation/http/documentation#oauth2-token

  def newAccessTokenFromClientCredentials(
      clientId: String,
      clientSecret: String,
      options: Map[String, String]
  ): RenewedAccessToken = {
    throw new UnsupportedOperationException("Dropbox does not support client credentials flow.")
  }

  def newAccessTokenFromRefreshToken(refreshToken: String, options: Map[String, String]): RenewedAccessToken = {
    // https://www.dropbox.com/developers/documentation/http/documentation#oauth2-token
    val formData = Map(
      "grant_type" -> "refresh_token",
      "refresh_token" -> refreshToken,
      "client_id" -> clientId
    )
    // Should send the codeVerifier?
    val request = HttpRequest
      .newBuilder()
      .timeout(readTimeout)
      .uri(tokenUri)
      .POST(ofFormData(formData))
      .build()
    logger.debug(s"Sending request to ${request.uri()}")
    val response: HttpResponse[String] = httpClient.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8))
    logger.debug(s"Response status: ${response.statusCode()}")

    handleErrorResponse(response)

    if (response.statusCode() == 200) {
      val newToken = mapper.readValue[DropboxAuth2TokenResponse](response.body())
      RenewedAccessToken(
        newToken.accessToken,
        Instant.now().plusSeconds(newToken.expiresIn),
        StringUtils.split(newToken.scopes),
        newToken.refreshToken.filterNot(_.isBlank)
      )
    } else {
      throw new CredentialsException(s"Unexpected response: ${response.statusCode()}. Error: ${response.body()}")
    }
  }

  def testAccess(accessToken: String): Unit = {
    val request = HttpRequest
      .newBuilder()
      .timeout(readTimeout)
      .uri(testAccessUri)
      .header(HttpHeaders.AUTHORIZATION, s"Bearer $accessToken")
      .POST(BodyPublishers.noBody())
      .build()

    val response = httpClient.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8))
    handleErrorResponse(response)
  }

  /** Generic error handling logic. */
  private def handleErrorResponse(response: HttpResponse[String]): Unit = {
    response.statusCode() match {
      case 200 | 201 | 202 | 204 => // Ok
      case 400 =>
        logger.warn(s"Error contacting REST API: ${response.body()}")
        response.headers().firstValue(HttpHeaders.CONTENT_TYPE).asScala match {
          case Some("application/json") =>
            val body = mapper.readValue[Map[String, String]](response.body())
            throw new CredentialsException(
              s"Could not exchange code: ${body("error")}: ${body("error_description")} }"
            )
          case _ => throw new CredentialsException(s"Could not exchange code: ${response.body()}")
        }
      case 401 =>
        logger.warn(s"Not authorized calling REST API: (${response.statusCode()}) ${response.body()}")
        throw new CredentialsException(
          s"Not authorized calling REST API. Status code: ${response.statusCode()}"
        )

      case _ =>
        logger.warn(s"Error calling REST API: (${response.statusCode()}) ${response.body()}")
        throw new CredentialsException(
          s"Unexpected error calling REST API. Status code: ${response.statusCode()}"
        )
    }
  }
}
