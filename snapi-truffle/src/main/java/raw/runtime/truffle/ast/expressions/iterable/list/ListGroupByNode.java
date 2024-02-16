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

package raw.runtime.truffle.ast.expressions.iterable.list;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.list.osr.OSRListFromNode;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.osr.OSREquiJoinInitNode;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.OffHeapGroupByKey;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.record_shaper.RecordShaper;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.list.ListNodes;
import raw.runtime.truffle.runtime.list.RawArrayList;
import raw.sources.api.SourceContext;

@NodeInfo(shortName = "List.GroupBy")
@NodeChild("input")
@NodeChild("keyFun")
@NodeField(name = "keyType", type = Rql2TypeWithProperties.class)
@NodeField(name = "rowType", type = Rql2TypeWithProperties.class)
public abstract class ListGroupByNode extends ExpressionNode {

  @Idempotent
  public abstract Rql2TypeWithProperties getKeyType();

  @Idempotent
  public abstract Rql2TypeWithProperties getRowType();

  public static LoopNode getInitNode() {
    return Truffle.getRuntime().createLoopNode(new OSREquiJoinInitNode());
  }

  public static LoopNode getFromLoopNode() {
    return Truffle.getRuntime().createLoopNode(new OSRListFromNode());
  }

  @Specialization
  protected static Object doGroup(
      VirtualFrame frame,
      Object input,
      Object keyFun,
      @Bind("this") Node thisNode,
      @Cached(value = "getInitNode()", allowUncached = true, neverDefault = true) LoopNode loopNode,
      @Cached(value = "getFromLoopNode()", allowUncached = true, neverDefault = true)
          LoopNode fromLoopNode,
      @Cached(inline = true) IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) GeneratorNodes.GeneratorInitNode initNode,
      @Cached(inline = true) GeneratorNodes.GeneratorNextNode nextNode,
      @Cached(inline = true) GeneratorNodes.GeneratorHasNextNode hasNextNode,
      @Cached(inline = true) GeneratorNodes.GeneratorCloseNode closeNode,
      @Cached(inline = true) OffHeapNodes.OffHeapGeneratorNode generatorNode,
      @Cached(inline = true) ListNodes.ToIterableNode toIterableNode) {
    Object iterable = toIterableNode.execute(thisNode, input);
    SourceContext context = RawContext.get(thisNode).getSourceContext();
    OffHeapGroupByKey map =
        new OffHeapGroupByKey(
            ((ListGroupByNode) thisNode).getKeyType(),
            ((ListGroupByNode) thisNode).getRowType(),
            RawLanguage.get(thisNode),
            context,
            new RecordShaper(RawLanguage.get(thisNode), true));
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      initNode.execute(thisNode, generator);
      OSREquiJoinInitNode osrNode = (OSREquiJoinInitNode) loopNode.getRepeatingNode();
      osrNode.init(keyFun, generator, map);
      loopNode.execute(frame);
    } finally {
      closeNode.execute(thisNode, generator);
    }
    Object mapGenerator = generatorNode.execute(thisNode, map);
    try {
      initNode.execute(thisNode, mapGenerator);
      OSRListFromNode osrNode = (OSRListFromNode) fromLoopNode.getRepeatingNode();
      osrNode.init(mapGenerator);
      fromLoopNode.execute(frame);
      return new RawArrayList(osrNode.getResult());
    } finally {
      closeNode.execute(thisNode, mapGenerator);
    }
  }
}
