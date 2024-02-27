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

import com.esotericsoftware.kryo.io.Output;
import com.oracle.truffle.api.frame.VirtualFrame;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations.JoinComputeNext;
import raw.runtime.truffle.runtime.kryo.KryoNodes;
import raw.runtime.truffle.runtime.kryo.KryoNodesFactory;

public class OSRCollectionJoinInitBodyNode extends ExpressionNode {

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child KryoNodes.KryoWriteNode kryoWrite = KryoNodesFactory.KryoWriteNodeGen.create();

  private final int generatorSlot;
  private final int computeNextSlot;
  private final int outputBufferSlot;

  public OSRCollectionJoinInitBodyNode(
      int generatorSlot, int computeNextSlot, int outputBufferSlot) {
    this.generatorSlot = generatorSlot;
    this.computeNextSlot = computeNextSlot;
    this.outputBufferSlot = outputBufferSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object generator = frame.getAuxiliarySlot(generatorSlot);
    JoinComputeNext computeNext = (JoinComputeNext) frame.getAuxiliarySlot(computeNextSlot);
    Output buffer = (Output) frame.getAuxiliarySlot(outputBufferSlot);
    Object row = nextNode.execute(this, generator);
    kryoWrite.execute(this, buffer, computeNext.getRightRowType(), row);
    computeNext.setSpilledRight(computeNext.getSpilledRight() + 1);
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
