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

package raw.client.sql.antlr4

import org.antlr.v4.runtime.{BaseErrorListener, RecognitionException, Recognizer, Token}
import raw.client.api.{ErrorMessage, ErrorPosition, ErrorRange, Message}

class RawSqlErrorListener() extends BaseErrorListener {

  private var errors = List[Message]()

  private def improveErrorMessage(msg: String): String = {
    val extraneousPattern = "extraneous input '(.+)' expecting \\{(.*?)}".r
    val extraneousPattern2 = "extraneous input '(.+)' expecting '(.*?)'".r
    val noViableAlternativePattern = "no viable alternative at input '(.+)'".r
    msg match {
      case extraneousPattern(input, expected) =>
        val result = expected.split(", ")
        val expectedElements = result.mkString(", ")
        s"the input '$input' is not valid here; expected elements are: $expectedElements."
      case extraneousPattern2(input, expected) =>
        val res = expected
        s"the input '$input' is not valid here; expected elements is: '$res'.'"
      case noViableAlternativePattern(_) => s"the input does not form a valid statement or expression."
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
    errors = errors :+ ErrorMessage(improveErrorMessage(msg), List(positions), SqlParserErrors.ParserErrorCode)
  }

  def getErrors: List[Message] = errors
  def hasErrors: Boolean = errors.nonEmpty
}
