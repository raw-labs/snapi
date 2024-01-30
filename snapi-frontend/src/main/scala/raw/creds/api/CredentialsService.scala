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

package raw.creds.api

import raw.utils.{AuthenticatedUser, RawService}
import raw.creds.api.CredentialsService.{bucketNameRegex, credNamePattern}

object CredentialsService {
  // Refer to https://docs.aws.amazon.com/AmazonS3/latest/userguide/bucketnamingrules.html
  private val bucketNameRegex = """^[a-z0-9][a-z0-9.-]*[a-z0-9]$""".r

  // A single word (letters, numbers or underscore) or a word , followed by zero or more words or dashes or dots, followed by a word.
  // examples of valid names: "a", "a1", "a-b"
  // examples of invalid names: "a b", "a-", "-a"
  // Used name validation for secrets, new-http-credentials and rdbms-servers.
  // things like s3 buckets, http-prefixes have their own validation
  private val credNamePattern = "^(\\w|\\w[\\w\\.-]*\\w)$".r

  //Should we have a list of regions?
  // maybe in the future amazon will add more and we have to keep updating
  private val validRegions = Array(
    "us-east-2",
    "us-east-1",
    "us-west-1",
    "us-west-2",
    "ap-east-1",
    "ap-south-1",
    "ap-northeast-3",
    "ap-northeast-2",
    "ap-southeast-1",
    "ap-southeast-2",
    "ap-northeast-1",
    "ca-central-1",
    "cn-north-1",
    "cn-northwest-1",
    "eu-central-1",
    "eu-west-1",
    "eu-west-2",
    "eu-west-3",
    "eu-north-1",
    "me-south-1",
    "sa-east-1",
    "us-gov-east-1",
    "us-gov-west-1"
  )

  def validateBucketRegion(region: String): Boolean = {
    validRegions.contains(region)
  }
}

trait CredentialsService extends RawService {

  /** S3 buckets */

  private def validateS3BucketName(name: String): Unit = {
    if (name == null) {
      throw new CredentialsException("null name")
    } else if (name.length < 3) {
      throw new CredentialsException(s"credential name too small: '$name'")
    } else if (name.length > 63) {
      throw new CredentialsException(s"credential name too long: '$name'")
    } else if (bucketNameRegex.findFirstMatchIn(name).isEmpty) {
      throw new CredentialsException(s"credential name is invalid: '$name'")
    }
  }

  final def registerS3Bucket(user: AuthenticatedUser, bucket: S3Bucket): Boolean = {
    validateS3BucketName(bucket.name)
    doRegisterS3Bucket(user, bucket)
  }

  protected def doRegisterS3Bucket(user: AuthenticatedUser, bucket: S3Bucket): Boolean

  def getS3Bucket(user: AuthenticatedUser, name: String): Option[S3Bucket]

  def existsS3Bucket(user: AuthenticatedUser, name: String): Boolean = getS3Bucket(user, name).isDefined

  def listS3Buckets(user: AuthenticatedUser): List[String]

  def unregisterS3Bucket(user: AuthenticatedUser, name: String): Boolean

  /** Dropbox tokens */

  def registerDropboxToken(user: AuthenticatedUser, dropboxToken: DropboxToken): Boolean

  def getDropboxToken(user: AuthenticatedUser): Option[DropboxToken]

  def existsDropboxToken(user: AuthenticatedUser): Boolean = getDropboxToken(user).isDefined

  def unregisterDropboxToken(user: AuthenticatedUser): Boolean

  /** ExternalConnector */

  def registerExternalConnectorCredential(
      user: AuthenticatedUser,
      name: String,
      credential: ExternalConnectorCredential
  ): Boolean

  def getExternalConnectorCredential(user: AuthenticatedUser, name: String): Option[ExternalConnectorCredential]

  def existsExternalConnectorCredential(user: AuthenticatedUser, name: String): Boolean =
    getExternalConnectorCredential(user, name).isDefined

  def listExternalConnectorCredentials(user: AuthenticatedUser): List[ExternalConnectorCredentialId]

  def unregisterExternalConnectorCredential(user: AuthenticatedUser, name: String): Boolean

  /** Http Credentials */

  protected def validateNewHttpCredentialName(name: String) = {
    if (name == null) {
      throw new CredentialsException("null name")
    } else if (name == "") {
      throw new CredentialsException(s"credential name is empty")
    } else if (name.length > 64) {
      throw new CredentialsException(s"credential name too long: '$name'")
    } else if (credNamePattern.findFirstMatchIn(name).isEmpty) {
      throw new CredentialsException(s"credential name is invalid: '$name'")
    }
  }

  // TODO: Distinguish between an OAuth2Credential from a plain access token. OAuth2Credential is a way of obtaining a token
  //   and can be using refresh tokens, client id/secret or a static access token
  protected def doRegisterNewHttpCredential(user: AuthenticatedUser, name: String, token: NewHttpCredential): Boolean

