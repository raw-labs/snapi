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

package raw.creds.client

import com.typesafe.scalalogging.StrictLogging
import org.apache.hc.core5.http.HttpStatus
import raw.utils.{AuthenticatedUser, RawSettings}
import raw.creds.api._
import raw.creds.protocol._
import raw.rest.client.{ClientAPIException, RestClient}

import java.net.URI

class ClientCredentials(serverAddress: URI)(implicit settings: RawSettings) extends StrictLogging {

  private val restClient = new RestClient(serverAddress, None, "credentials")

  /** S3 buckets */

  def registerS3Bucket(user: AuthenticatedUser, bucket: S3Bucket): Boolean = {
    try {
      restClient.doJsonPostWithEmptyResponse(
        "2/s3/register",
        RegisterS3BucketCredential(user, bucket),
        withAuth = false
      )
      true
    } catch {
      case ex: ClientAPIException if ex.errorCode == "s3CredentialsAlreadyExists" => false
    }
  }

  def getS3Bucket(user: AuthenticatedUser, name: String): Option[S3Bucket] = {
    try {
      Some(restClient.doJsonPost[S3Bucket]("2/s3/get", GetS3BucketCredential(user, name), withAuth = false))
    } catch {
      case ex: ClientAPIException if ex.errorCode == "s3CredentialsNotFound" => None
    }
  }

  def existsS3Bucket(user: AuthenticatedUser, name: String): Boolean = getS3Bucket(user, name).isDefined

  def listS3Buckets(user: AuthenticatedUser): List[String] = {
    restClient.doJsonPost[List[String]]("2/s3/list", ListS3BucketCredentials(user), withAuth = false)
  }

  def unregisterS3Bucket(user: AuthenticatedUser, name: String): Boolean = {
    try {
      restClient.doJsonPostWithEmptyResponse(
        "2/s3/unregister",
        UnregisterS3BucketCredential(user, name),
        HttpStatus.SC_NO_CONTENT,
        withAuth = false
      )
      true
    } catch {
      case ex: ClientAPIException if ex.errorCode == "s3CredentialsNotFound" => false
    }
  }

  /** Dropbox tokens */

  def registerDropboxToken(user: AuthenticatedUser, dropboxToken: DropboxToken): Boolean = {
    try {
      restClient.doJsonPostWithEmptyResponse("2/dropbox/register", dropboxToken, withAuth = false)
      true
    } catch {
      case ex: ClientAPIException if ex.errorCode == "dropboxTokenAlreadyExists" => false
    }
  }

  def getDropboxToken(user: AuthenticatedUser): Option[DropboxToken] = {
    try {
      Some(restClient.doJsonPost[DropboxToken]("2/dropbox/get", GetDropboxCredential(user), withAuth = false))
    } catch {
      case ex: ClientAPIException if ex.errorCode == "dropboxTokenNotFound" => None
    }
  }

  def existsDropboxToken(user: AuthenticatedUser): Boolean = getDropboxToken(user).isDefined

  def unregisterDropboxToken(user: AuthenticatedUser): Boolean = {
    try {
      restClient.doJsonPostWithEmptyResponse(
        "2/dropbox/unregister",
        UnregisterDropboxCredential(user),
        HttpStatus.SC_NO_CONTENT,
        withAuth = false
      )
      true
    } catch {
      case ex: ClientAPIException if ex.errorCode == "dropboxTokenNotFound" => false
    }
  }

  /** HTTP Credentials */

  def listHttpCredentials(user: AuthenticatedUser): List[HttpCredentialId] = {
    restClient.doJsonPost[List[HttpCredentialId]]("2/http/list", List(user), withAuth = false)
  }

  def registerHttpCredential(user: AuthenticatedUser, name: String, token: NewHttpCredential): Boolean = {
    try {
      restClient.doJsonPostWithEmptyResponse(
        "2/http/register",
        RegisterNewHttpCredential(user, name, token),
        withAuth = false
      )
      true
    } catch {
      case ex: ClientAPIException if ex.errorCode == "httpCredentialsAlreadyExists" => false
    }
  }

  def getHttpCredential(user: AuthenticatedUser, name: String): Option[NewHttpCredential] = {
    try {
      Some(restClient.doJsonPost[NewHttpCredential]("2/http/get", GetNewHttpCredential(user, name), withAuth = false))
    } catch {
      case ex: ClientAPIException if ex.errorCode == "httpCredentialsNotFound" => None
    }
  }

  def existsHttpCredential(user: AuthenticatedUser, name: String): Boolean = getHttpCredential(user, name).isDefined

  def unregisterHttpCredential(user: AuthenticatedUser, name: String): Boolean = {
    try {
      restClient.doJsonPostWithEmptyResponse(
        "2/http/unregister",
        UnregisterNewHttpCredential(user, name),
        HttpStatus.SC_NO_CONTENT,
        withAuth = false
      )
      true
    } catch {
      case ex: ClientAPIException if ex.errorCode == "httpCredentialsNotFound" => false
    }
  }

  def getHttpAuth(user: AuthenticatedUser, name: String): Option[NewHttpAuth] = {
    try {
      Some(
        restClient.doJsonPost[NewHttpAuth]("2/http/get-token", GetTokenNewHttpCredential(user, name), withAuth = false)
      )
    } catch {
      case ex: ClientAPIException if ex.errorCode == "httpCredentialsNotFound" => None
    }
  }

