package raw.runtime.truffle.ast.osr.bodies;

import static raw.runtime.truffle.runtime.generator.collection.off_heap_generator.StaticOffHeap.kryoWriteInt;

import com.esotericsoftware.kryo.io.Output;
import com.oracle.truffle.api.frame.VirtualFrame;
import java.util.ArrayList;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.data_structures.treemap.TreeMapIterator;
import raw.runtime.truffle.runtime.data_structures.treemap.TreeMapNode;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.OffHeapGroupByKey;
import raw.runtime.truffle.runtime.kryo.KryoNodes;
import raw.runtime.truffle.runtime.kryo.KryoNodesFactory;

public class OSROffHeapGroupByKeyBodyNode extends ExpressionNode {

  @Child KryoNodes.KryoWriteNode writer1 = KryoNodesFactory.KryoWriteNodeGen.create();
  @Child KryoNodes.KryoWriteNode writer2 = KryoNodesFactory.KryoWriteNodeGen.create();

  private final int kryoOutputSlot;

  private final int iteratorSlot;

  private final int offHeapFlushSlot;

  public OSROffHeapGroupByKeyBodyNode(int kryoOutputSlot, int iteratorSlot, int offHeapFlushSlot) {
    this.kryoOutputSlot = kryoOutputSlot;
    this.iteratorSlot = iteratorSlot;
    this.offHeapFlushSlot = offHeapFlushSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {

    TreeMapIterator iterator = (TreeMapIterator) frame.getAuxiliarySlot(iteratorSlot);
    Output kryoOutput = (Output) frame.getAuxiliarySlot(kryoOutputSlot);
    OffHeapGroupByKey offHeapGroupByKey =
        (OffHeapGroupByKey) frame.getAuxiliarySlot(offHeapFlushSlot);

    TreeMapNode treeNode = iterator.nextNode();
    @SuppressWarnings("unchecked")
    ArrayList<Object> values = (ArrayList<Object>) treeNode.getValue();
    // write key, then n, then values.
    writer1.execute(this, kryoOutput, offHeapGroupByKey.getKeyType(), treeNode.getKey());
    kryoWriteInt(kryoOutput, values.size());
    for (Object value : values) {
      writer2.execute(this, kryoOutput, offHeapGroupByKey.getRowType(), value);
    }
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
