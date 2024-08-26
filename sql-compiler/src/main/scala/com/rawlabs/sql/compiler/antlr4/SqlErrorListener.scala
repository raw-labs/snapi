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

package com.rawlabs.sql.compiler.antlr4

import com.rawlabs.compiler.{ErrorMessage, ErrorPosition, ErrorRange, Message}
import org.antlr.v4.runtime.{BaseErrorListener, RecognitionException, Recognizer, Token}

import scala.collection.mutable

class SqlErrorListener extends BaseErrorListener {

  private val errors = new mutable.ListBuffer[Message]

  private def improveErrorMessage(msg: String): String = {
    val extraneousPattern = "extraneous input '(.+)' expecting \\{(.*?)}".r
    val extraneousPattern2 = "extraneous input '(.+)' expecting '(.*?)'".r
    val noViableAlternativePattern = "no viable alternative at input '(.+)'".r
    val mismatchedInputPattern = "mismatched input '(.+)' expecting \\{(.*?)}".r
    msg match {
      case extraneousPattern(input, expected) =>
        val result = expected.split(", ")
        val expectedElements = result.mkString(", ")
        s"the input '$input' is not valid here; expected elements are: $expectedElements."
      case extraneousPattern2(input, expected) =>
        val res = expected
        s"the input '$input' is not valid here; expected elements is: '$res'.'"
      case noViableAlternativePattern(_) => s"the input does not form a valid statement or expression."
      case mismatchedInputPattern(input, _) => s"the input '$input' is not valid here."
      case _ => msg
    }
  }

  override def syntaxError(
      recognizer: Recognizer[_, _],
      offendingSymbol: Any,
      line: Int,
      charPositionInLine: Int,
      msg: String,
      e: RecognitionException
  ): Unit = {
    val getCharPositionInLinePlusOne = charPositionInLine + 1
    val positions =
      if (offendingSymbol.isInstanceOf[Token]) {
        Option(offendingSymbol)
          .map(_.asInstanceOf[Token])
          .map { token =>
            ErrorRange(
              ErrorPosition(token.getLine, token.getCharPositionInLine),
              ErrorPosition(token.getLine, token.getCharPositionInLine + token.getText.length)
            )
          }
          .getOrElse {
            ErrorRange(
              ErrorPosition(line, getCharPositionInLinePlusOne),
              ErrorPosition(line, getCharPositionInLinePlusOne + 1)
            )
          }
      } else {
        ErrorRange(
          ErrorPosition(line, getCharPositionInLinePlusOne),
          ErrorPosition(line, getCharPositionInLinePlusOne + 1)
        )
      }
    errors.append(ErrorMessage(improveErrorMessage(msg), List(positions), SqlParserErrors.ParserErrorCode))
  }

  def getErrors: List[Message] = errors.to

  def hasErrors: Boolean = errors.nonEmpty

}
