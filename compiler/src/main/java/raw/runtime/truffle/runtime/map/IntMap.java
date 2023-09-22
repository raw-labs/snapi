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

package raw.runtime.truffle.runtime.map;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.util.HashMap;
import raw.runtime.truffle.runtime.record.RecordObject;

@ExportLibrary(MapLibrary.class)
public final class IntMap {

  private final HashMap<Integer, RecordObject> map = new HashMap<>();

  @ExportMessage
  static class Put {
    @Specialization
    @TruffleBoundary
    static void doPut(IntMap receiver, Integer key, RecordObject value) {
      receiver.map.put(key, value);
    }
  }

  @ExportMessage
  static class Get {
    @Specialization
    @TruffleBoundary
    static Object doGet(IntMap receiver, Integer key) {
      return receiver.map.get(key);
    }
  }
}
