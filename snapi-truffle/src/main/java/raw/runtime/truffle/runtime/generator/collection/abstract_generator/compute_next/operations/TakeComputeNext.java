package raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations;

public class TakeComputeNext {
  private final Object parent;

  private final long takeCount;

  private long currentCount = 0;

  public TakeComputeNext(Object parent, long takeCount) {
    this.parent = parent;
    this.takeCount = takeCount;
  }

  public Object getParent() {
    return parent;
  }

  public long getTakeCount() {
    return takeCount;
  }

  public long getCurrentCount() {
    return currentCount;
  }

  public void incrementCurrentCount() {
    currentCount++;
  }
}
