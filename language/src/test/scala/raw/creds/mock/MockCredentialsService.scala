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

package raw.creds.mock

import raw.utils.{AuthenticatedUser, RawSettings, Uid}
import raw.creds.api._
import raw.sources.bytestream.http.oauth2clients.{
  Auth0OAuth2Client,
  DropboxOAuth2Client,
  GoogleApiKeyOAuth2Client,
  LinkedInOauth2Client,
  TwitterOauth2Client,
  ZohoOauth2Client
}

import java.time.Instant

// TODO (msb): This is a mock implementation and does not handle renewals/refresh tokens in the background.
class MockCredentialsService(implicit settings: RawSettings) extends CredentialsService {

  private val s3Buckets = scala.collection.mutable.Map[(Uid, String), S3Bucket]()

  override protected def doRegisterS3Bucket(user: AuthenticatedUser, bucket: S3Bucket): Boolean = {
    s3Buckets.put((user.uid, bucket.name), bucket).isEmpty
  }

  override def getS3Bucket(user: AuthenticatedUser, name: String): Option[S3Bucket] = {
    s3Buckets.get((user.uid, name))
  }

  override def listS3Buckets(user: AuthenticatedUser): List[String] = {
    s3Buckets.keys.filter(_._1 == user.uid).map(_._2).toList
  }

  override def unregisterS3Bucket(user: AuthenticatedUser, name: String): Boolean = {
    s3Buckets.remove((user.uid, name)).isDefined
  }

  private val dropboxTokens = scala.collection.mutable.Map[Uid, DropboxToken]()

  override def registerDropboxToken(user: AuthenticatedUser, dropboxToken: DropboxToken): Boolean = {
    dropboxTokens.put(user.uid, dropboxToken).isEmpty
  }

  override def getDropboxToken(user: AuthenticatedUser): Option[DropboxToken] = {
    dropboxTokens.get(user.uid)
  }

  override def unregisterDropboxToken(user: AuthenticatedUser): Boolean = {
    dropboxTokens.remove(user.uid).isDefined
  }

  private val newHttpCredentials = scala.collection.mutable.Map[(Uid, String), NewHttpCredential]()

  override protected def doRegisterNewHttpCredential(
      user: AuthenticatedUser,
      name: String,
      token: NewHttpCredential
  ): Boolean = {
    newHttpCredentials.put((user.uid, name), token).isEmpty
  }

  override def getNewHttpCredential(user: AuthenticatedUser, name: String): Option[NewHttpCredential] = {
    newHttpCredentials.get((user.uid, name))
  }

  override def unregisterNewHttpCredential(user: AuthenticatedUser, name: String): Boolean = {
    newHttpCredentials.remove((user.uid, name)).isDefined
  }

  override def listNewHttpCredentials(user: AuthenticatedUser): List[HttpCredentialId] = {
    newHttpCredentials.collect {
      case ((userCred, name), cred) if userCred == user =>
        val credType = cred match {
          case _: BasicAuthCredential => HttpCredentialType.UserPass
          case _: TokenCredential => HttpCredentialType.Token
          case _: ClientCredentialsCredential => HttpCredentialType.ClientCredentials
        }
        HttpCredentialId(name, credType)
    }.toList
  }

  override def getNewHttpAuth(user: AuthenticatedUser, name: String): Option[NewHttpAuth] = {
    newHttpCredentials
      .collectFirst {
        case ((userCred, name), cred) if userCred == user =>
          cred match {
            case cred: BasicAuthCredential => BasicAuth(cred.username, cred.password, cred.options)
            case cred: TokenCredential =>
              if (cred.refreshToken.isDefined) {
                cred.expiresBy match {
                  case Some(expireDate) =>
                    if (expireDate.isBefore(Instant.now())) {
                      refreshTokenCredential(cred)
                    } else {
                      BearerToken(cred.accessToken, cred.options)
                    }
                  case None => refreshTokenCredential(cred)
                }
              } else {
                BearerToken(cred.accessToken, cred.options)
              }
            case cred: ClientCredentialsCredential =>
              // If it has a valid access token, return it. Otherwise obtain a new token.
              cred.maybeAccessToken match {
                case Some(accessToken) =>
                  val expiresBy = cred.maybeExpiresBy
                    .getOrElse(throw new CredentialsException("Access token does not have an expiry date"))
                  if (expiresBy.isBefore(Instant.now())) {
                    refreshClientCredentialsCredential(cred)
                  } else {
                    BearerToken(accessToken, cred.options)
                  }
                case None => refreshClientCredentialsCredential(cred)
              }
          }
      }
  }

