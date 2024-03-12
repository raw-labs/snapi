package raw.runtime.truffle.runtime.record;

import com.oracle.truffle.api.staticobject.DefaultStaticProperty;
import com.oracle.truffle.api.staticobject.StaticShape;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

public class RecordShapeWithFields {
  public final DefaultStaticProperty[] fields;
  private final String[] keys;
  private final String[] distinctKeys;
  private final StaticShape<RecordStaticObjectFactory> shape;

  public RecordShapeWithFields(
      DefaultStaticProperty[] fields,
      String[] keys,
      String[] distinctKeys,
      StaticShape<RecordStaticObjectFactory> shape) {
    this.fields = fields;
    this.keys = keys;
    this.distinctKeys = distinctKeys;
    this.shape = shape;
  }

  public DefaultStaticProperty[] getFields() {
    return fields;
  }

  public String[] getKeys() {
    return keys;
  }

  public String[] getDistinctKeys() {
    return distinctKeys;
  }

  public StaticShape<RecordStaticObjectFactory> getShape() {
    return shape;
  }

  public DefaultStaticProperty getFieldByIndex(int index) {
    return fields[index];
  }

  public DefaultStaticProperty getFieldByKey(String key) {
    for (int i = 0; i < keys.length; i++) {
      if (keys[i].equals(key)) {
        return fields[i];
      }
    }
    throw new RawTruffleInternalErrorException("Field not found: " + key);
  }

  public DefaultStaticProperty getFieldByDistinctKey(String key) {
    for (int i = 0; i < distinctKeys.length; i++) {
      if (distinctKeys[i].equals(key)) {
        return fields[i];
      }
    }
    throw new RawTruffleInternalErrorException("Field not found: " + key);
  }

  public boolean hasFieldByKey(String key) {
    for (String k : keys) {
      if (k.equals(key)) {
        return true;
      }
    }
    throw new RawTruffleInternalErrorException("Field not found: " + key);
  }

  public boolean hasFieldByDistinctKey(String key) {
    for (String k : distinctKeys) {
      if (k.equals(key)) {
        return true;
      }
    }
    throw new RawTruffleInternalErrorException("Field not found: " + key);
  }
}
