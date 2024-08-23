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
import com.rawlabs.snapi.truffle.runtime.function.FunctionExecuteNodes;
import com.rawlabs.snapi.truffle.runtime.function.FunctionExecuteNodesFactory;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodesFactory;
import com.rawlabs.snapi.truffle.ast.tryable_nullable.TryableNullableNodes;
import com.rawlabs.snapi.truffle.ast.tryable_nullable.TryableNullableNodesFactory;

public class OSRExistsBodyNode extends ExpressionNode {

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child
  FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode =
      FunctionExecuteNodesFactory.FunctionExecuteOneNodeGen.create();

  @Child
  TryableNullableNodes.HandlePredicateNode handlePredicateNode =
      TryableNullableNodesFactory.HandlePredicateNodeGen.create();

  private final int generatorSlot;
  private final int functionSlot;
  private final int predicateResultSlot;

  public OSRExistsBodyNode(int generatorSlot, int functionSlot, int predicateResultSlot) {
    this.generatorSlot = generatorSlot;
    this.functionSlot = functionSlot;
    this.predicateResultSlot = predicateResultSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object generator = frame.getObject(generatorSlot);
    Object function = frame.getObject(functionSlot);
    boolean result =
        handlePredicateNode.execute(
            this,
            functionExecuteOneNode.execute(this, function, nextNode.execute(this, generator)),
            false);
    frame.setBoolean(predicateResultSlot, result);
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
