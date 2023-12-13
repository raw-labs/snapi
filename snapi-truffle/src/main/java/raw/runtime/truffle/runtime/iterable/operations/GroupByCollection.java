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
public class GroupByCollection implements TruffleObject {
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

  public Object getIterable() {
    return iterable;
  }

  public Object getKeyFun() {
    return keyFun;
  }

  public RawLanguage getLanguage() {
    return language;
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
