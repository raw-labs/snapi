package raw.runtime.truffle.runtime.record;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import raw.runtime.truffle.PropertyType;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

import static raw.runtime.truffle.PropertyType.*;
import static raw.runtime.truffle.PropertyType.OBJECT_TYPE;

public class DuplicateKeyRecordNodes {
  @NodeInfo(shortName = "DuplicateKeyRecord.AddPropNode")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class AddPropNode extends Node {

    public abstract Object execute(Node node, DuplicateKeyRecord record, String key, Object value);

    @Specialization(limit = "3")
    static Object exec(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        String key,
        int item,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      int keysSize = duplicateKeyRecord.getKeySize();
      valuesLibrary.putInt(duplicateKeyRecord, keysSize, item);
      duplicateKeyRecord.addKey(key);
      return duplicateKeyRecord;
    }

    @Specialization(limit = "3")
    static Object exec(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        String key,
        long item,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      int keysSize = duplicateKeyRecord.getKeySize();
      valuesLibrary.putLong(duplicateKeyRecord, keysSize, item);
      duplicateKeyRecord.addKey(key);
      return duplicateKeyRecord;
    }

    @Specialization(limit = "3")
    static Object exec(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        String key,
        double item,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      int keysSize = duplicateKeyRecord.getKeySize();
      valuesLibrary.putDouble(duplicateKeyRecord, keysSize, item);
      duplicateKeyRecord.addKey(key);
      return duplicateKeyRecord;
    }

    @Specialization(limit = "3")
    static Object exec(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        String key,
        Object item,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      int keysSize = duplicateKeyRecord.getKeySize();
      valuesLibrary.put(duplicateKeyRecord, keysSize, item);
      duplicateKeyRecord.addKey(key);
      return duplicateKeyRecord;
    }
  }

  @NodeInfo(shortName = "DuplicateKeyRecord.Exist")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class ExistNode extends Node {

    public abstract boolean execute(Node node, DuplicateKeyRecord duplicateKeyRecord, String key);

    @Specialization
    static boolean exec(Node node, DuplicateKeyRecord duplicateKeyRecord, String key) {
      return duplicateKeyRecord.keyExist(key);
    }
  }

  @NodeInfo(shortName = "DuplicateKeyRecord.RemoveProp")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class RemovePropNode extends Node {

    public abstract Object execute(Node node, DuplicateKeyRecord duplicateKeyRecord, String key);

    @Specialization(limit = "3")
    static DuplicateKeyRecord exec(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        String key,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      int keyIndex = duplicateKeyRecord.getKeyIndex(key);
      valuesLibrary.removeKey(duplicateKeyRecord, String.valueOf(keyIndex));
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
      return record.getKeys();
    }
  }

  @NodeInfo(shortName = "DuplicateKeyRecord.GetValue")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class GetValueNode extends Node {

    public abstract Object execute(Node node, DuplicateKeyRecord record, String key);

    @Specialization(
        limit = "3",
        guards = "isInt(valuesLibrary.getPropertyFlagsOrDefault(duplicateKeyRecord, key, 5))")
    static int getInt(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        String key,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      try {
        int idx = duplicateKeyRecord.getKeyIndex(key);
        return valuesLibrary.getIntOrDefault(duplicateKeyRecord, String.valueOf(idx), -1);
      } catch (UnexpectedResultException e) {
        throw new RawTruffleInternalErrorException("Unexpected result", e);
      }
    }

    @Specialization(
        limit = "3",
        guards = "isLong(valuesLibrary.getPropertyFlagsOrDefault(duplicateKeyRecord, key, 5))")
    static long getLong(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        String key,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      try {
        int idx = duplicateKeyRecord.getKeyIndex(key);
        return valuesLibrary.getLongOrDefault(duplicateKeyRecord, String.valueOf(idx), -1);
      } catch (UnexpectedResultException e) {
        throw new RawTruffleInternalErrorException("Unexpected result", e);
      }
    }

    @Specialization(
        limit = "3",
        guards = "isDouble(valuesLibrary.getPropertyFlagsOrDefault(duplicateKeyRecord, key, 5))")
    static double getDouble(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        String key,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      try {
        int idx = duplicateKeyRecord.getKeyIndex(key);
        return valuesLibrary.getDoubleOrDefault(duplicateKeyRecord, String.valueOf(idx), -1);
      } catch (UnexpectedResultException e) {
        throw new RawTruffleInternalErrorException("Unexpected result", e);
      }
    }

    @Specialization(limit = "3")
    static Object getObject(
        Node node,
        DuplicateKeyRecord duplicateKeyRecord,
        String key,
        @CachedLibrary("duplicateKeyRecord") DynamicObjectLibrary valuesLibrary) {
      int idx = duplicateKeyRecord.getKeyIndex(key);
      return valuesLibrary.getOrDefault(duplicateKeyRecord, String.valueOf(idx), null);
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
        throw new RawTruffleInternalErrorException("Index out of bounds in record");
      }
      return valuesLibrary.getOrDefault(record, String.valueOf(index), null);
    }
  }
}
