package antlr4_parser;

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

  public Antlr4SyntaxAnalyzer(Positions positions) {
    super(positions);
  }

  public Either<Tuple2<String, Position>, BaseProgram> parse(String s) {
    RawSnapiLexer lexer = new RawSnapiLexer(CharStreams.fromString(s));
    SnapiParser parser = new SnapiParser(new CommonTokenStream(lexer));
    ParseTree tree = parser.prog();

    System.out.println(tree.toStringTree(parser));

    RawSnapiVisitor visitor = new RawSnapiVisitor(new StringSource(s, "Snapi Program"));
    Rql2Program result = (Rql2Program) visitor.visit(tree);
    return new Right<>(result);
  }

  public Either<Tuple2<String, Position>, Type> parseType(String s) {
    RawSnapiLexer lexer = new RawSnapiLexer(CharStreams.fromString(s));
    SnapiParser parser = new SnapiParser(new CommonTokenStream(lexer));
    ParseTree tree = parser.type();
    RawSnapiVisitor visitor = new RawSnapiVisitor(new StringSource(s, "Snapi Type"));
    Type result = (Type) visitor.visit(tree);
    return new Right<>(result);
  }
}
