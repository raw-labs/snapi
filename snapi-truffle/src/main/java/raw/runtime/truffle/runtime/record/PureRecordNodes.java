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

public class PureRecordNodes {
  @NodeInfo(shortName = "PureRecord.AddPropNode")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class AddPropNode extends Node {

    public abstract Object execute(Node node, PureRecord record, String key, Object value);

    @Specialization(limit = "3")
    static Object exec(
        Node node,
        PureRecord pureRecord,
        String key,
        int item,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("getValue") PureRecordNodes.GetValueNode getValueNode,
        @Cached @Cached.Shared("getKeys") PureRecordNodes.GetKeysNode getKeysNode,
        @Cached @Cached.Shared("addProp") DuplicateKeyRecordNodes.AddPropNode addPropNode,
        @Cached @Cached.Shared("existsNode") PureRecordNodes.ExistNode existNode,
        @CachedLibrary("pureRecord") DynamicObjectLibrary valuesLibrary) {
      if (existNode.execute(thisNode, pureRecord, key)) {
        DuplicateKeyRecord duplicateKeyRecord =
            RawLanguage.get(thisNode).createDuplicateKeyRecord();
        Object[] keys = getKeysNode.execute(thisNode, pureRecord);
        DuplicateKeyRecord newRecord = RawLanguage.get(thisNode).createDuplicateKeyRecord();
        for (Object ikey : keys) {
          addPropNode.execute(
              thisNode, newRecord, (String) ikey, getValueNode.execute(thisNode, pureRecord, key));
        }
        return duplicateKeyRecord;
      }

      valuesLibrary.putInt(pureRecord, key, item);
      valuesLibrary.setPropertyFlags(pureRecord, key, INT_TYPE);
      return pureRecord;
    }

    @Specialization(limit = "3")
    static Object exec(
        Node node,
        PureRecord pureRecord,
        String key,
        long item,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("getValue") PureRecordNodes.GetValueNode getValueNode,
        @Cached @Cached.Shared("getKeys") PureRecordNodes.GetKeysNode getKeysNode,
        @Cached @Cached.Shared("addProp") DuplicateKeyRecordNodes.AddPropNode addPropNode,
        @Cached @Cached.Shared("existsNode") PureRecordNodes.ExistNode existNode,
        @CachedLibrary("pureRecord") DynamicObjectLibrary valuesLibrary) {
      if (existNode.execute(thisNode, pureRecord, key)) {
        DuplicateKeyRecord duplicateKeyRecord =
            RawLanguage.get(thisNode).createDuplicateKeyRecord();
        Object[] keys = getKeysNode.execute(thisNode, pureRecord);
        DuplicateKeyRecord newRecord = RawLanguage.get(thisNode).createDuplicateKeyRecord();
        for (Object ikey : keys) {
          addPropNode.execute(
              thisNode, newRecord, (String) ikey, getValueNode.execute(thisNode, pureRecord, key));
        }
        return duplicateKeyRecord;
      }

      valuesLibrary.putLong(pureRecord, key, item);
      valuesLibrary.setPropertyFlags(pureRecord, key, LONG_TYPE);
      return pureRecord;
    }

    @Specialization(limit = "3")
    static Object exec(
        Node node,
        PureRecord pureRecord,
        String key,
        double item,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("getValue") PureRecordNodes.GetValueNode getValueNode,
        @Cached @Cached.Shared("getKeys") PureRecordNodes.GetKeysNode getKeysNode,
        @Cached @Cached.Shared("addProp") DuplicateKeyRecordNodes.AddPropNode addPropNode,
        @Cached @Cached.Shared("existsNode") PureRecordNodes.ExistNode existNode,
        @CachedLibrary("pureRecord") DynamicObjectLibrary valuesLibrary) {

      if (existNode.execute(thisNode, pureRecord, key)) {
        DuplicateKeyRecord duplicateKeyRecord =
            RawLanguage.get(thisNode).createDuplicateKeyRecord();
        Object[] keys = getKeysNode.execute(thisNode, pureRecord);
        DuplicateKeyRecord newRecord = RawLanguage.get(thisNode).createDuplicateKeyRecord();
        for (Object ikey : keys) {
          addPropNode.execute(
              thisNode, newRecord, (String) ikey, getValueNode.execute(thisNode, pureRecord, key));
        }
        return duplicateKeyRecord;
      }

      valuesLibrary.putDouble(pureRecord, key, item);
      valuesLibrary.setPropertyFlags(pureRecord, key, DOUBLE_TYPE);
      return pureRecord;
    }

    @Specialization(limit = "3")
    static Object exec(
        Node node,
        PureRecord pureRecord,
        String key,
        Object item,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("getValue") PureRecordNodes.GetValueNode getValueNode,
        @Cached @Cached.Shared("getKeys") PureRecordNodes.GetKeysNode getKeysNode,
        @Cached @Cached.Shared("addProp") DuplicateKeyRecordNodes.AddPropNode addPropNode,
        @Cached @Cached.Shared("existsNode") PureRecordNodes.ExistNode existNode,
        @CachedLibrary("pureRecord") DynamicObjectLibrary valuesLibrary) {
      if (existNode.execute(thisNode, pureRecord, key)) {
        DuplicateKeyRecord duplicateKeyRecord =
            RawLanguage.get(thisNode).createDuplicateKeyRecord();
        Object[] keys = getKeysNode.execute(thisNode, pureRecord);
        DuplicateKeyRecord newRecord = RawLanguage.get(thisNode).createDuplicateKeyRecord();
        for (Object ikey : keys) {
          addPropNode.execute(
              thisNode, newRecord, (String) ikey, getValueNode.execute(thisNode, pureRecord, key));
        }
        return duplicateKeyRecord;
      }
      valuesLibrary.putWithFlags(pureRecord, key, item, OBJECT_TYPE);
      return pureRecord;
    }
  }

  @NodeInfo(shortName = "PureRecord.RemovePropNode")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class RemovePropNode extends Node {

    public abstract PureRecord execute(Node node, PureRecord pureRecord, String key);

    @Specialization
    static PureRecord exec(
        Node node,
        PureRecord pureRecord,
        String key,
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
              thisNode, newRecord, (String) k, getValueNode.execute(thisNode, pureRecord, k));
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

    public abstract boolean execute(Node node, PureRecord pureRecord, String key);

    @Specialization(limit = "3")
    static boolean exec(
        Node node,
        PureRecord pureRecord,
        String key,
        @CachedLibrary("pureRecord") DynamicObjectLibrary valuesLibrary) {
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
        guards = "isInt(valuesLibrary.getPropertyFlagsOrDefault(pureRecord, key, 5))")
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
        guards = "isLong(valuesLibrary.getPropertyFlagsOrDefault(pureRecord, key, 5))")
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
        guards = "isDouble(valuesLibrary.getPropertyFlagsOrDefault(pureRecord, key, 5))")
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
