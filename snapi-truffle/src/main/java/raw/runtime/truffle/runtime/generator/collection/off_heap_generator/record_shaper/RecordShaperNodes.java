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

package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.record_shaper;

import com.oracle.truffle.api.dsl.GenerateUncached;
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
  public abstract static class MakeRowNode extends Node {

    public abstract Object execute(Object shaper, Object key, Object[] values);

    @Specialization(guards = {"shaper != null", "!shaper.forList()"})
    static Object makeRowCollection(
        RecordShaper shaper,
        Object key,
        Object[] values,
        @CachedLibrary(limit = "3") InteropLibrary records) {
      RecordObject record = shaper.getLanguage().createRecord();
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

    @Specialization(guards = {"shaper != null", "shaper.forList()"})
    static Object makeRowList(
        RecordShaper shaper,
        Object key,
        Object[] values,
        @CachedLibrary(limit = "3") InteropLibrary records) {
      RecordObject record = shaper.getLanguage().createRecord();
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

    @Specialization(guards = "shaper == null")
    static Object makeRowEquiJoin(Object shaper, Object key, Object[] values) {
      return new Object[] {key, values};
    }
  }
}
