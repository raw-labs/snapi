package raw.runtime.truffle.runtime.generator.collection.compute_next.operations;

public class UnnestComputeNext {
  final Object parent;
  final Object transform;

  Object currentGenerator = null;

  public UnnestComputeNext(Object parent, Object transform) {
    this.parent = parent;
    this.transform = transform;
  }

  public void setCurrentGenerator(Object currentGenerator) {
    this.currentGenerator = currentGenerator;
  }

  public Object getParent() {
    return parent;
  }

  public Object getTransform() {
    return transform;
  }

  public Object getCurrentGenerator() {
    return currentGenerator;
  }
}
