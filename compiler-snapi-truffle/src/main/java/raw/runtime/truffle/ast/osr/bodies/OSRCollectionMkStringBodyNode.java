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
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;
import raw.runtime.truffle.runtime.operators.OperatorNodes;
import raw.runtime.truffle.runtime.operators.OperatorNodesFactory;

public class OSRCollectionMkStringBodyNode extends ExpressionNode {

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child OperatorNodes.AddNode add = OperatorNodesFactory.AddNodeGen.create();

  private final int generatorSlot;

  private final int sepSlot;

  private final int resultSlot;

  public OSRCollectionMkStringBodyNode(int generatorSlot, int sepSlot, int resultSlot) {
    this.generatorSlot = generatorSlot;
    this.sepSlot = sepSlot;
    this.resultSlot = resultSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object generator = frame.getObject(generatorSlot);
    Object next = nextNode.execute(this, generator);
    String sep = frame.getObject(sepSlot).toString();
    String resultString = frame.getObject(resultSlot).toString();
    String newResult = (String) add.execute(this, resultString + sep, next);
    frame.setObject(resultSlot, newResult);
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
