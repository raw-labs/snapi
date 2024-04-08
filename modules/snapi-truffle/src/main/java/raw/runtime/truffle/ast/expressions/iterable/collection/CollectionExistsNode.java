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
import raw.runtime.truffle.ast.osr.bodies.OSRExistsBodyNode;
import raw.runtime.truffle.ast.osr.conditions.OSRExistsConditionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodesFactory;
import raw.runtime.truffle.runtime.primitives.ErrorObject;

@NodeInfo(shortName = "Collection.Exists")
public class CollectionExistsNode extends ExpressionNode {
  @Child private ExpressionNode iterableNode;
  @Child private ExpressionNode functionNode;
  @Child private LoopNode existsLoopNode;

  @Child
  private GeneratorNodes.GeneratorInitNode generatorInitNode =
      GeneratorNodesFactory.GeneratorInitNodeGen.create();

  @Child
  private IterableNodes.GetGeneratorNode getGeneratorNode =
      IterableNodesFactory.GetGeneratorNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorCloseNode generatorCloseNode =
      GeneratorNodesFactory.GeneratorCloseNodeGen.create();

  private final int generatorSlot;
  private final int functionSlot;
  private final int predicateResultSlot;

  public CollectionExistsNode(
      ExpressionNode iterableNode,
      ExpressionNode functionNode,
      int generatorSlot,
      int functionSlot,
      int predicateResultSlot) {
    this.iterableNode = iterableNode;
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
    Object function = functionNode.executeGeneric(frame);
    Object iterable = iterableNode.executeGeneric(frame);
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      frame.setObject(generatorSlot, generator);
      frame.setObject(functionSlot, function);
      frame.setBoolean(predicateResultSlot, false);
      existsLoopNode.execute(frame);
      return frame.getBoolean(predicateResultSlot);
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }

  @Override
  public boolean executeBoolean(VirtualFrame virtualFrame) {
    return (boolean) executeGeneric(virtualFrame);
  }
}
