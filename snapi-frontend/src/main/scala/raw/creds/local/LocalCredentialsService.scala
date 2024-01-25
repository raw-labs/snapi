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

package raw.creds.local

import raw.utils.AuthenticatedUser
import raw.creds.api._

class LocalCredentialsService extends CredentialsService {

  override protected def doRegisterS3Bucket(user: AuthenticatedUser, bucket: S3Bucket): Boolean = {
    false
  }

  override def getS3Bucket(user: AuthenticatedUser, name: String): Option[S3Bucket] = {
    None
  }

  override def listS3Buckets(user: AuthenticatedUser): List[String] = {
    List.empty
  }

  override def unregisterS3Bucket(user: AuthenticatedUser, name: String): Boolean = {
    false
  }

  override def registerDropboxToken(user: AuthenticatedUser, dropboxToken: DropboxToken): Boolean = {
    false
  }

  override def getDropboxToken(user: AuthenticatedUser): Option[DropboxToken] = {
    None
  }

  override def unregisterDropboxToken(user: AuthenticatedUser): Boolean = {
    false
  }

  override def registerSalesforceCredential(
      user: AuthenticatedUser,
      name: String,
      salesforceCredential: SalesforceCredential
  ): Boolean = {
    false
  }

  override def getSalesforceCredential(user: AuthenticatedUser, name: String): Option[SalesforceCredential] = {
    None
  }

  override def listSalesforceCredentials(user: AuthenticatedUser): List[String] = {
    List.empty
  }

  override def unregisterSalesforceCredential(user: AuthenticatedUser, name: String): Boolean = {
    false
  }

  override protected def doRegisterNewHttpCredential(
      user: AuthenticatedUser,
      name: String,
      token: NewHttpCredential
  ): Boolean = {
    false
  }

  override def getNewHttpCredential(user: AuthenticatedUser, name: String): Option[NewHttpCredential] = {
    None
  }

  override def unregisterNewHttpCredential(user: AuthenticatedUser, name: String): Boolean = {
    false
  }

  override def listNewHttpCredentials(user: AuthenticatedUser): List[HttpCredentialId] = {
    List.empty
  }

  override def getNewHttpAuth(user: AuthenticatedUser, name: String): Option[NewHttpAuth] = {
    None
  }

  override protected def doRegisterRDBMSServer(
      user: AuthenticatedUser,
      name: String,
      db: RelationalDatabaseCredential
  ): Boolean = {
    false
  }

  override def getRDBMSServer(user: AuthenticatedUser, name: String): Option[RelationalDatabaseCredential] = {
    None
  }

  override def listRDBMSServers(user: AuthenticatedUser): List[String] = {
    List.empty
  }

  override def unregisterRDBMSServer(user: AuthenticatedUser, name: String): Boolean = {
    false
  }

  override protected def doRegisterHTTPCred(user: AuthenticatedUser, cred: HttpCredential): Boolean = {
    false
  }

  override def getHTTPCred(user: AuthenticatedUser, url: String): Option[HttpCredential] = {
    None
  }

  override def listHTTPCreds(user: AuthenticatedUser): List[String] = {
    List.empty
  }

  override def unregisterHTTPCred(user: AuthenticatedUser, url: String): Boolean = {
    false
  }

  override def doRegisterSecret(user: AuthenticatedUser, secret: Secret): Boolean = {
    false
  }

  override def getSecret(user: AuthenticatedUser, name: String): Option[Secret] = {
    val envVariable = s"SNAPI_${name.toUpperCase}"
    sys.env.get(envVariable).map(v => Secret(envVariable, v))
  }

  override def listSecrets(user: AuthenticatedUser): List[String] = {
    sys.env.keys.filter(_.startsWith("SNAPI_")).map(_.substring(6)).toList
  }

  override def unregisterSecret(user: AuthenticatedUser, name: String): Boolean = {
    false
  }

  override def doStop(): Unit = {}

}
