package raw.runtime.truffle.runtime.record;

import com.oracle.truffle.api.staticobject.DefaultStaticProperty;
import com.oracle.truffle.api.staticobject.StaticShape;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

public record RecordShapeWithFields(
    StaticRecordObjectField[] fields, StaticShape<RecordStaticObjectFactory> shape) {

  public StaticRecordObjectField getFieldByIndex(int index) {
    return fields[index];
  }

  public StaticRecordObjectField getFieldByKey(String key) {
    for (StaticRecordObjectField field : fields) {
      if (field.getKey().equals(key)) {
        return field;
      }
    }
    throw new RawTruffleInternalErrorException("Field not found: " + key);
  }

  public StaticRecordObjectField getFieldByDistinctKey(String key) {
    for (StaticRecordObjectField field : fields) {
      if (field.getDistinctKey().equals(key)) {
        return field;
      }
    }
    throw new RawTruffleInternalErrorException("Field not found: " + key);
  }

  public boolean hasFieldByKey(String key) {
    for (StaticRecordObjectField field : fields) {
      if (field.getKey().equals(key)) {
        return true;
      }
    }
    throw new RawTruffleInternalErrorException("Field not found: " + key);
  }

  public boolean hasFieldByDistinctKey(String key) {
    for (StaticRecordObjectField field : fields) {
      if (field.getDistinctKey().equals(key)) {
        return true;
      }
    }
    throw new RawTruffleInternalErrorException("Field not found: " + key);
  }
}
