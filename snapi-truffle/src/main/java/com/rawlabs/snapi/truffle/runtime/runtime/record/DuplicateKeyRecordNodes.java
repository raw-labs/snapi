/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.truffle.runtime.runtime.record;

import static com.rawlabs.snapi.truffle.runtime.PropertyType.*;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.rawlabs.snapi.truffle.runtime.PropertyType;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.TruffleInternalErrorException;

// (az) Whenever using any of these nodes, create one per property
public class DuplicateKeyRecordNodes {
  @NodeInfo(shortName = "DuplicateKeyRecord.AddPropNode")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class AddPropNode extends Node {

    public abstract DuplicateKeyRecord execute(Node node, Object record, Object key, Object value);

    @Specialization(limit = "3")
    static DuplicateKeyRecord exec(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        Object key,
        byte item,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      int keysSize = duplicateKeyRecord.getKeySize();
      valuesLibrary.putInt(duplicateKeyRecord, keysSize, item);
      valuesLibrary.setPropertyFlags(duplicateKeyRecord, keysSize, BYTE_TYPE);
      duplicateKeyRecord.addKey(key);
      return duplicateKeyRecord;
    }

    @Specialization(limit = "3")
    static DuplicateKeyRecord exec(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        Object key,
        short item,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      int keysSize = duplicateKeyRecord.getKeySize();
      valuesLibrary.putInt(duplicateKeyRecord, keysSize, item);
      valuesLibrary.setPropertyFlags(duplicateKeyRecord, keysSize, SHORT_TYPE);
      duplicateKeyRecord.addKey(key);
      return duplicateKeyRecord;
    }

    @Specialization(limit = "3")
    static DuplicateKeyRecord exec(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        Object key,
        int item,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      int keysSize = duplicateKeyRecord.getKeySize();
      valuesLibrary.putInt(duplicateKeyRecord, keysSize, item);
      valuesLibrary.setPropertyFlags(duplicateKeyRecord, keysSize, INT_TYPE);
      duplicateKeyRecord.addKey(key);
      return duplicateKeyRecord;
    }

    @Specialization(limit = "3")
    static DuplicateKeyRecord exec(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        Object key,
        long item,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      int keysSize = duplicateKeyRecord.getKeySize();
      valuesLibrary.putLong(duplicateKeyRecord, keysSize, item);
      valuesLibrary.setPropertyFlags(duplicateKeyRecord, keysSize, LONG_TYPE);
      duplicateKeyRecord.addKey(key);
      return duplicateKeyRecord;
    }

    @Specialization(limit = "3")
    static DuplicateKeyRecord exec(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        Object key,
        double item,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      int keysSize = duplicateKeyRecord.getKeySize();
      valuesLibrary.putDouble(duplicateKeyRecord, keysSize, item);
      valuesLibrary.setPropertyFlags(duplicateKeyRecord, keysSize, DOUBLE_TYPE);
      duplicateKeyRecord.addKey(key);
      return duplicateKeyRecord;
    }

    @Specialization(limit = "3")
    static DuplicateKeyRecord exec(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        Object key,
        Object item,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      int keysSize = duplicateKeyRecord.getKeySize();
      valuesLibrary.putWithFlags(duplicateKeyRecord, keysSize, item, OBJECT_TYPE);
      duplicateKeyRecord.addKey(key);
      return duplicateKeyRecord;
    }
  }

  @NodeInfo(shortName = "DuplicateKeyRecord.Exist")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class ExistNode extends Node {

    public abstract boolean execute(Node node, DuplicateKeyRecord duplicateKeyRecord, Object key);

    @Specialization
    static boolean exec(Node node, DuplicateKeyRecord duplicateKeyRecord, Object key) {
      return duplicateKeyRecord.keyExist(key);
    }
  }

  @NodeInfo(shortName = "DuplicateKeyRecord.RemoveProp")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class RemovePropNode extends Node {

    public abstract Object execute(Node node, DuplicateKeyRecord duplicateKeyRecord, Object key);

    @Specialization(limit = "3")
    static DuplicateKeyRecord exec(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        Object key,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      int keyIndex = duplicateKeyRecord.getKeyIndex(key);
      valuesLibrary.removeKey(duplicateKeyRecord, keyIndex);
      duplicateKeyRecord.removeKey(keyIndex);
      return duplicateKeyRecord;
    }
  }

