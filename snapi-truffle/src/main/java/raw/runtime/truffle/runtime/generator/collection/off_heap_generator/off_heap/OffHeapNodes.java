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

package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap;

import static raw.runtime.truffle.runtime.generator.collection.off_heap_generator.StaticOffHeap.*;

import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.unsafe.UnsafeOutput;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.util.ArrayList;
import raw.runtime.truffle.runtime.data_structures.treemap.TreeMapIterator;
import raw.runtime.truffle.runtime.data_structures.treemap.TreeMapNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.StaticOffHeap;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.distinct.DistinctMemoryGenerator;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.distinct.DistinctSpilledFilesGenerator;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.distinct.OffHeapDistinct;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.GroupByMemoryGenerator;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.GroupBySpilledFilesGenerator;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.OffHeapGroupByKey;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by.OffHeapGroupByKeys;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by.OrderByMemoryGenerator;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by.OrderBySpilledFilesGenerator;

public class OffHeapNodes {
  @NodeInfo(shortName = "OffHeap.Put")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(StaticOffHeap.class)
  public abstract static class OffHeapGroupByPutNode extends Node {

    public abstract void execute(Node node, Object offHeapGroupBy, Object key, Object value);

    @Specialization
    static void put(
        Node node,
        OffHeapGroupByKey offHeapGroupByKey,
        Object key,
        Object value,
        @Bind("$node") Node thisNode,
        @Cached(
                value = "getOffHeapGroupByKeyLoopNode(offHeapGroupByKey)",
                allowUncached = true,
                inline = false)
            LoopNode loopNode,
        @Cached TreeMapNodes.TreeMapGetOrCreate putIfNotExistNode) {
      @SuppressWarnings("unchecked")
      ArrayList<Object> list =
          (ArrayList<Object>)
              putIfNotExistNode.execute(thisNode, offHeapGroupByKey.getMemMap(), key);
      // add the size of the key to the memory footprint. (Row size added below in main path.)
      if (list.isEmpty()) {
        offHeapGroupByKey.setSize(offHeapGroupByKey.getSize() + offHeapGroupByKey.getKeySize());
      }

      list.add(value);
      // add the size of the row to the memory footprint.
      offHeapGroupByKey.setSize(offHeapGroupByKey.getSize() + offHeapGroupByKey.getRowSize());

      if (offHeapGroupByKey.getSize() >= offHeapGroupByKey.getMaxSize()) {
        // flush
        Output kryoOutput =
            new UnsafeOutput(
                getGroupByKeyNewDiskBuffer(offHeapGroupByKey, thisNode),
                offHeapGroupByKey.getKryoOutputBufferSize());
        TreeMapIterator iterator = offHeapGroupByKey.getMemMap().iterator();

        MaterializedFrame frame = offHeapGroupByKey.getFrame();
        frame.setAuxiliarySlot(offHeapGroupByKey.getKryoOutputSlot(), kryoOutput);
        frame.setAuxiliarySlot(offHeapGroupByKey.getIteratorSlot(), iterator);
        frame.setAuxiliarySlot(offHeapGroupByKey.getOffHeapGroupByKeySlot(), offHeapGroupByKey);
        loopNode.execute(frame);

        kryoOutputClose(kryoOutput);
        // reset both the memory map and memory footprint.

        offHeapGroupByKey.getMemMap().clear();
        offHeapGroupByKey.setSize(0);
      }
    }

