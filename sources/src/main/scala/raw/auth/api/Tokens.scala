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

package raw.auth.api

import java.time.Instant
import com.fasterxml.jackson.annotation.JsonIgnore

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
