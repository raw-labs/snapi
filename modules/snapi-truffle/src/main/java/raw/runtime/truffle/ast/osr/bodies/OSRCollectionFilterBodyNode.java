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

import com.oracle.truffle.api.frame.VirtualFrame;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodes;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;
import raw.runtime.truffle.tryable_nullable.TryableNullableNodes;
import raw.runtime.truffle.tryable_nullable.TryableNullableNodesFactory;

public class OSRCollectionFilterBodyNode extends ExpressionNode {

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

  private final int resultSlot;

  public OSRCollectionFilterBodyNode(int generatorSlot, int functionSlot, int resultSlot) {
    this.generatorSlot = generatorSlot;
    this.functionSlot = functionSlot;
    this.resultSlot = resultSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object generator = frame.getObject(generatorSlot);
    Object predicate = frame.getObject(functionSlot);
    Object v = nextNode.execute(this, generator);

    boolean isPredicateTrue =
        handlePredicateNode.execute(
            this, functionExecuteOneNode.execute(this, predicate, v), false);
    if (isPredicateTrue) {
      frame.setObject(resultSlot, v);
    }
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
