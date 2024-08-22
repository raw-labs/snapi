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

package com.rawlabs.sql.compiler.antlr4

import com.rawlabs.compiler.Message

import scala.collection.mutable

class SqlVisitorParseErrors {

  private val errors = new mutable.ListBuffer[Message]

  def addError(error: Message): Unit = errors.append(error)

  def getErrors: List[Message] = errors.to

}
