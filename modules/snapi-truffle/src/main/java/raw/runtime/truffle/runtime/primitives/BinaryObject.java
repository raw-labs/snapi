/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.runtime.truffle.runtime.primitives;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidBufferOffsetException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.nio.ByteOrder;

@ExportLibrary(InteropLibrary.class)
public final class BinaryObject implements TruffleObject {

  private final byte[] byteArray;

  public BinaryObject(byte[] byteArray) {
    this.byteArray = byteArray;
  }

  public byte[] getBytes() {
    return byteArray;
  }

  @ExportMessage
  boolean hasArrayElements() {
    return true;
  }

  @ExportMessage
  final long getArraySize() {
    return byteArray.length;
  }

  @ExportMessage
  final byte readArrayElement(long index) throws ArrayIndexOutOfBoundsException {
    return byteArray[(int) index];
  }

  @ExportMessage
  final boolean isArrayElementReadable(long index) {
    return index >= 0 && index < byteArray.length;
  }

  @ExportMessage
  final boolean hasBufferElements() {
    return true;
  }

  @ExportMessage
  final long getBufferSize() {
    return byteArray.length;
  }

  @ExportMessage
  final byte readBufferByte(long byteOffset) throws InvalidBufferOffsetException {
    int idx = (int) byteOffset;
    if (idx < 0 || idx >= byteArray.length) {
      throw InvalidBufferOffsetException.create(idx, 1);
    }
    return byteArray[idx];
  }

  @ExportMessage
  final short readBufferShort(ByteOrder order, long byteOffset)
      throws UnsupportedMessageException, InvalidBufferOffsetException {
    throw new UnsupportedOperationException();
  }

  @ExportMessage
  final int readBufferInt(ByteOrder order, long byteOffset)
      throws UnsupportedMessageException, InvalidBufferOffsetException {
    throw new UnsupportedOperationException();
  }

  @ExportMessage
  final long readBufferLong(ByteOrder order, long byteOffset)
      throws UnsupportedMessageException, InvalidBufferOffsetException {
    throw new UnsupportedOperationException();
  }

  @ExportMessage
  final float readBufferFloat(ByteOrder order, long byteOffset)
      throws UnsupportedMessageException, InvalidBufferOffsetException {
    throw new UnsupportedOperationException();
  }

  @ExportMessage
  final double readBufferDouble(ByteOrder order, long byteOffset)
      throws UnsupportedMessageException, InvalidBufferOffsetException {
    throw new UnsupportedOperationException();
  }
}
