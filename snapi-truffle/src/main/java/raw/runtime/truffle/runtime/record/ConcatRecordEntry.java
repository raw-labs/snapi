package raw.runtime.truffle.runtime.record;

public class ConcatRecordEntry {
  int objectIndex;
  String key;
  String distinctKey;
  boolean deleted;

  public ConcatRecordEntry(int objectIndex, String key, String distinctKey, boolean deleted) {
    this.objectIndex = objectIndex;
    this.key = key;
    this.distinctKey = distinctKey;
    this.deleted = deleted;
  }

  public ConcatRecordEntry(int objectIndex, String key) {
    this(objectIndex, key, key, false);
  }

  public int getObjectIndex() {
    return objectIndex;
  }

  public String getKey() {
    return key;
  }

  public String getDistinctKey() {
    return distinctKey;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
}
