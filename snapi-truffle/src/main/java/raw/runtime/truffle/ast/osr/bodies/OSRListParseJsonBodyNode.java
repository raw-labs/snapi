package raw.runtime.truffle.ast.osr.bodies;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import java.util.ArrayList;
import raw.runtime.truffle.ExpressionNode;

public class OSRListParseJsonBodyNode extends ExpressionNode {

  @Child DirectCallNode childCallNode;

  private final int llistSlot;
  private final int parserSlot;

  public OSRListParseJsonBodyNode(
      RootCallTarget childRootCallTarget, int llistSlot, int parserSlot) {
    this.childCallNode = DirectCallNode.create(childRootCallTarget);
    this.llistSlot = llistSlot;
    this.parserSlot = parserSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object parser = frame.getObject(parserSlot);
    @SuppressWarnings("unchecked")
    ArrayList<Object> llist = (ArrayList<Object>) frame.getObject(llistSlot);
    llist.add(childCallNode.call(parser));
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
