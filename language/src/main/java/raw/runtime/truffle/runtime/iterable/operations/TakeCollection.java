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
import raw.runtime.truffle.runtime.generator.collection.CollectionAbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.compute_next.operations.TakeComputeNext;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;

@ExportLibrary(IterableLibrary.class)
public class TakeCollection {

  final Object parentIterable;

  private final long cachedCount;

  public TakeCollection(Object iterable, long takeItems) {
    this.parentIterable = iterable;
    this.cachedCount = takeItems;
  }

  @ExportMessage
  boolean isIterable() {
    return true;
  }

  @ExportMessage
  Object getGenerator(@CachedLibrary("this.parentIterable") IterableLibrary iterables) {
    Object generator = iterables.getGenerator(parentIterable);
    return new CollectionAbstractGenerator(new TakeComputeNext(generator, cachedCount));
  }
}