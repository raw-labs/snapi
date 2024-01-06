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
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.AbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations.EquiJoinComputeNext;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.sources.api.SourceContext;

@ExportLibrary(InteropLibrary.class)
public class EquiJoinCollection implements TruffleObject {
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

  public Object getGenerator() {
    return new AbstractGenerator(
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
