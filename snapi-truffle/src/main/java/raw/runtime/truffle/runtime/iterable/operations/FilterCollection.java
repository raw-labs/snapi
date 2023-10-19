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

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.generator.collection.CollectionAbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.compute_next.operations.FilterComputeNext;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;

@ExportLibrary(IterableLibrary.class)
@ExportLibrary(InteropLibrary.class)
public final class FilterCollection implements TruffleObject {

  final Object parentIterable;
  final Closure predicate;

  public FilterCollection(Object iterable, Closure predicate) {
    this.parentIterable = iterable;
    this.predicate = predicate;
  }

  @ExportMessage
  boolean isIterable() {
    return true;
  }

  @ExportMessage
  Object getGenerator(@CachedLibrary("this.parentIterable") IterableLibrary iterables) {
    Object parentGenerator = iterables.getGenerator(parentIterable);
    return new CollectionAbstractGenerator(new FilterComputeNext(parentGenerator, predicate));
  }
  // InteropLibrary: Iterable

  @ExportMessage
  boolean hasIterator() {
    return true;
  }

  private final GeneratorLibrary generatorLibrary =
      GeneratorLibrary.getFactory().createDispatched(1);

  @ExportMessage
  Object getIterator(@CachedLibrary("this") IterableLibrary iterables) {
    Object generator = iterables.getGenerator(this);
    generatorLibrary.init(generator);
    return generator;
  }
}
