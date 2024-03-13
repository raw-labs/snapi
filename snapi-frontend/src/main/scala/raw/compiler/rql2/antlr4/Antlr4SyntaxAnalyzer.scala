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

package raw.compiler.rql2.antlr4

import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.bitbucket.inkytonik.kiama.parsing.Parsers
import org.bitbucket.inkytonik.kiama.util.{Positions, StringSource}
import raw.client.api.Message
import raw.compiler.base.source.Type
import raw.compiler.common.source.SourceProgram
import raw.compiler.rql2.generated.{SnapiLexer, SnapiParser}
import raw.compiler.rql2.source.Rql2Program

abstract class ParseResult(errors: List[Message]) {
  def hasErrors: Boolean = errors.nonEmpty

  def isSuccess: Boolean = errors.isEmpty
}
final case class ParseProgramResult[P](errors: List[Message], tree: P) extends ParseResult(errors)
final case class ParseTypeResult(errors: List[Message], tipe: Type) extends ParseResult(errors)

class Antlr4SyntaxAnalyzer(val positions: Positions, isFrontend: Boolean) extends Parsers(positions) {

  def parse(s: String): ParseProgramResult[SourceProgram] = {
    val source = StringSource(s)
    val rawErrorListener = new RawErrorListener()

    val lexer = new SnapiLexer(CharStreams.fromString(s))
    lexer.removeErrorListeners()
    lexer.addErrorListener(rawErrorListener)

    val parser = new SnapiParser(new CommonTokenStream(lexer))

    parser.removeErrorListeners()
    parser.addErrorListener(rawErrorListener)

    val tree: ParseTree = parser.prog
    val visitorParseErrors = RawVisitorParseErrors()
    val visitor = new RawSnapiVisitor(positions, source, isFrontend, visitorParseErrors)
    val result = visitor.visit(tree).asInstanceOf[Rql2Program]

    val totalErrors = rawErrorListener.getErrors ++ visitorParseErrors.getErrors
    ParseProgramResult(totalErrors, result)
  }

  def parseType(s: String): ParseTypeResult = {
    val source = StringSource(s)
    val rawErrorListener = new RawErrorListener()

    val lexer = new SnapiLexer(CharStreams.fromString(s))
    lexer.removeErrorListeners()
    lexer.addErrorListener(rawErrorListener)

    val parser = new SnapiParser(new CommonTokenStream(lexer))

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
