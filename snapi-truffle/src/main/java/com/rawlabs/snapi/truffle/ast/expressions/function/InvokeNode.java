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

package com.rawlabs.snapi.truffle.ast.expressions.function;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;

@NodeInfo(shortName = "InvokeWithNames")
public final class InvokeNode extends ExpressionNode {

  @Child private ExpressionNode functionNode;

  @Child private InvokeNodes.Invoke invoke = InvokeNodesFactory.InvokeNodeGen.create();

  @Children private final ExpressionNode[] argumentNodes;

  private final Object[] argumentValues;

  private final String[] argNames;

  public InvokeNode(
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
    for (int i = 0; i < argumentNodes.length; i++) {
      argumentValues[i] = argumentNodes[i].executeGeneric(frame);
    }
    return invoke.execute(this, function, argNames, argumentValues);
  }

  @Override
  public boolean hasTag(Class<? extends Tag> tag) {
    if (tag == StandardTags.CallTag.class) {
      return true;
    }
    return super.hasTag(tag);
  }
}
