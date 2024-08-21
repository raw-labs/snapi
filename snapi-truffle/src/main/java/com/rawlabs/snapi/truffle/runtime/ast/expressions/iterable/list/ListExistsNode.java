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

package com.rawlabs.snapi.truffle.runtime.ast.expressions.iterable.list;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.ast.osr.OSRGeneratorNode;
import com.rawlabs.snapi.truffle.runtime.ast.osr.bodies.OSRExistsBodyNode;
import com.rawlabs.snapi.truffle.runtime.ast.osr.conditions.OSRExistsConditionNode;
import com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.GeneratorNodesFactory;
import com.rawlabs.snapi.truffle.runtime.runtime.iterable.IterableNodes;
import com.rawlabs.snapi.truffle.runtime.runtime.iterable.IterableNodesFactory;
import com.rawlabs.snapi.truffle.runtime.runtime.list.ListNodes;
import com.rawlabs.snapi.truffle.runtime.runtime.list.ListNodesFactory;

@NodeInfo(shortName = "List.Exists")
public class ListExistsNode extends ExpressionNode {

  @Child private ExpressionNode listNode;
  @Child private ExpressionNode functionNode;
  @Child private LoopNode existsLoopNode;

  @Child
  private GeneratorNodes.GeneratorInitNode generatorInitNode =
      GeneratorNodesFactory.GeneratorInitNodeGen.create();

  @Child
  private IterableNodes.GetGeneratorNode getGeneratorNode =
      IterableNodesFactory.GetGeneratorNodeGen.create();

  @Child
  private ListNodes.ToIterableNode toIterableNode = ListNodesFactory.ToIterableNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorCloseNode generatorCloseNode =
      GeneratorNodesFactory.GeneratorCloseNodeGen.create();

  private final int generatorSlot;
  private final int functionSlot;
  private final int predicateResultSlot;

  public ListExistsNode(
      ExpressionNode listNode,
      ExpressionNode functionNode,
      int generatorSlot,
      int functionSlot,
      int predicateResultSlot) {
    this.listNode = listNode;
    this.functionNode = functionNode;
    this.generatorSlot = generatorSlot;
    this.functionSlot = functionSlot;
    this.predicateResultSlot = predicateResultSlot;
    this.existsLoopNode =
        Truffle.getRuntime()
            .createLoopNode(
                new OSRGeneratorNode(
                    new OSRExistsConditionNode(generatorSlot, predicateResultSlot),
                    new OSRExistsBodyNode(generatorSlot, functionSlot, predicateResultSlot)));
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object list = listNode.executeGeneric(frame);
    Object function = functionNode.executeGeneric(frame);
    Object iterable = toIterableNode.execute(this, list);
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      frame.setObject(generatorSlot, generator);
      frame.setObject(functionSlot, function);
      frame.setBoolean(predicateResultSlot, false);
      existsLoopNode.execute(frame);
      return frame.getBoolean(predicateResultSlot);
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }

  @Override
  public boolean executeBoolean(VirtualFrame virtualFrame) {
    return (boolean) executeGeneric(virtualFrame);
  }
}
