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

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.*;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.iterable.OffHeapCollectionGroupByKey;
import raw.runtime.truffle.runtime.operators.OperatorNodes;
import raw.sources.api.SourceContext;

@ExportLibrary(IterableLibrary.class)
@ExportLibrary(InteropLibrary.class)
public final class GroupByCollection implements TruffleObject {

  final Object iterable;
  final Object keyFun;

  final RawLanguage language;

  final Rql2TypeWithProperties keyType;
  final Rql2TypeWithProperties rowType;
  private final SourceContext context;

  public GroupByCollection(
      Object iterable,
      Object keyFun,
      Rql2TypeWithProperties kType,
      Rql2TypeWithProperties rowType,
      RawLanguage language,
      SourceContext context) {
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

  @ExportMessage
  Object getGenerator(
      @Cached OperatorNodes.CompareNode compare,
      @CachedLibrary("this.keyFun") InteropLibrary keyFunLib,
      @CachedLibrary("this.iterable") IterableLibrary iterables,
      @CachedLibrary(limit = "5") GeneratorLibrary generators) {
    OffHeapCollectionGroupByKey map =
        new OffHeapCollectionGroupByKey(compare::execute, keyType, rowType, language, context);
    Object inputGenerator = iterables.getGenerator(iterable);
    try {
      generators.init(inputGenerator);
      while (generators.hasNext(inputGenerator)) {
        Object v = generators.next(inputGenerator);
        Object key = keyFunLib.execute(keyFun, v);
        map.put(key, v);
      }
    } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
      throw new RawTruffleRuntimeException("failed to execute function");
    } finally {
      generators.close(inputGenerator);
    }
    return map.generator();
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
