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

package raw.creds.oauth2.api

import com.fasterxml.jackson.annotation.{JsonSetter, Nulls}
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.{ClassTagExtensions, DefaultScalaModule}
import com.typesafe.scalalogging.StrictLogging
import raw.utils.{RawService, RawSettings}

import java.net.URLEncoder
import java.net.http.{HttpClient, HttpRequest}
import java.nio.charset.StandardCharsets

abstract class OAuth2Client(implicit settings: RawSettings) extends RawService with StrictLogging {

  protected val connectTimeout = settings.getDuration("raw.creds.oauth2.connect-timeout")

  protected val readTimeout = settings.getDuration("raw.creds.oauth2.read-timeout")

  protected val mapper = new ObjectMapper with ClassTagExtensions
  mapper.registerModule(DefaultScalaModule)
  mapper.setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY))
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  protected val httpClient = HttpClient.newBuilder
    .connectTimeout(connectTimeout)
    .version(HttpClient.Version.HTTP_1_1)
    .build

  protected def ofFormData(data: Map[String, String]): HttpRequest.BodyPublisher = {
    val builder = new StringBuffer()
    for ((key, value) <- data) {
      if (builder.length() != 0) builder.append('&')
      builder
        .append(URLEncoder.encode(key, StandardCharsets.UTF_8))
        .append("=")
        .append(URLEncoder.encode(value, StandardCharsets.UTF_8))
    }
    HttpRequest.BodyPublishers.ofString(builder.toString())
  }

  override def doStop(): Unit = {
    httpClient.close()
  }

  // Custom header required in the access token.
  def customHeader: Option[String] = None

  def supportsRefreshToken: Boolean = false

  def supportsClientCredentials: Boolean = false

  /**
   *  Executes the OAuth2 refresh token flow to obtain a new access token. Implementation is specific to the provider.
   */
  def newAccessTokenFromRefreshToken(refreshToken: String, options: Map[String, String]): RenewedAccessToken = ???

  /**
   *  Executes the OAuth2 client credentials flow to obtain a new access token. Implementation is specific to the provider.
   */
  def newAccessTokenFromClientCredentials(
      clientId: String,
      clientSecret: String,
      options: Map[String, String]
  ): RenewedAccessToken = ???

}
