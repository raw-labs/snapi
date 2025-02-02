/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.compiler

import com.rawlabs.compiler.{ErrorMessage, ErrorPosition, ErrorRange}
import com.rawlabs.snapi.frontend.snapi.antlr4.ParserErrors
import org.graalvm.polyglot.PolyglotException

object TruffleValidationError {
  def unapply(t: Throwable): Option[List[ErrorMessage]] = t match {
    case ex: PolyglotException
        if ex.isGuestException && ex.getGuestObject != null &&
          ex.getGuestObject.hasMembers && ex.getGuestObject.hasMember("errors") =>
      val err = ex.getGuestObject
      // A validation error occurred in SnapiLanguage during the parse method
      val errorsValue = err.getMember("errors")
      val errors = (0L until errorsValue.getArraySize).map { i =>
        val errorValue = errorsValue.getArrayElement(i)
        val message = errorValue.asString
        val positions = (0L until errorValue.getArraySize).map { j =>
          val posValue = errorValue.getArrayElement(j)
          val beginValue = posValue.getMember("begin")
          val endValue = posValue.getMember("end")
          val begin = ErrorPosition(beginValue.getMember("line").asInt, beginValue.getMember("column").asInt)
          val end = ErrorPosition(endValue.getMember("line").asInt, endValue.getMember("column").asInt)
          ErrorRange(begin, end)
        }
        ErrorMessage(message, positions.to, ParserErrors.ParserErrorCode)
      }
      Some(errors.to)
    case _ => None
  }
}
