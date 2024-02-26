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

package raw.runtime.truffle.runtime.iterable;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ast.osr.AuxiliarySlots;
import raw.runtime.truffle.ast.osr.OSRGeneratorNode;
import raw.runtime.truffle.ast.osr.bodies.OSRCollectionEquiJoinInitBodyNode;
import raw.runtime.truffle.ast.osr.bodies.OSRDistinctGetGeneratorNode;
import raw.runtime.truffle.ast.osr.bodies.OSROrderByGetGeneratorNode;
import raw.runtime.truffle.ast.osr.conditions.OSRHasNextConditionAuxNode;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.AbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations.*;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.distinct.OffHeapDistinct;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.OffHeapGroupByKey;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by.OffHeapGroupByKeys;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.record_shaper.RecordShaper;
import raw.runtime.truffle.runtime.iterable.list.ListIterable;
import raw.runtime.truffle.runtime.iterable.operations.*;
import raw.runtime.truffle.runtime.iterable.sources.*;

public class IterableNodes {
  @NodeInfo(shortName = "Iterable.GetGenerator")
  @GenerateUncached
  @GenerateInline
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
          new FilterComputeNext(parentGenerator, collection.getPredicate(), collection.getFrame()));
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
      return new AbstractGenerator(
          new ZipComputeNext(parentGenerator1, parentGenerator2, collection.getLang()));
    }

    public static LoopNode getDistinctGenLoopNode() {
      return Truffle.getRuntime()
          .createLoopNode(
              new OSRGeneratorNode(
                  new OSRHasNextConditionAuxNode(), new OSRDistinctGetGeneratorNode()));
    }

    @Specialization
    static Object getGenerator(
        Node node,
        DistinctCollection collection,
        @Bind("$node") Node thisNode,
        @Cached(value = "getDistinctGenLoopNode()", inline = false, allowUncached = true)
            LoopNode loopNode,
        @Cached(inline = false) @Cached.Shared("getGenerator1")
            IterableNodes.GetGeneratorNode getGeneratorNode,
        @Cached GeneratorNodes.GeneratorInitNode initNode,
        @Cached @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode closeNode,
        @Cached @Cached.Shared("generator") OffHeapNodes.OffHeapGeneratorNode generatorNode) {
      OffHeapDistinct index =
          new OffHeapDistinct(
              collection.getRowType(), collection.getLang(), collection.getContext());
      Object generator = getGeneratorNode.execute(thisNode, collection.getIterable());
      try {
        initNode.execute(thisNode, generator);

        MaterializedFrame frame = collection.getFrame();
        FrameDescriptor frameDescriptor = frame.getFrameDescriptor();
        frame.setAuxiliarySlot(
            frameDescriptor.findOrAddAuxiliarySlot(AuxiliarySlots.GENERATOR_SLOT), generator);
        frame.setAuxiliarySlot(
            frameDescriptor.findOrAddAuxiliarySlot(AuxiliarySlots.OFF_HEAP_DISTINCT_SLOT), index);

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

    public static LoopNode getEquiJoinInitLoopNode() {
      return Truffle.getRuntime()
          .createLoopNode(
              new OSRGeneratorNode(
                  new OSRHasNextConditionAuxNode(), new OSRCollectionEquiJoinInitBodyNode()));
    }

    @Specialization
    static Object getGenerator(
        Node node,
        GroupByCollection collection,
        @Bind("$node") Node thisNode,
        @Cached(
                value = "getEquiJoinInitLoopNode()",
                inline = false,
                allowUncached = true,
                neverDefault = true)
            LoopNode loopNode,
        @Cached(inline = false) @Cached.Shared("getGenerator1")
            IterableNodes.GetGeneratorNode getGeneratorNode,
        @Cached(inline = false) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode,
        @Cached @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode closeNode,
        @Cached @Cached.Shared("generator") OffHeapNodes.OffHeapGeneratorNode generatorNode) {
      Frame frame = collection.getFrame();
      int mapSlot = frame.getFrameDescriptor().findOrAddAuxiliarySlot(AuxiliarySlots.MAP_SLOT);
      int generatorSlot =
          frame.getFrameDescriptor().findOrAddAuxiliarySlot(AuxiliarySlots.GENERATOR_SLOT);
      int functionSlot =
          frame.getFrameDescriptor().findOrAddAuxiliarySlot(AuxiliarySlots.FUNCTION_SLOT);
      OffHeapGroupByKey map =
          new OffHeapGroupByKey(
              collection.getKeyType(),
              collection.getRowType(),
              collection.getLang(),
              collection.getContext(),
              new RecordShaper(collection.getLang(), false));
      Object inputGenerator = getGeneratorNode.execute(thisNode, collection.getIterable());
      try {
        initNode.execute(thisNode, inputGenerator);
        frame.setAuxiliarySlot(generatorSlot, inputGenerator);
        frame.setAuxiliarySlot(mapSlot, map);
        frame.setAuxiliarySlot(functionSlot, collection.getKeyFun());
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

    public static LoopNode getOrderByGenNode() {
      return Truffle.getRuntime()
          .createLoopNode(
              new OSRGeneratorNode(
                  new OSRHasNextConditionAuxNode(), new OSROrderByGetGeneratorNode()));
    }

    @Specialization
    static Object getGenerator(
        Node node,
        OrderByCollection collection,
        @Bind("$node") Node thisNode,
        @Cached(
                value = "getOrderByGenNode()",
                inline = false,
                allowUncached = true,
                neverDefault = true)
            LoopNode loopNode,
        @Cached(inline = false) @Cached.Shared("getGenerator1")
            IterableNodes.GetGeneratorNode getGeneratorNode,
        @Cached(inline = false) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode,
        @Cached @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode closeNode,
        @Cached @Cached.Shared("generator") OffHeapNodes.OffHeapGeneratorNode generatorNode) {
      Object generator = getGeneratorNode.execute(thisNode, collection.getParentIterable());
      OffHeapGroupByKeys groupByKeys =
          new OffHeapGroupByKeys(
              collection.getKeyTypes(),
              collection.getRowType(),
              collection.getKeyOrderings(),
              collection.getLang(),
              collection.getContext());
      try {
        initNode.execute(thisNode, generator);

        Frame frame = collection.getFrame();
        FrameDescriptor frameDescriptor = frame.getFrameDescriptor();
        frame.setAuxiliarySlot(
            frameDescriptor.findOrAddAuxiliarySlot(AuxiliarySlots.GENERATOR_SLOT), generator);
        frame.setAuxiliarySlot(
            frameDescriptor.findOrAddAuxiliarySlot(AuxiliarySlots.OFF_HEAP_GROUP_BY_KEYS_SLOT),
            groupByKeys);
        frame.setAuxiliarySlot(
            frameDescriptor.findOrAddAuxiliarySlot(AuxiliarySlots.COLLECTION_SLOT), collection);

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
