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

package raw.sources.bytestream.http

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.{ObjectMapper, ObjectReader}
import com.fasterxml.jackson.module.scala.{ClassTagExtensions, DefaultScalaModule}
import com.typesafe.scalalogging.StrictLogging
import org.apache.commons.io.IOUtils
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.core5.http.HttpHeaders
import org.apache.hc.core5.net.URIBuilder
import raw.config.RawSettings
import raw.creds.api._
import raw.sources.bytestream.api.ByteStreamLocationBuilder
import raw.sources.bytestream.http.oauth2clients.Auth0OAuth2Client
import raw.sources.api.{LocationDescription, LocationException, SourceContext}

import java.io.IOException
import java.net.{HttpURLConnection, MalformedURLException, URISyntaxException}
import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.collection.mutable

object HttpByteStreamLocationBuilder extends StrictLogging {
  private val jsonMapReader: ObjectReader = {
    val om = new ObjectMapper() with ClassTagExtensions
    om.registerModule(DefaultScalaModule)
    om.readerFor(classOf[Map[String, String]])
  }

  private def getClientCredsToken(clientId: String, clientSecret: String, server: String, useBasicAuth: Boolean)(
      implicit settings: RawSettings
  ): String = {
    val response =
      try {
        // adding parameters
        val uriBuilder = new URIBuilder(server)
        if (useBasicAuth) {
          uriBuilder.addParameter("grant_type", "client_credentials")
        } else {
          Seq("grant_type" -> "client_credentials", "client_id" -> clientId, "client_secret" -> clientSecret).foreach(
            x => uriBuilder.addParameter(x._1, x._2)
          )
        }
        // Making the default a POST as most apis use that to renew the token.
        // TODO: Add more options for the token req (method, json body) or a big if then else per API (twitter, RTS, facebook)
        val request = new HttpPost(uriBuilder.build())
        if (useBasicAuth) {
          val bytesEncoded = Base64.getEncoder.encode(s"$clientId:$clientSecret".getBytes)
          request.setHeader(HttpHeaders.AUTHORIZATION, s"Basic ${new String(bytesEncoded)}")
        }
        request.setHeader(HttpHeaders.ACCEPT, "application/json")
        request.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache")
        val apacheHttpClient = ApacheRuntimeHttpClient.buildApacheHttpClient(settings)
        apacheHttpClient.httpClient.execute(request)
      } catch {
        case ex: MalformedURLException => throw new LocationException(s"invalid HTTP token URL '$server'", ex)
        case ex: URISyntaxException => throw new LocationException(s"invalid HTTP token URL '$server'", ex)
        case ex: java.net.UnknownHostException => throw new HttpClientException(s"host not found for $server", ex)
        case ex: IOException => throw new HttpClientException(s"error obtaining token with HTTP endpoint $server", ex)
      }

    try {
      val respCode = response.getCode
      if (response.getCode == 200) {
        val is = response.getEntity.getContent
        val contents =
          try {
            IOUtils.toString(is, StandardCharsets.UTF_8)
          } finally {
            is.close()
          }
        val token = jsonMapReader.readValue[Map[String, String]](contents)
        token.getOrElse(
          "access_token",
          throw new HttpClientException(
            s"""error obtaining token with HTTP endpoint $server: received json does not have 'access_token' field. Response: "$contents" """
          )
        )
      } else {
        logger.warn(s"Error obtaining token for HTTP endpoint $server. Unexpected response code: $respCode")
        throw new HttpClientException(
          s"error obtaining token with HTTP endpoint $server, unexpected response code: $respCode",
          null
        )
      }
    } catch {
      case ex: IOException => throw new HttpClientException(s"error obtaining token with HTTP endpoint $server", ex)
      case ex: JsonProcessingException => throw new HttpClientException(
          s"error obtaining token with HTTP endpoint $server: error processing json response",
          ex
        )
    } finally {
      response.close()
    }
  }

  private def getClientCredsTokenFromProvider(
      clientId: String,
      clientSecret: String,
      provider: OAuth2Provider.Value,
      options: Map[String, String]
  ): String = {
    provider match {
      case OAuth2Provider.Auth0 =>
        val client = new Auth0OAuth2Client()
        val token = client.newAccessTokenFromClientCredentials(clientId, clientSecret, options)
        token.accessToken
      case _ => throw new HttpClientException(
          s"provider ${provider.toString.toLowerCase} no supported for client-id/client-secret credentials"
        )
    }
  }

}

class HttpByteStreamLocationBuilder extends ByteStreamLocationBuilder with StrictLogging {
  import HttpByteStreamLocationBuilder._

  private val httpRegex = """(http|https)://(.*)""".r

  override def schemes: Seq[String] = Seq("http", "https")

