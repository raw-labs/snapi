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

package raw.runtime.truffle.runtime.generator.collection.compute_next.operations;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.generator.collection.compute_next.ComputeNextLibrary;
import raw.runtime.truffle.tryable_nullable.TryableNullable;

@ExportLibrary(ComputeNextLibrary.class)
public class FilterComputeNext {

  final Object parent;
  final Object predicate;

  public FilterComputeNext(Object parent, Object predicate) {
    this.parent = parent;
    this.predicate = predicate;
  }

  @ExportMessage
  void init(@CachedLibrary("this.parent") GeneratorLibrary generators) {
    generators.init(parent);
  }

  @ExportMessage
  void close(@CachedLibrary("this.parent") GeneratorLibrary generators) {
    generators.close(parent);
  }

  @ExportMessage
  public boolean isComputeNext() {
    return true;
  }

  @ExportMessage
  Object computeNext(
      @CachedLibrary("this.predicate") InteropLibrary interops,
      @CachedLibrary("this.parent") GeneratorLibrary generators) {
    Object[] argumentValues = new Object[1];

    while (generators.hasNext(parent)) {
      Object v = generators.next(parent);
      argumentValues[0] = v;
      Boolean isPredicateTrue = null;
      try {
        isPredicateTrue =
            TryableNullable.handlePredicate(
                interops.execute(predicate, argumentValues), false);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      if (isPredicateTrue) {
        return v;
      }
    }
    throw new BreakException();
  }
}
