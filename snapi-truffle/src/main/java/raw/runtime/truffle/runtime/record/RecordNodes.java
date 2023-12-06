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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

public class RecordNodes {
  @NodeInfo(shortName = "Record.ReadIndexNode")
  @GenerateUncached
  public abstract static class ReadIndexNode extends Node {

    public abstract Object execute(RecordObject record, int idx);

    @Specialization(limit = "3")
    Object exec(
        RecordObject record,
        int idx,
        @CachedLibrary("record.values") DynamicObjectLibrary valuesLibrary) {
      if (idx < 0 || idx >= record.keys.size()) {
        throw new RawTruffleInternalErrorException(InvalidArrayIndexException.create(idx));
      }
      return valuesLibrary.getOrDefault(record.values, idx, null);
    }
  }

  @NodeInfo(shortName = "Record.ReadByKeyNode")
  @GenerateUncached
  public abstract static class ReadByKeyNode extends Node {

    public abstract Object execute(RecordObject record, String key);

    @Specialization
    Object exec(RecordObject record, String key, @Cached("create()") ReadIndexNode readIdx) {
      return readIdx.execute(record, record.keys.indexOf(key));
    }
  }

  @NodeInfo(shortName = "Record.WriteIndexNode")
  @GenerateUncached
  public abstract static class WriteIndexNode extends Node {

    public abstract void execute(RecordObject record, int idx, String key, Object value);

    @Specialization(limit = "3")
    void exec(
        RecordObject record,
        int idx,
        String key,
        Object value,
        @CachedLibrary("record.values") DynamicObjectLibrary valuesLibrary) {
      if (idx >= record.keys.size()) {
        record.keys.setSize(idx + 1);
      }
      record.keys.set(idx, key);
      record.invalidateDistinctKeys();
      valuesLibrary.put(record.values, idx, value);
    }
  }

  @NodeInfo(shortName = "Record.AddByKeyNode")
  @GenerateUncached
  public abstract static class AddByKeyNode extends Node {

    public abstract void execute(RecordObject record, String key, Object value);

    @CompilerDirectives.TruffleBoundary
    @Specialization(limit = "3")
    void exec(
        RecordObject record,
        String key,
        Object value,
        @CachedLibrary("record.values") DynamicObjectLibrary valuesLibrary) {
      valuesLibrary.put(
          record.values,
          record.keys.size(),
          value); // "key" to use in the dynamic object is the current index.
      record.keys.add(key); // the original key is added (possible duplicate)
      record.invalidateDistinctKeys();
    }
  }
}
