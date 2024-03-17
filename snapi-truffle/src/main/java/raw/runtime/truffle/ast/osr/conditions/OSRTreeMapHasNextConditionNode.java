package raw.runtime.truffle.ast.osr.conditions;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.data_structures.treemap.TreeMapIterator;

public class OSRTreeMapHasNextConditionNode extends ExpressionNode {
  private final int iteratorSlot;

  public OSRTreeMapHasNextConditionNode(int iteratorSlot) {
    this.iteratorSlot = iteratorSlot;
  }

  public Object executeGeneric(VirtualFrame frame) {
    TreeMapIterator iterator = (TreeMapIterator) frame.getAuxiliarySlot(iteratorSlot);
    return iterator.hasNext();
  }

  @Override
  public boolean executeBoolean(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return (boolean) executeGeneric(virtualFrame);
  }
}
