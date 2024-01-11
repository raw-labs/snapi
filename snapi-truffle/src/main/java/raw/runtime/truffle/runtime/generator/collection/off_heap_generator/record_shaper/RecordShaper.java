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

import raw.runtime.truffle.RawLanguage;

public class RecordShaper {
  private final RawLanguage language;

  private final boolean forList;

  public RecordShaper(RawLanguage language, boolean forList) {
    this.language = language;
    this.forList = forList;
  }

  public RawLanguage getLanguage() {
    return language;
  }

  public boolean forList() {
    return forList;
  }
}
