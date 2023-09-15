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
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.ast.tryable_nullable.TryableNullableNodes;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.generator.collection.compute_next.ComputeNextLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.iterable.operations.EmptyCollection;

@ExportLibrary(ComputeNextLibrary.class)
public class UnnestComputeNext {
  final Object parent;
  final Closure transform;

  Object currentGenerator = null;

  public UnnestComputeNext(Object parent, Closure transform) {
    this.parent = parent;
    this.transform = transform;
  }

  @ExportMessage
  void init(@CachedLibrary("this.parent") GeneratorLibrary generators) {
    generators.init(parent);
  }

  @ExportMessage
  void close(@CachedLibrary("this.parent") GeneratorLibrary generators) {
    generators.close(parent);
    if (currentGenerator != null) {
      generators.close(currentGenerator);
    }
  }

  @ExportMessage
  public boolean isComputeNext() {
    return true;
  }

  private final Object empty =
      new EmptyCollection(); // the empty collection to return when the function result is
  // null/error

  @ExportMessage
  Object computeNext(
      @Cached("create()") TryableNullableNodes.GetOrElseNode getOrElse,
      @CachedLibrary(limit = "3") GeneratorLibrary generators,
      @CachedLibrary(limit = "5") IterableLibrary iterables) {
    Object next = null;

    while (next == null) {
      if (currentGenerator == null) {
        if (!generators.hasNext(parent)) {
          throw new BreakException();
        }
        Object functionResult = transform.call(generators.next(parent));
        // the function result could be tryable/nullable. If error/null,
        // we replace it by an empty collection.
        Object iterable = getOrElse.execute(functionResult, empty);
        currentGenerator = iterables.getGenerator(iterable);
        generators.init(currentGenerator);
      }
      if (generators.hasNext(currentGenerator)) {
        next = generators.next(currentGenerator);
      } else {
        generators.close(currentGenerator);
        currentGenerator = null;
      }
    }
    return next;
  }
}
