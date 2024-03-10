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

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import raw.runtime.truffle.PropertyType;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

import java.util.Vector;

import static raw.runtime.truffle.PropertyType.*;
import static raw.runtime.truffle.PropertyType.OBJECT_TYPE;

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
        ConcatRecord record,
        String key,
        Object value,
        @Bind("$node") Node thisNode,
        @Cached ConcatRecordNodes.AddPropNode addPropNode) {
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
    static boolean exec(Node node, ConcatRecord record, String key) {
      return record.keyExist(key);
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
        ConcatRecord record,
        String key,
        @Bind("$node") Node thisNode,
        @Cached ConcatRecordNodes.GetValueNode getValueNode) {
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
        ConcatRecord record,
        int index,
        @Bind("$node") Node thisNode,
        @Cached ConcatRecordNodes.GetValueByIndexNode getValueByIndexNode) {
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
        ConcatRecord record,
        @Bind("$node") Node thisNode,
        @Cached ConcatRecordNodes.GetKeysNode getKeysNode) {
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
        @Cached PureRecordNodes.RemovePropNode getKeysNode) {
      return getKeysNode.execute(thisNode, record, key);
    }

    @Specialization
    static Object exec(
        Node node,
        ConcatRecord record,
        String key,
        @Bind("$node") Node thisNode,
        @Cached ConcatRecordNodes.RemovePropNode getKeysNode) {
      return getKeysNode.execute(thisNode, record, key);
    }
  }

  @NodeInfo(shortName = "Record.AddField")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class AddConcatedFieldNode extends Node {

    public abstract Object execute(Node node, Object record, String key, Object value);

    @Specialization
    static Object exec(
        Node node,
        Object record,
        String key,
        int item,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("getKeysNode") RecordNodes.GetKeysNode getKeysNode,
        @CachedLibrary(limit = "3") @Cached.Shared("values") DynamicObjectLibrary valuesLibrary) {
      RawLanguage lang = RawLanguage.get(thisNode);
      PureRecord newPureRecord = lang.createPureRecord();
      valuesLibrary.putInt(newPureRecord, key, item);
      valuesLibrary.setPropertyFlags(newPureRecord, key, INT_TYPE);

      Object[] keys = getKeysNode.execute(thisNode, record);
      Vector<String> stringKeys = new Vector<>();
      for (Object k : keys) {
        stringKeys.add((String) k);
      }

      Vector<String> newKeys = new Vector<>();
      newKeys.add(key);

      return new ConcatRecord(record, newPureRecord, stringKeys, newKeys);
    }

    @Specialization
    static Object exec(
        Node node,
        Object record,
        String key,
        long item,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("getKeysNode") RecordNodes.GetKeysNode getKeysNode,
        @CachedLibrary(limit = "3") @Cached.Shared("values") DynamicObjectLibrary valuesLibrary) {
      RawLanguage lang = RawLanguage.get(thisNode);
      PureRecord newPureRecord = lang.createPureRecord();
      valuesLibrary.putLong(newPureRecord, key, item);
      valuesLibrary.setPropertyFlags(newPureRecord, key, LONG_TYPE);

      Object[] keys = getKeysNode.execute(thisNode, record);
      Vector<String> stringKeys = new Vector<>();
      for (Object k : keys) {
        stringKeys.add((String) k);
      }

      Vector<String> newKeys = new Vector<>();
      newKeys.add(key);

      return new ConcatRecord(record, newPureRecord, stringKeys, newKeys);
    }

    @Specialization
    static Object exec(
        Node node,
        Object record,
        String key,
        double item,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("getKeysNode") RecordNodes.GetKeysNode getKeysNode,
        @CachedLibrary(limit = "3") @Cached.Shared("values") DynamicObjectLibrary valuesLibrary) {
      RawLanguage lang = RawLanguage.get(thisNode);
      PureRecord newPureRecord = lang.createPureRecord();
      valuesLibrary.putDouble(newPureRecord, key, item);
      valuesLibrary.setPropertyFlags(newPureRecord, key, DOUBLE_TYPE);

      Object[] keys = getKeysNode.execute(thisNode, record);
      Vector<String> stringKeys = new Vector<>();
      for (Object k : keys) {
        stringKeys.add((String) k);
      }

      Vector<String> newKeys = new Vector<>();
      newKeys.add(key);

      return new ConcatRecord(record, newPureRecord, stringKeys, newKeys);
    }

    @Specialization
    static Object exec(
        Node node,
        Object record,
        String key,
        Object item,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("getKeysNode") RecordNodes.GetKeysNode getKeysNode,
        @CachedLibrary(limit = "3") @Cached.Shared("values") DynamicObjectLibrary valuesLibrary) {
      RawLanguage lang = RawLanguage.get(thisNode);
      PureRecord newPureRecord = lang.createPureRecord();
      valuesLibrary.putWithFlags(newPureRecord, key, item, OBJECT_TYPE);

      Object[] keys = getKeysNode.execute(thisNode, record);
      Vector<String> stringKeys = new Vector<>();
      for (Object k : keys) {
        stringKeys.add((String) k);
      }

      Vector<String> newKeys = new Vector<>();
      newKeys.add(key);

      return new ConcatRecord(record, newPureRecord, stringKeys, newKeys);
    }
  }
}
