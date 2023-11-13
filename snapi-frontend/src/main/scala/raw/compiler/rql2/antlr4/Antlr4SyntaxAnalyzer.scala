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

import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.antlr.v4.runtime.tree.ParseTree
import org.bitbucket.inkytonik.kiama.parsing.Parsers
import org.bitbucket.inkytonik.kiama.util.{Position, Positions, StringSource}
import raw.compiler.base.source.{BaseProgram, Type}
import raw.compiler.rql2.generated.{SnapiLexer, SnapiParser}
import raw.compiler.rql2.source.Rql2Program

class Antlr4SyntaxAnalyzer(val positions: Positions, isFrontend: Boolean) extends Parsers(positions) {

  def parse(s: String): Either[(String, Position), BaseProgram] = {
    val source = StringSource(s)
    val rawErrorListener = new RawErrorListener(source)

    val lexer = new SnapiLexer(CharStreams.fromString(s))
    lexer.removeErrorListeners()
    lexer.addErrorListener(rawErrorListener)

    val parser = new SnapiParser(new CommonTokenStream(lexer))

    parser.removeErrorListeners()
    parser.addErrorListener(rawErrorListener)

    val tree: ParseTree = parser.prog
//    print(tree.toStringTree(parser))
    val visitor = new RawSnapiVisitor(positions, StringSource(s), isFrontend)
    val result = visitor.visit(tree).asInstanceOf[Rql2Program]

    if (rawErrorListener.hasErrors) Left(rawErrorListener.getErrors.head)
    else Right(result)
  }

  def parseType(s: String): Either[(String, Position), Type] = {
    val source = StringSource(s)
    val rawErrorListener = new RawErrorListener(source)

    val lexer = new SnapiLexer(CharStreams.fromString(s))
    lexer.removeErrorListeners()
    lexer.addErrorListener(rawErrorListener)

    val parser = new SnapiParser(new CommonTokenStream(lexer))

    parser.removeErrorListeners()
    parser.addErrorListener(rawErrorListener)

    val tree: ParseTree = parser.tipe
    val visitor: RawSnapiVisitor = new RawSnapiVisitor(positions, StringSource(s), isFrontend)
    val result: Type = visitor.visit(tree).asInstanceOf[Type]

    if (rawErrorListener.hasErrors) Left(rawErrorListener.getErrors.head)
    else Right(result)
  }

}
