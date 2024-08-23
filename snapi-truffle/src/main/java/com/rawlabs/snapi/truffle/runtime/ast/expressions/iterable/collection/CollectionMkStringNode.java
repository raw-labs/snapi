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

package com.rawlabs.snapi.truffle.runtime.ast.expressions.iterable.collection;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.ast.osr.OSRGeneratorNode;
import com.rawlabs.snapi.truffle.runtime.ast.osr.bodies.OSRCollectionMkStringBodyNode;
import com.rawlabs.snapi.truffle.runtime.ast.osr.conditions.OSRHasNextConditionNode;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.GeneratorNodesFactory;
import com.rawlabs.snapi.truffle.runtime.runtime.iterable.IterableNodes;
import com.rawlabs.snapi.truffle.runtime.runtime.iterable.IterableNodesFactory;
import com.rawlabs.snapi.truffle.runtime.runtime.operators.OperatorNodes;
import com.rawlabs.snapi.truffle.runtime.runtime.operators.OperatorNodesFactory;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.ErrorObject;

@NodeInfo(shortName = "Collection.MkString")
public class CollectionMkStringNode extends ExpressionNode {

  @Child private ExpressionNode iterableNode;
  @Child private ExpressionNode startNode;
  @Child private ExpressionNode sepNode;
  @Child private ExpressionNode endNode;
  @Child private LoopNode mkStringLoopNode;

  @Child
  private GeneratorNodes.GeneratorInitNode initNode =
      GeneratorNodesFactory.GeneratorInitNodeGen.create();

  @Child
  private IterableNodes.GetGeneratorNode getGeneratorNode =
      IterableNodesFactory.GetGeneratorNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorCloseNode closeNode =
      GeneratorNodesFactory.GeneratorCloseNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorHasNextNode hasNextNode =
      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child private OperatorNodes.AddNode add = OperatorNodesFactory.AddNodeGen.create();

  private final int generatorSlot;
  private final int sepSlot;
  private final int resultSlot;

  public CollectionMkStringNode(
      ExpressionNode iterableNode,
      ExpressionNode startNode,
      ExpressionNode sepNode,
      ExpressionNode endNode,
      int generatorSlot,
      int sepSlot,
      int resultSlot) {
    this.iterableNode = iterableNode;
    this.startNode = startNode;
    this.sepNode = sepNode;
    this.endNode = endNode;
    this.generatorSlot = generatorSlot;
    this.sepSlot = sepSlot;
    this.resultSlot = resultSlot;
    this.mkStringLoopNode =
        Truffle.getRuntime()
            .createLoopNode(
                new OSRGeneratorNode(
                    new OSRHasNextConditionNode(generatorSlot),
                    new OSRCollectionMkStringBodyNode(generatorSlot, sepSlot, resultSlot)));
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object iterable = iterableNode.executeGeneric(frame);
    String start = (String) startNode.executeGeneric(frame);
    String sep = (String) sepNode.executeGeneric(frame);
    String end = (String) endNode.executeGeneric(frame);
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      initNode.execute(this, generator);
      String currentString = start;
      if (!hasNextNode.execute(this, generator)) {
        return start + end;
      } else {
        Object next = nextNode.execute(this, generator);
        currentString = (String) add.execute(this, currentString, next);
      }

      frame.setObject(generatorSlot, generator);
      frame.setObject(sepSlot, sep);
      frame.setObject(resultSlot, currentString);
      mkStringLoopNode.execute(frame);
      currentString = (String) frame.getObject(resultSlot);

      return currentString + end;
    } catch (TruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      closeNode.execute(this, generator);
    }
  }
}