    @Specialization
    static void put(
        Node node,
        OffHeapGroupByKeys offHeapGroupByKeys,
        Object[] keys,
        Object value,
        @Bind("$node") Node thisNode,
        @Cached(
                value = "getOffHeapGroupByKeysLoopNode(offHeapGroupByKeys)",
                allowUncached = true,
                inline = false)
            LoopNode loopNode,
        @Cached TreeMapNodes.TreeMapGetOrCreateArrayKeysNode getOrCreateArrayKeysNode) {
      @SuppressWarnings("unchecked")
      ArrayList<Object> list =
          (ArrayList<Object>)
              getOrCreateArrayKeysNode.execute(
                  thisNode,
                  offHeapGroupByKeys.getMemMap(),
                  keys,
                  offHeapGroupByKeys.getKeyOrderings());

      if (list.isEmpty()) {
        offHeapGroupByKeys.setSize(offHeapGroupByKeys.getSize() + offHeapGroupByKeys.getKeysSize());
      }

      list.add(value);
      // add the size of the row to the memory footprint.
      offHeapGroupByKeys.setSize(offHeapGroupByKeys.getSize() + offHeapGroupByKeys.getRowSize());
      if (offHeapGroupByKeys.getSize() >= offHeapGroupByKeys.getMaxSize()) {
        // flush
        Output kryoOutput =
            new UnsafeOutput(
                groupByKeysNextFile(offHeapGroupByKeys, thisNode),
                offHeapGroupByKeys.getKryoOutputBufferSize());
        TreeMapIterator iterator = offHeapGroupByKeys.getMemMap().iterator();

        MaterializedFrame frame = offHeapGroupByKeys.getFrame();
        frame.setAuxiliarySlot(offHeapGroupByKeys.getKryoOutputSlot(), kryoOutput);
        frame.setAuxiliarySlot(offHeapGroupByKeys.getIteratorSlot(), iterator);
        frame.setAuxiliarySlot(offHeapGroupByKeys.getOffHeapGroupByKeysSlot(), offHeapGroupByKeys);
        loopNode.execute(frame);

        kryoOutputClose(kryoOutput);
        // reset the memory map and footprint
        offHeapGroupByKeys.getMemMap().clear();
        offHeapGroupByKeys.setSize(0);
      }
    }

    @Specialization
    static void put(
        Node node,
        OffHeapDistinct offHeapDistinct,
        Object item,
        Object value,
        @Bind("$node") Node thisNode,
        @Cached(
                value = "getOffHeapDistinctLoopNode(offHeapDistinct)",
                allowUncached = true,
                inline = false)
            LoopNode loopNode,
        @Cached @Cached.Exclusive TreeMapNodes.TreeMapGetOrCreateDistinct addDistinct) {
      // append the value to the list of values for the key.
      boolean added = addDistinct.execute(thisNode, offHeapDistinct.getIndex(), item);
      if (added) {
        offHeapDistinct.setBinarySize(
            offHeapDistinct.getBinarySize() + offHeapDistinct.getItemSize());
        if (offHeapDistinct.getBinarySize() >= offHeapDistinct.getBlockSize()) {
          // flush
          Output kryoOutput =
              new UnsafeOutput(
                  distinctNextFile(offHeapDistinct, node),
                  offHeapDistinct.getKryoInputBufferSize());
          TreeMapIterator iterator = offHeapDistinct.getIndex().iterator();

          MaterializedFrame frame = offHeapDistinct.getFrame();
          frame.setAuxiliarySlot(offHeapDistinct.getKryoOutputSlot(), kryoOutput);
          frame.setAuxiliarySlot(offHeapDistinct.getIteratorSlot(), iterator);
          frame.setAuxiliarySlot(offHeapDistinct.getOffHeapDistinctSlot(), offHeapDistinct);
          loopNode.execute(frame);

          kryoOutputClose(kryoOutput);
          // reset the memory map and footprint
          offHeapDistinct.getIndex().clear();
          offHeapDistinct.setBinarySize(0);
        }
      }
    }
  }

  @NodeInfo(shortName = "OffHeap.Generator")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(StaticOffHeap.class)
  public abstract static class OffHeapGeneratorNode extends Node {

    public abstract Object execute(Node node, Object offHeap);

    @Specialization(guards = "offHeapGroupByKey.getSpilledBuffers().isEmpty()")
    static GroupByMemoryGenerator generator(Node node, OffHeapGroupByKey offHeapGroupByKey) {
      return new GroupByMemoryGenerator(offHeapGroupByKey);
    }

