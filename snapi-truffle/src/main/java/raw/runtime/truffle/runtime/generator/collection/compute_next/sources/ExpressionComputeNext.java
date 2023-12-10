package raw.runtime.truffle.runtime.generator.collection.compute_next.sources;

import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

public class ExpressionComputeNext {
  private final Object[] values;
  private int position;

  public ExpressionComputeNext(Object[] values) {
    this.values = values;
    this.position = 0;
  }

  public void incrementPosition() {
    position++;
  }

  public boolean isTerminated() {
    return position >= values.length;
  }

  public Object getCurrent() {
    return values[position];
  }

  public Object next() {
    if (this.isTerminated()) {
      throw new BreakException();
    }
    try {
      return this.getCurrent();
    } catch (RawTruffleRuntimeException e) {
      return new RawTruffleRuntimeException(e.getMessage());
    } finally {
      this.incrementPosition();
    }
  }
}
