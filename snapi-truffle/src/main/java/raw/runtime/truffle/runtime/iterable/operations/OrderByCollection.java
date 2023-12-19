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
public class OrderByCollection implements TruffleObject {
  final Object parentIterable;
  final Object[] keyFunctions;

  final int[] keyOrderings;
  final Rql2TypeWithProperties[] keyTypes;
  final Rql2TypeWithProperties rowType;
  private final RawLanguage language;
  private final SourceContext context;

  public OrderByCollection(
      Object iterable,
      Object[] keyFunctions,
      int[] keyOrderings,
      Rql2TypeWithProperties[] keyTypes,
      Rql2TypeWithProperties rowType,
      RawLanguage language,
      SourceContext context) {
    this.parentIterable = iterable;
    this.keyFunctions = keyFunctions;
    this.keyOrderings = keyOrderings;
    this.keyTypes = keyTypes;
    this.rowType = rowType;
    this.language = language;
    this.context = context;
  }

  public Object getParentIterable() {
    return parentIterable;
  }

  public Object[] getKeyFunctions() {
    return keyFunctions;
  }

  public int[] getKeyOrderings() {
    return keyOrderings;
  }

  public Rql2TypeWithProperties[] getKeyTypes() {
    return keyTypes;
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
