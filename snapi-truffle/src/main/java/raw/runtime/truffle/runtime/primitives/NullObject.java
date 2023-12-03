package raw.runtime.truffle.runtime.primitives;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

@ExportLibrary(InteropLibrary.class)
public final class NullObject implements TruffleObject {
  public static final NullObject INSTANCE = new NullObject();

  private NullObject() {}

  @ExportMessage
  boolean isNull() {
    return true;
  }

  @ExportMessage
  Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
    return this.toString();
  }

  @Override
  public String toString() {
    return "null";
  }
}
