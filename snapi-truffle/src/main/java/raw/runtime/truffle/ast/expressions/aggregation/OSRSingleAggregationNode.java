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

package raw.runtime.truffle.ast.expressions.aggregation;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;

public class OSRSingleAggregationNode extends Node implements RepeatingNode {

  @Child
  private GeneratorNodes.GeneratorHasNextNode hasNextNode =
      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child AggregatorNodes.Merge mergeNode = AggregatorNodesFactory.MergeNodeGen.create();

  @CompilationFinal private Object generator;

  private final byte aggregationType;

  private Object currentResult;

  private boolean hasNext = true;

  public OSRSingleAggregationNode(byte aggregationType) {
    this.aggregationType = aggregationType;
  }

  public void init(Object generator, Object zero) {
    this.generator = generator;
    this.currentResult = zero;
  }

  public boolean executeRepeating(VirtualFrame frame) {
    // ignored
    return false;
  }

  // On first execution the return value is always CONTINUE_LOOP_STATUS
  // We don't want to return this status as a result of this execution.
  // In case of an empty input, hasNext will return false and executeRepeatingWithValue never
  // executes leading to CONTINUE_LOOP_STATUS returning
  public boolean shouldContinue(Object returnValue) {
    hasNext = hasNextNode.execute(this, generator);
    return hasNext || returnValue == this.initialLoopStatus();
  }

  public Object executeRepeatingWithValue(VirtualFrame frame) {
    if (hasNext) {
      Object next = nextNode.execute(this, generator);
      currentResult = mergeNode.execute(this, aggregationType, currentResult, next);
    }
    return currentResult;
  }
}
