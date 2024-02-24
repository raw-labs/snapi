package raw.runtime.truffle.ast.osr.conditions;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;

public class OSRExistsConditionNode extends ExpressionNode {

  @Child
  private GeneratorNodes.GeneratorHasNextNode hasNextNode =
      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();

  private final int generatorSlot;

  private final int predicateResultSlot;

  public OSRExistsConditionNode(int generatorSlot, int predicateResultSlot) {
    this.generatorSlot = generatorSlot;
    this.predicateResultSlot = predicateResultSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return !frame.getBoolean(predicateResultSlot)
        && hasNextNode.execute(this, frame.getObject(generatorSlot));
  }

  @Override
  public boolean executeBoolean(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return (boolean) executeGeneric(virtualFrame);
  }
}
