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

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import java.io.InputStream;
import java.io.Reader;
import raw.client.utils.RawException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.sources.api.Encoding;
import raw.sources.api.SourceContext;
import raw.sources.bytestream.api.ByteStreamLocation;
import raw.sources.bytestream.api.ByteStreamLocationProvider;
import scala.util.Either;

public class TruffleInputStream {
  private final LocationObject locationObject;

  private final SourceContext context;

  public TruffleInputStream(LocationObject locationObject, SourceContext context) {
    this.context = context;
    this.locationObject = locationObject;
  }

  @TruffleBoundary
  public String getUrl() {
    return locationObject.getLocationDescription().url();
  }

  @TruffleBoundary
  public ByteStreamLocation getLocation() {
    try {
      return ByteStreamLocationProvider.build(locationObject.getLocationDescription(), context);
    } catch (RawException ex) {
      throw new RawTruffleRuntimeException(ex.getMessage(), ex, null);
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
      throw new RawTruffleRuntimeException(ex.getMessage(), ex, null);
    }
  }

  @TruffleBoundary
  public Reader getReader(String encoding) {
    try {
      return getLocation().getReader(toEncoding(encoding));
    } catch (RawException ex) {
      throw new RawTruffleRuntimeException(ex.getMessage(), ex, null);
    }
  }

  @TruffleBoundary
  private static Encoding toEncoding(String s) {
    Either<String, Encoding> r = Encoding.fromEncodingString(s);
    if (r.isRight()) {
      return r.right().get();
    } else {
      throw new RawTruffleRuntimeException(r.left().get());
    }
  }

  @TruffleBoundary
  public Reader getReader(Encoding encoding) {
    try {
      return getLocation().getReader(encoding);
    } catch (RawException ex) {
      throw new RawTruffleRuntimeException(ex.getMessage(), ex, null);
    }
  }
}