  final def registerNewHttpCredential(user: AuthenticatedUser, name: String, token: NewHttpCredential): Boolean = {
    validateNewHttpCredentialName(name)
    doRegisterNewHttpCredential(user, name, token)
  }

  def getNewHttpCredential(user: AuthenticatedUser, name: String): Option[NewHttpCredential]

  def existsNewHttpCredential(user: AuthenticatedUser, name: String): Boolean =
    getNewHttpCredential(user, name).isDefined

  def unregisterNewHttpCredential(user: AuthenticatedUser, name: String): Boolean

  def listNewHttpCredentials(user: AuthenticatedUser): List[HttpCredentialId]

  /**
   * Obtain an access token from the OAuth2 credential with the given name. The service will try do the necessary to
   * generate a valid token, using the OAuth2 flow associated with the credential (client credentials, refresh token).
   * This may not always be possible, if the credential does not have enough information to obtain a new token. In this
   * case, it may return expired access tokens.
   */
  def getNewHttpAuth(user: AuthenticatedUser, name: String): Option[NewHttpAuth]

  /** RDBMS servers */

  protected def validateRDBMSName(name: String) = {
    if (name == null) {
      throw new CredentialsException("null name")
    } else if (name == "") {
      throw new CredentialsException(s"credential name is empty")
    } else if (name.length > 64) {
      throw new CredentialsException(s"credential name too long: '$name'")
    } else if (credNamePattern.findFirstMatchIn(name).isEmpty) {
      throw new CredentialsException(s"credential name is invalid: '$name'")
    }
  }

  final def registerRDBMSServer(user: AuthenticatedUser, name: String, db: RelationalDatabaseCredential): Boolean = {
    validateRDBMSName(name)
    doRegisterRDBMSServer(user, name, db)
  }

  protected def doRegisterRDBMSServer(user: AuthenticatedUser, name: String, db: RelationalDatabaseCredential): Boolean

  def getRDBMSServer(user: AuthenticatedUser, name: String): Option[RelationalDatabaseCredential]

  def existsRDBMSServer(user: AuthenticatedUser, name: String): Boolean = getRDBMSServer(user, name).isDefined

  def listRDBMSServers(user: AuthenticatedUser): List[String]

  def unregisterRDBMSServer(user: AuthenticatedUser, name: String): Boolean

  /** HTTP credentials */

  protected def validateHttpPrefix(prefix: String) = {
    if (prefix == null) throw new CredentialsException("null prefix")
    else if (prefix == "") throw new CredentialsException("empty prefix")
  }

  @deprecated("Use registerNewHttpCredentials instead", since = "2022-06-14")
  final def registerHTTPCred(user: AuthenticatedUser, cred: HttpCredential): Boolean = {
    validateHttpPrefix(cred.prefixUrl)
    doRegisterHTTPCred(user, cred)
  }

  protected def doRegisterHTTPCred(user: AuthenticatedUser, cred: HttpCredential): Boolean

  @deprecated("Use getNewHttpCredentials instead", since = "2022-06-14")
  def getHTTPCred(user: AuthenticatedUser, url: String): Option[HttpCredential]

  @deprecated("Use existsNewHttpCredentials instead", since = "2022-06-14")
  def existsHTTPCred(user: AuthenticatedUser, url: String): Boolean = getHTTPCred(user, url).isDefined

  @deprecated("Use listNewHttpCredentials instead", since = "2022-06-14")
  def listHTTPCreds(user: AuthenticatedUser): List[String]

  @deprecated("Use unregisterNewHttpCredentials instead", since = "2022-06-14")
  def unregisterHTTPCred(user: AuthenticatedUser, url: String): Boolean

  protected def doRegisterSecret(user: AuthenticatedUser, secret: Secret): Boolean

  private def validateSecretName(name: String) = {
    if (name == null) {
      throw new CredentialsException("null name")
    } else if (name == "") {
      throw new CredentialsException(s"credential name is empty")
    } else if (name.length > 64) {
      throw new CredentialsException(s"credential name too long: '$name'")
    } else if (credNamePattern.findFirstMatchIn(name).isEmpty) {
      throw new CredentialsException(s"credential name is invalid: '$name'")
    }
  }

  final def registerSecret(user: AuthenticatedUser, secret: Secret): Boolean = {
    validateSecretName(secret.name)
    doRegisterSecret(user, secret)
  }

  def getSecret(user: AuthenticatedUser, name: String): Option[Secret]

  def existsSecret(user: AuthenticatedUser, name: String): Boolean = getSecret(user, name).isDefined

  def listSecrets(user: AuthenticatedUser): List[String]

  def unregisterSecret(user: AuthenticatedUser, name: String): Boolean
}
