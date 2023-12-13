package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.input_buffer;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.ComputeNextNodes;
import raw.runtime.truffle.runtime.kryo.KryoNodes;

public class InputBufferNodes {
  @NodeInfo(shortName = "InputBuffer.Close")
  @GenerateUncached
  public abstract static class InputBufferCloseNode extends Node {

    public abstract void execute(Object generator);

    @Specialization
    static void close(GroupByInputBuffer buffer, @Cached ComputeNextNodes.CloseNode closeNode) {
      buffer.getInput().close();
    }

    @Specialization
    static void close(OrderByInputBuffer buffer, @Cached ComputeNextNodes.CloseNode closeNode) {
      buffer.getInput().close();
    }
  }

  @NodeInfo(shortName = "InputBuffer.HeadKey")
  @GenerateUncached
  public abstract static class InputBufferHeadKeyNode extends Node {

    public abstract Object execute(Object buffer);

    @Specialization
    static Object headKey(GroupByInputBuffer buffer, @Cached KryoNodes.KryoReadNode kryoRead) {
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

    @Specialization
    static Object[] headKey(OrderByInputBuffer buffer, @Cached KryoNodes.KryoReadNode kryoRead) {
      // read the next key (if it is null, otherwise keep the current one).
      if (buffer.getKeys() == null) {
        Rql2TypeWithProperties[] keyTypes = buffer.getOffHeapGroupByKey().getKeyTypes();
        Object[] keys = new Object[keyTypes.length];
        for (int i = 0; i < keyTypes.length; i++) {
          keys[i] =
              kryoRead.execute(
                  buffer.getOffHeapGroupByKey().getLanguage(), buffer.getInput(), keyTypes[i]);
        }
        buffer.setKeys(keys);
        buffer.setItemsLeftFromInput();
      }
      return buffer.getKeys();
    }
  }

  @NodeInfo(shortName = "InputBuffer.Read")
  @GenerateUncached
  public abstract static class InputBufferReadNode extends Node {

    public abstract Object execute(Object buffer);

    @Specialization
    static Object read(GroupByInputBuffer buffer, @Cached KryoNodes.KryoReadNode kryoRead) {
      buffer.decreaseItemsLeft();
      if (buffer.getItemsLeft() == 0) {
        buffer.setKey(null);
      }
      return kryoRead.execute(
          buffer.getOffHeapGroupByKey().getLanguage(),
          buffer.getInput(),
          buffer.getOffHeapGroupByKey().getKeyType());
    }

    @Specialization
    static Object read(OrderByInputBuffer buffer, @Cached KryoNodes.KryoReadNode kryoRead) {
      buffer.decreaseItemsLeft();
      if (buffer.getItemsLeft() == 0) {
        buffer.setKeys(null);
      }
      return kryoRead.execute(
          buffer.getOffHeapGroupByKey().getLanguage(),
          buffer.getInput(),
          buffer.getOffHeapGroupByKey().getRowType());
    }
  }
}
