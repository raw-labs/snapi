package raw.runtime.truffle.ast.osr.bodies;

import com.oracle.truffle.api.frame.VirtualFrame;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.iterable.ArrayOperationNodes;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodes;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodesFactory;
import raw.runtime.truffle.runtime.list.ListNodes;
import raw.runtime.truffle.runtime.list.ListNodesFactory;

public class OSRListTransformBodyNode extends ExpressionNode {

  @Child
  FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode =
      FunctionExecuteNodesFactory.FunctionExecuteOneNodeGen.create();

  @Child ListNodes.GetNode getNode = ListNodesFactory.GetNodeGen.create();

  @Child
  ArrayOperationNodes.ArraySetArrayItemNode arraySetNode =
      ArrayOperationNodesFactory.ArraySetArrayItemNodeGen.create();

  private final int listSlot;
  private final int functionSlot;
  private final int currentIdxSlot;
  private final int resultSlot;

  public OSRListTransformBodyNode(
      int listSlot, int functionSlot, int currentIdxSlot, int resultSlot) {
    this.currentIdxSlot = currentIdxSlot;
    this.resultSlot = resultSlot;
    this.listSlot = listSlot;
    this.functionSlot = functionSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    int currentIdx = frame.getInt(currentIdxSlot);
    Object transformFunction = frame.getObject(functionSlot);
    Object transformingList = frame.getObject(listSlot);
    Object resultArray = frame.getObject(resultSlot);

    Object transformedItem =
        functionExecuteOneNode.execute(
            this, transformFunction, getNode.execute(this, transformingList, currentIdx));

    arraySetNode.execute(this, resultArray, transformedItem, currentIdx);

    frame.setInt(currentIdxSlot, currentIdx + 1);
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
