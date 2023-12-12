package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.group_by_key;

import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.unsafe.UnsafeOutput;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.kryo.KryoNodes;
import raw.runtime.truffle.utils.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class OffHeapGroupByKeyNodes {
  @NodeInfo(shortName = "OffHeapGroupBy.Put")
  @GenerateUncached
  public abstract static class OffHeapGroupByPutNode extends Node {

    public abstract void execute(Object offHeapGroupBy, Object key, Object value);

    @Specialization
    static void put(
        OffHeapGroupByKey offHeapGroupByKey,
        Object key,
        Object value,
        @Cached OffHeapGroupByFlushNode flushNode) {
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
        flushNode.execute(offHeapGroupByKey);
      }
    }

    @Specialization
    static void put(
        OffHeapGroupByKeys offHeapGroupByKeys,
        Object[] keys,
        Object value,
        @Cached OffHeapGroupByFlushNode flushNode) {
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
        flushNode.execute(offHeapGroupByKeys);
      }
    }
  }

  @NodeInfo(shortName = "OffHeapGroupBy.Flush")
  @GenerateUncached
  public abstract static class OffHeapGroupByFlushNode extends Node {

    public abstract void execute(Object offHeapGroupBy);

    @Specialization
    static void flush(
        OffHeapGroupByKey offHeapGroupByKey,
        @Cached OffHeapGroupByKeyNewDiskBufferNode newDiskBufferNode,
        @Cached KryoNodes.KryoWriteNode writer) {
      Output kryoOutput =
          new UnsafeOutput(
              newDiskBufferNode.execute(offHeapGroupByKey),
              offHeapGroupByKey.getKryoOutputBufferSize());
      for (Object key : offHeapGroupByKey.getMemMap().keySet()) {
        ArrayList<Object> values = offHeapGroupByKey.getMemMap().get(key);
        // write key, then n, then values.
        writer.execute(kryoOutput, offHeapGroupByKey.getKeyType(), key);
        kryoOutput.writeInt(values.size());
        for (Object value : values) {
          writer.execute(kryoOutput, offHeapGroupByKey.getRowType(), value);
        }
      }
      kryoOutput.close();
      // reset both the memory map and memory footprint.
      offHeapGroupByKey.getMemMap().clear();
      offHeapGroupByKey.setSize(0);
    }

    @Specialization
    static void flush(
        OffHeapGroupByKeys offHeapGroupByKeys,
        @Cached OffHeapGroupByKeysNextFileNode nextFileNode,
        @Cached KryoNodes.KryoWriteNode writer) {
      Output kryoOutput =
          new UnsafeOutput(
              nextFileNode.execute(offHeapGroupByKeys),
              offHeapGroupByKeys.getKryoOutputBufferSize());
      for (Object[] keys : offHeapGroupByKeys.getMemMap().keySet()) {
        // write keys, then n, then values.
        for (int i = 0; i < keys.length; i++) {
          writer.execute(kryoOutput, offHeapGroupByKeys.getKeyTypes()[i], keys[i]);
        }
        ArrayList<Object> values = offHeapGroupByKeys.getMemMap().get(keys);
        kryoOutput.writeInt(values.size());
        for (Object value : values) {
          writer.execute(kryoOutput, offHeapGroupByKeys.getRowType(), value);
        }
      }
      kryoOutput.close();
      // reset the memory map and footprint
      offHeapGroupByKeys.getMemMap().clear();
      offHeapGroupByKeys.setSize(0);
    }
  }

  @NodeInfo(shortName = "OffHeapGroupBy.Generator")
  @GenerateUncached
  public abstract static class OffHeapGroupByGeneratorNode extends Node {

    public abstract Object execute(Object offHeapGroupByKey);

    @Specialization(guards = "offHeapGroupByKey.getSpilledBuffers().isEmpty()")
    static GroupByMemoryGenerator generator(OffHeapGroupByKey offHeapGroupByKey) {
      return new GroupByMemoryGenerator(offHeapGroupByKey);
    }

    @Specialization(guards = "!offHeapGroupByKey.getSpilledBuffers().isEmpty()")
    static GroupBySpilledFilesGenerator generator(
        OffHeapGroupByKey offHeapGroupByKey, @Cached OffHeapGroupByFlushNode flushNode) {
      flushNode.execute(offHeapGroupByKey);
      return new GroupBySpilledFilesGenerator(offHeapGroupByKey);
    }

    @Specialization(guards = "offHeapGroupByKeys.getSpilledBuffers().isEmpty()")
    static OrderByMemoryGenerator generator(OffHeapGroupByKeys offHeapGroupByKeys) {
      return new OrderByMemoryGenerator(offHeapGroupByKeys);
    }

    @Specialization(guards = "!offHeapGroupByKeys.getSpilledBuffers().isEmpty()")
    static OrderBySpilledFilesGenerator generator(
        OffHeapGroupByKeys offHeapGroupByKeys, @Cached OffHeapGroupByFlushNode flushNode) {
      flushNode.execute(offHeapGroupByKeys);
      return new OrderBySpilledFilesGenerator(offHeapGroupByKeys);
    }
  }

  @NodeInfo(shortName = "OffHeapGroupBy.NewDiskBuffer")
  @GenerateUncached
  public abstract static class OffHeapGroupByKeyNewDiskBufferNode extends Node {

    public abstract FileOutputStream execute(Object offHeapGroupByKey);

    @Specialization
    static FileOutputStream newDiskBuffer(OffHeapGroupByKey offHeapGroupByKey) {
      File file;
      file = IOUtils.getScratchFile("groupby.", ".kryo", offHeapGroupByKey.getContext()).toFile();
      offHeapGroupByKey.getSpilledBuffers().add(file);
      try {
        return new FileOutputStream(file);
      } catch (FileNotFoundException e) {
        throw new RawTruffleRuntimeException(e.getMessage());
      }
    }
  }

  @NodeInfo(shortName = "OffHeapGroupByKeys.nextFile")
  @GenerateUncached
  public abstract static class OffHeapGroupByKeysNextFileNode extends Node {

    public abstract FileOutputStream execute(Object offHeapGroupByKeys);

    @Specialization
    static FileOutputStream nextFile(OffHeapGroupByKeys offHeapGroupByKeys) {
      File file;
      file = IOUtils.getScratchFile("orderby.", ".kryo", offHeapGroupByKeys.getContext()).toFile();
      offHeapGroupByKeys.getSpilledBuffers().add(file);
      try {
        return new FileOutputStream(file);
      } catch (FileNotFoundException e) {
        throw new RawTruffleRuntimeException(e.getMessage());
      }
    }
  }
}
