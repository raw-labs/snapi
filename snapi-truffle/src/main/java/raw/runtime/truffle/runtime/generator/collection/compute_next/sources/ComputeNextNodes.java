package raw.runtime.truffle.runtime.generator.collection.compute_next.sources;

import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

public class ComputeNextNodes {

  @NodeInfo(shortName = "Generator.ComputeNext")
  @GenerateUncached
  public abstract static class NextNode extends Node {

    public abstract Object execute(Object generator);

    @Specialization
    static Object next(ExpressionComputeNext generator) {
      if (generator.isTerminated()) {
        throw new BreakException();
      }
      try {
        return generator.getCurrent();
      } catch (RawTruffleRuntimeException e) {
        return new RawTruffleRuntimeException(e.getMessage());
      } finally {
        generator.incrementPosition();
      }
    }
  }

  @NodeInfo(shortName = "Generator.Init")
  @GenerateUncached
  public abstract static class InitNode extends Node {

    public abstract Object execute(Object generator);

    @Specialization
    static void init(ExpressionComputeNext generator) {}
  }

  @NodeInfo(shortName = "Generator.Close")
  @GenerateUncached
  public abstract static class CloseNode extends Node {

    public abstract Object execute(Object generator);

    @Specialization
    static void close(ExpressionComputeNext generator) {}
  }
}
