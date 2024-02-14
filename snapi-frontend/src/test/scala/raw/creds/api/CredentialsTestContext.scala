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

import org.scalatest.BeforeAndAfterAll
import raw.utils.{AuthenticatedUser, RawTestSuite, RawUtils, SettingsTestContext}

import java.time.temporal.ChronoUnit
import scala.collection.mutable

trait CredentialsTestContext extends BeforeAndAfterAll {
  this: RawTestSuite with SettingsTestContext =>

  private var instance: CredentialsService = _

  private val newHttpCreds = new mutable.ArrayBuffer[(AuthenticatedUser, (String, NewHttpCredential))]()
  private val s3Buckets = new mutable.HashSet[(AuthenticatedUser, S3Bucket)]()
  private val rdbmsServers = new mutable.HashMap[(AuthenticatedUser, String), RelationalDatabaseCredential]()
  private val dropboxTokens = new mutable.HashMap[AuthenticatedUser, DropboxToken]()
  private val secrets = new mutable.HashSet[(AuthenticatedUser, Secret)]()

  def rdbms(user: AuthenticatedUser, name: String, db: RelationalDatabaseCredential): Unit = {
    assert(rdbmsServers.put((user, name), db).isEmpty, "Reusing database name with different server")
  }

  def s3Bucket(user: AuthenticatedUser, s3Bucket: S3Bucket): Unit = {
    s3Buckets.add((user, s3Bucket))
  }

  def secret(user: AuthenticatedUser, name: String, value: String): Unit = {
    secrets.add((user, Secret(name, value)))
  }

  def dropbox(user: AuthenticatedUser, dropboxToken: DropboxToken): Unit = {
    dropboxTokens.put(user, dropboxToken)
  }

  def oauth(user: AuthenticatedUser, name: String, token: NewHttpCredential): Unit = {
    newHttpCreds.append((user, (name, token)))
  }

  def credentials: CredentialsService = instance

  def setCredentials(credentials: CredentialsService): Unit = {
    instance = credentials
    CredentialsServiceProvider.set(credentials)
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    s3Buckets.foreach { case (user, bucket) => credentials.registerS3Bucket(user, bucket) }
    rdbmsServers.foreach { case ((user, name), rdbms) => credentials.registerRDBMSServer(user, name, rdbms) }
    newHttpCreds.foreach { case (user, (name, cred)) => credentials.registerNewHttpCredential(user, name, cred) }
    dropboxTokens.foreach { case (user, token) => credentials.registerDropboxToken(user, token) }
    secrets.foreach { case (user, secret) => credentials.registerSecret(user, secret) }
    // Set the system properties for the credentials so that this is overriding what RawLanguage
    // runs with. This way, the credential server booted by the test framework is used.
  }

  override def afterAll(): Unit = {
    s3Buckets.foreach {
      case (user, bucket) => RawUtils.withSuppressNonFatalException(credentials.unregisterS3Bucket(user, bucket.name))
    }
    rdbmsServers.foreach {
      case ((user, name), _) => RawUtils.withSuppressNonFatalException(credentials.unregisterRDBMSServer(user, name))
    }
    newHttpCreds.foreach {
      case (user, (name, _)) =>
        RawUtils.withSuppressNonFatalException(credentials.unregisterNewHttpCredential(user, name))
    }
    dropboxTokens.foreach {
      case (user, _) => RawUtils.withSuppressNonFatalException(credentials.unregisterDropboxToken(user))
    }
    secrets.foreach {
      case (user, secret) => RawUtils.withSuppressNonFatalException(credentials.unregisterSecret(user, secret.name))
    }
    setCredentials(null)
    super.afterAll()
  }

  protected def compare(expected: TokenCredential, actual: TokenCredential): Unit = {
    assertResult(expected.provider)(actual.provider)
    assertResult(expected.accessToken)(actual.accessToken)
    assertResult(expected.scopes)(actual.scopes)
    assertResult(expected.refreshToken)(actual.refreshToken)
    assertResult(expected.expiresBy.map(_.truncatedTo(ChronoUnit.MILLIS)))(
      actual.expiresBy.map(_.truncatedTo(ChronoUnit.MILLIS))
    )
    assertResult(expected.options)(actual.options)
  }

  protected def compare(expected: ClientCredentialsCredential, actual: ClientCredentialsCredential): Unit = {
    assertResult(expected.provider)(actual.provider)
    assertResult(expected.clientId)(actual.clientId)
    assertResult(expected.clientSecret)(actual.clientSecret)
    assertResult(expected.options)(actual.options)
    assertResult(expected.maybeAccessToken)(actual.maybeAccessToken)
    assertResult(expected.maybeScopes)(actual.maybeScopes)
    assertResult(expected.maybeExpiresBy.map(_.truncatedTo(ChronoUnit.MILLIS)))(
      actual.maybeExpiresBy.map(_.truncatedTo(ChronoUnit.MILLIS))
    )
  }
}
