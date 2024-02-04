package raw.runtime.truffle.ast.expressions.aggregation;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;

public class OSRMultiAggregationNode extends Node implements RepeatingNode {

  @Child
  private GeneratorNodes.GeneratorHasNextNode hasNextNode =
      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child AggregatorNodes.Merge mergeNode = AggregatorNodesFactory.MergeNodeGen.create();

  @CompilationFinal private Object generator;

  @CompilationFinal(dimensions = 1)
  private byte[] aggregationTypes;

  private Object[] currentResults;

  public void init(Object generator, byte[] aggregationTypes, Object[] zeros) {
    this.generator = generator;
    this.aggregationTypes = aggregationTypes;
    this.currentResults = zeros;
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
    for (int i = 0; i < aggregationTypes.length; i++) {
      currentResults[i] = mergeNode.execute(this, aggregationTypes[i], currentResults[i], next);
    }
    return currentResults;
  }
}
