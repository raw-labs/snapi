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

package raw.runtime.truffle.runtime.iterable.osr;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodes;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by.OffHeapGroupByKeys;
import raw.runtime.truffle.runtime.iterable.operations.OrderByCollection;

public class OSROrderByGetGeneratorNode extends Node implements RepeatingNode {

  @Child
  private GeneratorNodes.GeneratorHasNextNode hasNextNode =
      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child
  private OffHeapNodes.OffHeapGroupByPutNode putNode =
      OffHeapNodesFactory.OffHeapGroupByPutNodeGen.create();

  @Child
  private FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode =
      FunctionExecuteNodesFactory.FunctionExecuteOneNodeGen.create();

  @CompilerDirectives.CompilationFinal private Object generator;
  @CompilerDirectives.CompilationFinal OrderByCollection collection;
  @CompilerDirectives.CompilationFinal OffHeapGroupByKeys groupByKeys;
  @CompilerDirectives.CompilationFinal private int funLen;

  public void init(Object generator, OrderByCollection collection, OffHeapGroupByKeys groupByKeys) {
    this.generator = generator;
    this.collection = collection;
    this.groupByKeys = groupByKeys;
    funLen = collection.getKeyFunctions().length;
  }

  // keep iterating until we find matching keys
  public boolean executeRepeating(VirtualFrame frame) {
    if (hasNextNode.execute(this, generator)) {
      Object v = nextNode.execute(this, generator);
      Object[] key = new Object[funLen];
      for (int i = 0; i < funLen; i++) {
        key[i] = functionExecuteOneNode.execute(this, collection.getKeyFunctions()[i], v);
      }
      putNode.execute(this, groupByKeys, key, v);
      return true;
    }
    return false;
  }
}
