package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.record_shaper;

import raw.runtime.truffle.RawLanguage;

public class RecordShaper {
  private final RawLanguage language;

  private final boolean forList;

  public RecordShaper(RawLanguage language, boolean forList) {
    this.language = language;
    this.forList = forList;
  }

  public RawLanguage getLanguage() {
    return language;
  }

  public boolean forList() {
    return forList;
  }
}
