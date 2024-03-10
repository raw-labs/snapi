package raw.runtime.truffle.runtime.record;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import raw.runtime.truffle.PropertyType;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

public class ConcatRecordNodes {
  @NodeInfo(shortName = "ConcatRecord.AddPropNode")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class AddPropNode extends Node {

    public abstract Object execute(Node node, ConcatRecord record, String key, Object value);

    @Specialization
    static Object exec(
        Node node,
        ConcatRecord concatRecord,
        String key,
        Object value,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) RecordNodes.AddPropNode addPropNode,
        @CachedLibrary(limit = "3") DynamicObjectLibrary valuesLibrary) {
      boolean hasOwnProperties = concatRecord.hasOwnProperties();
      if (!hasOwnProperties) {
        RawLanguage lang = RawLanguage.get(thisNode);
        PureRecord newPureRecord = lang.createPureRecord();
        concatRecord.setOwnProperties(newPureRecord, key);
        valuesLibrary.put(newPureRecord, key, value);
        return concatRecord;
      }
      Object result = addPropNode.execute(thisNode, concatRecord.getOwnProperties(), key, value);
      concatRecord.setOwnProperties(result, key);
      return concatRecord;
    }
  }

  @NodeInfo(shortName = "ConcatRecord.Exist")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class ExistNode extends Node {

    public abstract boolean execute(Node node, ConcatRecord concatRecord, String key);

    @Specialization
    static boolean exec(Node node, ConcatRecord concatRecord, String key) {
      return concatRecord.keyExist(key);
    }
  }

  @NodeInfo(shortName = "ConcatRecord.RemoveProp")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class RemovePropNode extends Node {

    public abstract Object execute(Node node, ConcatRecord concatRecord, String key);

    @Specialization
    static ConcatRecord exec(Node node, ConcatRecord concatRecord, String key) {
      return concatRecord.removeKey(key);
    }
  }

  @NodeInfo(shortName = "ConcatRecord.GetKeys")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class GetKeysNode extends Node {

    public abstract Object[] execute(Node node, ConcatRecord record);

    @Specialization
    static Object[] exec(Node node, ConcatRecord pureRecord) {
      return pureRecord.getKeys();
    }
  }

  @NodeInfo(shortName = "ConcatRecord.GetValue")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class GetValueNode extends Node {

    public abstract Object execute(Node node, ConcatRecord record, String key);

    @Specialization
    static Object exec(
        Node node,
        ConcatRecord concatRecord,
        String key,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) RecordNodes.GetValueNode getValueNode) {
      ConcatRecordEntry entry = concatRecord.getEntry(key);
      Object record = concatRecord.getRecords()[entry.objectIndex];
      return getValueNode.execute(thisNode, record, entry.getKey());
    }
  }

  @NodeInfo(shortName = "ConcatRecord.GetValueByIndex")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class GetValueByIndexNode extends Node {
    public abstract Object execute(Node node, ConcatRecord record, int index);

    @Specialization
    static Object exec(
        Node node,
        ConcatRecord concatRecord,
        int index,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) RecordNodes.GetValueNode getValueNode) {
      if (index < 0 || index >= concatRecord.getKeys().length) {
        throw new RawTruffleInternalErrorException("Index out of bounds in record");
      }
      ConcatRecordEntry entry = concatRecord.getEntryByIndex(index);
      Object record = concatRecord.getRecords()[0];
      return getValueNode.execute(thisNode, record, entry.getKey());
    }
  }
}
