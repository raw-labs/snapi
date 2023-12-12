package raw.runtime.truffle.runtime.iterable.sources;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.AbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.sources.IntRangeComputeNext;

@ExportLibrary(InteropLibrary.class)
public class IntRangeCollection implements TruffleObject {
  private final int start;
  private final int end;
  private final int step;

  public IntRangeCollection(int start, int end, int step) {
    this.start = start;
    this.end = end;
    this.step = step;
  }

  public AbstractGenerator getGenerator() {
    return new AbstractGenerator(new IntRangeComputeNext(start, end, step));
  }

  // InteropLibrary: Iterable
  @ExportMessage
  boolean hasIterator() {
    return true;
  }

  @ExportMessage
  Object getIterator(@Cached GeneratorNodes.GeneratorInitNode initNode) {
    Object generator = getGenerator();
    initNode.execute(generator);
    return generator;
  }
}
