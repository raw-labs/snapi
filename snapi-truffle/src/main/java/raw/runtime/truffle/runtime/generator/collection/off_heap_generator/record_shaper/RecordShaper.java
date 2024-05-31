/*
 * Copyright 2024 RAW Labs S.A.
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

public class RecordShaper {
  private final boolean forList;

  public RecordShaper(boolean forList) {
    this.forList = forList;
  }

  public boolean forList() {
    return forList;
  }
}
