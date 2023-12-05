package raw.runtime.truffle.runtime.generator;

import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.generators.compute_next.ExpressionComputeNext;

public class GeneratorNodes {

  @NodeInfo(shortName = "Generator.ComputeNext")
  @GenerateUncached
  public abstract static class ComputeNextNode extends Node {

    public abstract Object execute(Object generator);

    @Specialization
    static Object computeNext(ExpressionComputeNext generator) {
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
}
