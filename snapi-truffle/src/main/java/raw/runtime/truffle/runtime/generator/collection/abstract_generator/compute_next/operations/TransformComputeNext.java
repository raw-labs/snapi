package raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations;

public class TransformComputeNext {
  final Object parent;
  final Object transform;

  public TransformComputeNext(Object parent, Object transform) {
    this.parent = parent;
    this.transform = transform;
  }

  public Object getParent() {
    return parent;
  }

  public Object getTransform() {
    return transform;
  }
}
