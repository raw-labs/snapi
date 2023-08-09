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
import raw.runtime.truffle.runtime.generator.collection.CollectionAbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.compute_next.operations.JoinComputeNext;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;

@ExportLibrary(IterableLibrary.class)
public final class JoinCollection {

  final Object leftIterable;
  final Object rightIterable;
  final Closure predicate;
  final Closure remap;
  final Rql2TypeWithProperties rightType;
  final RuntimeContext context;
  final RawLanguage language;
  private final Boolean reshapeBeforePredicate;

  public JoinCollection(
      Object leftIterable,
      Object rightIterable,
      Closure remap,
      Closure predicate,
      Rql2TypeWithProperties rightType,
      Boolean reshapeBeforePredicate,
      RuntimeContext context,
      RawLanguage language) {
    this.leftIterable = leftIterable;
    this.rightIterable = rightIterable;
    this.remap = remap;
    this.predicate = predicate;
    this.rightType = rightType;
    this.context = context;
    this.language = language;
    this.reshapeBeforePredicate = reshapeBeforePredicate;
  }

  @ExportMessage
  boolean isIterable() {
    return true;
  }

  @ExportMessage
  Object getGenerator() {
    return new CollectionAbstractGenerator(
        new JoinComputeNext(
            leftIterable,
            rightIterable,
            remap,
            predicate,
            reshapeBeforePredicate,
            rightType,
            context,
            language));
  }
}
