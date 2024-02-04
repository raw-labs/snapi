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

package raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.osr;

import com.esotericsoftware.kryo.io.Output;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations.JoinComputeNext;
import raw.runtime.truffle.runtime.kryo.KryoNodes;
import raw.runtime.truffle.runtime.kryo.KryoNodesFactory;

public class OSRJoinInitNode extends Node implements RepeatingNode {

  @Child
  private GeneratorNodes.GeneratorHasNextNode hasNextNode =
      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child KryoNodes.KryoWriteNode kryoWrite = KryoNodesFactory.KryoWriteNodeGen.create();

  private Object generator;
  private JoinComputeNext computeNext;
  private Output buffer;

  public void init(JoinComputeNext computeNext, Object generator, Output buffer) {
    this.computeNext = computeNext;
    this.buffer = buffer;
    this.generator = generator;
  }

  // keep iterating until we find matching keys
  public boolean executeRepeating(VirtualFrame frame) {
    if (hasNextNode.execute(this, generator)) {
      Object row = nextNode.execute(this, generator);
      kryoWrite.execute(this, buffer, computeNext.getRightRowType(), row);
      computeNext.setSpilledRight(computeNext.getSpilledRight() + 1);
      return true;
    }
    return false;
  }
}
