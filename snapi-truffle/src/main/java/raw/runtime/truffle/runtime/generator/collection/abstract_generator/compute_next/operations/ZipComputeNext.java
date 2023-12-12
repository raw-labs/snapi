package raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations;

import raw.runtime.truffle.RawLanguage;

public class ZipComputeNext {
  private final Object parent1;

  private final Object parent2;

  private final RawLanguage language;

  public ZipComputeNext(Object parent1, Object parent2, RawLanguage language) {
    this.parent1 = parent1;
    this.parent2 = parent2;
    this.language = language;
  }

  public Object getParent1() {
    return parent1;
  }

  public Object getParent2() {
    return parent2;
  }

  public RawLanguage getLanguage() {
    return language;
  }
}
