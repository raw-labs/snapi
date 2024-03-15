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

package raw.runtime.truffle.runtime.record;

import static raw.runtime.truffle.PropertyType.*;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;

// (az) Whenever using any of these nodes, create one per property
public class RecordNodes {

  @NodeInfo(shortName = "Record.AddProp")
  @GenerateUncached
  @GenerateInline
  public abstract static class AddPropNode extends Node {

    public abstract void execute(
        Node node, Object record, Object key, Object value, boolean hasDuplicateKeys);

    @Specialization(guards = "!hasDuplicateKeys")
    static void exec(
        Node node,
        PureRecord record,
        Object key,
        Object value,
        boolean hasDuplicateKeys,
        @Bind("$node") Node thisNode,
        @Cached PureRecordNodes.AddPropNode addPropNode) {
      addPropNode.execute(thisNode, record, key, value);
    }

    @Specialization(guards = "hasDuplicateKeys")
    static void exec(
        Node node,
        DuplicateKeyRecord record,
        Object key,
        Object value,
        boolean hasDuplicateKeys,
        @Bind("$node") Node thisNode,
        @Cached DuplicateKeyRecordNodes.AddPropNode addPropNode) {
      addPropNode.execute(thisNode, record, key, value);
    }
  }

  @NodeInfo(shortName = "Record.Exists")
  @GenerateUncached
  @GenerateInline
  public abstract static class ExistsNode extends Node {

    public abstract boolean execute(Node node, Object record, String key);

    @Specialization
    static boolean exec(
        Node node,
        PureRecord record,
        String key,
        @Bind("$node") Node thisNode,
        @Cached PureRecordNodes.ExistNode addPropNode) {
      return addPropNode.execute(thisNode, record, key);
    }

    @Specialization
    static boolean exec(Node node, DuplicateKeyRecord record, String key) {
      return record.keyExist(key);
    }
  }

  @NodeInfo(shortName = "Record.GetValue")
  @GenerateUncached
  @GenerateInline
  public abstract static class GetValueNode extends Node {

    public abstract Object execute(Node node, Object record, Object key);

    @Specialization
    static Object exec(
        Node node,
        PureRecord record,
        Object key,
        @Bind("$node") Node thisNode,
        @Cached PureRecordNodes.GetValueNode getValueNode) {
      return getValueNode.execute(thisNode, record, key);
    }

    @Specialization
    static Object exec(
        Node node,
        DuplicateKeyRecord record,
        Object key,
        @Bind("$node") Node thisNode,
        @Cached DuplicateKeyRecordNodes.GetValueNode getValueNode) {
      return getValueNode.execute(thisNode, record, key);
    }
  }

  @NodeInfo(shortName = "Record.GetValueByIndex")
  @GenerateUncached
  @GenerateInline
  public abstract static class GetValueByIndexNode extends Node {

    public abstract Object execute(Node node, Object record, int index);

    @Specialization
    static Object exec(
        Node node,
        PureRecord record,
        int index,
        @Bind("$node") Node thisNode,
        @Cached PureRecordNodes.GetValueByIndexNode getValueByIndexNode) {
      return getValueByIndexNode.execute(thisNode, record, index);
    }

    @Specialization
    static Object exec(
        Node node,
        DuplicateKeyRecord record,
        int index,
        @Bind("$node") Node thisNode,
        @Cached DuplicateKeyRecordNodes.GetValueByIndexNode getValueByIndexNode) {
      return getValueByIndexNode.execute(thisNode, record, index);
    }
  }

  @NodeInfo(shortName = "Record.GetKeys")
  @GenerateUncached
  @GenerateInline
  public abstract static class GetKeysNode extends Node {

    public abstract Object[] execute(Node node, Object record);

    @Specialization
    static Object[] exec(
        Node node,
        PureRecord record,
        @Bind("$node") Node thisNode,
        @Cached PureRecordNodes.GetKeysNode getKeysNode) {
      return getKeysNode.execute(thisNode, record);
    }

    @Specialization
    static Object[] exec(
        Node node,
        DuplicateKeyRecord record,
        @Bind("$node") Node thisNode,
        @Cached DuplicateKeyRecordNodes.GetKeysNode getKeysNode) {
      return getKeysNode.execute(thisNode, record);
    }
  }

  @NodeInfo(shortName = "Record.RemoveProp")
  @GenerateUncached
  @GenerateInline
  public abstract static class RemovePropNode extends Node {

    public abstract Object execute(Node node, Object record, String key);

    @Specialization
    static Object exec(
        Node node,
        PureRecord record,
        String key,
        @Bind("$node") Node thisNode,
        @Cached PureRecordNodes.RemovePropNode removePropNode) {
      return removePropNode.execute(thisNode, record, key);
    }

    @Specialization
    static Object exec(
        Node node,
        DuplicateKeyRecord record,
        String key,
        @Bind("$node") Node thisNode,
        @Cached DuplicateKeyRecordNodes.RemovePropNode removePropNode) {
      return removePropNode.execute(thisNode, record, key);
    }
  }
}
