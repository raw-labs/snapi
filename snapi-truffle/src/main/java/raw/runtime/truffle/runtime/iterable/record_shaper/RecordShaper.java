package raw.runtime.truffle.runtime.iterable.record_shaper;

import raw.runtime.truffle.RawLanguage;

public class RecordShaper {
  private final RawLanguage language;

  private final short type;

  public RecordShaper(RawLanguage language, short type) {
    this.language = language;
    this.type = type;
  }

  public RawLanguage getLanguage() {
    return language;
  }

  public short getType() {
    return type;
  }
}
