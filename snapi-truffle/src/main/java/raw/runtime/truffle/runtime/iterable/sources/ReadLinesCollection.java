package raw.runtime.truffle.runtime.iterable.sources;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.generator.collection.AbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.AbstractGeneratorNodes;
import raw.runtime.truffle.utils.TruffleCharInputStream;
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.ReadLinesComputeNext;

@ExportLibrary(InteropLibrary.class)
public class ReadLinesCollection implements TruffleObject {
  private final TruffleCharInputStream stream;

  public ReadLinesCollection(TruffleCharInputStream stream) {
    this.stream = stream;
  }

  public AbstractGenerator getGenerator() {
    return new AbstractGenerator(new ReadLinesComputeNext(stream));
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
