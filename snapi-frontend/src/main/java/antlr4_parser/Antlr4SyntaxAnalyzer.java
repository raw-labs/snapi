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

package antlr4_parser;

import antlr4_parser.generated.SnapiLexer;
import antlr4_parser.generated.SnapiParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.bitbucket.inkytonik.kiama.parsing.Parsers;
import org.bitbucket.inkytonik.kiama.util.Position;
import org.bitbucket.inkytonik.kiama.util.Positions;
import org.bitbucket.inkytonik.kiama.util.StringSource;
import raw.compiler.base.source.BaseProgram;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.source.Rql2Program;
import scala.Tuple2;
import scala.util.Either;
import scala.util.Right;

public class Antlr4SyntaxAnalyzer extends Parsers {

  private final Positions positions;

  public Antlr4SyntaxAnalyzer(Positions positions) {
    super(positions);
    this.positions = positions;
  }

  public Either<Tuple2<String, Position>, BaseProgram> parse(String s) {
    SnapiLexer lexer = new SnapiLexer(CharStreams.fromString(s));
    SnapiParser parser = new SnapiParser(new CommonTokenStream(lexer));
    ParseTree tree = parser.prog();

    System.out.println(tree.toStringTree(parser));

    RawSnapiVisitor visitor = new RawSnapiVisitor(new StringSource(s, ""), positions);
    Rql2Program result = (Rql2Program) visitor.visit(tree);
    return new Right<>(result);
  }

  public Either<Tuple2<String, Position>, Type> parseType(String s) {
    SnapiLexer lexer = new SnapiLexer(CharStreams.fromString(s));
    SnapiParser parser = new SnapiParser(new CommonTokenStream(lexer));
    ParseTree tree = parser.type();
    RawSnapiVisitor visitor = new RawSnapiVisitor(new StringSource(s, ""), positions);
    Type result = (Type) visitor.visit(tree);
    return new Right<>(result);
  }

  public Positions getPositions() {
    return positions;
  }
}
