package raw.runtime.truffle.runtime.iterable.sources;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.generator.collection.AbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.AbstractGeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.LongRangeComputeNext;

@ExportLibrary(InteropLibrary.class)
public class LongRangeCollection implements TruffleObject {
  private final long start;
  private final long end;
  private final long step;

  public LongRangeCollection(long start, long end, long step) {
    this.start = start;
    this.end = end;
    this.step = step;
  }

  public AbstractGenerator getGenerator() {
    return new AbstractGenerator(new LongRangeComputeNext(start, end, step));
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
