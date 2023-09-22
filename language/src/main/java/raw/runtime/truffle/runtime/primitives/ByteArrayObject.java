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
