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

import org.antlr.v4.runtime.{BaseErrorListener, RecognitionException, Recognizer}
import org.bitbucket.inkytonik.kiama.util.{Position, Source}

class RawErrorListener(private val source: Source) extends BaseErrorListener {

  private var errors = List[(String, Position)]()

  override def syntaxError(
      recognizer: Recognizer[_, _],
      offendingSymbol: Any,
      line: Int,
      charPositionInLine: Int,
      msg: String,
      e: RecognitionException
  ): Unit = {
    errors = errors :+ (msg, Position(line + 1, charPositionInLine + 1, source))
  }

  def getErrors: List[(String, Position)] = errors
  def hasErrors: Boolean = errors.nonEmpty
}
