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

package raw.runtime.truffle.ast.io.json.reader.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.json.JsonExpectedNothingException;

public class UndefinedParseJsonNode extends ExpressionNode {

  public Object executeGeneric(VirtualFrame frame) {
    JsonParser parser = (JsonParser) frame.getArguments()[0];
    doParse(parser);
    return null;
  }

  @CompilerDirectives.TruffleBoundary
  private void doParse(JsonParser parser) {
    throw new JsonExpectedNothingException(parser.currentToken().toString());
  }
}
