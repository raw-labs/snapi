package raw.runtime.truffle.runtime.primitives;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

@ExportLibrary(InteropLibrary.class)
public class ErrorObject implements TruffleObject {
  private final String message;

  public ErrorObject(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  @ExportMessage
  public boolean isException() {
    return true;
  }

  @ExportMessage
  public RuntimeException throwException() {
    return new RawTruffleRuntimeException(message);
  }
}
