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

import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.unsafe.UnsafeOutput;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import raw.runtime.truffle.runtime.data_structures.treemap.TreeMapIterator;
import raw.runtime.truffle.runtime.data_structures.treemap.TreeMapNode;
import raw.runtime.truffle.runtime.data_structures.treemap.TreeMapNodes;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.distinct.DistinctMemoryGenerator;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.distinct.DistinctSpilledFilesGenerator;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.distinct.OffHeapDistinct;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.GroupByMemoryGenerator;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.GroupBySpilledFilesGenerator;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.OffHeapGroupByKey;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by.OffHeapGroupByKeys;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by.OrderByMemoryGenerator;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by.OrderBySpilledFilesGenerator;
import raw.runtime.truffle.runtime.kryo.KryoNodes;
import raw.runtime.truffle.utils.IOUtils;

public class OffHeapNodes {
  @NodeInfo(shortName = "OffHeap.Put")
  @GenerateUncached
  @GenerateInline
  public abstract static class OffHeapGroupByPutNode extends Node {

    public abstract void execute(Node node, Object offHeapGroupBy, Object key, Object value);

    @Specialization
    static void put(
        Node node,
        OffHeapGroupByKey offHeapGroupByKey,
        Object key,
        Object value,
        @Bind("$node") Node thisNode,
        @Cached TreeMapNodes.TreeMapGetOrCreate putIfNotExistNode,
        @Cached @Cached.Exclusive OffHeapFlushNode flushNode) {
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
        flushNode.execute(thisNode, offHeapGroupByKey);
      }
    }

