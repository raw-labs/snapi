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
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import raw.runtime.truffle.PropertyType;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

// (az) Whenever using any of these nodes, create one per property
public class PureRecordNodes {
  @NodeInfo(shortName = "PureRecord.AddPropNode")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class AddPropNode extends Node {

    public abstract void execute(Node node, PureRecord record, Object key, Object value);

    @Specialization(limit = "3")
    static void exec(
        Node node,
        PureRecord pureRecord,
        Object key,
        byte item,
        @CachedLibrary("pureRecord") DynamicObjectLibrary valuesLibrary) {
      valuesLibrary.putInt(pureRecord, key, item);
      valuesLibrary.setPropertyFlags(pureRecord, key, BYTE_TYPE);
    }

    @Specialization(limit = "3")
    static void exec(
        Node node,
        PureRecord pureRecord,
        Object key,
        short item,
        @CachedLibrary("pureRecord") DynamicObjectLibrary valuesLibrary) {
      valuesLibrary.putInt(pureRecord, key, item);
      valuesLibrary.setPropertyFlags(pureRecord, key, SHORT_TYPE);
    }

    @Specialization(limit = "3")
    static void exec(
        Node node,
        PureRecord pureRecord,
        Object key,
        int item,
        @CachedLibrary("pureRecord") DynamicObjectLibrary valuesLibrary) {
      valuesLibrary.putInt(pureRecord, key, item);
      valuesLibrary.setPropertyFlags(pureRecord, key, INT_TYPE);
    }

    @Specialization(limit = "3")
    static void exec(
        Node node,
        PureRecord pureRecord,
        Object key,
        long item,
        @CachedLibrary("pureRecord") @Cached.Exclusive DynamicObjectLibrary valuesLibrary) {
      valuesLibrary.putLong(pureRecord, key, item);
      valuesLibrary.setPropertyFlags(pureRecord, key, LONG_TYPE);
    }

    @Specialization(limit = "3")
    static void exec(
        Node node,
        PureRecord pureRecord,
        Object key,
        float item,
        @CachedLibrary("pureRecord") @Cached.Exclusive DynamicObjectLibrary valuesLibrary) {
      valuesLibrary.putDouble(pureRecord, key, item);
      valuesLibrary.setPropertyFlags(pureRecord, key, FLOAT_TYPE);
    }

    @Specialization(limit = "3")
    static void exec(
        Node node,
        PureRecord pureRecord,
        Object key,
        double item,
        @CachedLibrary("pureRecord") @Cached.Exclusive DynamicObjectLibrary valuesLibrary) {
      valuesLibrary.putDouble(pureRecord, key, item);
      valuesLibrary.setPropertyFlags(pureRecord, key, DOUBLE_TYPE);
    }

    @Specialization(limit = "3")
    static void exec(
        Node node,
        PureRecord pureRecord,
        Object key,
        Object item,
        @CachedLibrary("pureRecord") @Cached.Exclusive DynamicObjectLibrary valuesLibrary) {
      valuesLibrary.putWithFlags(pureRecord, key, item, OBJECT_TYPE);
    }

    //    @Specialization(guards = "existNode.execute(thisNode, pureRecord, key)")
    //    static Object execTransition(
    //        Node node,
    //        PureRecord pureRecord,
    //        Object key,
    //        Object item,
    //        @Bind("$node") Node thisNode,
    //        @Cached PureRecordNodes.GetValueNode getValueNode,
    //        @Cached PureRecordNodes.GetKeysNode getKeysNode,
    //        @Cached DuplicateKeyRecordNodes.AddPropNode addPropNode,
    //        @Cached @Cached.Shared PureRecordNodes.ExistNode existNode) {
    //      Object[] keys = getKeysNode.execute(thisNode, pureRecord);
    //      DuplicateKeyRecord newRecord = RawLanguage.get(thisNode).createDuplicateKeyRecord();
    //      for (Object ikey : keys) {
    //        newRecord =
    //            addPropNode.execute(
    //                thisNode, newRecord, ikey, getValueNode.execute(thisNode, pureRecord, ikey));
    //      }
    //      addPropNode.execute(thisNode, newRecord, key, item);
    //      return newRecord;
    //    }
  }

  @NodeInfo(shortName = "PureRecord.RemovePropNode")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class RemovePropNode extends Node {

    public abstract Object execute(Node node, PureRecord pureRecord, Object key);

    @Specialization
    static Object exec(
        Node node,
        PureRecord pureRecord,
        Object key,
        @Bind("$node") Node thisNode,
        @Cached AddPropNode addPropNode,
        @Cached GetKeysNode getKeysNode,
        @Cached GetValueNode getValueNode) {
      RawLanguage lang = RawLanguage.get(thisNode);
      PureRecord newRecord = lang.createPureRecord();
      Object[] keys = getKeysNode.execute(thisNode, pureRecord);
      for (Object k : keys) {
        if (!k.equals(key)) {
          addPropNode.execute(
              thisNode, newRecord, k, getValueNode.execute(thisNode, pureRecord, k));
        }
      }
      return newRecord;
    }
  }

