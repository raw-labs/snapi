package raw.runtime.truffle.runtime.generator.collection;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.compute_next.ComputeNextNodes;

public class AbstractGeneratorNodes {
  @NodeInfo(shortName = "AbstractGenerator.Next")
  @GenerateUncached
  public abstract static class AbstractGeneratorNextNode extends Node {

    public abstract Object execute(Object generator);

    @Specialization
    static Object next(
        AbstractGenerator generator, @Cached ComputeNextNodes.NextNode computeNextNode) {
      if (generator.isTerminated()) {
        throw new BreakException();
      }
      if (generator.getNext() == null) {
        try {
          generator.setNext(computeNextNode.execute(generator.getNextGenerator()));
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

  @NodeInfo(shortName = "AbstractGenerator.HasNext")
  @GenerateUncached
  public abstract static class AbstractGeneratorHasNextNode extends Node {

    public abstract boolean execute(Object generator);

    @Specialization
    static Object hasNext(
        AbstractGenerator generator, @Cached ComputeNextNodes.NextNode computeNextNode) {
      if (generator.isTerminated()) {
        return false;
      } else if (generator.getNext() == null) {
        try {
          generator.setNext(computeNextNode.execute(generator.getNextGenerator()));
        } catch (BreakException e) {
          generator.setTerminated(true);
          return false;
        } catch (RawTruffleRuntimeException e) { // store the runtime error
          generator.setException(e);
        }
      }
      return true;
    }
  }

  @NodeInfo(shortName = "AbstractGenerator.Init")
  @GenerateUncached
  public abstract static class AbstractGeneratorInitNode extends Node {

    public abstract boolean execute(Object generator);

    @Specialization
    static void init(AbstractGenerator generator, @Cached ComputeNextNodes.InitNode initNode) {
      initNode.execute(generator.getNextGenerator());
    }
  }

  @NodeInfo(shortName = "AbstractGenerator.Close")
  @GenerateUncached
  public abstract static class AbstractGeneratorCloseNode extends Node {

    public abstract boolean execute(Object generator);

    @Specialization
    static void close(AbstractGenerator generator, @Cached ComputeNextNodes.CloseNode closeNode) {
      closeNode.execute(generator.getNextGenerator());
    }
  }
}
