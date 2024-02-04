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

package raw.runtime.truffle.ast.expressions.iterable.list.osr;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import java.util.ArrayList;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodes;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;
import raw.runtime.truffle.tryable_nullable.TryableNullable;

public class OSRListFilterNode extends Node implements RepeatingNode {

  @Child
  private GeneratorNodes.GeneratorHasNextNode hasNextNode =
      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child
  FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode =
      FunctionExecuteNodesFactory.FunctionExecuteOneNodeGen.create();

  private Object generator;
  private Object function;

  ArrayList<Object> llist;

  private boolean hasNext = false;

  public void init(Object generator, Object function) {
    this.generator = generator;
    this.function = function;
    llist = new ArrayList<>();
  }

  public boolean executeRepeating(VirtualFrame frame) {
    // ignored
    return false;
  }

  public boolean shouldContinue(Object returnValue) {
    hasNext = hasNextNode.execute(this, generator);
    return returnValue == this.initialLoopStatus() || hasNext;
  }

  public Object executeRepeatingWithValue(VirtualFrame frame) {
    if (hasNext) {
      Object v = nextNode.execute(this, generator);
      Boolean predicate = null;
      predicate =
          TryableNullable.handlePredicate(functionExecuteOneNode.execute(this, function, v), false);
      if (predicate) {
        llist.add(v);
      }
    }
    return llist;
  }
}