  private def resolveCredentials(
      location: LocationDescription
  )(implicit sourceContext: SourceContext): Option[NewHttpAuth] = {
    if (location.getStringSetting("http-auth-cred-name").isDefined) {
      val authName = location.getStringSetting("http-auth-cred-name").get
      val maybeCred = sourceContext.credentialsService.getNewHttpAuth(sourceContext.user, authName)
      if (maybeCred.isEmpty) {
        throw new HttpClientException(s"Cannot find credential: $authName")
      }
      maybeCred
    } else if (location.getStringSetting("http-token").isDefined) {
      val token = location.getStringSetting("http-token").get
      Some(BearerToken(token, Map.empty))
    } else if (location.getStringSetting("http-client-id").isDefined) {
      val clientId = location.getStringSetting("http-client-id")
      val clientSecret = location.getStringSetting("http-client-secret")
      val authProvider = location.getStringSetting("http-auth-provider")
      val tokenUrl = location.getStringSetting("http-token-url")
      if (clientSecret.isEmpty) {
        throw new LocationException(
          "http client-id/client-secret credentials need http-client-id and http-client-secret"
        )
      }
      if (authProvider.isDefined) {
        val options = location.getKVSetting("http-auth-options").getOrElse(Array.empty).toMap
        val token =
          getClientCredsTokenFromProvider(clientId.get, clientSecret.get, OAuth2Provider(authProvider.get), options)
        Some(BearerToken(token, Map.empty))
      } else if (tokenUrl.isDefined) {
        val useBasicAuth = location.getBooleanSetting("http-use-basic-auth").getOrElse(false)
        val token =
          getClientCredsToken(clientId.get, clientSecret.get, tokenUrl.get, useBasicAuth)(sourceContext.settings)
        Some(BearerToken(token, Map.empty))
      } else {
        throw new LocationException(
          "http client-id/secret credentials need one of: http-auth-provider or http-token-url property"
        )
      }

    } else if (location.getStringSetting("http-user-name").isDefined) {
      val userName = location.getStringSetting("http-user-name").get
      val password = location
        .getStringSetting("http-password")
        .getOrElse(
          throw new LocationException(
            "http basic-auth credentials need http-username and http-password properties"
          )
        )
      Some(BasicAuth(userName, password, Map.empty))

    } else {
      sourceContext.credentialsService
        .getHTTPCred(sourceContext.user, location.url)
        .map(c =>
          c.credentials match {
            case OauthClientCredentials(clientId, clientSecret, tokenUrl, useBasicAuth) =>
              val token = getClientCredsToken(clientId, clientSecret, tokenUrl, useBasicAuth)(sourceContext.settings)
              BearerToken(token, Map.empty)
            case OauthToken(token, _, _) => BearerToken(token, Map.empty)
            case BasicAuthCredentials(user, password) => BasicAuth(user, password, Map.empty)
            case AccessToken(accessToken) => BearerToken(accessToken, Map.empty)
          }
        )
    }
  }

  @throws(classOf[LocationException])
  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): HttpByteStreamLocation = {
    location.url match {
      case httpRegex(_, _) =>
        // Test if URL is valid
        try {
          // Build the location
          val method = location.getStringSetting("http-method").getOrElse("get")
          val args = location.getKVSetting("http-args").getOrElse(Array.empty)
          val headers: mutable.Map[String, String] = location
            .getKVSetting("http-headers")
            .map(a => {
              val mutableMap = new mutable.HashMap[String, String]()
              a.foreach(e => mutableMap += e)
              mutableMap
            })
            .getOrElse(new mutable.HashMap[String, String]())
          val body = location.getBinarySetting("http-body")
          val bodyString = location.getStringSetting("http-body-string").map(_.getBytes("utf-8"))

          if (!headers.contains(HttpHeaders.AUTHORIZATION)) {
            resolveCredentials(location) match {
              case Some(BearerToken(token, _)) => headers.put(HttpHeaders.AUTHORIZATION, s"Bearer $token")
              case Some(BasicAuth(username, password, _)) =>
                val bytesEncoded = Base64.getEncoder.encode(s"$username:$password".getBytes)
                headers.put(HttpHeaders.AUTHORIZATION, s"Basic ${new String(bytesEncoded)}")
              case Some(CustomHeaderToken(header, token, _)) => headers.put(header, token)
              case None =>
            }
          }

          val expectedStatus = location
            .getIntArraySetting("http-expected-status")
            .getOrElse(
              Array(
                HttpURLConnection.HTTP_OK,
                HttpURLConnection.HTTP_ACCEPTED,
                HttpURLConnection.HTTP_CREATED,
                HttpURLConnection.HTTP_PARTIAL
              )
            )
          val cli = new JavaRuntimeHttpClient(method, args, headers.to, body.orElse(bodyString), expectedStatus)(
            sourceContext.settings
          )
          new HttpByteStreamLocation(
            cli,
            location.url,
            location
          )
        } catch {
          case ex: MalformedURLException => throw new LocationException(s"invalid HTTP URL: ${ex.getMessage}", ex)
          case ex: URISyntaxException => throw new LocationException(s"invalid HTTP URL: ${ex.getMessage}", ex)
        }
      case _ => throw new LocationException("not an HTTP location")
    }
  }
}
