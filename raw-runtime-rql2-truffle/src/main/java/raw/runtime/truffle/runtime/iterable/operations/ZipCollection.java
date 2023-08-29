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

package raw.runtime.truffle.runtime.iterable.operations;

import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.generator.collection.CollectionAbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.compute_next.operations.ZipComputeNext;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;

@ExportLibrary(IterableLibrary.class)
public class ZipCollection {
  final Object parentIterable1;
  final Object parentIterable2;

  final RawLanguage language;

  public ZipCollection(Object iterable1, Object iterable2, RawLanguage language) {
    this.parentIterable1 = iterable1;
    this.parentIterable2 = iterable2;
    this.language = language;
  }

  @ExportMessage
  boolean isIterable() {
    return true;
  }

  @ExportMessage
  Object getGenerator(
      @CachedLibrary("this.parentIterable1") IterableLibrary iterables1,
      @CachedLibrary("this.parentIterable2") IterableLibrary iterables2) {
    Object generator1 = iterables1.getGenerator(parentIterable1);
    Object generator2 = iterables2.getGenerator(parentIterable2);
    return new CollectionAbstractGenerator(new ZipComputeNext(generator1, generator2, language));
  }
}
