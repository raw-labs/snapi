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

package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.input_buffer;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.ComputeNextNodes;
import raw.runtime.truffle.runtime.kryo.KryoNodes;

public class InputBufferNodes {
  @NodeInfo(shortName = "InputBuffer.Close")
  @GenerateUncached
  @GenerateInline
  public abstract static class InputBufferCloseNode extends Node {

    public abstract void execute(Node node, Object generator);

    @Specialization
    static void close(
        Node node,
        GroupByInputBuffer buffer,
        @Cached @Cached.Shared("close") ComputeNextNodes.CloseNode closeNode) {
      buffer.getInput().close();
    }

    @Specialization
    static void close(
        Node node,
        OrderByInputBuffer buffer,
        @Cached @Cached.Shared("close") ComputeNextNodes.CloseNode closeNode) {
      buffer.getInput().close();
    }
  }

  @NodeInfo(shortName = "InputBuffer.HeadKey")
  @GenerateUncached
  @GenerateInline
  public abstract static class InputBufferHeadKeyNode extends Node {

    public abstract Object execute(Node node, Object buffer);

    @Specialization
    static Object headKey(
        Node node,
        GroupByInputBuffer buffer,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("kryoRead") KryoNodes.KryoReadNode kryoRead) {
      // read the next key (if it is null, otherwise keep the current one).
      if (buffer.getKey() == null) {
        buffer.setKey(
            kryoRead.execute(
                thisNode,
                buffer.getOffHeapGroupByKey().getLanguage(),
                buffer.getInput(),
                buffer.getOffHeapGroupByKey().getKeyType()));
        buffer.setItemsLeftFromInput();
      }
      return buffer.getKey();
    }

    @Specialization
    static Object[] headKey(
        Node node,
        OrderByInputBuffer buffer,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("kryoRead") KryoNodes.KryoReadNode kryoRead) {
      // read the next key (if it is null, otherwise keep the current one).
      if (buffer.getKeys() == null) {
        Rql2TypeWithProperties[] keyTypes = buffer.getOffHeapGroupByKey().getKeyTypes();
        Object[] keys = new Object[keyTypes.length];
        for (int i = 0; i < keyTypes.length; i++) {
          keys[i] =
              kryoRead.execute(
                  thisNode,
                  buffer.getOffHeapGroupByKey().getLanguage(),
                  buffer.getInput(),
                  keyTypes[i]);
        }
        buffer.setKeys(keys);
        buffer.setItemsLeftFromInput();
      }
      return buffer.getKeys();
    }
  }

  @NodeInfo(shortName = "InputBuffer.Read")
  @GenerateUncached
  @GenerateInline
  public abstract static class InputBufferReadNode extends Node {

    public abstract Object execute(Node node, Object buffer);

    @Specialization
    static Object read(
        Node node,
        GroupByInputBuffer buffer,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("kryoRead") KryoNodes.KryoReadNode kryoRead) {
      buffer.decreaseItemsLeft();
      if (buffer.getItemsLeft() == 0) {
        buffer.setKey(null);
      }
      return kryoRead.execute(
          thisNode,
          buffer.getOffHeapGroupByKey().getLanguage(),
          buffer.getInput(),
          buffer.getOffHeapGroupByKey().getKeyType());
    }

    @Specialization
    static Object read(
        Node node,
        OrderByInputBuffer buffer,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("kryoRead") KryoNodes.KryoReadNode kryoRead) {
      buffer.decreaseItemsLeft();
      if (buffer.getItemsLeft() == 0) {
        buffer.setKeys(null);
      }
      return kryoRead.execute(
          thisNode,
          buffer.getOffHeapGroupByKey().getLanguage(),
          buffer.getInput(),
          buffer.getOffHeapGroupByKey().getRowType());
    }
  }
}
