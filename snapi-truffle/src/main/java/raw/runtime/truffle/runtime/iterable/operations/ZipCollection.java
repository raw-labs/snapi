package raw.runtime.truffle.runtime.iterable.operations;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodes;

@ExportLibrary(InteropLibrary.class)
public class ZipCollection implements TruffleObject {
  final Object parentIterable1;
  final Object parentIterable2;

  final RawLanguage language;

  public ZipCollection(Object iterable1, Object iterable2, RawLanguage language) {
    this.parentIterable1 = iterable1;
    this.parentIterable2 = iterable2;
    this.language = language;
  }

  public Object getParentIterable1() {
    return parentIterable1;
  }

  public Object getParentIterable2() {
    return parentIterable2;
  }

  public RawLanguage getLang() {
    return language;
  }

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
