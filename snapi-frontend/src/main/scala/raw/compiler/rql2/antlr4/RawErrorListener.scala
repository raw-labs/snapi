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
import org.bitbucket.inkytonik.kiama.util.{Position, Source}
import raw.client.api.{ErrorMessage, ErrorPosition, ErrorRange}

class RawErrorListener(private val source: Source) extends BaseErrorListener {

  private var errors = List[ErrorMessage]()

  override def syntaxError(
      recognizer: Recognizer[_, _],
      offendingSymbol: Any,
      line: Int,
      charPositionInLine: Int,
      msg: String,
      e: RecognitionException
  ): Unit = {
    val positions = Option(offendingSymbol)
      .map(_.asInstanceOf[Token])
      .map { token =>
        ErrorRange(
          ErrorPosition(token.getLine, token.getCharPositionInLine + 1),
          ErrorPosition(token.getLine, token.getCharPositionInLine + token.getText.length + 1)
        )
      }
      .getOrElse {
        ErrorRange(ErrorPosition(line, charPositionInLine + 1), ErrorPosition(line, charPositionInLine + 1))
      }
    errors = errors :+ ErrorMessage(msg, List(positions))
  }

  def getErrors: List[ErrorMessage] = errors
  def hasErrors: Boolean = errors.nonEmpty
}
