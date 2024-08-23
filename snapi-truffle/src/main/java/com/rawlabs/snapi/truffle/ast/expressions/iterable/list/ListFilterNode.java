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

package com.rawlabs.snapi.truffle.ast.expressions.iterable.list;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.frontend.rql2.source.Rql2Type;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.iterable.ArrayOperationNodes;
import com.rawlabs.snapi.truffle.ast.expressions.iterable.ArrayOperationNodesFactory;
import com.rawlabs.snapi.truffle.ast.osr.OSRGeneratorNode;
import com.rawlabs.snapi.truffle.ast.osr.bodies.OSRListFilterBodyNode;
import com.rawlabs.snapi.truffle.ast.osr.bodies.OSRToArrayBodyNode;
import com.rawlabs.snapi.truffle.ast.osr.conditions.OSRHasNextConditionNode;
import com.rawlabs.snapi.truffle.ast.osr.conditions.OSRIsLessThanSizeConditionNode;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodesFactory;
import com.rawlabs.snapi.truffle.runtime.iterable.IterableNodes;
import com.rawlabs.snapi.truffle.runtime.iterable.IterableNodesFactory;
import com.rawlabs.snapi.truffle.runtime.list.*;
import java.util.ArrayList;

@NodeInfo(shortName = "List.Filter")
public class ListFilterNode extends ExpressionNode {

  @Child private ExpressionNode listNode;
  @Child private ExpressionNode functionNode;
  @Child private LoopNode filterLoopNode;
  @Child private LoopNode toArrayLoopNode;

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

  @Child
  ArrayOperationNodes.ArrayBuildNode arrayBuildNode =
      ArrayOperationNodesFactory.ArrayBuildNodeGen.create();

  @Child
  ArrayOperationNodes.ArrayBuildListNode arrayBuildListNode =
      ArrayOperationNodesFactory.ArrayBuildListNodeGen.create();

  private final Rql2Type resultType;

  private final int generatorSlot;
  private final int functionSlot;
  private final int llistSlot;
  private final int currentIdxSlot;
  private final int listSizeSlot;
  private final int resultSlot;

  public ListFilterNode(
      ExpressionNode listNode,
      ExpressionNode functionNode,
      Rql2Type resultType,
      int generatorSlot,
      int functionSlot,
      int listSlot,
      int currentIdxSlot,
      int listSizeSlot,
      int resultSlot) {
    this.resultType = resultType;
    this.listNode = listNode;
    this.functionNode = functionNode;
    this.generatorSlot = generatorSlot;
    this.functionSlot = functionSlot;
    this.llistSlot = listSlot;
    this.currentIdxSlot = currentIdxSlot;
    this.listSizeSlot = listSizeSlot;
    this.resultSlot = resultSlot;
    this.filterLoopNode =
        Truffle.getRuntime()
            .createLoopNode(
                new OSRGeneratorNode(
                    new OSRHasNextConditionNode(generatorSlot),
                    new OSRListFilterBodyNode(generatorSlot, functionSlot, listSlot)));
    toArrayLoopNode =
        Truffle.getRuntime()
            .createLoopNode(
                new OSRGeneratorNode(
                    new OSRIsLessThanSizeConditionNode(currentIdxSlot, listSizeSlot),
                    new OSRToArrayBodyNode(resultType, listSlot, currentIdxSlot, resultSlot)));
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
      frame.setObject(llistSlot, new ArrayList<>());
      filterLoopNode.execute(frame);
      @SuppressWarnings("unchecked")
      ArrayList<Object> llist = (ArrayList<Object>) frame.getObject(llistSlot);
      int size = llist.size();
      frame.setObject(resultSlot, arrayBuildNode.execute(this, resultType, size));
      frame.setInt(currentIdxSlot, 0);
      frame.setInt(listSizeSlot, size);
      frame.setObject(llistSlot, llist);
      toArrayLoopNode.execute(frame);
      return arrayBuildListNode.execute(this, frame.getObject(resultSlot));
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }
}
