/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.truffle.ast.osr.bodies;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.iterable.ArrayOperationNodes;
import com.rawlabs.snapi.truffle.ast.expressions.iterable.ArrayOperationNodesFactory;
import com.rawlabs.snapi.truffle.runtime.function.FunctionExecuteNodes;
import com.rawlabs.snapi.truffle.runtime.function.FunctionExecuteNodesFactory;
import com.rawlabs.snapi.truffle.runtime.list.ListNodes;
import com.rawlabs.snapi.truffle.runtime.list.ListNodesFactory;

public class OSRListTransformBodyNode extends ExpressionNode {

  @Child
  FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode =
      FunctionExecuteNodesFactory.FunctionExecuteOneNodeGen.create();

  @Child ListNodes.GetNode getNode = ListNodesFactory.GetNodeGen.create();

  @Child
  ArrayOperationNodes.ArraySetArrayItemNode arraySetNode =
      ArrayOperationNodesFactory.ArraySetArrayItemNodeGen.create();

  private final int listSlot;
  private final int functionSlot;
  private final int currentIdxSlot;
  private final int resultSlot;

  public OSRListTransformBodyNode(
      int listSlot, int functionSlot, int currentIdxSlot, int resultSlot) {
    this.currentIdxSlot = currentIdxSlot;
    this.resultSlot = resultSlot;
    this.listSlot = listSlot;
    this.functionSlot = functionSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    int currentIdx = frame.getInt(currentIdxSlot);
    Object transformFunction = frame.getObject(functionSlot);
    Object transformingList = frame.getObject(listSlot);
    Object resultArray = frame.getObject(resultSlot);

    Object transformedItem =
        functionExecuteOneNode.execute(
            this, transformFunction, getNode.execute(this, transformingList, currentIdx));

    arraySetNode.execute(this, resultArray, transformedItem, currentIdx);

    frame.setInt(currentIdxSlot, currentIdx + 1);
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