  @NodeInfo(shortName = "DuplicateKeyRecord.GetKeys")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class GetKeysNode extends Node {

    public abstract Object[] execute(Node node, DuplicateKeyRecord record);

    @Specialization
    static Object[] exec(Node node, DuplicateKeyRecord record) {
      return record.getDistinctKeys();
    }
  }

  @NodeInfo(shortName = "DuplicateKeyRecord.GetValue")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class GetValueNode extends Node {

    public abstract Object execute(Node node, DuplicateKeyRecord record, Object key);

    @Specialization(
        limit = "3",
        guards = "isByte(valuesLibrary.getPropertyFlagsOrDefault(duplicateKeyRecord, key, 7))")
    static byte getByte(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        Object key,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      try {
        int idx = duplicateKeyRecord.getKeyIndex(key);
        return (byte) valuesLibrary.getIntOrDefault(duplicateKeyRecord, idx, -1);
      } catch (UnexpectedResultException e) {
        throw new TruffleInternalErrorException("Unexpected result", e);
      }
    }

    @Specialization(
        limit = "3",
        guards = "isShort(valuesLibrary.getPropertyFlagsOrDefault(duplicateKeyRecord, key, 7))")
    static short getShort(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        Object key,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      try {
        int idx = duplicateKeyRecord.getKeyIndex(key);
        return (short) valuesLibrary.getIntOrDefault(duplicateKeyRecord, idx, -1);
      } catch (UnexpectedResultException e) {
        throw new TruffleInternalErrorException("Unexpected result", e);
      }
    }

    @Specialization(
        limit = "3",
        guards = "isInt(valuesLibrary.getPropertyFlagsOrDefault(duplicateKeyRecord, key, 7))")
    static int getInt(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        Object key,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      try {
        int idx = duplicateKeyRecord.getKeyIndex(key);
        return valuesLibrary.getIntOrDefault(duplicateKeyRecord, idx, -1);
      } catch (UnexpectedResultException e) {
        throw new TruffleInternalErrorException("Unexpected result", e);
      }
    }

    @Specialization(
        limit = "3",
        guards = "isLong(valuesLibrary.getPropertyFlagsOrDefault(duplicateKeyRecord, key, 7))")
    static long getLong(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        Object key,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      try {
        int idx = duplicateKeyRecord.getKeyIndex(key);
        return valuesLibrary.getLongOrDefault(duplicateKeyRecord, idx, -1);
      } catch (UnexpectedResultException e) {
        throw new TruffleInternalErrorException("Unexpected result", e);
      }
    }

    @Specialization(
        limit = "3",
        guards = "isDouble(valuesLibrary.getPropertyFlagsOrDefault(duplicateKeyRecord, key, 7))")
    static double getDouble(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        Object key,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      try {
        int idx = duplicateKeyRecord.getKeyIndex(key);
        return valuesLibrary.getDoubleOrDefault(duplicateKeyRecord, idx, -1);
      } catch (UnexpectedResultException e) {
        throw new TruffleInternalErrorException("Unexpected result", e);
      }
    }

    @Specialization(limit = "3")
    static Object getObject(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        Object key,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      int idx = duplicateKeyRecord.getKeyIndex(key);
      return valuesLibrary.getOrDefault(duplicateKeyRecord, idx, null);
    }
  }

  @NodeInfo(shortName = "DuplicateKeyRecord.GetValueByIndex")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class GetValueByIndexNode extends Node {
    public abstract Object execute(Node node, DuplicateKeyRecord record, int index);

    @Specialization(limit = "3")
    static Object exec(
        Node node,
        DuplicateKeyRecord record,
        int index,
        @Bind("$node") Node thisNode,
        @CachedLibrary("record") DynamicObjectLibrary valuesLibrary) {
      Object[] keys = valuesLibrary.getKeyArray(record);
      if (index < 0 || index >= keys.length) {
        throw new TruffleInternalErrorException("Index out of bounds in record");
      }
      return valuesLibrary.getOrDefault(record, index, null);
    }
  }
}
