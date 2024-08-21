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

package raw.runtime.truffle.ast.expressions.iterable.collection;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.osr.OSRGeneratorNode;
import raw.runtime.truffle.ast.osr.bodies.OSRCollectionMkStringBodyNode;
import raw.runtime.truffle.ast.osr.conditions.OSRHasNextConditionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodesFactory;
import raw.runtime.truffle.runtime.operators.OperatorNodes;
import raw.runtime.truffle.runtime.operators.OperatorNodesFactory;
import raw.runtime.truffle.runtime.primitives.ErrorObject;

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
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      closeNode.execute(this, generator);
    }
  }
}
