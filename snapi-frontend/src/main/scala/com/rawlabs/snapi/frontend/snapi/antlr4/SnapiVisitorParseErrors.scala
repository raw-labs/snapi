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

package com.rawlabs.snapi.frontend.snapi.antlr4

import com.rawlabs.snapi.frontend.api.Message

import scala.collection.mutable

class SnapiVisitorParseErrors {

  private val errors = new mutable.ListBuffer[Message]

  def addError(error: Message): Unit = errors.append(error)

  def getErrors: List[Message] = errors.to
}
