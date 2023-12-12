package raw.runtime.truffle.runtime.generator.collection;

import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.unsafe.UnsafeInput;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.AbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.ComputeNextNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.group_by_key.GroupByMemoryGenerator;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.group_by_key.GroupBySpilledFilesGenerator;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.input_buffer.InputBuffer;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.input_buffer.InputBufferNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.record_shaper.RecordShaperNodes;
import raw.runtime.truffle.runtime.operators.OperatorNodes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class GeneratorNodes {
  @NodeInfo(shortName = "AbstractGenerator.Next")
  @GenerateUncached
  public abstract static class GeneratorNextNode extends Node {

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

    @Specialization
    static Object next(
        GroupByMemoryGenerator generator, @Cached RecordShaperNodes.MakeRowNode reshape) {
      Object key = generator.getKeys().next();
      ArrayList<Object> values = generator.getOffHeapGroupByKey().getMemMap().get(key);
      return reshape.execute(generator.getOffHeapGroupByKey().getReshape(), key, values.toArray());
    }

    @Specialization
    static Object next(
        GroupBySpilledFilesGenerator generator,
        @Cached InputBufferNodes.HeadKeyNode headKeyNode,
        @Cached OperatorNodes.CompareNode keyCompare,
        @Cached InputBufferNodes.InputBufferCloseNode closeNode,
        @Cached InputBufferNodes.ReadRowNode readRowNode,
        @Cached RecordShaperNodes.MakeRowNode reshape) {
      Object key = null;
      // read missing keys and compute the smallest
      for (int idx = 0; idx < generator.getInputBuffers().size(); idx++) {
        InputBuffer inputBuffer = generator.getInputBuffers().get(idx);
        try {
          Object bufferKey = headKeyNode.execute(inputBuffer);
          if (key == null || keyCompare.execute(bufferKey, key) < 0) {
            key = bufferKey;
          }
        } catch (KryoException e) {
          // we reached the end of that buffer
          final InputBuffer removed = generator.getInputBuffers().remove(idx);
          closeNode.execute(removed);
          idx--;
        }
      }

      // First walk through the buffers to find the ones that expose the same smallest key.
      // Take note of the number of items stored in each in order to allocate the right amount
      // of
      // memory.
      int numberOfRows = 0;
      for (InputBuffer inputBuffer : generator.getInputBuffers()) {
        Object bufferKey = headKeyNode.execute(inputBuffer);
        if (keyCompare.execute(key, bufferKey) == 0) {
          numberOfRows += inputBuffer.getItemsLeft();
        }
      }
      // Allocate the exact amount of memory needed to store the values.
      Object[] values = new Object[numberOfRows];

      // Walk through the buffers that had the matching key and read values into the single
      // array.
      int n = 0;
      for (InputBuffer inputBuffer : generator.getInputBuffers()) {
        if (keyCompare.execute(key, headKeyNode.execute(inputBuffer)) == 0) {
          int inThatBuffer = inputBuffer.getItemsLeft();
          for (int i = 0; i < inThatBuffer; i++) {
            values[n++] = readRowNode.execute(inputBuffer);
          }
        }
      }
      return reshape.execute(generator.getOffHeapGroupByKey().getReshape(), key, values);
    }
  }

  @NodeInfo(shortName = "AbstractGenerator.HasNext")
  @GenerateUncached
  public abstract static class GeneratorHasNextNode extends Node {

    public abstract boolean execute(Object generator);

    @Specialization
    static boolean hasNext(
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

    @Specialization
    static boolean hasNext(GroupByMemoryGenerator generator) {
      return generator.getKeys().hasNext();
    }

    @Specialization
    static boolean hasNext(
        GroupBySpilledFilesGenerator generator,
        @Cached InputBufferNodes.HeadKeyNode headKeyNode,
        @Cached OperatorNodes.CompareNode keyCompare,
        @Cached InputBufferNodes.InputBufferCloseNode closeNode) {
      Object key = null;
      // read missing keys and compute the smallest
      for (int idx = 0; idx < generator.getInputBuffers().size(); idx++) {
        InputBuffer inputBuffer = generator.getInputBuffers().get(idx);
        try {
          Object bufferKey = headKeyNode.execute(inputBuffer);
          if (key == null || keyCompare.execute(bufferKey, key) < 0) {
            key = bufferKey;
          }
        } catch (KryoException e) {
          // we reached the end of that buffer
          final InputBuffer removed = generator.getInputBuffers().remove(idx);
          closeNode.execute(removed);
          idx--;
        }
      }
      return key != null;
    }
  }

  @NodeInfo(shortName = "AbstractGenerator.Init")
  @GenerateUncached
  public abstract static class GeneratorInitNode extends Node {

    public abstract boolean execute(Object generator);

    @Specialization
    static void init(AbstractGenerator generator, @Cached ComputeNextNodes.InitNode initNode) {
      initNode.execute(generator.getNextGenerator());
    }

    @Specialization
    static void init(GroupByMemoryGenerator generator) {}

    @Specialization
    static void init(GroupBySpilledFilesGenerator generator) {
      // turn the list of spilled files into a list of Kryo buffers. Buffers will be read and
      // dropped from
      // the list as they are exhausted.
      int nSpilledFiles = generator.getOffHeapGroupByKey().getSpilledBuffers().size();
      generator.setInputBuffers(new ArrayList<>(nSpilledFiles));
      generator
          .getOffHeapGroupByKey()
          .getSpilledBuffers()
          .forEach(
              file -> {
                try {
                  Input kryoBuffer =
                      new UnsafeInput(
                          new FileInputStream(file),
                          generator.getOffHeapGroupByKey().getKryoInputBufferSize());
                  InputBuffer buffer =
                      new InputBuffer(kryoBuffer, generator.getOffHeapGroupByKey());
                  generator.addInputBuffer(buffer);
                } catch (FileNotFoundException e) {
                  throw new RawTruffleRuntimeException(e.getMessage());
                }
              });
    }
  }

  @NodeInfo(shortName = "AbstractGenerator.Close")
  @GenerateUncached
  public abstract static class GeneratorCloseNode extends Node {

    public abstract boolean execute(Object generator);

    @Specialization
    static void close(AbstractGenerator generator, @Cached ComputeNextNodes.CloseNode closeNode) {
      closeNode.execute(generator.getNextGenerator());
    }

    @Specialization
    static void close(GroupByMemoryGenerator generator) {}

    @Specialization
    static void close(GroupBySpilledFilesGenerator generator) {
      generator.getInputBuffers().forEach(buffer -> buffer.getInput().close());
    }
  }
}
