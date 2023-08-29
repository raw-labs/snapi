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
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodes;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodesFactory;
import raw.runtime.truffle.runtime.exceptions.json.JsonParserRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.json.JsonReaderRawTruffleException;
import raw.runtime.truffle.runtime.nullable_tryable.NullableTryableLibrary;
import raw.runtime.truffle.runtime.nullable_tryable.RuntimeNullableTryableHandler;
import raw.runtime.truffle.runtime.tryable.ErrorTryable;

@NodeInfo(shortName = "TryableParseJson")
public class TryableParseJsonNode extends ExpressionNode {

  @Child private DirectCallNode childDirectCall;
  private final RuntimeNullableTryableHandler nullableTryableHandler =
      new RuntimeNullableTryableHandler();

  @Child
  private NullableTryableLibrary nullableTryable =
      NullableTryableLibrary.getFactory().create(nullableTryableHandler);

  @Child
  private JsonParserNodes.SkipNextJsonParserNode skipNext =
      JsonParserNodesFactory.SkipNextJsonParserNodeGen.create();

  public TryableParseJsonNode(ProgramExpressionNode childProgramStatementNode) {
    this.childDirectCall = DirectCallNode.create(childProgramStatementNode.getCallTarget());
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    JsonParser parser = (JsonParser) args[0];
    try {
      Object result = childDirectCall.call(parser);
      return nullableTryable.boxTryable(nullableTryableHandler, result);
    } catch (JsonParserRawTruffleException ex) {
      try {
        skipNext.execute(parser);
      } catch (JsonReaderRawTruffleException e) {
        return ErrorTryable.BuildFailure(ex.getMessage());
      }
      return ErrorTryable.BuildFailure(ex.getMessage());
    }
  }
}
