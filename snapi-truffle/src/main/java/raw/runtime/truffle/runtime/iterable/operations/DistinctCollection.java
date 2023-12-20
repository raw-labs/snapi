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
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.sources.api.SourceContext;

@ExportLibrary(InteropLibrary.class)
public class DistinctCollection implements TruffleObject {
  final Object iterable;

  final RawLanguage language;

  final Rql2TypeWithProperties rowType;
  private final SourceContext context;

  public DistinctCollection(
      Object iterable, Rql2TypeWithProperties vType, RawLanguage language, SourceContext context) {
    this.iterable = iterable;
    this.language = language;
    this.rowType = vType;
    this.context = context;
  }

  public Object getIterable() {
    return iterable;
  }

  public Rql2TypeWithProperties getRowType() {
    return rowType;
  }

  public SourceContext getContext() {
    return context;
  }

  public RawLanguage getLang() {
    return language;
  }

  // InteropLibrary: Iterable

  @ExportMessage
  boolean hasIterator() {
    return true;
  }

  @ExportMessage
  Object getIterator(
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode initNode) {
    Object generator = getGeneratorNode.execute(this);
    initNode.execute(generator);
    return generator;
  }
}
