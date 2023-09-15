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
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.iterable.OffHeapCollectionGroupByKey;
import raw.runtime.truffle.runtime.operators.OperatorNodes;
import raw.runtime.truffle.runtime.operators.OperatorNodesFactory;

@ExportLibrary(IterableLibrary.class)
public final class GroupByCollection {

  final Object iterable;
  final Closure keyFun;

  final RawLanguage language;

  final Rql2TypeWithProperties keyType;
  final Rql2TypeWithProperties rowType;
  private final RuntimeContext context;

  public GroupByCollection(
      Object iterable,
      Closure keyFun,
      Rql2TypeWithProperties kType,
      Rql2TypeWithProperties rowType,
      RawLanguage language,
      RuntimeContext context) {
    this.iterable = iterable;
    this.keyFun = keyFun;
    this.language = language;
    this.keyType = kType;
    this.rowType = rowType;
    this.context = context;
  }

  @ExportMessage
  boolean isIterable() {
    return true;
  }

  private final OperatorNodes.CompareNode compare = OperatorNodesFactory.CompareNodeGen.create();

  @ExportMessage
  Object getGenerator(
      @CachedLibrary("this.iterable") IterableLibrary iterables,
      @CachedLibrary(limit = "5") GeneratorLibrary generators) {
    OffHeapCollectionGroupByKey map =
        new OffHeapCollectionGroupByKey(compare::execute, keyType, rowType, language, context);
    Object inputGenerator = iterables.getGenerator(iterable);
    try {
      generators.init(inputGenerator);
      while (generators.hasNext(inputGenerator)) {
        Object v = generators.next(inputGenerator);
        Object key = keyFun.call(v);
        map.put(key, v);
      }
    } finally {
      generators.close(inputGenerator);
    }
    return map.generator();
  }
}
