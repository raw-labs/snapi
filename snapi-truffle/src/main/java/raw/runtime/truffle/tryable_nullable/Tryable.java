package raw.runtime.truffle.tryable_nullable;

import raw.runtime.truffle.runtime.primitives.ErrorObject;

public class Tryable {
  public static boolean isFailure(Object value) {
    return value instanceof ErrorObject;
  }

  public static boolean isSuccess(Object value) {
    return !(value instanceof ErrorObject);
  }

  public static String getFailure(Object value) {
    if (!isFailure(value)) throw new RuntimeException("not a failure");
    return ((ErrorObject) value).getMessage();
  }

}
