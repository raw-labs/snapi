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

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidBufferOffsetException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.nio.ByteOrder;
import raw.compiler.rql2.api.LocationDescription;
import raw.compiler.rql2.api.LocationDescription$;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import com.rawlabs.utils.sources.api.Location;
import com.rawlabs.utils.sources.bytestream.api.ByteStreamLocation;
import com.rawlabs.utils.sources.bytestream.http.HttpByteStreamLocation;
import com.rawlabs.utils.sources.filesystem.api.FileSystemLocation;
import com.rawlabs.utils.sources.jdbc.api.JdbcServerLocation;

/**
 * Truffle object representing a location.
 *
 * <p>The location is represented in Interop as a serialized byte array. The public description is
 * represented in Interop as a string.
 */
@ExportLibrary(InteropLibrary.class)
public final class LocationObject implements TruffleObject {
  private final Location location;
  private final String publicDescription;
  private final LocationDescription locationDescription;
  private final byte[] byteArray;

  @TruffleBoundary
  public LocationObject(Location location, String publicDescription) {
    this.location = location;
    this.publicDescription = publicDescription;
    this.locationDescription = LocationDescription$.MODULE$.toLocationDescription(location);
    this.byteArray = LocationDescription$.MODULE$.serialize(this.locationDescription);
  }

  public Location getLocation() {
    return location;
  }

  @TruffleBoundary
  public String getPublicDescription() {
    return publicDescription;
  }

  public ByteStreamLocation getByteStreamLocation() {
    if (location instanceof ByteStreamLocation) {
      return (ByteStreamLocation) location;
    } else {
      throw new RawTruffleRuntimeException("not a byte stream location");
    }
  }

  public FileSystemLocation getFileSystemLocation() {
    if (location instanceof FileSystemLocation) {
      return (FileSystemLocation) location;
    } else {
      throw new RawTruffleRuntimeException("not a file system location");
    }
  }

  public JdbcServerLocation getJdbcServerLocation() {
    if (location instanceof JdbcServerLocation) {
      return (JdbcServerLocation) location;
    } else {
      throw new RawTruffleRuntimeException("not a database location");
    }
  }

  public HttpByteStreamLocation getHttpByteStreamLocation() {
    if (location instanceof HttpByteStreamLocation) {
      return (HttpByteStreamLocation) location;
    } else {
      throw new RawTruffleRuntimeException("not an HTTP location");
    }
  }

  public byte[] getBytes() {
    return byteArray;
  }

  @ExportMessage
  final boolean isString() {
    return true;
  }

  @ExportMessage
  final String asString() {
    return publicDescription;
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
