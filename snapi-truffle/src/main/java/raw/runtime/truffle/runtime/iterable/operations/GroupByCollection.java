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

import com.oracle.truffle.api.dsl.Bind;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.sources.api.SourceContext;

@ExportLibrary(InteropLibrary.class)
public class GroupByCollection implements TruffleObject {
  final Object iterable;
  final Closure keyFun;

  final RawLanguage language;

  final Rql2TypeWithProperties keyType;
  final Rql2TypeWithProperties rowType;
  private final SourceContext context;

  public GroupByCollection(
      Object iterable,
      Closure keyFun,
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

  public Object getIterable() {
    return iterable;
  }

  public Closure getKeyFun() {
    return keyFun;
  }

  public Rql2TypeWithProperties getKeyType() {
    return keyType;
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
      @Bind("$node") Node thisNode,
      @Cached(inline = true) IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) GeneratorNodes.GeneratorInitNode initNode) {
    Object generator = getGeneratorNode.execute(thisNode, this);
    initNode.execute(thisNode, generator);
    return generator;
  }
}
