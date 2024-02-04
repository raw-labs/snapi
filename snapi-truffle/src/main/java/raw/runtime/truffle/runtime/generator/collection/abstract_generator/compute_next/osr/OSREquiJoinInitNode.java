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
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.OffHeapGroupByKey;

public class OSREquiJoinInitNode extends Node implements RepeatingNode {

  @Child
  private GeneratorNodes.GeneratorHasNextNode hasNextNode =
      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child
  FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode =
      FunctionExecuteNodesFactory.FunctionExecuteOneNodeGen.create();

  @Child
  OffHeapNodes.OffHeapGroupByPutNode putNode =
      OffHeapNodesFactory.OffHeapGroupByPutNodeGen.create();

  @CompilerDirectives.CompilationFinal private Object generator;
  @CompilerDirectives.CompilationFinal OffHeapGroupByKey map;
  @CompilerDirectives.CompilationFinal Object keyFunc;

  public void init(Object keyFunc, Object generator, OffHeapGroupByKey map) {
    this.keyFunc = keyFunc;
    this.generator = generator;
    this.map = map;
  }

  // keep iterating until we find matching keys
  public boolean executeRepeating(VirtualFrame frame) {
    if (hasNextNode.execute(this, generator)) {
      Object item = nextNode.execute(this, generator);
      Object key = functionExecuteOneNode.execute(this, this.keyFunc, item);
      putNode.execute(this, map, key, item);
      return true;
    }
    return false;
  }
}
