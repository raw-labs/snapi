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

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import raw.creds.api._
import raw.creds.client.ClientCredentialsService.{CACHE_FDW_EXPIRY_IN_HOURS, CACHE_FDW_SIZE, DEFAULT_CACHE_FDW_EXPIRY_IN_HOURS, DEFAULT_CACHE_FDW_SIZE, SERVER_ADDRESS}
import raw.rest.client.APIException
import raw.utils.{AuthenticatedUser, RawSettings}

import java.net.URI
import java.util.concurrent.TimeUnit

object ClientCredentialsService {
  private val SERVER_ADDRESS = "raw.creds.client.server-address"
  private val CACHE_FDW_SIZE = "raw.creds.client.fdw-db-cache.size"
  private val DEFAULT_CACHE_FDW_SIZE = 1000
  private val CACHE_FDW_EXPIRY_IN_HOURS = "raw.creds.client.fdw-db-cache.expiry-in-hours"
  private val DEFAULT_CACHE_FDW_EXPIRY_IN_HOURS = 1
}

class ClientCredentialsService(implicit settings: RawSettings) extends CredentialsService {

  private val serverAddress = new URI(settings.getString(SERVER_ADDRESS))

  private val client = new ClientCredentials(serverAddress)

  /** S3 buckets */

  override protected def doRegisterS3Bucket(user: AuthenticatedUser, bucket: S3Bucket): Boolean = {
    try {
      client.registerS3Bucket(user, bucket)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def getS3Bucket(user: AuthenticatedUser, name: String): Option[S3Bucket] = {
    try {
      client.getS3Bucket(user, name)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def listS3Buckets(user: AuthenticatedUser): List[String] = {
    try {
      client.listS3Buckets(user)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def unregisterS3Bucket(user: AuthenticatedUser, name: String): Boolean = {
    try {
      client.unregisterS3Bucket(user, name)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  /** Dropbox tokens */

  override def registerDropboxToken(user: AuthenticatedUser, dropboxToken: DropboxToken): Boolean = {
    try {
      client.registerDropboxToken(user, dropboxToken)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def getDropboxToken(user: AuthenticatedUser): Option[DropboxToken] = {
    try {
      client.getDropboxToken(user)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def unregisterDropboxToken(user: AuthenticatedUser): Boolean = {
    try {
      client.unregisterDropboxToken(user)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  /** ExternalConnector */

  override def registerExternalConnectorCredential(
      user: AuthenticatedUser,
      name: String,
      externalConnectorCredential: ExternalConnectorCredential
  ): Boolean = {
    try {
      client.registerExternalConnectorCredential(user, name, externalConnectorCredential)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def getExternalConnectorCredential(
      user: AuthenticatedUser,
      name: String
  ): Option[ExternalConnectorCredential] = {
    try {
      client.getExternalConnectorCredential(user, name)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def listExternalConnectorCredentials(user: AuthenticatedUser): List[ExternalConnectorCredentialId] = {
    try {
      client.listExternalConnectorCredentials(user)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def unregisterExternalConnectorCredential(user: AuthenticatedUser, name: String): Boolean = {
    try {
      client.unregisterExternalConnectorCredential(user, name)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  /** Http Credentials */

  override protected def doRegisterNewHttpCredential(
      user: AuthenticatedUser,
      name: String,
      token: NewHttpCredential
  ): Boolean = {
    try {
      client.registerHttpCredential(user, name, token)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def getNewHttpCredential(user: AuthenticatedUser, name: String): Option[NewHttpCredential] = {
    try {
      client.getHttpCredential(user, name)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def unregisterNewHttpCredential(user: AuthenticatedUser, name: String): Boolean = {
    try {
      client.unregisterHttpCredential(user, name)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def listNewHttpCredentials(user: AuthenticatedUser): List[HttpCredentialId] = {
    try {
      client.listHttpCredentials(user)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def getNewHttpAuth(user: AuthenticatedUser, name: String): Option[NewHttpAuth] = {
    try {
      client.getHttpAuth(user, name)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  /** RDBMS servers */

  override protected def doRegisterRDBMSServer(
      user: AuthenticatedUser,
      name: String,
      db: RelationalDatabaseCredential
  ): Boolean = {
    try {
      client.registerRDBMSServer(user, name, db)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def getRDBMSServer(user: AuthenticatedUser, name: String): Option[RelationalDatabaseCredential] = {
    try {
      client.getRDBMSServer(user, name)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def listRDBMSServers(user: AuthenticatedUser): List[String] = {
    try {
      client.listRDBMSServers(user)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def unregisterRDBMSServer(user: AuthenticatedUser, name: String): Boolean = {
    try {
      client.unregisterRDBMSServer(user, name)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  /** HTTP credentials */

  override protected def doRegisterHTTPCred(user: AuthenticatedUser, cred: HttpCredential): Boolean = {
    try {
      client.registerHTTPCred(user, cred)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def getHTTPCred(user: AuthenticatedUser, url: String): Option[HttpCredential] = {
    try {
      client.getHTTPCred(user, url)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def listHTTPCreds(user: AuthenticatedUser): List[String] = {
    try {
      client.listHTTPCreds(user)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def unregisterHTTPCred(user: AuthenticatedUser, url: String): Boolean = {
    try {
      client.unregisterHTTPCred(user, url)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  /** Secret credentials */

  override def doRegisterSecret(user: AuthenticatedUser, secret: Secret): Boolean = {
    try {
      client.registerSecret(user, secret)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def getSecret(user: AuthenticatedUser, name: String): Option[Secret] = {
    try {
      client.getSecret(user, name)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def listSecrets(user: AuthenticatedUser): List[String] = {
    try {
      client.listSecrets(user)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def unregisterSecret(user: AuthenticatedUser, name: String): Boolean = {
    try {
      client.unregisterSecret(user, name)
    } catch {
      case ex: APIException => throw new ClientCredentialsException(ex.getMessage, ex)
    }
  }

  override def doStop(): Unit = {
    client.close()
  }

  // Define the CacheLoader
  private val dbCacheLoader = new CacheLoader[AuthenticatedUser, String]() {
    override def load(user: AuthenticatedUser): String = {
      // Directly call the provisioning method on the client
      logger.debug(s"Retrieving user database for $user from origin server")
      client.getUserDb(user)
    }
  }

  // Initialize FDW DB LoadingCache
  private val dbCache: LoadingCache[AuthenticatedUser, String] = CacheBuilder
    .newBuilder()
    .maximumSize(settings.getIntOpt(CACHE_FDW_SIZE).getOrElse(DEFAULT_CACHE_FDW_SIZE))
    .expireAfterAccess(settings.getIntOpt(CACHE_FDW_EXPIRY_IN_HOURS).getOrElse(DEFAULT_CACHE_FDW_EXPIRY_IN_HOURS), TimeUnit.HOURS)
    .build(dbCacheLoader)

  override def getUserDb(user: AuthenticatedUser): String = {
    // Retrieve the database name from the cache, provisioning it if necessary
    logger.debug(s"Retrieving user database for $user")
    dbCache.get(user)
  }

}
