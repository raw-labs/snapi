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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodes;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations.FilterComputeNext;
import raw.runtime.truffle.tryable_nullable.TryableNullable;

public class OSRFilterNode extends Node implements RepeatingNode {

  @Child
  private GeneratorNodes.GeneratorHasNextNode hasNextNode =
      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child
  FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode =
      FunctionExecuteNodesFactory.FunctionExecuteOneNodeGen.create();

  @CompilerDirectives.CompilationFinal private FilterComputeNext computeNext;

  public void init(FilterComputeNext computeNext) {
    this.computeNext = computeNext;
  }

  public boolean executeRepeating(VirtualFrame frame) {
    // ignored
    return false;
  }

  public boolean shouldContinue(Object returnValue) {
    return returnValue == this.initialLoopStatus()
        || (returnValue == null && hasNextNode.execute(this, computeNext.getParent()));
  }

  public Object executeRepeatingWithValue(VirtualFrame frame) {
    Object v = nextNode.execute(this, computeNext.getParent());
    Boolean isPredicateTrue = null;
    isPredicateTrue =
        TryableNullable.handlePredicate(
            functionExecuteOneNode.execute(this, computeNext.getPredicate(), v), false);
    if (isPredicateTrue) {
      return v;
    } else {
      return null;
    }
  }
}
