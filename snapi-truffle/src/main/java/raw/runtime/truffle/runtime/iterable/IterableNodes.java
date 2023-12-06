package raw.runtime.truffle.runtime.iterable;

import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.iterable.sources.ExpressionCollection;

public class IterableNodes {
  @NodeInfo(shortName = "Iterable.GetGenerator")
  @GenerateUncached
  public abstract static class GetGeneratorNode extends Node {

    public abstract Object execute(Object generator);

    @Specialization
    static Object getGenerator(ExpressionCollection collection) {
      return collection.getGenerator();
    }
    
  }
}
