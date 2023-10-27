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
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;

public class RawSnapiLexer extends SnapiLexer implements SnapiKeywords {

  public RawSnapiLexer(CharStream input) {
    super(input);
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
    if (getType() == IDENT) {
      if (getText().startsWith("`") && getText().endsWith("`")) {
        // Escaped identifier
        setText(getText().replace("`", ""));
      } else {
        handleNonEscIdentifier();
      }
    }
    return super.emit();
  }
}