  @NodeInfo(shortName = "PureRecord.Exist")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class ExistNode extends Node {

    public abstract boolean execute(Node node, PureRecord pureRecord, Object key);

    @Specialization
    static boolean exec(
        Node node,
        PureRecord pureRecord,
        Object key,
        @CachedLibrary(limit = "8") DynamicObjectLibrary valuesLibrary) {
      return valuesLibrary.containsKey(pureRecord, key);
    }
  }

  @NodeInfo(shortName = "PureRecord.GetKeysNode")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class GetKeysNode extends Node {

    public abstract Object[] execute(Node node, PureRecord record);

    @Specialization(limit = "3")
    static Object[] exec(
        Node node,
        PureRecord pureRecord,
        @CachedLibrary("pureRecord") DynamicObjectLibrary valuesLibrary) {
      return valuesLibrary.getKeyArray(pureRecord);
    }
  }

  @NodeInfo(shortName = "ConcatRecord.GetValueByIndex")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class GetValueByIndexNode extends Node {
    public abstract Object execute(Node node, PureRecord record, int index);

    @Specialization(limit = "3")
    static Object exec(
        Node node,
        PureRecord pureRecord,
        int index,
        @Bind("$node") Node thisNode,
        @CachedLibrary("pureRecord") DynamicObjectLibrary valuesLibrary) {
      Object[] keys = valuesLibrary.getKeyArray(pureRecord);
      if (index < 0 || index >= keys.length) {
        throw new RawTruffleInternalErrorException("Index out of bounds in record");
      }
      return valuesLibrary.getOrDefault(pureRecord, keys[index], null);
    }
  }

  @NodeInfo(shortName = "PureRecord.Get")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class GetValueNode extends Node {

    public abstract Object execute(Node node, PureRecord pureRecord, Object key);

    @Specialization(
        limit = "3",
        guards = "isByte(valuesLibrary.getPropertyFlagsOrDefault(pureRecord, key, 7))")
    static byte getByte(
        Node node,
        PureRecord pureRecord,
        Object key,
        @CachedLibrary("pureRecord") DynamicObjectLibrary valuesLibrary) {
      try {
        return (byte) valuesLibrary.getIntOrDefault(pureRecord, key, -1);
      } catch (UnexpectedResultException e) {
        throw new RawTruffleInternalErrorException("Unexpected result", e);
      }
    }

    @Specialization(
        limit = "3",
        guards = "isShort(valuesLibrary.getPropertyFlagsOrDefault(pureRecord, key, 7))")
    static short getShort(
        Node node,
        PureRecord pureRecord,
        Object key,
        @CachedLibrary("pureRecord") DynamicObjectLibrary valuesLibrary) {
      try {
        return (short) valuesLibrary.getIntOrDefault(pureRecord, key, -1);
      } catch (UnexpectedResultException e) {
        throw new RawTruffleInternalErrorException("Unexpected result", e);
      }
    }

    @Specialization(
        limit = "3",
        guards = "isInt(valuesLibrary.getPropertyFlagsOrDefault(pureRecord, key, 7))")
    static int getInt(
        Node node,
        PureRecord pureRecord,
        Object key,
        @CachedLibrary("pureRecord") DynamicObjectLibrary valuesLibrary) {
      try {
        return valuesLibrary.getIntOrDefault(pureRecord, key, -1);
      } catch (UnexpectedResultException e) {
        throw new RawTruffleInternalErrorException("Unexpected result", e);
      }
    }

    @Specialization(
        limit = "3",
        guards = "isLong(valuesLibrary.getPropertyFlagsOrDefault(pureRecord, key, 7))")
    static long getLong(
        Node node,
        PureRecord pureRecord,
        Object key,
        @CachedLibrary("pureRecord") DynamicObjectLibrary valuesLibrary) {
      try {
        return valuesLibrary.getLongOrDefault(pureRecord, key, -1);
      } catch (UnexpectedResultException e) {
        throw new RawTruffleInternalErrorException("Unexpected result", e);
      }
    }

    @Specialization(
        limit = "3",
        guards = "isFloat(valuesLibrary.getPropertyFlagsOrDefault(pureRecord, key, 7))")
    static float getFloat(
        Node node,
        PureRecord pureRecord,
        Object key,
        @CachedLibrary("pureRecord") DynamicObjectLibrary valuesLibrary) {
      try {
        return (float) valuesLibrary.getDoubleOrDefault(pureRecord, key, -1);
      } catch (UnexpectedResultException e) {
        throw new RawTruffleInternalErrorException("Unexpected result", e);
      }
    }

    @Specialization(
        limit = "3",
        guards = "isDouble(valuesLibrary.getPropertyFlagsOrDefault(pureRecord, key, 7))")
    static double getDouble(
        Node node,
        PureRecord pureRecord,
        Object key,
        @CachedLibrary("pureRecord") DynamicObjectLibrary valuesLibrary) {
      try {
        return valuesLibrary.getDoubleOrDefault(pureRecord, key, -1);
      } catch (UnexpectedResultException e) {
        throw new RawTruffleInternalErrorException("Unexpected result", e);
      }
    }

    @Specialization(limit = "3")
    static Object getObject(
        Node node,
        PureRecord pureRecord,
        Object key,
        @CachedLibrary("pureRecord") DynamicObjectLibrary valuesLibrary) {
      return valuesLibrary.getOrDefault(pureRecord, key, null);
    }
  }
}
