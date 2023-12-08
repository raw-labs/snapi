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

package raw.compiler.rql2.antlr4

import raw.client.api.ErrorMessage

case class RawVisitorParseErrors() {
  private var errors: List[ErrorMessage] = List.empty
  def addError(error: ErrorMessage): Unit = errors = errors :+ error
  def getErrors: List[ErrorMessage] = errors
}