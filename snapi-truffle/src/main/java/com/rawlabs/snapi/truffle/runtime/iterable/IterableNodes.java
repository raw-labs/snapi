/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.truffle.runtime.iterable;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.osr.OSRGeneratorNode;
import com.rawlabs.snapi.truffle.ast.osr.bodies.OSRCollectionEquiJoinInitBodyNode;
import com.rawlabs.snapi.truffle.ast.osr.bodies.OSRDistinctGetGeneratorNode;
import com.rawlabs.snapi.truffle.ast.osr.bodies.OSROrderByGetGeneratorNode;
import com.rawlabs.snapi.truffle.ast.osr.conditions.OSRHasNextConditionNode;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.generator.collection.StaticInitializers;
import com.rawlabs.snapi.truffle.runtime.generator.collection.abstract_generator.AbstractGenerator;
import com.rawlabs.snapi.truffle.runtime.generator.collection.abstract_generator.compute_next.operations.*;
import com.rawlabs.snapi.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodes;
import com.rawlabs.snapi.truffle.runtime.generator.collection.off_heap_generator.off_heap.distinct.OffHeapDistinct;
import com.rawlabs.snapi.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.OffHeapGroupByKey;
import com.rawlabs.snapi.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by.OffHeapGroupByKeys;
import com.rawlabs.snapi.truffle.runtime.generator.collection.off_heap_generator.record_shaper.RecordShaper;
import com.rawlabs.snapi.truffle.runtime.iterable.list.ListIterable;
import com.rawlabs.snapi.truffle.runtime.iterable.operations.*;
import com.rawlabs.snapi.truffle.runtime.iterable.sources.*;

public class IterableNodes {
  @NodeInfo(shortName = "Iterable.GetGenerator")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(StaticInitializers.class)
  public abstract static class GetGeneratorNode extends Node {

    public abstract Object execute(Node node, Object generator);

