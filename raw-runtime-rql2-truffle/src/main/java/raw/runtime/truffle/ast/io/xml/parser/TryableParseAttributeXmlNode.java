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
import raw.runtime.truffle.runtime.nullable_tryable.NullableTryableLibrary;
import raw.runtime.truffle.runtime.nullable_tryable.RuntimeNullableTryableHandler;
import raw.runtime.truffle.runtime.tryable.ErrorTryable;

@NodeInfo(shortName = "TryableParseAttributeXml")
public class TryableParseAttributeXmlNode extends ExpressionNode {

  @Child private DirectCallNode childDirectCall;
  private final RuntimeNullableTryableHandler nullableTryableHandler =
      new RuntimeNullableTryableHandler();

  @Child
  private NullableTryableLibrary nullableTryable =
      NullableTryableLibrary.getFactory().create(nullableTryableHandler);

  public TryableParseAttributeXmlNode(ProgramExpressionNode childProgramStatementNode) {
    this.childDirectCall = DirectCallNode.create(childProgramStatementNode.getCallTarget());
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    try {
      Object result = childDirectCall.call(args);
      return nullableTryable.boxTryable(nullableTryableHandler, result);
    } catch (RawTruffleRuntimeException ex) {
      return ErrorTryable.BuildFailure(ex.getMessage());
    }
  }
}
