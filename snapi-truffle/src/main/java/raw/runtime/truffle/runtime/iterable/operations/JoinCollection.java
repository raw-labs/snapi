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
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.AbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations.JoinComputeNext;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.sources.api.SourceContext;

@ExportLibrary(InteropLibrary.class)
public class JoinCollection implements TruffleObject {
  final Object leftIterable;
  final Object rightIterable;
  final Object predicate, remap;
  final Rql2TypeWithProperties rightType;
  final SourceContext context;
  final RawLanguage language;
  private final Boolean reshapeBeforePredicate;
  private final MaterializedFrame frame;
  private final int computeNextSlot;
  private final int shouldContinueSlot;
  private final int resultSlot;
  private final int generatorSlot;
  private final int outputBufferSlot;

  public JoinCollection(
      Object leftIterable,
      Object rightIterable,
      Object remap,
      Object predicate,
      Rql2TypeWithProperties rightType,
      Boolean reshapeBeforePredicate,
      SourceContext context,
      RawLanguage language,
      MaterializedFrame frame,
      int computeNextSlot,
      int shouldContinueSlot,
      int resultSlot,
      int generatorSlot,
      int outputBufferSlot) {
    this.leftIterable = leftIterable;
    this.rightIterable = rightIterable;
    this.remap = remap;
    this.predicate = predicate;
    this.rightType = rightType;
    this.context = context;
    this.language = language;
    this.reshapeBeforePredicate = reshapeBeforePredicate;
    this.frame = frame;
    this.computeNextSlot = computeNextSlot;
    this.shouldContinueSlot = shouldContinueSlot;
    this.resultSlot = resultSlot;
    this.generatorSlot = generatorSlot;
    this.outputBufferSlot = outputBufferSlot;
  }

  public Object getGenerator() {
    return new AbstractGenerator(
        new JoinComputeNext(
            leftIterable,
            rightIterable,
            remap,
            predicate,
            reshapeBeforePredicate,
            rightType,
            context,
            frame,
            computeNextSlot,
            shouldContinueSlot,
            resultSlot,
            generatorSlot,
            outputBufferSlot));
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
