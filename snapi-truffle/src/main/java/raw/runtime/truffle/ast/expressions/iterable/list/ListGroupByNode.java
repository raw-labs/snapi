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

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.util.ArrayList;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.OffHeapGroupByKey;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.record_shaper.RecordShaper;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.list.ListLibrary;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.operators.OperatorNodes;
import raw.runtime.truffle.runtime.operators.OperatorNodesFactory;
import raw.runtime.truffle.runtime.record.RecordObject;
import raw.sources.api.SourceContext;

@NodeInfo(shortName = "List.GroupBy")
@NodeChild("input")
@NodeChild("keyFun")
@NodeField(name = "keyType", type = Rql2TypeWithProperties.class)
@NodeField(name = "rowType", type = Rql2TypeWithProperties.class)
public abstract class ListGroupByNode extends ExpressionNode {

  @Child
  OperatorNodes.CompareNode compare = insert(OperatorNodesFactory.CompareNodeGen.getUncached());

  protected abstract Rql2TypeWithProperties getKeyType();

  protected abstract Rql2TypeWithProperties getRowType();

  static final int LIB_LIMIT = 2;

  @Specialization(limit = "3")
  protected Object doGroup(
      Object input,
      Object keyFun,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode initNode,
      @Cached GeneratorNodes.GeneratorNextNode nextNode,
      @Cached GeneratorNodes.GeneratorHasNextNode hasNextNode,
      @Cached GeneratorNodes.GeneratorCloseNode closeNode,
      @Cached OffHeapNodes.OffHeapGroupByPutNode putNode,
      @Cached OffHeapNodes.OffHeapGeneratorNode generatorNode,
      @CachedLibrary("keyFun") InteropLibrary keyFunLib,
      @CachedLibrary("input") ListLibrary lists) {
    Object iterable = lists.toIterable(input);
    SourceContext context = RawContext.get(this).getSourceContext();
    OffHeapGroupByKey map =
        new OffHeapGroupByKey(
            getKeyType(),
            getRowType(),
            RawLanguage.get(this),
            context,
            new RecordShaper(RawLanguage.get(this), true));
    Object generator = getGeneratorNode.execute(iterable);
    try {
      initNode.execute(generator);
      while (hasNextNode.execute(generator)) {
        Object v = nextNode.execute(generator);
        Object key = keyFunLib.execute(keyFun, v);
        putNode.execute(map, key, v);
      }
    } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
      throw new RawTruffleRuntimeException("failed to execute function");
    } finally {
      closeNode.execute(generator);
    }
    ArrayList<RecordObject> items = new ArrayList<>();
    Object mapGenerator = generatorNode.execute(map);
    try {
      initNode.execute(mapGenerator);
      while (hasNextNode.execute(mapGenerator)) {
        RecordObject record = (RecordObject) nextNode.execute(mapGenerator);
        items.add(record);
      }
    } finally {
      closeNode.execute(mapGenerator);
    }
    return new ObjectList(items.toArray());
  }
}
