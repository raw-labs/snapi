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

class Antlr4SyntaxAnalyzer(val positions: Positions) extends Parsers(positions) {

  def parse(s: String): Either[(String, Position), BaseProgram] = {
    val lexer = new SnapiLexer(CharStreams.fromString(s))
    val parser = new SnapiParser(new CommonTokenStream(lexer))
    val tree: ParseTree = parser.prog
    print(tree.toStringTree(parser))
    val visitor = new RawSnapiVisitor(positions, StringSource(s))
    val result = visitor.visit(tree).asInstanceOf[Rql2Program]
    Right(result)
  }

  def parseType(s: String): Either[(String, Position), Type] = {
    val lexer: SnapiLexer = new SnapiLexer(CharStreams.fromString(s))
    val parser: SnapiParser = new SnapiParser(new CommonTokenStream(lexer))
    val tree: ParseTree = parser.tipe
    val visitor: RawSnapiVisitor = new RawSnapiVisitor(positions, StringSource(s))
    val result: Type = visitor.visit(tree).asInstanceOf[Type]
    Right(result)
  }

}
