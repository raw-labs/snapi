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

package raw.runtime.truffle.ast.io.xml.parser;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.ast.tryable_nullable.TryableNullableNodes;
import raw.runtime.truffle.ast.tryable_nullable.TryableNullableNodesFactory;
import raw.runtime.truffle.runtime.exceptions.xml.XmlParserRawTruffleException;
import raw.runtime.truffle.runtime.tryable.ErrorTryable;

@NodeInfo(shortName = "TryableParseXml")
public class TryableParseXmlNode extends ExpressionNode {

  @Child private DirectCallNode childDirectCall;

  @Child
  private TryableNullableNodes.BoxTryableNode boxTryable =
      TryableNullableNodesFactory.BoxTryableNodeGen.create();

  public TryableParseXmlNode(ProgramExpressionNode childProgramStatementNode) {
    this.childDirectCall = DirectCallNode.create(childProgramStatementNode.getCallTarget());
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    RawTruffleXmlParser parser = (RawTruffleXmlParser) args[0];
    try {
      Object result = childDirectCall.call(args);
      return boxTryable.execute(result);
    } catch (XmlParserRawTruffleException ex) {
      Object failure = ErrorTryable.BuildFailure(ex.getMessage());
      try {
        parser.finishConsuming();
      } catch (XmlParserRawTruffleException e) {
        return failure;
      }
      return failure;
    }
  }
}
