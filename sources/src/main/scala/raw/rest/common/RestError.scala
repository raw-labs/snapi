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

package raw.rest.common

// Base interface for REST errors.
// Every REST error has a code as a string (e.g. "thisHappened") and a user-visible message.
trait RestError {
  def code: String

  def message: String
}

final case class GenericRestError(code: String, message: String) extends RestError
