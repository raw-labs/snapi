package raw.runtime.truffle.runtime.iterable.record_shaper;

import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.record.RecordObject;

public class RecordShaperNodes {
  @NodeInfo(shortName = "RecordShaper.MakeRow")
  @GenerateUncached
  @ImportStatic(RecordShaperType.class)
  public abstract static class MakeRowNode extends Node {

    public abstract Object execute(Object shaper, Object key, Object[] values);

    @Specialization(guards = "shaper.getType() == RecordShaperType.COLLECTION_GROUP_BY")
    static Object makeRowCollection(
        RecordShaper shaper,
        Object key,
        Object[] values,
        @CachedLibrary(limit = "3") InteropLibrary records) {
      RecordObject record = shaper.getLanguage().createRecord();
      if (records == null) {
        records = InteropLibrary.getFactory().getUncached();
      }
      try {
        records.writeMember(record, "key", key);
        records.writeMember(record, "group", new ObjectList(values).toIterable());
      } catch (UnsupportedMessageException
          | UnknownIdentifierException
          | UnsupportedTypeException e) {
        throw new RawTruffleInternalErrorException(e);
      }
      return record;
    }

    @Specialization(guards = "shaper.getType() == RecordShaperType.EQUIJOIN_GROUP_BY")
    static Object makeRowEquiJoin(RecordShaper shaper, Object key, Object[] values) {
      return new Object[] {key, values};
    }

    @Specialization(guards = "shaper.getType() == RecordShaperType.LIST_GROUP_BY")
    static Object makeRowList(
        RecordShaper shaper,
        Object key,
        Object[] values,
        @CachedLibrary(limit = "3") InteropLibrary records) {
      RecordObject record = shaper.getLanguage().createRecord();
      if (records == null) {
        records = InteropLibrary.getFactory().getUncached(record);
      }
      try {
        records.writeMember(record, "key", key);
        records.writeMember(record, "group", new ObjectList(values));
      } catch (UnsupportedMessageException
          | UnknownIdentifierException
          | UnsupportedTypeException e) {
        throw new RawTruffleInternalErrorException(e);
      }
      return record;
    }
  }
}
