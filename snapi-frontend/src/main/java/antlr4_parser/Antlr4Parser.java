package antlr4_parser;

import org.bitbucket.inkytonik.kiama.parsing.Parsers;
import org.bitbucket.inkytonik.kiama.util.Position;
import org.bitbucket.inkytonik.kiama.util.Positions;
import raw.compiler.base.source.BaseProgram;
import raw.compiler.base.source.Type;
import scala.Tuple2;
import scala.util.Either;

public class Antlr4Parser extends Parsers {

  public Antlr4Parser(Positions positions) {
    super(positions);
  }

  public Either<Tuple2<String, Position>, BaseProgram> parse(String s) {

    return null;
  }

  public Either<Tuple2<String, Position>, Type> parseType(String s) {
    return null;
  }
}
