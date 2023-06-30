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

package raw.auth

import java.time.Instant
import java.time.temporal.ChronoUnit
import com.fasterxml.jackson.annotation.JsonIgnore
import raw.Uid
import raw.api.{AuthenticatedUser, Service}

object AuthService {
  val PERMISSION_MANAGEMENT = "management"
  val PERMISSION_IMPERSONATE = "impersonate"

  val AllPermissions = Seq(PERMISSION_IMPERSONATE, PERMISSION_MANAGEMENT)
}

trait AuthService extends Service {
  def public: PublicAuthService

  def mgmt: MgmtAuthService
}

trait Token {
  def accessToken: String

  def remainingValidityInSeconds: Long
}

final case class TokenWithValidity(accessToken: String, validUntil: Instant) {
  @JsonIgnore
  def isExpired: Boolean = {
    Instant.now().isAfter(validUntil)
  }
}

trait TokenProvider {
  def token: TokenWithValidity
}

trait PublicAuthService {

  /**
   * Resolve a user information from a Token.
   */
  def getUser(token: Token): AuthenticatedUser

  def authenticate(accessToken: String, maybeIdToken: Option[String] = None): AuthenticatedUser

  def exchangeCode(code: String, redirectUri: String): Token

  def doLogin(callbackUrl: String): LoginResult

  def login(username: String, password: String): AuthenticatedUser

  def displayName: String
}

final case class LoginResult(url: String, state: String)

trait MgmtAuthService {

  def displayName: String

  def findByUid(uid: Uid): Option[AuthenticatedUser]

  def findByEmail(email: String): Option[AuthenticatedUser]

  def findByUsername(usernName: String): Option[AuthenticatedUser]

  def getPermissions(uid: Uid): Seq[String]

  def getRawMgmtToken: Token

  def users: Seq[AuthenticatedUser]

}

class MgmtTokenProvider(authService: MgmtAuthService) extends TokenProvider {
  override def token: TokenWithValidity = {
    val mgmtToken = authService.getRawMgmtToken
    TokenWithValidity(
      mgmtToken.accessToken,
      Instant.now().plus(mgmtToken.remainingValidityInSeconds, ChronoUnit.SECONDS)
    )
  }
}
