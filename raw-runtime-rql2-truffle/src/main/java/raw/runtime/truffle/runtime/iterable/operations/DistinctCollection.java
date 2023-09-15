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
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.iterable.OffHeapDistinct;
import raw.runtime.truffle.runtime.operators.OperatorNodes;
import raw.runtime.truffle.runtime.operators.OperatorNodesFactory;

@ExportLibrary(IterableLibrary.class)
public final class DistinctCollection {

  final Object iterable;

  final RawLanguage language;

  final Rql2TypeWithProperties rowType;
  private final RuntimeContext context;

  public DistinctCollection(
      Object iterable, Rql2TypeWithProperties vType, RawLanguage language, RuntimeContext context) {
    this.iterable = iterable;
    this.language = language;
    this.rowType = vType;
    this.context = context;
  }

  @ExportMessage
  boolean isIterable() {
    return true;
  }

  private final OperatorNodes.CompareNode compare = OperatorNodesFactory.CompareNodeGen.create();

  private int compareKey(Object key1, Object key2) {
    return compare.execute(key1, key2);
  }

  @ExportMessage
  Object getGenerator(
      @CachedLibrary(limit = "5") IterableLibrary iterables,
      @CachedLibrary(limit = "5") GeneratorLibrary generators) {
    OffHeapDistinct index = new OffHeapDistinct(this::compareKey, rowType, language, context);
    Object generator = iterables.getGenerator(iterable);
    try {
      generators.init(generator);
      while (generators.hasNext(generator)) {
        Object next = generators.next(generator);
        index.put(next);
      }
    } finally {
      generators.close(generator);
    }
    return index.generator();
  }
}
