package raw.runtime.truffle.runtime.generator.generators;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.GeneratorNodes;

public class AbstractGeneratorNodes {
  @NodeInfo(shortName = "Generator.AbstractNext")
  @GenerateUncached
  public abstract static class ComputeNextNode extends Node {

    public abstract Object execute(Object generator);

    @Specialization
    static Object AbstractNext(
        AbstractGenerator generator, @Cached GeneratorNodes.ComputeNextNode computeNextNode) {
      if (generator.isTerminated()) {
        throw new BreakException();
      }
      if (generator.getNext() == null) {
        try {
          generator.setNext(computeNextNode.execute(generator.getComputeNext()));
        } catch (BreakException e) { // case end of data
          generator.setTerminated(true);
          throw e;
        } catch (RawTruffleRuntimeException e) { // case runtime exception
          generator.setException(e);
        }
      } else if (generator.hasException()) { // if hasNext returned a runtime error
        generator.setTerminated(true);
        throw generator.getException();
      }
      Object result = generator.getNext();
      generator.setNext(null);
      return result;
    }
  }
}
