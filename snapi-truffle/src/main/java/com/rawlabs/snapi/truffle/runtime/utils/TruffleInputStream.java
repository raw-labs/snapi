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

package com.rawlabs.snapi.truffle.runtime.utils;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.LocationObject;
import com.rawlabs.utils.core.RawException;
import com.rawlabs.utils.sources.api.Encoding;
import com.rawlabs.utils.sources.bytestream.api.ByteStreamLocation;
import java.io.InputStream;
import java.io.Reader;
import scala.util.Either;

public class TruffleInputStream {
  private final LocationObject locationObject;

  public TruffleInputStream(LocationObject locationObject) {
    this.locationObject = locationObject;
  }

  @TruffleBoundary
  public String getPublicDescription() {
    return locationObject.getPublicDescription();
  }

  @TruffleBoundary
  public ByteStreamLocation getLocation() {
    try {
      return locationObject.getByteStreamLocation();
    } catch (RawException ex) {
      throw new TruffleRuntimeException(ex.getMessage(), ex, null);
    }
  }

  @TruffleBoundary
  public boolean testAccess() {
    try {
      getLocation().testAccess();
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  @TruffleBoundary
  public InputStream getInputStream() {
    try {
      return getLocation().getInputStream();
    } catch (RawException ex) {
      throw new TruffleRuntimeException(ex.getMessage(), ex, null);
    }
  }

  @TruffleBoundary
  public Reader getReader(String encoding) {
    try {
      return getLocation().getReader(toEncoding(encoding));
    } catch (RawException ex) {
      throw new TruffleRuntimeException(ex.getMessage(), ex, null);
    }
  }

  @TruffleBoundary
  private static Encoding toEncoding(String s) {
    Either<String, Encoding> r = Encoding.fromEncodingString(s);
    if (r.isRight()) {
      return r.right().get();
    } else {
      throw new TruffleRuntimeException(r.left().get());
    }
  }

  @TruffleBoundary
  public Reader getReader(Encoding encoding) {
    try {
      return getLocation().getReader(encoding);
    } catch (RawException ex) {
      throw new TruffleRuntimeException(ex.getMessage(), ex, null);
    }
  }
}
