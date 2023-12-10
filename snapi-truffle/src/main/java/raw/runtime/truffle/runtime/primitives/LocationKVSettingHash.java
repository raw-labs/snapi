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

package raw.runtime.truffle.runtime.primitives;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.util.Objects;

import raw.runtime.truffle.runtime.generator.collection.AbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.ExpressionComputeNext;
import raw.runtime.truffle.runtime.list.StringList;

/* This class is used to export Location KVSettings (as a Hash) */

@ExportLibrary(InteropLibrary.class)
final class LocationKVSettingHash implements TruffleObject {

  private final java.util.Map<String, String> map = new java.util.HashMap<>();

  public LocationKVSettingHash(String[][] pairs) {
    for (String[] pair : pairs) {
      map.put(pair[0], pair[1]);
    }
  }

  @ExportMessage
  final boolean hasHashEntries() {
    return true;
  }

  @ExportMessage
  final long getHashSize() {
    return map.size();
  }

  @ExportMessage
  final boolean isHashEntryReadable(Object key) {
    if (Objects.requireNonNull(key) instanceof String stringKey) {
      return map.containsKey(stringKey);
    } else {
      return false;
    }
  }

  @ExportMessage
  final Object readHashValue(Object key) {
    if (Objects.requireNonNull(key) instanceof String stringKey) {
      return map.get(stringKey);
    } else {
      return null;
    }
  }

  @ExportMessage
  final Object getHashEntriesIterator() {
    return new AbstractGenerator(
        new ExpressionComputeNext(
            map.keySet().stream()
                .map(k -> new StringList(new String[] {k, map.get(k)}))
                .toArray()));
    //    return map.keySet().stream().map(k -> new Object[]{k, map.get(k)}).iterator();
  }
}
