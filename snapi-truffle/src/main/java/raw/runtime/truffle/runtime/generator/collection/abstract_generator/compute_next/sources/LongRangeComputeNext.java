package raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.sources;

import raw.runtime.truffle.runtime.exceptions.BreakException;

import static java.lang.Math.addExact;

public class LongRangeComputeNext {
  private final long end;
  private final long step;
  private long position;

  public LongRangeComputeNext(long start, long end, long step) {
    this.end = end;
    this.step = step;
    this.position = start - step;
  }

  public long next() {
    long current;
    try {
      current = addExact(position, step);
    } catch (ArithmeticException e) {
      throw new BreakException();
    }
    if (current >= end) {
      throw new BreakException();
    }
    position = current;
    return current;
  }
}
