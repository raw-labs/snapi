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

import org.antlr.v4.runtime.{BaseErrorListener, RecognitionException, Recognizer, Token}
import raw.client.api.{ErrorMessage, ErrorPosition, ErrorRange}

class RawErrorListener() extends BaseErrorListener {

  private var errors = List[ErrorMessage]()

  private def improveErrorMessage(msg: String): String = {
    val extraneousPattern = "extraneous input '(.+)' expecting \\{(.*?)}".r
    val extraneousPattern2 = "extraneous input '(.+)' expecting '(.*?)'".r
    val noViableAlternativePattern = "no viable alternative at input '(.+)'".r
    msg match {
      case extraneousPattern(input, expected) =>
        val splitted = expected.split(", ").filter(x => !x.equals("'$'") && !x.equals("START_TRIPLE_QUOTE"))
        val result =
          if (splitted.contains("NON_ESC_IDENTIFIER") || splitted.contains("ESC_IDENTIFIER")) {
            splitted.filter(x => !x.equals("NON_ESC_IDENTIFIER") && !x.equals("ESC_IDENTIFIER")) :+ "identifier"
          } else {
            splitted
          }
        val expectedElements = result.mkString(", ")
        s"the input '$input' is not valid here; expected elements are: $expectedElements."
      case extraneousPattern2(input, expected) =>
        val res = expected
          .replace("NON_ESC_IDENTIFIER", "identifier")
          .replace("ESC_IDENTIFIER", "identifier")
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
    errors = errors :+ ErrorMessage(improveErrorMessage(msg), List(positions))
  }

  def getErrors: List[ErrorMessage] = errors
  def hasErrors: Boolean = errors.nonEmpty
}
