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

package raw.runtime.truffle.ast.expressions.function;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodes;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodesFactory;
import raw.runtime.truffle.runtime.function.Lambda;

@NodeInfo(shortName = "InvokeWithNames")
public final class InvokeWithNamesNode extends ExpressionNode {

  @Child private ExpressionNode functionNode;

  @Child
  private FunctionExecuteNodes.FunctionExecuteWithNames functionExecWithNames =
      FunctionExecuteNodesFactory.FunctionExecuteWithNamesNodeGen.create();

  @Child
  private FunctionExecuteNodes.FunctionExecuteZero functionExecZero =
      FunctionExecuteNodesFactory.FunctionExecuteZeroNodeGen.create();

  @Child
  private FunctionExecuteNodes.FunctionExecuteOne functionExecOne =
      FunctionExecuteNodesFactory.FunctionExecuteOneNodeGen.create();

  @Child
  private FunctionExecuteNodes.FunctionExecuteTwo functionExecTwo =
      FunctionExecuteNodesFactory.FunctionExecuteTwoNodeGen.create();

  @Children private final ExpressionNode[] argumentNodes;

  private final Object[] argumentValues;

  private final String[] argNames;

  public InvokeWithNamesNode(
      ExpressionNode functionNode, String[] argNames, ExpressionNode[] argumentNodes) {
    this.functionNode = functionNode;
    assert (argNames.length == argumentNodes.length);
    this.argNames = argNames;
    this.argumentNodes = argumentNodes;
    this.argumentValues = new Object[argumentNodes.length];
  }

  @ExplodeLoop
  @Override
  public Object executeGeneric(VirtualFrame frame) {
    CompilerAsserts.compilationConstant(argumentNodes.length);
    Object function = functionNode.executeGeneric(frame);
    if (function instanceof Lambda) {
      if (argNames.length == 0) {
        return functionExecZero.execute(this, function);
      } else if (argNames.length == 1) {
        return functionExecOne.execute(this, function, argumentNodes[0].executeGeneric(frame));
      } else if (argNames.length == 2) {
        return functionExecTwo.execute(
            this,
            function,
            argumentNodes[0].executeGeneric(frame),
            argumentNodes[1].executeGeneric(frame));
      }
    }
    for (int i = 0; i < argumentNodes.length; i++) {
      argumentValues[i] = argumentNodes[i].executeGeneric(frame);
    }
    return functionExecWithNames.execute(this, function, argNames, argumentValues);
  }

  @Override
  public boolean hasTag(Class<? extends Tag> tag) {
    if (tag == StandardTags.CallTag.class) {
      return true;
    }
    return super.hasTag(tag);
  }
}
