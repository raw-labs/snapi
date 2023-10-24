package antlr4_parser;

import antlr4_parser.generated.SnapiLexer;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import raw.compiler.rql2.Keywords;

public class RawSnapiLexer extends SnapiLexer implements Keywords {

  public RawSnapiLexer(CharStream input) {
    super(input);
  }

  @Override
  public boolean isReserved(String idn) {
    return Keywords.super.isReserved(idn);
  }

  @Override
  public boolean isReservedType(String idn) {
    return Keywords.super.isReservedType(idn);
  }

  private void handleNonEscIdentifier() {
    if (isReserved(getText())) {
      ANTLRErrorListener listener = getErrorListenerDispatch();
      int line = getLine();
      int charPositionInLine = getCharPositionInLine();
      listener.syntaxError(this, null, line, charPositionInLine, "reserved keyword", null);
    }
  }

  @Override
  public Token emit() {
    if (getType() == NON_ESC_IDENTIFIER) {
      handleNonEscIdentifier();
    }
    if (getType() == ESC_IDENTIFIER) {
      setText(getText().replace("`", ""));
    }
    return super.emit();
  }
}
