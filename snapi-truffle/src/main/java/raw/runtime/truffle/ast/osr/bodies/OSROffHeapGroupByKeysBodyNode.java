package raw.runtime.truffle.ast.osr.bodies;

import static raw.runtime.truffle.runtime.generator.collection.off_heap_generator.StaticOffHeap.kryoWriteInt;

import com.esotericsoftware.kryo.io.Output;
import com.oracle.truffle.api.frame.VirtualFrame;
import java.util.ArrayList;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.data_structures.treemap.TreeMapIterator;
import raw.runtime.truffle.runtime.data_structures.treemap.TreeMapNode;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by.OffHeapGroupByKeys;
import raw.runtime.truffle.runtime.kryo.KryoNodes;
import raw.runtime.truffle.runtime.kryo.KryoNodesFactory;

public class OSROffHeapGroupByKeysBodyNode extends ExpressionNode {

  @Child KryoNodes.KryoWriteNode writer1 = KryoNodesFactory.KryoWriteNodeGen.create();
  @Child KryoNodes.KryoWriteNode writer2 = KryoNodesFactory.KryoWriteNodeGen.create();

  private final int kryoOutputSlot;

  private final int iteratorSlot;

  private final int offHeapFlushSlot;

  public OSROffHeapGroupByKeysBodyNode(int kryoOutputSlot, int iteratorSlot, int offHeapFlushSlot) {
    this.kryoOutputSlot = kryoOutputSlot;
    this.iteratorSlot = iteratorSlot;
    this.offHeapFlushSlot = offHeapFlushSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {

    TreeMapIterator iterator = (TreeMapIterator) frame.getAuxiliarySlot(iteratorSlot);
    Output kryoOutput = (Output) frame.getAuxiliarySlot(kryoOutputSlot);
    OffHeapGroupByKeys offHeapGroupByKeys =
        (OffHeapGroupByKeys) frame.getAuxiliarySlot(offHeapFlushSlot);

    TreeMapNode treeNode = iterator.nextNode();
    // write keys, then n, then values.
    for (int i = 0; i < offHeapGroupByKeys.getKeyTypes().length; i++) {
      Object[] keys = (Object[]) treeNode.getKey();
      writer1.execute(this, kryoOutput, offHeapGroupByKeys.getKeyTypes()[i], keys[i]);
    }
    @SuppressWarnings("unchecked")
    ArrayList<Object> values = (ArrayList<Object>) treeNode.getValue();
    kryoWriteInt(kryoOutput, values.size());
    for (Object value : values) {
      writer2.execute(this, kryoOutput, offHeapGroupByKeys.getRowType(), value);
    }
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
