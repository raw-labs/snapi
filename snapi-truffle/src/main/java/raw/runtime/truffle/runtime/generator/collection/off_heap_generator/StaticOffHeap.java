package raw.runtime.truffle.runtime.generator.collection.off_heap_generator;

import com.esotericsoftware.kryo.io.Output;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.Node;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import raw.runtime.truffle.ast.osr.OSRGeneratorNode;
import raw.runtime.truffle.ast.osr.bodies.OSROffHeapDistinctBodyNode;
import raw.runtime.truffle.ast.osr.bodies.OSROffHeapGroupByKeyBodyNode;
import raw.runtime.truffle.ast.osr.bodies.OSROffHeapGroupByKeysBodyNode;
import raw.runtime.truffle.ast.osr.conditions.OSRTreeMapHasNextConditionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.distinct.OffHeapDistinct;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.OffHeapGroupByKey;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by.OffHeapGroupByKeys;
import raw.runtime.truffle.utils.IOUtils;

public class StaticOffHeap {

  @CompilerDirectives.TruffleBoundary
  public static void kryoWriteInt(Output kryoOutput, int size) {
    kryoOutput.writeInt(size);
  }

  @CompilerDirectives.TruffleBoundary
  public static FileOutputStream getGroupByKeyNewDiskBuffer(
      OffHeapGroupByKey offHeapGroupByKey, Node node) {
    File file;
    file = IOUtils.getScratchFile("groupby.", ".kryo", offHeapGroupByKey.getContext()).toFile();
    offHeapGroupByKey.getSpilledBuffers().add(file);
    try {
      return new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      throw new RawTruffleRuntimeException(e, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  public static FileOutputStream groupByKeysNextFile(
      OffHeapGroupByKeys offHeapGroupByKeys, Node node) {
    File file;
    file = IOUtils.getScratchFile("orderby.", ".kryo", offHeapGroupByKeys.getContext()).toFile();
    offHeapGroupByKeys.getSpilledBuffers().add(file);
    try {
      return new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      throw new RawTruffleRuntimeException(e, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  public static FileOutputStream distinctNextFile(OffHeapDistinct offHeapDistinct, Node node) {
    File file;
    file = IOUtils.getScratchFile("distinct.", ".kryo", offHeapDistinct.getContext()).toFile();
    offHeapDistinct.getSpilledBuffers().add(file);
    try {
      return new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      throw new RawTruffleRuntimeException(e, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  public static void kryoOutputClose(Output kryoOutput) {
    kryoOutput.close();
  }

  public static LoopNode getOffHeapGroupByKeyLoopNode(OffHeapGroupByKey offHeapGroupByKey) {
    return Truffle.getRuntime()
        .createLoopNode(
            new OSRGeneratorNode(
                new OSRTreeMapHasNextConditionNode(offHeapGroupByKey.getIteratorSlot()),
                new OSROffHeapGroupByKeyBodyNode(
                    offHeapGroupByKey.getKryoOutputSlot(),
                    offHeapGroupByKey.getIteratorSlot(),
                    offHeapGroupByKey.getOffHeapGroupByKeySlot())));
  }

  public static LoopNode getOffHeapGroupByKeysLoopNode(OffHeapGroupByKeys offHeapGroupByKeys) {
    return Truffle.getRuntime()
        .createLoopNode(
            new OSRGeneratorNode(
                new OSRTreeMapHasNextConditionNode(offHeapGroupByKeys.getIteratorSlot()),
                new OSROffHeapGroupByKeysBodyNode(
                    offHeapGroupByKeys.getKryoOutputSlot(),
                    offHeapGroupByKeys.getIteratorSlot(),
                    offHeapGroupByKeys.getOffHeapGroupByKeysSlot())));
  }

  public static LoopNode getOffHeapDistinctLoopNode(OffHeapDistinct offHeapDistinct) {
    return Truffle.getRuntime()
        .createLoopNode(
            new OSRGeneratorNode(
                new OSRTreeMapHasNextConditionNode(offHeapDistinct.getIteratorSlot()),
                new OSROffHeapDistinctBodyNode(
                    offHeapDistinct.getKryoOutputSlot(),
                    offHeapDistinct.getIteratorSlot(),
                    offHeapDistinct.getOffHeapDistinctSlot())));
  }
}
