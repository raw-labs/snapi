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

package raw.runtime.truffle.utils;

import com.oracle.truffle.api.CompilerDirectives;
import java.io.InputStream;
import java.io.Reader;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.sources.api.Encoding;
import raw.sources.bytestream.api.ByteStreamLocation;
import raw.sources.bytestream.api.ByteStreamLocationProvider;
import raw.utils.RawException;
import scala.util.Either;

public class TruffleInputStream {
  private final LocationObject locationObject;

  private final RuntimeContext context;

  public TruffleInputStream(LocationObject locationObject, RuntimeContext context) {
    this.context = context;
    this.locationObject = locationObject;
  }

  @CompilerDirectives.TruffleBoundary
  public String getUrl() {
    return locationObject.getLocationDescription().url();
  }

  @CompilerDirectives.TruffleBoundary
  public ByteStreamLocation getLocation() {
    try {
      return ByteStreamLocationProvider.build(
          locationObject.getLocationDescription(), context.sourceContext());
    } catch (RawException ex) {
      throw new RawTruffleRuntimeException(ex.getMessage());
    }
  }

  @CompilerDirectives.TruffleBoundary
  public boolean testAccess() {
    try {
      getLocation().testAccess();
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  @CompilerDirectives.TruffleBoundary
  public InputStream getInputStream() {
    try {
      return getLocation().getInputStream();
    } catch (RawException ex) {
      throw new RawTruffleRuntimeException(ex.getMessage());
    }
  }

  @CompilerDirectives.TruffleBoundary
  public Reader getReader(String encoding) {
    try {
      return getLocation().getReader(toEncoding(encoding));
    } catch (RawException ex) {
      throw new RawTruffleRuntimeException(ex.getMessage());
    }
  }

  @CompilerDirectives.TruffleBoundary
  private static Encoding toEncoding(String s) {
    Either<String, Encoding> r = Encoding.fromEncodingString(s);
    if (r.isRight()) {
      return r.right().get();
    } else {
      throw new RawTruffleRuntimeException(r.left().get());
    }
  }

  @CompilerDirectives.TruffleBoundary
  public Reader getReader(Encoding encoding) {
    try {
      return getLocation().getReader(encoding);
    } catch (RawException ex) {
      throw new RawTruffleRuntimeException(ex.getMessage());
    }
  }
}