    @Specialization(guards = "!offHeapGroupByKey.getSpilledBuffers().isEmpty()")
    static GroupBySpilledFilesGenerator generator(
        Node node,
        OffHeapGroupByKey offHeapGroupByKey,
        @Bind("$node") Node thisNode,
        @Cached(
                value = "getOffHeapGroupByKeyLoopNode(offHeapGroupByKey)",
                allowUncached = true,
                inline = false,
                neverDefault = true)
            LoopNode loopNode) {
      // flush
      Output kryoOutput =
          new UnsafeOutput(
              getGroupByKeyNewDiskBuffer(offHeapGroupByKey, thisNode),
              offHeapGroupByKey.getKryoOutputBufferSize());
      TreeMapIterator iterator = offHeapGroupByKey.getMemMap().iterator();

      MaterializedFrame frame = offHeapGroupByKey.getFrame();
      frame.setAuxiliarySlot(offHeapGroupByKey.getKryoOutputSlot(), kryoOutput);
      frame.setAuxiliarySlot(offHeapGroupByKey.getIteratorSlot(), iterator);
      frame.setAuxiliarySlot(offHeapGroupByKey.getOffHeapGroupByKeySlot(), offHeapGroupByKey);
      loopNode.execute(frame);

      kryoOutputClose(kryoOutput);
      // reset both the memory map and memory footprint.

      offHeapGroupByKey.getMemMap().clear();
      offHeapGroupByKey.setSize(0);
      return new GroupBySpilledFilesGenerator(offHeapGroupByKey);
    }

    @Specialization(guards = "offHeapGroupByKeys.getSpilledBuffers().isEmpty()")
    static OrderByMemoryGenerator generator(Node node, OffHeapGroupByKeys offHeapGroupByKeys) {
      return new OrderByMemoryGenerator(offHeapGroupByKeys);
    }

    @Specialization(guards = "!offHeapGroupByKeys.getSpilledBuffers().isEmpty()")
    static OrderBySpilledFilesGenerator generator(
        Node node,
        OffHeapGroupByKeys offHeapGroupByKeys,
        @Bind("$node") Node thisNode,
        @Cached(
                value = "getOffHeapGroupByKeysLoopNode(offHeapGroupByKeys)",
                allowUncached = true,
                inline = false,
                neverDefault = true)
            LoopNode loopNode) {

      Output kryoOutput =
          new UnsafeOutput(
              groupByKeysNextFile(offHeapGroupByKeys, thisNode),
              offHeapGroupByKeys.getKryoOutputBufferSize());
      TreeMapIterator iterator = offHeapGroupByKeys.getMemMap().iterator();

      MaterializedFrame frame = offHeapGroupByKeys.getFrame();
      frame.setAuxiliarySlot(offHeapGroupByKeys.getKryoOutputSlot(), kryoOutput);
      frame.setAuxiliarySlot(offHeapGroupByKeys.getIteratorSlot(), iterator);
      frame.setAuxiliarySlot(offHeapGroupByKeys.getOffHeapGroupByKeysSlot(), offHeapGroupByKeys);
      loopNode.execute(frame);

      kryoOutputClose(kryoOutput);
      // reset the memory map and footprint
      offHeapGroupByKeys.getMemMap().clear();
      offHeapGroupByKeys.setSize(0);

      return new OrderBySpilledFilesGenerator(offHeapGroupByKeys);
    }

    @Specialization(guards = "offHeapDistinct.getSpilledBuffers().isEmpty()")
    static DistinctMemoryGenerator generator(Node node, OffHeapDistinct offHeapDistinct) {
      return new DistinctMemoryGenerator(offHeapDistinct);
    }

    @Specialization(guards = "!offHeapDistinct.getSpilledBuffers().isEmpty()")
    static DistinctSpilledFilesGenerator generator(
        Node node,
        OffHeapDistinct offHeapDistinct,
        @Cached(
                value = "getOffHeapDistinctLoopNode(offHeapDistinct)",
                allowUncached = true,
                inline = false,
                neverDefault = true)
            LoopNode loopNode) {
      Output kryoOutput =
          new UnsafeOutput(
              distinctNextFile(offHeapDistinct, node), offHeapDistinct.getKryoInputBufferSize());
      TreeMapIterator iterator = offHeapDistinct.getIndex().iterator();

      MaterializedFrame frame = offHeapDistinct.getFrame();
      frame.setAuxiliarySlot(offHeapDistinct.getKryoOutputSlot(), kryoOutput);
      frame.setAuxiliarySlot(offHeapDistinct.getIteratorSlot(), iterator);
      frame.setAuxiliarySlot(offHeapDistinct.getOffHeapDistinctSlot(), offHeapDistinct);
      loopNode.execute(frame);

      kryoOutputClose(kryoOutput);
      // reset the memory map and footprint
      offHeapDistinct.getIndex().clear();
      offHeapDistinct.setBinarySize(0);
      return new DistinctSpilledFilesGenerator(offHeapDistinct);
    }
  }
}
