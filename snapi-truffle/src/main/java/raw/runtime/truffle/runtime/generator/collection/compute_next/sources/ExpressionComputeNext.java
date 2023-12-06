package raw.runtime.truffle.runtime.generator.collection.compute_next.sources;

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
}
