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

package raw.cli

import raw.api.AuthenticatedUser
import raw.config.RawSettings
import raw.creds._

class LocalCredentialsService(implicit settings: RawSettings) extends CredentialsService {

  override protected def doRegisterS3Bucket(user: AuthenticatedUser, bucket: S3Bucket): Boolean = {
    throw new NotImplementedError("unsupported operation")
  }

  override def getS3Bucket(user: AuthenticatedUser, name: String): Option[S3Bucket] = {
    throw new NotImplementedError("unsupported operation")
  }

  override def listS3Buckets(user: AuthenticatedUser): List[String] = {
    throw new NotImplementedError("unsupported operation")
  }

  override def unregisterS3Bucket(user: AuthenticatedUser, name: String): Boolean = {
    throw new NotImplementedError("unsupported operation")
  }

  override def registerDropboxToken(user: AuthenticatedUser, dropboxToken: DropboxToken): Boolean = {
    throw new NotImplementedError("unsupported operation")
  }

  override def getDropboxToken(user: AuthenticatedUser): Option[DropboxToken] = {
    throw new NotImplementedError("unsupported operation")
  }

  override def unregisterDropboxToken(user: AuthenticatedUser): Boolean = {
    throw new NotImplementedError("unsupported operation")
  }

  override protected def doRegisterNewHttpCredential(
      user: AuthenticatedUser,
      name: String,
      token: NewHttpCredential
  ): Boolean = {
    throw new NotImplementedError("unsupported operation")
  }

  override def getNewHttpCredential(user: AuthenticatedUser, name: String): Option[NewHttpCredential] = {
    throw new NotImplementedError("unsupported operation")
  }

  override def unregisterNewHttpCredential(user: AuthenticatedUser, name: String): Boolean = {
    throw new NotImplementedError("unsupported operation")
  }

  override def listNewHttpCredentials(user: AuthenticatedUser): List[HttpCredentialId] = {
    throw new NotImplementedError("unsupported operation")
  }

  override def getNewHttpAuth(user: AuthenticatedUser, name: String): Option[NewHttpAuth] = {
    throw new NotImplementedError("unsupported operation")
  }

  override protected def doRegisterRDBMSServer(
      user: AuthenticatedUser,
      name: String,
      db: RelationalDatabaseCredential
  ): Boolean = {
    throw new NotImplementedError("unsupported operation")
  }

  override def getRDBMSServer(user: AuthenticatedUser, name: String): Option[RelationalDatabaseCredential] = {
    throw new NotImplementedError("unsupported operation")
  }

  override def listRDBMSServers(user: AuthenticatedUser): List[String] = {
    throw new NotImplementedError("unsupported operation")
  }

  override def unregisterRDBMSServer(user: AuthenticatedUser, name: String): Boolean = {
    throw new NotImplementedError("unsupported operation")
  }

  override protected def doRegisterHTTPCred(user: AuthenticatedUser, cred: HttpCredential): Boolean = {
    throw new NotImplementedError("unsupported operation")
  }

  override def getHTTPCred(user: AuthenticatedUser, url: String): Option[HttpCredential] = {
    throw new NotImplementedError("unsupported operation")
  }

  override def listHTTPCreds(user: AuthenticatedUser): List[String] = {
    throw new NotImplementedError("unsupported operation")
  }

  override def unregisterHTTPCred(user: AuthenticatedUser, url: String): Boolean = {
    throw new NotImplementedError("unsupported operation")
  }

  override def doRegisterSecret(user: AuthenticatedUser, secret: Secret): Boolean = {
    throw new NotImplementedError("unsupported operation")
  }

  override def getSecret(user: AuthenticatedUser, name: String): Option[Secret] = {
    throw new NotImplementedError("unsupported operation")
  }

  override def listSecrets(user: AuthenticatedUser): List[String] = {
    throw new NotImplementedError("unsupported operation")
  }

  override def unregisterSecret(user: AuthenticatedUser, name: String): Boolean = {
    throw new NotImplementedError("unsupported operation")
  }

  override def doStop(): Unit = {}
}
