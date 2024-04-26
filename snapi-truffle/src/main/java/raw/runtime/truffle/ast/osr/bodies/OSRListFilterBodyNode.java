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

package raw.runtime.truffle.ast.osr.bodies;

import com.oracle.truffle.api.frame.VirtualFrame;
import java.util.ArrayList;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodes;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;
import raw.runtime.truffle.tryable_nullable.TryableNullableNodes;
import raw.runtime.truffle.tryable_nullable.TryableNullableNodesFactory;

public class OSRListFilterBodyNode extends ExpressionNode {

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
  private final int llistSlot;

  public OSRListFilterBodyNode(int generatorSlot, int functionSlot, int llistSlot) {
    this.generatorSlot = generatorSlot;
    this.functionSlot = functionSlot;
    this.llistSlot = llistSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object generator = frame.getObject(generatorSlot);
    Object v = nextNode.execute(this, generator);
    Boolean predicate = null;
    Object function = frame.getObject(functionSlot);
    predicate =
        handlePredicateNode.execute(this, functionExecuteOneNode.execute(this, function, v), false);
    if (predicate) {
      @SuppressWarnings("unchecked")
      ArrayList<Object> llist = (ArrayList<Object>) frame.getObject(llistSlot);
      llist.add(v);
    }
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
