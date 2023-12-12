package raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.sources;

import raw.runtime.truffle.runtime.exceptions.BreakException;

import static java.lang.Math.addExact;

public class IntRangeComputeNext {
  private final int end;
  private final int step;
  private int position;

  public IntRangeComputeNext(int start, int end, int step) {
    this.end = end;
    this.step = step;
    this.position = start - step;
  }

  public int next() {
    int current;
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
