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

package com.rawlabs.compiler.snapi.rql2.antlr4

import com.rawlabs.compiler.api.Message
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream, TokenStream}
import org.bitbucket.inkytonik.kiama.parsing.Parsers
import org.bitbucket.inkytonik.kiama.util.{Positions, Source, StringSource}
import com.rawlabs.compiler.snapi.base.source.Type
import com.rawlabs.compiler.snapi.common.source.SourceProgram
import com.rawlabs.compiler.snapi.generated.{SnapiLexer, SnapiParser}
import com.rawlabs.compiler.snapi.rql2.source.Rql2Program

abstract class ParseResult(errors: List[Message]) {
  def hasErrors: Boolean = errors.nonEmpty

  def isSuccess: Boolean = errors.isEmpty
}
final case class ParseProgramResult[P](errors: List[Message], tree: P) extends ParseResult(errors)
final case class ParseTypeResult(errors: List[Message], tipe: Type) extends ParseResult(errors)

class Antlr4SyntaxAnalyzer(val positions: Positions, isFrontend: Boolean) extends Parsers(positions) {

  def parse(s: String): ParseProgramResult[SourceProgram] = {
    val rawErrorListener = new RawErrorListener()
    val stream = getTokenStream(s, rawErrorListener)
    parse(stream, StringSource(s), rawErrorListener)
  }

  protected def parse(
      stream: TokenStream,
      source: Source,
      errorListener: RawErrorListener
  ): ParseProgramResult[SourceProgram] = {
    val parser = new SnapiParser(stream)
    parser.removeErrorListeners()
    parser.addErrorListener(errorListener)

    val tree: ParseTree = parser.prog
    val visitorParseErrors = RawVisitorParseErrors()
    val visitor = new RawSnapiVisitor(positions, source, isFrontend, visitorParseErrors)
    val result = visitor.visit(tree).asInstanceOf[Rql2Program]

    val totalErrors = errorListener.getErrors ++ visitorParseErrors.getErrors
    ParseProgramResult(totalErrors, result)
  }

  protected def getTokenStream(s: String, errorListener: RawErrorListener): CommonTokenStream = {
    val lexer = new SnapiLexer(CharStreams.fromString(s))
    lexer.removeErrorListeners()
    lexer.addErrorListener(errorListener)

    new CommonTokenStream(lexer)
  }

  def parseType(s: String): ParseTypeResult = {
    val source = StringSource(s)
    val rawErrorListener = new RawErrorListener()

    val parser = new SnapiParser(getTokenStream(s, rawErrorListener))
    parser.removeErrorListeners()
    parser.addErrorListener(rawErrorListener)

    val tree: ParseTree = parser.tipe
    val visitorParseErrors = RawVisitorParseErrors()
    val visitor: RawSnapiVisitor = new RawSnapiVisitor(positions, source, isFrontend, visitorParseErrors)
    val result: Type = visitor.visit(tree).asInstanceOf[Type]

    val totalErrors = rawErrorListener.getErrors ++ visitorParseErrors.getErrors

    ParseTypeResult(totalErrors, result)
  }

}
