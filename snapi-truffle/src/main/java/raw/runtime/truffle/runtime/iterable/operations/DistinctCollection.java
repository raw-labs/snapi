package raw.runtime.truffle.runtime.iterable.operations;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
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

  public RawLanguage getLanguage() {
    return language;
  }

  public Rql2TypeWithProperties getRowType() {
    return rowType;
  }

  public SourceContext getContext() {
    return context;
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