    @Specialization
    static void put(
        Node node,
        OffHeapGroupByKeys offHeapGroupByKeys,
        Object[] keys,
        Object value,
        @Bind("$node") Node thisNode,
        @Cached TreeMapNodes.TreeMapGetOrCreateArrayKeysNode getOrCreateArrayKeysNode,
        @Cached @Cached.Exclusive OffHeapFlushNode flushNode) {
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
        flushNode.execute(thisNode, offHeapGroupByKeys);
      }
    }

    @Specialization
    static void put(
        Node node,
        OffHeapDistinct offHeapDistinct,
        Object item,
        Object value,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Exclusive OffHeapFlushNode flushNode) {
      // append the value to the list of values for the key.
      boolean added = offHeapDistinct.getIndex().add(item);
      if (added) {
        offHeapDistinct.setBinarySize(
            offHeapDistinct.getBinarySize() + offHeapDistinct.getItemSize());
        if (offHeapDistinct.getBinarySize() >= offHeapDistinct.getBlockSize()) {
          flushNode.execute(thisNode, offHeapDistinct);
        }
      }
    }
  }

  @NodeInfo(shortName = "OffHeap.Flush")
  @GenerateUncached
  @GenerateInline
  public abstract static class OffHeapFlushNode extends Node {

    public abstract void execute(Node node, Object offHeap);

    @CompilerDirectives.TruffleBoundary
    public static void kryoOutputClose(Output kryoOutput) {
      kryoOutput.close();
    }

    @CompilerDirectives.TruffleBoundary
    public static void kryoWriteInt(Output kryoOutput, int size) {
      kryoOutput.writeInt(size);
    }

    @Specialization
    static void flush(
        Node node,
        OffHeapGroupByKey offHeapGroupByKey,
        @Cached OffHeapNewDiskBufferNode newDiskBufferNode,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Exclusive KryoNodes.KryoWriteNode writer1,
        @Cached @Cached.Exclusive KryoNodes.KryoWriteNode writer2) {
      Output kryoOutput =
          new UnsafeOutput(
              newDiskBufferNode.execute(thisNode, offHeapGroupByKey),
              offHeapGroupByKey.getKryoOutputBufferSize());
      TreeMapIterator iterator = offHeapGroupByKey.getMemMap().iterator();
      while (iterator.hasNext()) {
        TreeMapNode treeNode = iterator.nextNode();
        @SuppressWarnings("unchecked")
        ArrayList<Object> values = (ArrayList<Object>) treeNode.getValue();
        // write key, then n, then values.
        writer1.execute(thisNode, kryoOutput, offHeapGroupByKey.getKeyType(), treeNode.getKey());
        kryoWriteInt(kryoOutput, values.size());
        for (Object value : values) {
          writer2.execute(thisNode, kryoOutput, offHeapGroupByKey.getRowType(), value);
        }
      }
      kryoOutputClose(kryoOutput);
      // reset both the memory map and memory footprint.

      offHeapGroupByKey.getMemMap().clear();
      offHeapGroupByKey.setSize(0);
    }

    @Specialization
    static void flush(
        Node node,
        OffHeapGroupByKeys offHeapGroupByKeys,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Exclusive OffHeapNextFileNode nextFileNode,
        @Cached @Cached.Exclusive KryoNodes.KryoWriteNode writer1,
        @Cached @Cached.Exclusive KryoNodes.KryoWriteNode writer2) {
      Output kryoOutput =
          new UnsafeOutput(
              nextFileNode.execute(thisNode, offHeapGroupByKeys),
              offHeapGroupByKeys.getKryoOutputBufferSize());
      TreeMapIterator iterator = offHeapGroupByKeys.getMemMap().iterator();
      while (iterator.hasNext()) {
        TreeMapNode treeNode = iterator.nextNode();
        // write keys, then n, then values.
        for (int i = 0; i < offHeapGroupByKeys.getKeyTypes().length; i++) {
          Object[] keys = (Object[]) treeNode.getKey();
          writer1.execute(thisNode, kryoOutput, offHeapGroupByKeys.getKeyTypes()[i], keys[i]);
        }
        @SuppressWarnings("unchecked")
        ArrayList<Object> values = (ArrayList<Object>) treeNode.getValue();
        kryoWriteInt(kryoOutput, values.size());
        for (Object value : values) {
          writer2.execute(thisNode, kryoOutput, offHeapGroupByKeys.getRowType(), value);
        }
      }
      kryoOutputClose(kryoOutput);
      // reset the memory map and footprint
      offHeapGroupByKeys.getMemMap().clear();
      offHeapGroupByKeys.setSize(0);
    }

    @Specialization
    static void flush(
        Node node,
        OffHeapDistinct offHeapDistinct,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Exclusive OffHeapNextFileNode nextFileNode,
        @Cached @Cached.Exclusive KryoNodes.KryoWriteNode writer) {
      Output kryoOutput =
          new UnsafeOutput(
              nextFileNode.execute(thisNode, offHeapDistinct),
              offHeapDistinct.getKryoInputBufferSize());
      for (Object item : offHeapDistinct.getIndex()) {
        writer.execute(thisNode, kryoOutput, offHeapDistinct.getItemType(), item);
      }
      kryoOutputClose(kryoOutput);
      // reset the memory map and footprint
      offHeapDistinct.getIndex().clear();
      offHeapDistinct.setBinarySize(0);
    }
  }

  @NodeInfo(shortName = "OffHeap.Generator")
  @GenerateUncached
  @GenerateInline
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
        @Cached @Cached.Exclusive OffHeapFlushNode flushNode) {
      flushNode.execute(thisNode, offHeapGroupByKey);
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
        @Cached @Cached.Exclusive OffHeapFlushNode flushNode) {
      flushNode.execute(thisNode, offHeapGroupByKeys);
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
        @Bind("$node") Node thisNode,
        @Cached @Cached.Exclusive OffHeapFlushNode flushNode) {
      flushNode.execute(thisNode, offHeapDistinct);
      return new DistinctSpilledFilesGenerator(offHeapDistinct);
    }
  }

  @NodeInfo(shortName = "OffHeap.NewDiskBuffer")
  @GenerateUncached
  @GenerateInline
  public abstract static class OffHeapNewDiskBufferNode extends Node {

    public abstract FileOutputStream execute(Node node, Object offHeapGroupByKey);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    static FileOutputStream newDiskBuffer(
        Node node, OffHeapGroupByKey offHeapGroupByKey, @Bind("$node") Node thisNode) {
      File file;
      file = IOUtils.getScratchFile("groupby.", ".kryo", offHeapGroupByKey.getContext()).toFile();
      offHeapGroupByKey.getSpilledBuffers().add(file);
      try {
        return new FileOutputStream(file);
      } catch (FileNotFoundException e) {
        throw new RawTruffleRuntimeException(e, thisNode);
      }
    }
  }

  @NodeInfo(shortName = "OffHeap.nextFile")
  @GenerateUncached
  @GenerateInline
  public abstract static class OffHeapNextFileNode extends Node {

    public abstract FileOutputStream execute(Node node, Object offHeapGroupByKeys);

    @Specialization
    static FileOutputStream nextFile(
        Node node, OffHeapGroupByKeys offHeapGroupByKeys, @Bind("$node") Node thisNode) {
      File file;
      file = IOUtils.getScratchFile("orderby.", ".kryo", offHeapGroupByKeys.getContext()).toFile();
      offHeapGroupByKeys.getSpilledBuffers().add(file);
      try {
        return new FileOutputStream(file);
      } catch (FileNotFoundException e) {
        throw new RawTruffleRuntimeException(e, thisNode);
      }
    }

    @Specialization
    static FileOutputStream nextFile(
        Node node, OffHeapDistinct offHeapDistinct, @Bind("$node") Node thisNode) {
      File file;
      file = IOUtils.getScratchFile("distinct.", ".kryo", offHeapDistinct.getContext()).toFile();
      offHeapDistinct.getSpilledBuffers().add(file);
      try {
        return new FileOutputStream(file);
      } catch (FileNotFoundException e) {
        throw new RawTruffleRuntimeException(e, thisNode);
      }
    }
  }
}
