package raw.runtime.truffle.runtime.iterable.sources;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.generator.collection.AbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.AbstractGeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.EmptyComputeNext;

@ExportLibrary(InteropLibrary.class)
public class EmptyCollection implements TruffleObject {

  private EmptyCollection() {}

  public static final EmptyCollection INSTANCE = new EmptyCollection();

  public AbstractGenerator getGenerator() {
    return new AbstractGenerator(EmptyComputeNext.INSTANCE);
  }

  // InteropLibrary: Iterable
  @ExportMessage
  boolean hasIterator() {
    return true;
  }

  @ExportMessage
  Object getIterator(@Cached AbstractGeneratorNodes.AbstractGeneratorInitNode initNode) {
    Object generator = getGenerator();
    initNode.execute(generator);
    return generator;
  }
}
