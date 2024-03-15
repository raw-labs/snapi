/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.runtime.truffle.runtime.generator.collection;

import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.unsafe.UnsafeInput;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import raw.runtime.truffle.runtime.data_structures.treemap.TreeMapNode;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.AbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.ComputeNextNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.input_buffer.GroupByInputBuffer;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.input_buffer.InputBufferNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.input_buffer.OrderByInputBuffer;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.distinct.DistinctMemoryGenerator;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.distinct.DistinctSpilledFilesGenerator;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.GroupByMemoryGenerator;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.GroupBySpilledFilesGenerator;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by.OrderByMemoryGenerator;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by.OrderBySpilledFilesGenerator;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.record_shaper.RecordShaperNodes;
import raw.runtime.truffle.runtime.generator.list.ListGenerator;
import raw.runtime.truffle.runtime.kryo.KryoNodes;
import raw.runtime.truffle.runtime.list.ListNodes;
import raw.runtime.truffle.runtime.operators.OperatorNodes;

public class GeneratorNodes {
  @NodeInfo(shortName = "AbstractGenerator.Next")
  @GenerateUncached
  @GenerateInline
  public abstract static class GeneratorNextNode extends Node {

    public abstract Object execute(Node node, Object generator);

    @Specialization
    static Object next(
        Node node,
        AbstractGenerator generator,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) ComputeNextNodes.NextNode computeNextNode) {
      if (generator.isTerminated()) {
        throw new BreakException();
      }
      if (generator.getNext() == null && !generator.hasException()) {
        try {
          generator.setNext(computeNextNode.execute(thisNode, generator.getNextGenerator()));
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
        Node node,
        GroupByMemoryGenerator generator,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Exclusive RecordShaperNodes.MakeRowNode reshape) {
      TreeMapNode treeNode = generator.getTreeNodesIterator().nextNode();
      @SuppressWarnings("unchecked")
      ArrayList<Object> values = (ArrayList<Object>) treeNode.getValue();
      return reshape.execute(
          thisNode,
          generator.getOffHeapGroupByKey().getReshape(),
          treeNode.getKey(),
          values.toArray());
    }

    @CompilerDirectives.TruffleBoundary
    static void closeInput(Input input) {
      input.close();
    }

    @Specialization
    static Object next(
        Node node,
        GroupBySpilledFilesGenerator generator,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Exclusive InputBufferNodes.InputBufferHeadKeyNode headKeyNode,
        @Cached @Cached.Shared("keyCompare") OperatorNodes.CompareNode keyCompare,
        @Cached @Cached.Exclusive InputBufferNodes.InputBufferReadNode readNode,
        @Cached @Cached.Exclusive RecordShaperNodes.MakeRowNode reshape) {
      Object key = null;
      // read missing keys and compute the smallest
      for (int idx = 0; idx < generator.getInputBuffers().size(); idx++) {
        GroupByInputBuffer inputBuffer = generator.getInputBuffers().get(idx);
        try {
          Object bufferKey = headKeyNode.execute(thisNode, inputBuffer);
          if (key == null || keyCompare.execute(thisNode, bufferKey, key) < 0) {
            key = bufferKey;
          }
        } catch (KryoException e) {
          // we reached the end of that buffer
          final GroupByInputBuffer removed = generator.getInputBuffers().remove(idx);
          closeInput(removed.getInput());
          idx--;
        }
      }

      // First walk through the buffers to find the ones that expose the same smallest key.
      // Take note of the number of items stored in each in order to allocate the right amount
      // of
      // memory.
      int numberOfRows = 0;
      for (GroupByInputBuffer inputBuffer : generator.getInputBuffers()) {
        Object bufferKey = headKeyNode.execute(thisNode, inputBuffer);
        if (keyCompare.execute(thisNode, key, bufferKey) == 0) {
          numberOfRows += inputBuffer.getItemsLeft();
        }
      }
      // Allocate the exact amount of memory needed to store the values.
      Object[] values = new Object[numberOfRows];

      // Walk through the buffers that had the matching key and read values into the single
      // array.
      int n = 0;
      for (GroupByInputBuffer inputBuffer : generator.getInputBuffers()) {
        if (keyCompare.execute(thisNode, key, headKeyNode.execute(thisNode, inputBuffer)) == 0) {
          int inThatBuffer = inputBuffer.getItemsLeft();
          for (int i = 0; i < inThatBuffer; i++) {
            values[n++] = readNode.execute(thisNode, inputBuffer);
          }
        }
      }
      return reshape.execute(thisNode, generator.getOffHeapGroupByKey().getReshape(), key, values);
    }

    @Specialization
    static Object next(Node node, OrderByMemoryGenerator generator) {
      Object n;
      if (generator.getValues() == null) {
        // no iterator over the values, create one from the next key.
        TreeMapNode treeNode = generator.getNodeIterator().nextNode();
        @SuppressWarnings("unchecked")
        ArrayList<Object> value = (ArrayList<Object>) treeNode.getValue();
        generator.setValues(Arrays.stream(value.toArray()).iterator());
      }
      n = generator.getValues().next();
      if (!generator.getValues().hasNext()) {
        // reset values to make sure we create a new iterator on the next call to next().
        generator.setValues(null);
      }
      return n;
    }

    @Specialization
    static Object next(
        Node node,
        OrderBySpilledFilesGenerator generator,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Exclusive InputBufferNodes.InputBufferHeadKeyNode headKeyNode,
        @Cached @Cached.Exclusive InputBufferNodes.InputBufferReadNode readNode,
        @Cached OperatorNodes.CompareKeys keysCompare) {
      if (generator.getCurrentKryoBuffer() == null) {
        // we need to read the next keys and prepare the new buffer to read from.
        Object[] keys = null;
        // read missing keys and compute the smallest
        for (int idx = 0; idx < generator.getInputBuffers().size(); idx++) {
          OrderByInputBuffer inputBuffer = generator.getInputBuffers().get(idx);
          try {
            Object[] bufferKeys = (Object[]) headKeyNode.execute(thisNode, inputBuffer);
            if (keys == null
                || keysCompare.execute(
                        thisNode,
                        bufferKeys,
                        keys,
                        generator.getOffHeapGroupByKeys().getKeyOrderings())
                    < 0) {
              keys = bufferKeys;
            }
          } catch (KryoException e) {
            // we reached the end of that buffer
            OrderByInputBuffer removed = generator.getInputBuffers().remove(idx);
            closeInput(removed.getInput());
            idx--;
          }
        }
        // First walk through the buffers to find the ones that expose the same smallest
        // key.
        // Take note of the number of items stored in each in order to allocate the right
        // amount of
        // memory.
        for (OrderByInputBuffer inputBuffer : generator.getInputBuffers()) {
          Object[] bufferKeys = inputBuffer.getKeys();
          if (keysCompare.execute(
                  thisNode, keys, bufferKeys, generator.getOffHeapGroupByKeys().getKeyOrderings())
              == 0) {
            generator.setCurrentKryoBuffer(inputBuffer);
            break;
          }
        }
      }
      Object row = readNode.execute(thisNode, generator.getCurrentKryoBuffer());
      if (generator.getCurrentKryoBuffer().getItemsLeft() == 0) {
        generator.setCurrentKryoBuffer(null);
      }
      return row;
    }

    @Specialization
    static Object next(Node node, DistinctMemoryGenerator generator) {
      return generator.getItems().next();
    }

    @Specialization
    static Object next(
        Node node,
        DistinctSpilledFilesGenerator generator,
        @Bind("$node") Node thisNode,
        @Cached KryoNodes.KryoReadNode reader,
        @Cached @Cached.Shared("keyCompare") OperatorNodes.CompareNode keyCompare) {
      Object key = null;
      // read missing keys and compute the smallest
      for (int idx = 0; idx < generator.getKryoBuffers().size(); idx++) {
        Object bufferKey = generator.getHeadKeys().get(idx);
        if (bufferKey == null) {
          // The buffer next key hasn't been read yet.
          Input buffer = generator.getKryoBuffers().get(idx);
          try {
            bufferKey =
                reader.execute(thisNode, buffer, generator.getOffHeapDistinct().getItemType());
          } catch (KryoException e) {
            // we reached the end of that buffer
            // remove both the buffer and its key from the lists
            final Input removed = generator.getKryoBuffers().remove(idx);
            removed.close();
            generator.getHeadKeys().remove(idx);
            idx--;
            continue;
          }
          generator.getHeadKeys().set(idx, bufferKey);
        }
        if (key == null || keyCompare.execute(thisNode, bufferKey, key) < 0) {
          key = bufferKey;
        }
      }

      // Walk through the buffers to consume the ones that expose the same smallest key.
      for (int idx = 0; idx < generator.getKryoBuffers().size(); idx++) {
        Object bufferKey = generator.getHeadKeys().get(idx);
        if (keyCompare.execute(thisNode, key, bufferKey) == 0) {
          // reset the key since we read its data
          generator.getHeadKeys().set(idx, null);
        }
      }
      return key;
    }

    @Specialization
    static Object next(
        Node node,
        ListGenerator generator,
        @Bind("$node") Node thisNode,
        @Cached ListNodes.GetNode getNode) {
      Object item = getNode.execute(thisNode, generator.getList(), generator.getPosition());
      generator.incrementPosition();
      return item;
    }
  }

  @NodeInfo(shortName = "AbstractGenerator.HasNext")
  @GenerateUncached
  @GenerateInline
  public abstract static class GeneratorHasNextNode extends Node {

    public abstract boolean execute(Node node, Object generator);

    @Specialization
    static boolean hasNext(
        Node node,
        AbstractGenerator generator,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) ComputeNextNodes.NextNode computeNextNode) {
      if (generator.isTerminated()) {
        return false;
      } else if (generator.getNext() == null && !generator.hasException()) {
        try {
          generator.setNext(computeNextNode.execute(thisNode, generator.getNextGenerator()));
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
    static boolean hasNext(Node node, GroupByMemoryGenerator generator) {
      return generator.getTreeNodesIterator().hasNext();
    }

    @CompilerDirectives.TruffleBoundary
    static void closeInput(Input input) {
      input.close();
    }

    @Specialization
    static boolean hasNext(
        Node node,
        GroupBySpilledFilesGenerator generator,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Exclusive InputBufferNodes.InputBufferHeadKeyNode headKeyNode,
        @Cached @Cached.Shared("keyCompare") OperatorNodes.CompareNode keyCompare) {
      Object key = null;
      // read missing keys and compute the smallest
      for (int idx = 0; idx < generator.getInputBuffers().size(); idx++) {
        GroupByInputBuffer inputBuffer = generator.getInputBuffers().get(idx);
        try {
          Object bufferKey = headKeyNode.execute(thisNode, inputBuffer);
          if (key == null || keyCompare.execute(thisNode, bufferKey, key) < 0) {
            key = bufferKey;
          }
        } catch (KryoException e) {
          // we reached the end of that buffer
          final GroupByInputBuffer removed = generator.getInputBuffers().remove(idx);
          closeInput(removed.getInput());
          idx--;
        }
      }
      return key != null;
    }

    @Specialization
    static boolean hasNext(Node node, OrderByMemoryGenerator generator) {
      return generator.getNodeIterator().hasNext() || generator.getValues() != null;
    }

    @Specialization
    static boolean hasNext(
        Node node,
        OrderBySpilledFilesGenerator generator,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Exclusive InputBufferNodes.InputBufferHeadKeyNode headKeyNode,
        @Cached OperatorNodes.CompareKeys keysCompare) {
      // we need to read the next keys and prepare the new buffer to read from.
      Object[] keys = null;
      // read missing keys and compute the smallest
      for (int idx = 0; idx < generator.getInputBuffers().size(); idx++) {
        OrderByInputBuffer inputBuffer = generator.getInputBuffers().get(idx);
        try {
          Object[] bufferKeys = (Object[]) headKeyNode.execute(thisNode, inputBuffer);
          if (keys == null
              || keysCompare.execute(
                      thisNode,
                      bufferKeys,
                      keys,
                      generator.getOffHeapGroupByKeys().getKeyOrderings())
                  < 0) {
            keys = bufferKeys;
          }
        } catch (KryoException e) {
          // we reached the end of that buffer
          OrderByInputBuffer removed = generator.getInputBuffers().remove(idx);
          closeInput(removed.getInput());
          idx--;
        }
      }
      return keys != null;
    }

    @Specialization
    static boolean hasNext(Node node, DistinctMemoryGenerator generator) {
      return generator.getItems().hasNext();
    }

    @Specialization
    static boolean hasNext(
        Node node,
        DistinctSpilledFilesGenerator generator,
        @Bind("$node") Node thisNode,
        @Cached KryoNodes.KryoReadNode reader,
        @Cached @Cached.Shared("keyCompare") OperatorNodes.CompareNode keyCompare) {
      Object key = null;
      // read missing keys and compute the smallest
      for (int idx = 0; idx < generator.getKryoBuffers().size(); idx++) {
        Object bufferKey = generator.getHeadKeys().get(idx);
        if (bufferKey == null) {
          // The buffer next key hasn't been read yet.
          Input buffer = generator.getKryoBuffers().get(idx);
          try {
            bufferKey =
                reader.execute(thisNode, buffer, generator.getOffHeapDistinct().getItemType());
          } catch (KryoException e) {
            // we reached the end of that buffer
            // remove both the buffer and its key from the lists
            final Input removed = generator.getKryoBuffers().remove(idx);
            removed.close();
            generator.getHeadKeys().remove(idx);
            idx--;
            continue;
          }
          generator.getHeadKeys().set(idx, bufferKey);
        }
        if (key == null || keyCompare.execute(thisNode, bufferKey, key) < 0) {
          key = bufferKey;
        }
      }
      return key != null;
    }

    @Specialization
    static boolean hasNext(
        Node node,
        ListGenerator generator,
        @Bind("$node") Node thisNode,
        @Cached ListNodes.SizeNode sizeNode) {
      return generator.getPosition() < sizeNode.execute(thisNode, generator.getList());
    }
  }

  @NodeInfo(shortName = "AbstractGenerator.Init")
  @GenerateUncached
  @GenerateInline
  public abstract static class GeneratorInitNode extends Node {

    public abstract void execute(Node node, Object generator);

    @Specialization
    static void init(
        Node node,
        AbstractGenerator generator,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) ComputeNextNodes.InitNode initNode) {
      initNode.execute(thisNode, generator.getNextGenerator());
    }

    @Specialization
    static void init(Node node, GroupByMemoryGenerator generator) {}

    @Specialization
    static void init(
        Node node, GroupBySpilledFilesGenerator generator, @Bind("$node") Node thisNode) {
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
                  GroupByInputBuffer buffer =
                      new GroupByInputBuffer(generator.getOffHeapGroupByKey(), kryoBuffer);
                  generator.addInputBuffer(buffer);
                } catch (FileNotFoundException e) {
                  throw new RawTruffleRuntimeException(e.getMessage(), e, thisNode);
                }
              });
    }

