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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;

public class OSRMultiAggregationNode extends Node implements RepeatingNode {

  @Child
  private GeneratorNodes.GeneratorHasNextNode hasNextNode =
      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child AggregatorNodes.Merge mergeNode = AggregatorNodesFactory.MergeNodeGen.create();
  private final byte[] aggregationTypes;

  @CompilerDirectives.CompilationFinal private Object generator;

  @CompilerDirectives.CompilationFinal(dimensions = 0)
  private Object[] currentResults;

  public OSRMultiAggregationNode(byte[] aggregationTypes) {
    this.aggregationTypes = aggregationTypes;
  }

  public Object[] getResults() {
    return currentResults;
  }

  public void init(Object generator, Object[] zeroes) {
    this.generator = generator;
    this.currentResults = zeroes;
  }

  public boolean executeRepeating(VirtualFrame frame) {
    if (!hasNextNode.execute(this, generator)) {
      return false;
    }
    Object next = nextNode.execute(this, generator);
    for (int i = 0; i < aggregationTypes.length; i++) {
      currentResults[i] = mergeNode.execute(this, aggregationTypes[i], currentResults[i], next);
    }
    return true;
  }
}
