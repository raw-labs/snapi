package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.input_buffer;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.ComputeNextNodes;
import raw.runtime.truffle.runtime.kryo.KryoNodes;

public class InputBufferNodes {
  @NodeInfo(shortName = "InputBuffer.Close")
  @GenerateUncached
  public abstract static class InputBufferCloseNode extends Node {

    public abstract void execute(Object generator);

    @Specialization
    static void close(InputBuffer buffer, @Cached ComputeNextNodes.CloseNode closeNode) {
      buffer.getInput().close();
    }
  }

  @NodeInfo(shortName = "InputBuffer.HeadKey")
  @GenerateUncached
  public abstract static class HeadKeyNode extends Node {

    public abstract Object execute(Object buffer);

    @Specialization
    static Object headKey(InputBuffer buffer, @Cached KryoNodes.KryoReadNode kryoRead) {
      // read the next key (if it is null, otherwise keep the current one).
      if (buffer.getKey() == null) {
        buffer.setKey(
            kryoRead.execute(
                buffer.getOffHeapGroupByKey().getLanguage(),
                buffer.getInput(),
                buffer.getOffHeapGroupByKey().getKeyType()));
        buffer.setItemsLeftFromInput();
      }
      return buffer.getKey();
    }
  }

  @NodeInfo(shortName = "InputBuffer.readRow")
  @GenerateUncached
  public abstract static class ReadRowNode extends Node {

    public abstract Object execute(Object buffer);

    @Specialization
    static Object headKey(InputBuffer buffer, @Cached KryoNodes.KryoReadNode kryoRead) {
      buffer.decreaseItemsLeft();
      if (buffer.getItemsLeft() == 0) {
        buffer.setKey(null);
      }
      return kryoRead.execute(
          buffer.getOffHeapGroupByKey().getLanguage(),
          buffer.getInput(),
          buffer.getOffHeapGroupByKey().getKeyType());
    }
  }
}