    @Specialization
    static void init(Node node, OrderByMemoryGenerator generator) {}

    @Specialization
    static void init(
        Node node, OrderBySpilledFilesGenerator generator, @Bind("$node") Node thisNode) {
      int nSpilledFiles = generator.getOffHeapGroupByKeys().getSpilledBuffers().size();
      generator.setInputBuffers(new ArrayList<>(nSpilledFiles));
      generator
          .getOffHeapGroupByKeys()
          .getSpilledBuffers()
          .forEach(
              file -> {
                try {
                  Input kryoBuffer =
                      new UnsafeInput(
                          new FileInputStream(file),
                          generator.getOffHeapGroupByKeys().getKryoInputBufferSize());
                  OrderByInputBuffer buffer =
                      new OrderByInputBuffer(generator.getOffHeapGroupByKeys(), kryoBuffer);
                  generator.addInputBuffer(buffer);
                } catch (FileNotFoundException e) {
                  throw new RawTruffleRuntimeException(e.getMessage(), e, thisNode);
                }
              });
    }

    @Specialization
    static void init(Node node, DistinctMemoryGenerator generator) {}

    @Specialization
    static void init(
        Node node, DistinctSpilledFilesGenerator generator, @Bind("$node") Node thisNode) {
      int nSpilledFiles = generator.getOffHeapDistinct().getSpilledBuffers().size();
      generator.setKryoBuffers(new ArrayList<>(nSpilledFiles));
      generator.setHeadKeys(new ArrayList<>(nSpilledFiles));
      generator
          .getOffHeapDistinct()
          .getSpilledBuffers()
          .forEach(
              file -> {
                try {
                  generator
                      .getKryoBuffers()
                      .add(
                          new UnsafeInput(
                              new FileInputStream(file),
                              generator.getOffHeapDistinct().getKryoInputBufferSize()));
                  generator.getHeadKeys().add(null);
                } catch (FileNotFoundException e) {
                  throw new RawTruffleRuntimeException(e.getMessage(), e, thisNode);
                }
              });
    }

