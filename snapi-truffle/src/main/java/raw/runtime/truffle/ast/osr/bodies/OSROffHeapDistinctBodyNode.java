package raw.runtime.truffle.ast.osr.bodies;

import com.esotericsoftware.kryo.io.Output;
import com.oracle.truffle.api.frame.VirtualFrame;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.data_structures.treemap.TreeMapIterator;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.distinct.OffHeapDistinct;
import raw.runtime.truffle.runtime.kryo.KryoNodes;
import raw.runtime.truffle.runtime.kryo.KryoNodesFactory;

public class OSROffHeapDistinctBodyNode extends ExpressionNode {
  @Child KryoNodes.KryoWriteNode writer = KryoNodesFactory.KryoWriteNodeGen.create();

  private final int kryoOutputSlot;

  private final int iteratorSlot;

  private final int offHeapFlushSlot;

  public OSROffHeapDistinctBodyNode(
      int kryoOutputSlot, int iteratorSlot, int offHeapGroupByKeySlot) {
    this.kryoOutputSlot = kryoOutputSlot;
    this.iteratorSlot = iteratorSlot;
    this.offHeapFlushSlot = offHeapGroupByKeySlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {

    TreeMapIterator iterator = (TreeMapIterator) frame.getAuxiliarySlot(iteratorSlot);
    Output kryoOutput = (Output) frame.getAuxiliarySlot(kryoOutputSlot);
    OffHeapDistinct offHeapDistinct = (OffHeapDistinct) frame.getAuxiliarySlot(offHeapFlushSlot);

    Object key = iterator.nextKey();
    writer.execute(this, kryoOutput, offHeapDistinct.getItemType(), key);

    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
