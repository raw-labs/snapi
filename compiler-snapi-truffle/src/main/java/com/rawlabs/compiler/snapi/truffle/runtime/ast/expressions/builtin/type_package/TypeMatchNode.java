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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.type_package;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.function.FunctionExecuteNodes;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.function.FunctionExecuteNodesFactory;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.or.OrObject;

public class TypeMatchNode extends ExpressionNode {
  @Child private ExpressionNode typeExp;

  @Children private final ExpressionNode[] functionExps;

  @Child
  FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode =
      FunctionExecuteNodesFactory.FunctionExecuteOneNodeGen.create();

  public TypeMatchNode(ExpressionNode child, ExpressionNode[] children) {
    this.typeExp = child;
    this.functionExps = children;
  }

  @ExplodeLoop
  public Object executeGeneric(VirtualFrame frame) {
    OrObject orType = (OrObject) this.typeExp.executeGeneric(frame);
    int index = orType.getIndex();
    Object[] functions = new Object[functionExps.length];
    for (int i = 0; i < functionExps.length; i++) {
      functions[i] = functionExps[i].executeGeneric(frame);
    }
    return functionExecuteOneNode.execute(this, functions[index], orType.getValue());
  }
}