    @Specialization
    static void init(Node node, ListGenerator generator) {}
  }

  @NodeInfo(shortName = "Generator.Close")
  @GenerateUncached
  @GenerateInline
  public abstract static class GeneratorCloseNode extends Node {

    public abstract void execute(Node node, Object generator);

    @Specialization
    static void close(
        Node node,
        AbstractGenerator generator,
        @Bind("$node") Node thisNode,
        @Cached ComputeNextNodes.CloseNode closeNode) {
      closeNode.execute(thisNode, generator.getNextGenerator());
    }

    @Specialization
    static void close(Node node, GroupByMemoryGenerator generator) {}

    @Specialization
    static void close(Node node, GroupBySpilledFilesGenerator generator) {
      generator.getInputBuffers().forEach(buffer -> buffer.getInput().close());
    }

    @Specialization
    static void close(Node node, OrderByMemoryGenerator generator) {}

    @Specialization
    static void close(Node node, OrderBySpilledFilesGenerator generator) {
      generator.getInputBuffers().forEach(buffer -> buffer.getInput().close());
    }

    @Specialization
    static void close(Node node, DistinctMemoryGenerator generator) {}

    @Specialization
    static void close(Node node, DistinctSpilledFilesGenerator generator) {
      generator.getKryoBuffers().forEach(Input::close);
    }

    @Specialization
    static void close(Node node, ListGenerator generator) {}
  }
}
