package raw.runtime.truffle.runtime.iterable.operations;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.library.ExportMessage;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.AbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations.JoinComputeNext;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.sources.api.SourceContext;

public class JoinCollection {
  final Object leftIterable;
  final Object rightIterable;
  final Closure predicate;
  final Closure remap;
  final Rql2TypeWithProperties rightType;
  final SourceContext context;
  final RawLanguage language;
  private final Boolean reshapeBeforePredicate;

  public JoinCollection(
      Object leftIterable,
      Object rightIterable,
      Closure remap,
      Closure predicate,
      Rql2TypeWithProperties rightType,
      Boolean reshapeBeforePredicate,
      SourceContext context,
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
            language));
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
