package raw.runtime.truffle.runtime.generator.collection.compute_next.sources;

public class UnionComputeNext {
  private final Object[] inputs;
  private int index;
  private Object currentGenerator = null;

  public UnionComputeNext(Object[] inputs) {
    this.inputs = inputs;
    this.index = 0;
  }

  public void setCurrentGenerator(Object currentGenerator) {
    this.currentGenerator = currentGenerator;
  }

  public Object getCurrentGenerator() {
    return currentGenerator;
  }

  public boolean isTerminated() {
    return index >= inputs.length;
  }

  public Object getIterable() {
    return inputs[index];
  }

  public void incrementIndex() {
    index++;
  }
}