  private def refreshTokenCredential(cred: TokenCredential): NewHttpAuth = {
    cred.provider match {
      case OAuth2Provider.Shopify => CustomHeaderToken("X-Shopify-Access-Token", cred.accessToken, Map.empty)
      case _ =>
        val cli = cred.provider match {
          case raw.creds.api.OAuth2Provider.Dropbox => new DropboxOAuth2Client()
          case raw.creds.api.OAuth2Provider.Auth0 => new Auth0OAuth2Client()
          case raw.creds.api.OAuth2Provider.Twitter => new TwitterOauth2Client()
          case raw.creds.api.OAuth2Provider.LinkedIn => new LinkedInOauth2Client()
          case raw.creds.api.OAuth2Provider.Zoho => new ZohoOauth2Client()
          case raw.creds.api.OAuth2Provider.GoogleApi => new GoogleApiKeyOAuth2Client()
        }
        val newToken = cli.newAccessTokenFromRefreshToken(cred.refreshToken.get, cred.options)
        BearerToken(newToken.accessToken, cred.options)
    }
  }

  private def refreshClientCredentialsCredential(cred: ClientCredentialsCredential): NewHttpAuth = {
    cred.provider match {
      case OAuth2Provider.Shopify => CustomHeaderToken("X-Shopify-Access-Token", cred.maybeAccessToken.get, Map.empty)
      case _ =>
        val cli = cred.provider match {
          case raw.creds.api.OAuth2Provider.Dropbox => new DropboxOAuth2Client()
          case raw.creds.api.OAuth2Provider.Auth0 => new Auth0OAuth2Client()
          case raw.creds.api.OAuth2Provider.Twitter => new TwitterOauth2Client()
          case raw.creds.api.OAuth2Provider.LinkedIn => new LinkedInOauth2Client()
          case raw.creds.api.OAuth2Provider.Zoho => new ZohoOauth2Client()
          case raw.creds.api.OAuth2Provider.GoogleApi => new GoogleApiKeyOAuth2Client()
        }
        val newToken = cli.newAccessTokenFromClientCredentials(cred.clientId, cred.clientSecret, cred.options)
        BearerToken(newToken.accessToken, cred.options)
    }
  }

  private val rdbmsServers = scala.collection.mutable.Map[(Uid, String), RelationalDatabaseCredential]()

  override protected def doRegisterRDBMSServer(
      user: AuthenticatedUser,
      name: String,
      db: RelationalDatabaseCredential
  ): Boolean = {
    rdbmsServers.put((user.uid, name), db).isEmpty
  }

  override def getRDBMSServer(user: AuthenticatedUser, name: String): Option[RelationalDatabaseCredential] = {
    rdbmsServers.get((user.uid, name))
  }

  override def listRDBMSServers(user: AuthenticatedUser): List[String] = {
    rdbmsServers.keys.filter(_._1 == user.uid).map(_._2).toList
  }

  override def unregisterRDBMSServer(user: AuthenticatedUser, name: String): Boolean = {
    rdbmsServers.remove((user.uid, name)).isDefined
  }

  private val httpCreds = scala.collection.mutable.Map[(Uid, String), HttpCredential]()

  override protected def doRegisterHTTPCred(user: AuthenticatedUser, cred: HttpCredential): Boolean = {
    httpCreds.put((user.uid, cred.prefixUrl), cred).isEmpty
  }

  override def getHTTPCred(user: AuthenticatedUser, url: String): Option[HttpCredential] = {
    httpCreds.get((user.uid, url))
  }

  override def listHTTPCreds(user: AuthenticatedUser): List[String] = {
    httpCreds.keys.filter(_._1 == user.uid).map(_._2).toList
  }

  override def unregisterHTTPCred(user: AuthenticatedUser, url: String): Boolean = {
    httpCreds.remove((user.uid, url)).isDefined
  }

  private val secrets = scala.collection.mutable.Map[(Uid, String), Secret]()

  override protected def doRegisterSecret(user: AuthenticatedUser, secret: Secret): Boolean = {
    secrets.put((user.uid, secret.name), secret).isEmpty
  }

  override def getSecret(user: AuthenticatedUser, name: String): Option[Secret] = {
    secrets.get((user.uid, name))
  }

  override def listSecrets(user: AuthenticatedUser): List[String] = {
    secrets.keys.filter(_._1 == user.uid).map(_._2).toList
  }

  override def unregisterSecret(user: AuthenticatedUser, name: String): Boolean = {
    secrets.remove((user.uid, name)).isDefined
  }

  override def doStop(): Unit = {}
}
