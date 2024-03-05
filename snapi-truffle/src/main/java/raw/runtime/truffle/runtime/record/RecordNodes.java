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
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

public class RecordNodes {
  @NodeInfo(shortName = "Record.ReadIndexNode")
  @GenerateUncached
  @GenerateInline
  public abstract static class ReadIndexNode extends Node {

    public abstract Object execute(Node node, RecordObject record, int idx);

    @Specialization(limit = "3")
    static Object exec(
        Node node,
        RecordObject record,
        int idx,
        @CachedLibrary("record") DynamicObjectLibrary valuesLibrary) {
      if (idx < 0 || idx >= record.keys.size()) {
        throw new RawTruffleInternalErrorException(InvalidArrayIndexException.create(idx));
      }
      return valuesLibrary.getOrDefault(record, idx, null);
    }
  }

  @NodeInfo(shortName = "Record.ReadByKeyNode")
  @GenerateUncached
  @GenerateInline
  public abstract static class ReadByKeyNode extends Node {

    public abstract Object execute(Node node, RecordObject record, String key);

    @Specialization
    static Object exec(
        Node node,
        RecordObject record,
        String key,
        @Bind("$node") Node thisNode,
        @Cached ReadIndexNode readIdx) {
      return readIdx.execute(thisNode, record, record.keys.indexOf(key));
    }
  }

  @NodeInfo(shortName = "Record.WriteIndexNode")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteIndexNode extends Node {

    public abstract void execute(Node node, RecordObject record, int idx, String key, Object value);

    @Specialization(limit = "3")
    static void exec(
        Node node,
        RecordObject record,
        int idx,
        String key,
        Object value,
        @CachedLibrary("record") DynamicObjectLibrary valuesLibrary) {
      if (idx >= record.keys.size()) {
        record.keys.setSize(idx + 1);
      }
      record.keys.set(idx, key);
      record.invalidateDistinctKeys();
      valuesLibrary.put(record, idx, value);
    }
  }

  @NodeInfo(shortName = "Record.AddByKeyNode")
  @GenerateUncached
  @GenerateInline
  public abstract static class AddByKeyNode extends Node {

    public abstract void execute(Node node, RecordObject record, String key, Object value);

    @Specialization(limit = "3")
    static void exec(
        Node node,
        RecordObject record,
        String key,
        Object value,
        @CachedLibrary("record") DynamicObjectLibrary valuesLibrary) {
      valuesLibrary.put(
          record,
          record.keys.size(),
          value); // "key" to use in the dynamic object is the current index.
      addKey(record, key); // the original key is added (possible duplicate)
      record.invalidateDistinctKeys();
    }

    @TruffleBoundary
    private static void addKey(RecordObject record, String key) {
      record.keys.add(key);
    }
  }
}
