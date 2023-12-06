package raw.runtime.truffle.runtime.generator.collection;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.StopIterationException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.ComputeNextNodes;
import raw.runtime.truffle.runtime.list.StringList;

import java.util.Objects;

public class AbstractGeneratorNodes {
  @NodeInfo(shortName = "AbstractGenerator.Next")
  @GenerateUncached
  public abstract static class AbstractGeneratorNextNode extends Node {

    public abstract Object execute(Object generator);

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

  // InteropLibrary: Iterator (az) to do

//  @ExportMessage
//  final boolean isIterator() {
//    return true;
//  }
//
//  @ExportMessage
//  final boolean hasIteratorNextElement(@CachedLibrary("this") GeneratorLibrary generatorLibrary)
//      throws UnsupportedMessageException {
//    return generatorLibrary.hasNext(this);
//  }
//
//  @ExportMessage
//  final Object getIteratorNextElement(@CachedLibrary("this") GeneratorLibrary generatorLibrary)
//      throws UnsupportedMessageException, StopIterationException {
//    return generatorLibrary.next(this);
//  }
//
//  @ExportMessage
//  final boolean hasMembers() {
//    return true;
//  }
//
//  @ExportMessage
//  final Object getMembers(boolean includeInternal) {
//    return new StringList(new String[] {"close"});
//  }
//
//  @ExportMessage
//  final boolean isMemberInvocable(String member) {
//    return Objects.equals(member, "close");
//  }
//
//  @ExportMessage
//  final Object invokeMember(
//      String member, Object[] args, @CachedLibrary("this") GeneratorLibrary generatorLibrary) {
//    assert (Objects.equals(member, "close"));
//    generatorLibrary.close(this);
//    return 0;
//  }
}
