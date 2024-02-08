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
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
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
        @Cached @Cached.Shared("flushNode") OffHeapFlushNode flushNode) {
      ArrayList<Object> list = offHeapGroupByKey.getMemMap().get(key);
      if (list == null) {
        list = new ArrayList<>();
        offHeapGroupByKey.getMemMap().put(key, list);
        // add the size of the key to the memory footprint. (Row size added below in main path.)
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
        @Cached @Cached.Shared("flushNode") OffHeapFlushNode flushNode) {
      ArrayList<Object> list = offHeapGroupByKeys.getMemMap().get(keys);
      if (list == null) {
        list = new ArrayList<>();
        offHeapGroupByKeys.getMemMap().put(keys, list);
        // add the size of the key to the memory footprint. (Row size added below in main path.)
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
        @Cached @Cached.Shared("flushNode") OffHeapFlushNode flushNode) {
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

    @Specialization
    static void flush(
        Node node,
        OffHeapGroupByKey offHeapGroupByKey,
        @Cached OffHeapNewDiskBufferNode newDiskBufferNode,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("kryoWriter") KryoNodes.KryoWriteNode writer) {
      Output kryoOutput =
          new UnsafeOutput(
              newDiskBufferNode.execute(thisNode, offHeapGroupByKey),
              offHeapGroupByKey.getKryoOutputBufferSize());
      for (Object key : offHeapGroupByKey.getMemMap().keySet()) {
        ArrayList<Object> values = offHeapGroupByKey.getMemMap().get(key);
        // write key, then n, then values.
        writer.execute(thisNode, kryoOutput, offHeapGroupByKey.getKeyType(), key);
        kryoOutput.writeInt(values.size());
        for (Object value : values) {
          writer.execute(thisNode, kryoOutput, offHeapGroupByKey.getRowType(), value);
        }
      }
      kryoOutput.close();
      // reset both the memory map and memory footprint.
      offHeapGroupByKey.getMemMap().clear();
      offHeapGroupByKey.setSize(0);
    }

    @Specialization
    static void flush(
        Node node,
        OffHeapGroupByKeys offHeapGroupByKeys,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("nextFile") OffHeapNextFileNode nextFileNode,
        @Cached @Cached.Shared("kryoWriter") KryoNodes.KryoWriteNode writer) {
      Output kryoOutput =
          new UnsafeOutput(
              nextFileNode.execute(thisNode, offHeapGroupByKeys),
              offHeapGroupByKeys.getKryoOutputBufferSize());
      for (Object[] keys : offHeapGroupByKeys.getMemMap().keySet()) {
        // write keys, then n, then values.
        for (int i = 0; i < keys.length; i++) {
          writer.execute(thisNode, kryoOutput, offHeapGroupByKeys.getKeyTypes()[i], keys[i]);
        }
        ArrayList<Object> values = offHeapGroupByKeys.getMemMap().get(keys);
        kryoOutput.writeInt(values.size());
        for (Object value : values) {
          writer.execute(thisNode, kryoOutput, offHeapGroupByKeys.getRowType(), value);
        }
      }
      kryoOutput.close();
      // reset the memory map and footprint
      offHeapGroupByKeys.getMemMap().clear();
      offHeapGroupByKeys.setSize(0);
    }

    @Specialization
    static void flush(
        Node node,
        OffHeapDistinct offHeapDistinct,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("nextFile") OffHeapNextFileNode nextFileNode,
        @Cached @Cached.Shared("kryoWriter") KryoNodes.KryoWriteNode writer) {
      Output kryoOutput =
          new UnsafeOutput(
              nextFileNode.execute(thisNode, offHeapDistinct),
              offHeapDistinct.getKryoInputBufferSize());
      for (Object item : offHeapDistinct.getIndex()) {
        writer.execute(thisNode, kryoOutput, offHeapDistinct.getItemType(), item);
      }
      kryoOutput.close();
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
        @Cached @Cached.Shared("flush") OffHeapFlushNode flushNode) {
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
        @Cached @Cached.Shared("flush") OffHeapFlushNode flushNode) {
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
        @Cached @Cached.Shared("flush") OffHeapFlushNode flushNode) {
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
