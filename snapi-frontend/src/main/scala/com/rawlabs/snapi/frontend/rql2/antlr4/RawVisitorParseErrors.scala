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

package com.rawlabs.snapi.frontend.rql2.antlr4

import com.rawlabs.compiler.Message

case class RawVisitorParseErrors() {
  private var errors: List[Message] = List.empty
  def addError(error: Message): Unit = errors = errors :+ error
  def getErrors: List[Message] = errors
}
