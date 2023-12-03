package raw.runtime.truffle.tryable_nullable;

import raw.runtime.truffle.runtime.primitives.NullObject;

public class Nullable {
  public static boolean isNull(Object value) {
    return value == NullObject.INSTANCE;
  }

  public static boolean isNotNull(Object value) {
    return value != NullObject.INSTANCE;
  }
}