  /** RDBMS servers */

  def registerRDBMSServer(user: AuthenticatedUser, name: String, db: RelationalDatabaseCredential): Boolean = {
    try {
      restClient.doJsonPostWithEmptyResponse(
        "2/rdbms/register",
        RegisterRelationalDatabaseCredential(user, name, db),
        withAuth = false
      )
      true
    } catch {
      case ex: ClientAPIException if ex.errorCode == "databaseCredentialsAlreadyExists" => false
    }
  }

  def getRDBMSServer(user: AuthenticatedUser, name: String): Option[RelationalDatabaseCredential] = {
    try {
      Some(
        restClient.doJsonPost[RelationalDatabaseCredential](
          "2/rdbms/get",
          GetRelationalDatabaseCredential(user, name),
          withAuth = false
        )
      )
    } catch {
      case ex: ClientAPIException if ex.errorCode == "databaseCredentialsNotFound" => None
    }
  }

  def existsRDBMSServer(user: AuthenticatedUser, name: String): Boolean = getRDBMSServer(user, name).isDefined

  def listRDBMSServers(user: AuthenticatedUser): List[String] = {
    restClient.doJsonPost[List[String]]("2/rdbms/list", ListRelationalDatabaseCredentials(user), withAuth = false)
  }

  def unregisterRDBMSServer(user: AuthenticatedUser, name: String): Boolean = {
    try {
      restClient.doJsonPostWithEmptyResponse(
        "2/rdbms/unregister",
        UnregisterRelationalDatabaseCredential(user, name),
        HttpStatus.SC_NO_CONTENT,
        withAuth = false
      )
      true
    } catch {
      case ex: ClientAPIException if ex.errorCode == "databaseCredentialsNotFound" => false

    }
  }

  /** HTTP credentials */
  @deprecated("Use registerHttpCredential instead", since = "2022-06-14")
  def registerHTTPCred(user: AuthenticatedUser, auth: HttpCredential): Boolean = {
    try {
      restClient.doJsonPostWithEmptyResponse(
        "2/http-legacy/register",
        RegisterHttpCredential(user, auth),
        withAuth = false
      )
      true
    } catch {
      case ex: ClientAPIException if ex.errorCode == "httpCredentialsAlreadyExists" => false
    }
  }

  @deprecated("Use getHttpCredential instead", since = "2022-06-14")
  def getHTTPCred(user: AuthenticatedUser, url: String): Option[HttpCredential] = {
    try {
      Some(restClient.doJsonPost[HttpCredential]("2/http-legacy/get", GetHttpCredential(user, url), withAuth = false))
    } catch {
      case ex: ClientAPIException if ex.errorCode == "httpCredentialsNotFound" => None
    }
  }

  @deprecated("Use existsHttpCredential instead", since = "2022-06-14")
  def existsHTTPCred(user: AuthenticatedUser, url: String): Boolean = getHTTPCred(user, url).isDefined

  @deprecated("Use listHttpCredentials instead", since = "2022-06-14")
  def listHTTPCreds(user: AuthenticatedUser): List[String] = {
    restClient.doJsonPost[List[String]]("2/http-legacy/list", ListHttpCredentials(user), withAuth = false)
  }

  @deprecated("Use unregisterHttpCredential instead", since = "2022-06-14")
  def unregisterHTTPCred(user: AuthenticatedUser, url: String): Boolean = {
    try {
      restClient.doJsonPostWithEmptyResponse(
        "2/http-legacy/unregister",
        UnregisterHttpCredential(user, url),
        HttpStatus.SC_NO_CONTENT,
        withAuth = false
      )
      true
    } catch {
      case ex: ClientAPIException if ex.errorCode == "httpCredentialsNotFound" => false
    }
  }

  /** Secrets */

  def registerSecret(user: AuthenticatedUser, secret: Secret): Boolean = {
    try {
      restClient.doJsonPostWithEmptyResponse(
        "2/secrets/register",
        RegisterSecretCredential(user, secret),
        withAuth = false
      )
      true
    } catch {
      case ex: ClientAPIException if ex.errorCode == "secretAlreadyExists" => false
    }
  }

  def getSecret(user: AuthenticatedUser, name: String): Option[Secret] = {
    try {
      Some(restClient.doJsonPost[Secret]("2/secrets/get", GetSecretCredential(user, name), withAuth = false))
    } catch {
      case ex: ClientAPIException if ex.errorCode == "secretNotFound" => None
    }
  }

  def existsSecret(user: AuthenticatedUser, name: String): Boolean = getSecret(user, name).isDefined

  def unregisterSecret(user: AuthenticatedUser, name: String): Boolean = {
    try {
      restClient.doJsonPostWithEmptyResponse(
        "2/secrets/unregister",
        UnregisterSecretCredential(user, name),
        HttpStatus.SC_NO_CONTENT,
        withAuth = false
      )
      true
    } catch {
      case ex: ClientAPIException if ex.errorCode == "secretNotFound" => false
    }
  }

  def listSecrets(user: AuthenticatedUser): List[String] = {
    restClient.doJsonPost[List[String]]("2/secrets/list", ListSecretCredentials(user), withAuth = false)
  }

  def close(): Unit = {
    restClient.close()
  }

}
