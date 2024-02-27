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

package raw.runtime.truffle.ast.osr.bodies;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodes;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;
import raw.runtime.truffle.tryable_nullable.TryableNullable;

public class OSRCollectionFilterBodyNode extends ExpressionNode {

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child
  FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode =
      FunctionExecuteNodesFactory.FunctionExecuteOneNodeGen.create();

  private final int generatorSlot;

  private final int functionSlot;

  private final int resultSlot;

  public OSRCollectionFilterBodyNode(int generatorSlot, int functionSlot, int resultSlot) {
    this.generatorSlot = generatorSlot;
    this.functionSlot = functionSlot;
    this.resultSlot = resultSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    FrameDescriptor frameDescriptor = frame.getFrameDescriptor();
    Object generator = frame.getAuxiliarySlot(generatorSlot);
    Object predicate = frame.getAuxiliarySlot(functionSlot);
    Object v = nextNode.execute(this, generator);

    boolean isPredicateTrue =
        TryableNullable.handlePredicate(functionExecuteOneNode.execute(this, predicate, v), false);
    if (isPredicateTrue) {
      frame.setAuxiliarySlot(resultSlot, v);
    }
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
