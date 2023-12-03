package raw.runtime.truffle.tryable_nullable;

import raw.runtime.truffle.runtime.primitives.ErrorObject;
import raw.runtime.truffle.runtime.primitives.NullObject;

public class TryableNullable {
  public static Boolean handlePredicate(Object value, boolean defaultValue) {
    if (value == null || value == NullObject.INSTANCE || value instanceof ErrorObject) {
      return defaultValue;
    }
    return (Boolean) value;
  }

  public static Object getOrElse(Object value, Object defaultValue) {
    if (value == null || value == NullObject.INSTANCE || value instanceof ErrorObject) {
      return defaultValue;
    }
    return value;
  }

  public static boolean isValue(Object value) {
    return Nullable.isNotNull(value) && Tryable.isSuccess(value);
  }
}
