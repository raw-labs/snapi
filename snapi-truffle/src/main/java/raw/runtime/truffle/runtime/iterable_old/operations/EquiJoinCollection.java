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

package raw.runtime.truffle.runtime.iterable_old.operations;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.generator.collection_old.CollectionAbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection_old.compute_next.operations.EquiJoinComputeNext;
import raw.runtime.truffle.runtime.iterable_old.IterableLibrary;
import raw.sources.api.SourceContext;

@ExportLibrary(IterableLibrary.class)
@ExportLibrary(InteropLibrary.class)
public final class EquiJoinCollection implements TruffleObject {

  final Object leftIterable, rightIterable;
  final Closure leftKeyF, rightKeyF;
  final Rql2TypeWithProperties leftRowType, rightRowType;
  final Rql2TypeWithProperties keyType;
  final Closure reshapeFun;
  private final RawLanguage language;
  private final SourceContext context;

  public EquiJoinCollection(
      Object leftIterable,
      Closure leftKeyF,
      Rql2TypeWithProperties leftRowType,
      Object rightIterable,
      Closure rightKeyF,
      Rql2TypeWithProperties rightRowType,
      Rql2TypeWithProperties keyType,
      Closure reshapeFun,
      RawLanguage language,
      SourceContext context) {
    this.leftIterable = leftIterable;
    this.leftKeyF = leftKeyF;
    this.leftRowType = leftRowType;
    this.rightIterable = rightIterable;
    this.rightKeyF = rightKeyF;
    this.rightRowType = rightRowType;
    this.keyType = keyType;
    this.reshapeFun = reshapeFun;
    this.language = language;
    this.context = context;
  }

  @ExportMessage
  boolean isIterable() {
    return true;
  }

  @ExportMessage
  Object getGenerator() {
    return new CollectionAbstractGenerator(
        new EquiJoinComputeNext(
            leftIterable,
            leftKeyF,
            leftRowType,
            rightIterable,
            rightKeyF,
            rightRowType,
            keyType,
            reshapeFun,
            language,
            context));
  }

  // InteropLibrary: Iterable

  @ExportMessage
  final boolean hasIterator() {
    return true;
  }

  private final GeneratorLibrary generatorLibrary =
      GeneratorLibrary.getFactory().createDispatched(1);

  @ExportMessage
  final Object getIterator(@CachedLibrary("this") IterableLibrary iterables) {
    Object generator = iterables.getGenerator(this);
    generatorLibrary.init(generator);
    return generator;
  }
}
