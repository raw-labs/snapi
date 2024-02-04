package raw.runtime.truffle.ast.expressions.aggregation;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;

public class OSRSingleAggregationNode extends Node implements RepeatingNode {

  @Child
  private GeneratorNodes.GeneratorHasNextNode hasNextNode =
      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child AggregatorNodes.Merge mergeNode = AggregatorNodesFactory.MergeNodeGen.create();

  @CompilationFinal private Object generator;

  @CompilationFinal private byte aggregationType;

  private Object currentResult;

  public void init(Object generator, byte aggregationType, Object zero) {
    this.generator = generator;
    this.aggregationType = aggregationType;
    this.currentResult = zero;
  }

  public boolean executeRepeating(VirtualFrame frame) {
    // ignored
    return false;
  }

  public boolean shouldContinue(Object returnValue) {
    return hasNextNode.execute(this, generator);
  }

  public Object executeRepeatingWithValue(VirtualFrame frame) {
    Object next = nextNode.execute(this, generator);
    currentResult = mergeNode.execute(this, aggregationType, currentResult, next);
    return currentResult;
  }
}
