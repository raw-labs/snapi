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

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.util.ArrayList;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.OffHeapGroupByKey;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.record_shaper.RecordShaper;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.list.ListNodes;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.record.RecordObject;
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

  static final int LIB_LIMIT = 2;

  @Specialization
  protected static Object doGroup(
      Object input,
      Object keyFun,
      @Bind("this") Node thisNode,
      @Cached(inline = true) IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) GeneratorNodes.GeneratorInitNode initNode,
      @Cached(inline = true) GeneratorNodes.GeneratorNextNode nextNode,
      @Cached(inline = true) GeneratorNodes.GeneratorHasNextNode hasNextNode,
      @Cached(inline = true) GeneratorNodes.GeneratorCloseNode closeNode,
      @Cached(inline = true) OffHeapNodes.OffHeapGroupByPutNode putNode,
      @Cached(inline = true) OffHeapNodes.OffHeapGeneratorNode generatorNode,
      @Cached(inline = true) FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode,
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
      while (hasNextNode.execute(thisNode, generator)) {
        Object v = nextNode.execute(thisNode, generator);
        Object key = functionExecuteOneNode.execute(thisNode, keyFun, v);
        putNode.execute(thisNode, map, key, v);
      }
    } finally {
      closeNode.execute(thisNode, generator);
    }
    ArrayList<RecordObject> items = new ArrayList<>();
    Object mapGenerator = generatorNode.execute(thisNode, map);
    try {
      initNode.execute(thisNode, mapGenerator);
      while (hasNextNode.execute(thisNode, mapGenerator)) {
        RecordObject record = (RecordObject) nextNode.execute(thisNode, mapGenerator);
        items.add(record);
      }
    } finally {
      closeNode.execute(thisNode, mapGenerator);
    }
    return new ObjectList(items.toArray());
  }
}