    @Specialization
    static Object getGenerator(Node node, ExpressionCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(Node node, CsvCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(Node node, CsvFromStringCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(Node node, IntRangeCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(Node node, JdbcQueryCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(Node node, JsonReadCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(Node node, LongRangeCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(Node node, ReadLinesCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(Node node, TimestampRangeCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(Node node, UnionCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(Node node, XmlParseCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(Node node, XmlReadCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(Node node, EmptyCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(
        Node node,
        FilterCollection collection,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("getGenerator2")
            IterableNodes.GetGeneratorNode getGeneratorNode) {
      Object parentGenerator = getGeneratorNode.execute(thisNode, collection.getParentIterable());
      return new AbstractGenerator(
          new FilterComputeNext(
              parentGenerator,
              collection.getPredicate(),
              collection.getFrame(),
              collection.getGeneratorSlot(),
              collection.getFunctionSlot(),
              collection.getResultSlot()));
    }

    @Specialization
    static Object getGenerator(
        Node node,
        TakeCollection collection,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("getGenerator2")
            IterableNodes.GetGeneratorNode getGeneratorNode) {
      Object parentGenerator = getGeneratorNode.execute(thisNode, collection.getParentIterable());
      return new AbstractGenerator(
          new TakeComputeNext(parentGenerator, collection.getCachedCount()));
    }

    @Specialization
    static Object getGenerator(
        Node node,
        TransformCollection collection,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("getGenerator2")
            IterableNodes.GetGeneratorNode getGeneratorNode) {
      Object parentGenerator = getGeneratorNode.execute(thisNode, collection.getParentIterable());
      return new AbstractGenerator(
          new TransformComputeNext(parentGenerator, collection.getTransform()));
    }

    @Specialization
    static Object getGenerator(
        Node node,
        UnnestCollection collection,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("getGenerator2")
            IterableNodes.GetGeneratorNode getGeneratorNode) {
      Object parentGenerator = getGeneratorNode.execute(thisNode, collection.getParentIterable());
      return new AbstractGenerator(
          new UnnestComputeNext(parentGenerator, collection.getTransform()));
    }

    @Specialization
    static Object getGenerator(
        Node node,
        ZipCollection collection,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("getGenerator1")
            IterableNodes.GetGeneratorNode getGeneratorNode1,
        @Cached(inline = false) @Cached.Shared("getGenerator2")
            IterableNodes.GetGeneratorNode getGeneratorNode2) {
      Object parentGenerator1 =
          getGeneratorNode1.execute(thisNode, collection.getParentIterable1());
      Object parentGenerator2 =
          getGeneratorNode2.execute(thisNode, collection.getParentIterable2());
      return new AbstractGenerator(new ZipComputeNext(parentGenerator1, parentGenerator2));
    }

    public static LoopNode getDistinctGenLoopNode(DistinctCollection collection) {
      return Truffle.getRuntime()
          .createLoopNode(
              new OSRGeneratorNode(
                  new OSRHasNextConditionNode(collection.getGeneratorSlot()),
                  new OSRDistinctGetGeneratorNode(
                      collection.getGeneratorSlot(), collection.getOffHeapDistinctSlot())));
    }

    @Specialization(guards = "cachedCollection.hasSameSlots(collection)", limit = "8", unroll = 8)
    static Object getGenerator(
        Node node,
        DistinctCollection collection,
        @Bind("$node") Node thisNode,
        @Cached("collection") DistinctCollection cachedCollection,
        @Cached(
                value = "getDistinctGenLoopNode(cachedCollection)",
                inline = false,
                allowUncached = true)
            LoopNode loopNode,
        @Cached(inline = false) @Cached.Shared("getGenerator1")
            IterableNodes.GetGeneratorNode getGeneratorNode,
        @Cached GeneratorNodes.GeneratorInitNode initNode,
        @Cached @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode closeNode,
        @Cached @Cached.Shared("generator") OffHeapNodes.OffHeapGeneratorNode generatorNode,
        @Cached(value = "getContextValues(thisNode)", dimensions = 1, allowUncached = true)
            long[] contextValues) {
      OffHeapDistinct index =
          new OffHeapDistinct(
              collection.getRowType(),
              collection.getFrame(),
              contextValues[0],
              (int) contextValues[1],
              (int) contextValues[2]);
      Object generator = getGeneratorNode.execute(thisNode, collection.getIterable());
      try {
        initNode.execute(thisNode, generator);

        MaterializedFrame frame = collection.getFrame();
        frame.setObject(collection.getGeneratorSlot(), generator);
        frame.setObject(collection.getOffHeapDistinctSlot(), index);

        loopNode.execute(frame);
      } finally {
        closeNode.execute(thisNode, generator);
      }
      return generatorNode.execute(thisNode, index);
    }

    @Specialization
    static Object getGenerator(Node node, EquiJoinCollection collection) {
      return collection.getGenerator();
    }

    public static LoopNode getEquiJoinInitLoopNode(GroupByCollection collection) {
      return Truffle.getRuntime()
          .createLoopNode(
              new OSRGeneratorNode(
                  new OSRHasNextConditionNode(collection.getGeneratorSlot()),
                  new OSRCollectionEquiJoinInitBodyNode(
                      collection.getGeneratorSlot(),
                      collection.getKeyFunctionSlot(),
                      collection.getMapSlot())));
    }

    @Specialization(guards = "cachedCollection.hasSameSlots(collection)", limit = "8", unroll = 8)
    static Object getGenerator(
        Node node,
        GroupByCollection collection,
        @Bind("$node") Node thisNode,
        @Cached("collection") GroupByCollection cachedCollection,
        @Cached(
                value = "getEquiJoinInitLoopNode(cachedCollection)",
                inline = false,
                allowUncached = true)
            LoopNode loopNode,
        @Cached(inline = false) @Cached.Shared("getGenerator1")
            IterableNodes.GetGeneratorNode getGeneratorNode,
        @Cached(inline = false) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode,
        @Cached @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode closeNode,
        @Cached @Cached.Shared("generator") OffHeapNodes.OffHeapGeneratorNode generatorNode,
        @Cached(value = "getContextValues(thisNode)", dimensions = 1, allowUncached = true)
            long[] contextValues) {
      MaterializedFrame frame = collection.getFrame();
      OffHeapGroupByKey map =
          new OffHeapGroupByKey(
              collection.getKeyType(),
              collection.getRowType(),
              new RecordShaper(false),
              contextValues[0],
              (int) contextValues[1],
              (int) contextValues[2]);
      Object inputGenerator = getGeneratorNode.execute(thisNode, collection.getIterable());
      try {
        initNode.execute(thisNode, inputGenerator);
        frame.setObject(collection.getGeneratorSlot(), inputGenerator);
        frame.setObject(collection.getMapSlot(), map);
        frame.setObject(collection.getKeyFunctionSlot(), collection.getKeyFun());
        loopNode.execute(collection.getFrame());
      } finally {
        closeNode.execute(thisNode, inputGenerator);
      }
      return generatorNode.execute(thisNode, map);
    }

    @Specialization
    static Object getGenerator(Node node, JoinCollection collection) {
      return collection.getGenerator();
    }

    public static LoopNode getOrderByGenNode(OrderByCollection collection) {
      return Truffle.getRuntime()
          .createLoopNode(
              new OSRGeneratorNode(
                  new OSRHasNextConditionNode(collection.getGeneratorSlot()),
                  new OSROrderByGetGeneratorNode(
                      collection.getGeneratorSlot(),
                      collection.getCollectionSlot(),
                      collection.getOffHeapGroupByKeysSlot())));
    }

    @Specialization(guards = "cachedCollection.hasSameSlots(collection)", limit = "8", unroll = 8)
    static Object getGenerator(
        Node node,
        OrderByCollection collection,
        @Bind("$node") Node thisNode,
        @Cached("collection") OrderByCollection cachedCollection,
        @Cached(value = "getOrderByGenNode(cachedCollection)", inline = false, allowUncached = true)
            LoopNode loopNode,
        @Cached(inline = false) @Cached.Shared("getGenerator1")
            IterableNodes.GetGeneratorNode getGeneratorNode,
        @Cached(inline = false) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode,
        @Cached @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode closeNode,
        @Cached @Cached.Shared("generator") OffHeapNodes.OffHeapGeneratorNode generatorNode,
        @Cached(value = "getContextValues(thisNode)", dimensions = 1, allowUncached = true)
            long[] contextValues) {
      Object generator = getGeneratorNode.execute(thisNode, collection.getParentIterable());
      OffHeapGroupByKeys groupByKeys =
          new OffHeapGroupByKeys(
              collection.getKeyTypes(),
              collection.getRowType(),
              collection.getKeyOrderings(),
              contextValues[0],
              (int) contextValues[1],
              (int) contextValues[2]);
      try {
        initNode.execute(thisNode, generator);

        Frame frame = collection.getFrame();
        frame.setObject(collection.getGeneratorSlot(), generator);
        frame.setObject(collection.getOffHeapGroupByKeysSlot(), groupByKeys);
        frame.setObject(collection.getCollectionSlot(), collection);

        loopNode.execute(collection.getFrame());
      } finally {
        closeNode.execute(thisNode, generator);
      }
      return generatorNode.execute(thisNode, groupByKeys);
    }

    @Specialization
    static Object getGenerator(Node node, ListIterable collection) {
      return collection.getGenerator();
    }
  }
}
