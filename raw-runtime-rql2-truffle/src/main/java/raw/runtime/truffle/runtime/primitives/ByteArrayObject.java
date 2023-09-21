package raw.runtime.truffle.runtime.primitives;

import com.oracle.truffle.api.interop.TruffleObject;

// A Wrapper object that is needed in order to store a byte array in a Record.

public class ByteArrayObject implements TruffleObject {

  private final byte[] byteArray;

  public ByteArrayObject(byte[] byteArray) {
    this.byteArray = byteArray;
  }

  public byte[] getByteArray() {
    return byteArray;
  }
}
