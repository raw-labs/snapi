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
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.rawlabs.snapi.frontend.snapi.source.SnapiType;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.TypeGuards;
import com.rawlabs.snapi.truffle.ast.expressions.iterable.ArrayOperationNodes;
import com.rawlabs.snapi.truffle.ast.expressions.iterable.ArrayOperationNodesFactory;
import com.rawlabs.snapi.truffle.ast.osr.OSRGeneratorNode;
import com.rawlabs.snapi.truffle.ast.osr.bodies.OSRListFromBodyNode;
import com.rawlabs.snapi.truffle.ast.osr.bodies.OSRToArrayBodyNode;
import com.rawlabs.snapi.truffle.ast.osr.conditions.OSRHasNextConditionNode;
import com.rawlabs.snapi.truffle.ast.osr.conditions.OSRIsLessThanSizeConditionNode;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodesFactory;
import com.rawlabs.snapi.truffle.runtime.iterable.IterableNodes;
import com.rawlabs.snapi.truffle.runtime.iterable.IterableNodesFactory;
import com.rawlabs.snapi.truffle.runtime.primitives.ErrorObject;
import java.util.ArrayList;

@ImportStatic(value = TypeGuards.class)
public class ListFromNode extends ExpressionNode {

  @Child private ExpressionNode iterableNode;
  @Child private LoopNode listFromLoopNode;
  @Child private LoopNode toArrayLoopNode;

  @Child
  private GeneratorNodes.GeneratorInitNode generatorInitNode =
      GeneratorNodesFactory.GeneratorInitNodeGen.create();

  @Child
  private IterableNodes.GetGeneratorNode getGeneratorNode =
      IterableNodesFactory.GetGeneratorNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorCloseNode generatorCloseNode =
      GeneratorNodesFactory.GeneratorCloseNodeGen.create();

  @Child
  private ArrayOperationNodes.ArrayBuildNode arrayBuildNode =
      ArrayOperationNodesFactory.ArrayBuildNodeGen.create();

  @Child
  private ArrayOperationNodes.ArrayBuildListNode arrayBuildListNode =
      ArrayOperationNodesFactory.ArrayBuildListNodeGen.create();

  private final SnapiType resultType;

  private final int generatorSlot;
  private final int listSlot;
  private final int currentIdxSlot;
  private final int listSizeSlot;
  private final int resultSlot;

  public ListFromNode(
          ExpressionNode iterableNode,
          SnapiType resultType,
          int generatorSlot,
          int listSlot,
          int currentIdxSlot,
          int listSizeSlot,
          int resultSlot) {
    this.resultType = resultType;
    this.iterableNode = iterableNode;
    this.generatorSlot = generatorSlot;
    this.listSlot = listSlot;
    this.currentIdxSlot = currentIdxSlot;
    this.listSizeSlot = listSizeSlot;
    this.resultSlot = resultSlot;
    this.listFromLoopNode =
        Truffle.getRuntime()
            .createLoopNode(
                new OSRGeneratorNode(
                    new OSRHasNextConditionNode(generatorSlot),
                    new OSRListFromBodyNode(generatorSlot, listSlot)));
    toArrayLoopNode =
        Truffle.getRuntime()
            .createLoopNode(
                new OSRGeneratorNode(
                    new OSRIsLessThanSizeConditionNode(currentIdxSlot, listSizeSlot),
                    new OSRToArrayBodyNode(resultType, listSlot, currentIdxSlot, resultSlot)));
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object iterable = iterableNode.executeGeneric(frame);
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      frame.setObject(generatorSlot, generator);
      frame.setObject(listSlot, new ArrayList<>());
      listFromLoopNode.execute(frame);
      @SuppressWarnings("unchecked")
      ArrayList<Object> llist = (ArrayList<Object>) frame.getObject(listSlot);
      int size = llist.size();
      frame.setObject(resultSlot, arrayBuildNode.execute(this, resultType, size));
      frame.setInt(currentIdxSlot, 0);
      frame.setInt(listSizeSlot, size);
      frame.setObject(listSlot, llist);
      toArrayLoopNode.execute(frame);
      return arrayBuildListNode.execute(this, frame.getObject(resultSlot));
    } catch (TruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }
}
