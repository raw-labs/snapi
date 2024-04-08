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
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.typesafe.scalalogging.StrictLogging
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import org.apache.hc.core5.http.HttpHeaders
import raw.creds.api.CredentialsException
import raw.sources.bytestream.http.oauth2clients.OAuth2Client.{httpClient, mapper, ofFormData, readTimeout}

import java.io.IOException
import java.net.URI
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpRequest, HttpResponse}
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.spec.{InvalidKeySpecException, PKCS8EncodedKeySpec}
import java.time.Instant
import java.util.Base64

@JsonNaming(classOf[PropertyNamingStrategy.SnakeCaseStrategy])
case class GoogleAuth2TokenResponse(
    accessToken: String,
    expiresIn: Int,
    tokenType: String,
    refreshToken: Option[String]
)

class GoogleApiKeyOAuth2Client extends OAuth2Client with StrictLogging {

  /**
   * Executes the OAuth2 refresh token flow to obtain a new access token. Implementation is specific to the provider.
   */
  override def newAccessTokenFromRefreshToken(refreshToken: String, options: Map[String, String]): RenewedAccessToken =
    throw new UnsupportedOperationException("Google-api refresh tokens are not supported.")

  /**
   * Executes the OAuth2 client credentials flow to obtain a new access token. Implementation is specific to the provider.
   */
  override def newAccessTokenFromClientCredentials(
      clientId: String,
      clientSecret: String,
      options: Map[String, String]
  ): RenewedAccessToken = {

    def getOption(key: String) =
      options.getOrElse(key, throw new CredentialsException(s"""missing "$key" in options"""))

    val privateKey = clientSecret
    val clientEmail = getOption("client_email")
    val tokenUri = getOption("token_uri")
    val scope = getOption("scope")

    val uri = new URI(tokenUri)
    try {
      val cleaned = privateKey
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .replaceAll("\\s+", "")

      val pkcs8EncodedBytes = Base64.getDecoder().decode(cleaned.replaceAll("\"\"", ""))

      val keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes)
      val kf = KeyFactory.getInstance("RSA");
      val privKey = kf.generatePrivate(keySpec);
      val currentTime = Instant.now.getEpochSecond

      val claim = JwtClaim()
        .by(clientEmail)
        .to(tokenUri)
        .issuedAt(currentTime)
        .expiresAt(currentTime + 3600) // 1 hour
        .+("scope", scope)

      val jwtToken = Jwt.encode(claim, privKey, JwtAlgorithm.RS256)

      val formData = Map(
        "grant_type" -> "urn:ietf:params:oauth:grant-type:jwt-bearer",
        "assertion" -> jwtToken
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
        val newToken = mapper.readValue[GoogleAuth2TokenResponse](response.body())

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
      case ex: JsonProcessingException => throw new CredentialsException(
          s"error processing json response while getting token from $uri",
          ex
        )
      case ex: InvalidKeySpecException =>
        throw new CredentialsException(s"error trying to get google-api access token: invalid private key", ex)
      case ex: IOException => throw new CredentialsException(s"error getting google api token from $uri", ex)
    }
  }

}
