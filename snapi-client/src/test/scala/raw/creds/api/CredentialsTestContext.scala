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
import raw.utils.{RawTestSuite, SettingsTestContext}

import java.time.temporal.ChronoUnit

trait CredentialsTestContext extends BeforeAndAfterAll {
  this: RawTestSuite with SettingsTestContext =>

  private var instance: CredentialsService = _

  def credentials: CredentialsService = instance

  def setCredentials(credentials: CredentialsService): Unit = {
    instance = credentials
    CredentialsServiceProvider.set(credentials)
  }

  override def afterAll(): Unit = {
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
