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
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.primitives.ErrorObject;

@NodeInfo(shortName = "TryableParseAttributeXml")
public class TryableParseAttributeXmlNode extends ExpressionNode {

  @Child private DirectCallNode childDirectCall;

  public TryableParseAttributeXmlNode(ProgramExpressionNode childProgramStatementNode) {
    this.childDirectCall = DirectCallNode.create(childProgramStatementNode.getCallTarget());
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    try {
      return childDirectCall.call(args);
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    }
  }
}
