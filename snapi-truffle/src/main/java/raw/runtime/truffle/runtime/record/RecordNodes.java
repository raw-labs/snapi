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

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.staticobject.DefaultStaticProperty;
import raw.runtime.truffle.RawLanguage;

public class RecordNodes {

  @NodeInfo(shortName = "Record.AddProp")
  @GenerateUncached
  @GenerateInline
  public abstract static class AddPropNode extends Node {

    public abstract Object execute(Node node, Object record, String key, Object value);

    @Specialization
    static Object exec(
        Node node,
        PureRecord record,
        String key,
        Object value,
        @Bind("$node") Node thisNode,
        @Cached PureRecordNodes.AddPropNode addPropNode) {
      return addPropNode.execute(thisNode, record, key, value);
    }

    @Specialization
    static Object exec(
        Node node,
        DuplicateKeyRecord record,
        String key,
        Object value,
        @Bind("$node") Node thisNode,
        @Cached DuplicateKeyRecordNodes.AddPropNode addPropNode) {
      return addPropNode.execute(thisNode, record, key, value);
    }

    @Specialization
    static Object exec(
        Node node,
        StaticObjectRecord record,
        String key,
        Object value,
        @Bind("$node") Node thisNode,
        @Cached StaticRecordNodes.AddPropNode addPropNode) {
      return addPropNode.execute(thisNode, record, key, value);
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

    @Specialization
    static boolean exec(Node node, StaticObjectRecord record, String key) {
      return record.__shapeRef__.hasFieldByKey(key);
    }
  }

  @NodeInfo(shortName = "Record.GetValue")
  @GenerateUncached
  @GenerateInline
  public abstract static class GetValueNode extends Node {

    public abstract Object execute(Node node, Object record, String key);

    @Specialization
    static Object exec(
        Node node,
        PureRecord record,
        String key,
        @Bind("$node") Node thisNode,
        @Cached PureRecordNodes.GetValueNode getValueNode) {
      return getValueNode.execute(thisNode, record, key);
    }

    @Specialization
    static Object exec(
        Node node,
        DuplicateKeyRecord record,
        String key,
        @Bind("$node") Node thisNode,
        @Cached DuplicateKeyRecordNodes.GetValueNode getValueNode) {
      return getValueNode.execute(thisNode, record, key);
    }

    @Specialization
    static Object exec(Node node, StaticObjectRecord record, String key) {
      return record.__shapeRef__.getFieldByKey(key).getObject(record);
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

    @Specialization
    static Object exec(Node node, StaticObjectRecord record, int index) {
      return record.__shapeRef__.getFieldByIndex(index).getObject(record);
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

    @Specialization
    static Object[] exec(Node node, StaticObjectRecord record) {
      return record.__shapeRef__.getDistinctKeys();
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
        @Cached PureRecordNodes.RemovePropNode getKeysNode) {
      return getKeysNode.execute(thisNode, record, key);
    }

    @Specialization
    static Object exec(
        Node node,
        DuplicateKeyRecord record,
        String key,
        @Bind("$node") Node thisNode,
        @Cached DuplicateKeyRecordNodes.RemovePropNode getKeysNode) {
      return getKeysNode.execute(thisNode, record, key);
    }

    @Specialization
    static Object exec(
        Node node,
        StaticObjectRecord record,
        String key,
        @Bind("$node") Node thisNode,
        @Cached StaticRecordNodes.RemovePropNode removePropNode) {
      return removePropNode.execute(thisNode, record, key);
    }
  }

  @NodeInfo(shortName = "Record.Clone")
  @GenerateUncached
  @GenerateInline
  public abstract static class CloneNode extends Node {

    public abstract Object execute(Node node, Object record);

    @Specialization
    static Object exec(
        Node node,
        PureRecord record,
        @Bind("$node") Node thisNode,
        @Cached PureRecordNodes.GetKeysNode getKeysNode,
        @Cached PureRecordNodes.AddPropNode addPropNode,
        @Cached PureRecordNodes.GetValueNode getValueNode) {
      Object[] keys = getKeysNode.execute(thisNode, record);
      PureRecord newRecord = RawLanguage.get(thisNode).createPureRecord();
      for (Object key : keys) {
        addPropNode.execute(
            thisNode, newRecord, (String) key, getValueNode.execute(thisNode, record, key));
      }
      return newRecord;
    }

    @Specialization
    static Object exec(
        Node node,
        DuplicateKeyRecord record,
        @Bind("$node") Node thisNode,
        @Cached DuplicateKeyRecordNodes.GetKeysNode getKeysNode,
        @Cached DuplicateKeyRecordNodes.AddPropNode addPropNode,
        @Cached DuplicateKeyRecordNodes.GetValueNode getValueNode) {
      Object[] keys = getKeysNode.execute(thisNode, record);
      DuplicateKeyRecord newRecord = RawLanguage.get(thisNode).createDuplicateKeyRecord();
      for (Object key : keys) {
        newRecord =
            addPropNode.execute(
                thisNode,
                newRecord,
                (String) key,
                getValueNode.execute(thisNode, record, (String) key));
        newRecord.addKey((String) key);
      }
      return newRecord;
    }

    @Specialization
    static Object exec(Node node, StaticObjectRecord record) {
      StaticObjectRecord newObject =
          record.__shapeRef__.getShape().getFactory().create(record.__shapeRef__);
      for (DefaultStaticProperty field : record.__shapeRef__.getFields()) {
        field.setObject(newObject, field.getObject(record));
      }
      return newObject;
    }
  }
}
