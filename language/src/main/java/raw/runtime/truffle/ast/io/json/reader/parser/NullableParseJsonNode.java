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
import com.fasterxml.jackson.core.JsonToken;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodes;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodesFactory;
import raw.runtime.truffle.ast.tryable_nullable.TryableNullableNodes;
import raw.runtime.truffle.ast.tryable_nullable.TryableNullableNodesFactory;
import raw.runtime.truffle.runtime.exceptions.json.JsonExpectedNothingException;
import raw.runtime.truffle.runtime.exceptions.json.JsonParserRawTruffleException;
import raw.runtime.truffle.runtime.option.EmptyOption;

public class NullableParseJsonNode extends ExpressionNode {

  @Child private DirectCallNode childDirectCall;

  @Child
  private JsonParserNodes.NextTokenJsonParserNode nextTokenNode =
      JsonParserNodesFactory.NextTokenJsonParserNodeGen.getUncached();

  @Child
  private JsonParserNodes.CurrentTokenJsonParserNode currentTokenNode =
      JsonParserNodesFactory.CurrentTokenJsonParserNodeGen.getUncached();

  @Child
  private TryableNullableNodes.BoxOptionNode boxOption =
      TryableNullableNodesFactory.BoxOptionNodeGen.create();

  public NullableParseJsonNode(ProgramExpressionNode childProgramStatementNode) {
    this.childDirectCall = DirectCallNode.create(childProgramStatementNode.getCallTarget());
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    JsonParser parser = (JsonParser) args[0];
    if (currentTokenNode.execute(parser) == JsonToken.VALUE_NULL) {
      nextTokenNode.execute(parser);
      return new EmptyOption();
    } else {
      try {
        Object result = childDirectCall.call(parser);
        return boxOption.execute(result);
      } catch (JsonExpectedNothingException ex) {
        throw new JsonParserRawTruffleException("expected null but got non-null", this);
      }
    }
  }
}