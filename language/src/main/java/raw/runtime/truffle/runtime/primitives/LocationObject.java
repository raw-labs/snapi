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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.sources.api.*;
import scala.collection.JavaConverters;
import scala.collection.immutable.HashMap;
import scala.collection.immutable.Map;
import scala.collection.JavaConverters;

@ExportLibrary(InteropLibrary.class)
public class LocationObject implements TruffleObject {
  private final LocationDescription locationDescription;

  @CompilerDirectives.TruffleBoundary
  public LocationObject(String url) {
    this.locationDescription = new LocationDescription(url, new HashMap<>());
  }

  @CompilerDirectives.TruffleBoundary
  public LocationObject(String url, Map<LocationSettingKey, LocationSettingValue> params) {
    this.locationDescription = new LocationDescription(url, params);
  }

  @CompilerDirectives.TruffleBoundary
  public LocationObject(LocationDescription locationDescription) {
    this.locationDescription = locationDescription;
  }

  public LocationDescription getLocationDescription() {
    return locationDescription;
  }

  @ExportMessage
  final boolean isString() {
    return true;
  }

  @ExportMessage
  final String asString() {
    return locationDescription.url();
  }

  @ExportMessage
  final boolean hasMembers() {
    return true;
  }

  @ExportMessage
  final String[] getMembers(boolean includeInternal) {
    return (String[])
        JavaConverters.asJavaCollection(locationDescription.settings().keys()).stream().map(LocationSettingKey::key).toArray();
  }

  @ExportMessage
  final boolean isMemberReadable(String member) {
    return locationDescription.settings().keySet().contains(new LocationSettingKey(member));
  }

  @ExportMessage
  final Object readMember(String member) {
    return switch (locationDescription.settings().get(new LocationSettingKey(member)).get()) {
      case LocationIntSetting v -> v.value();
      case LocationStringSetting v -> v.value();
      case LocationBinarySetting v -> v.value();
      case LocationBooleanSetting v -> v.value();
      case LocationDurationSetting v -> v.value();
      case LocationKVSetting v -> JavaConverters.asJavaCollectionConverter(v.map());
      case LocationIntArraySetting v -> v.value();
      default -> throw new RawTruffleInternalErrorException();
    };
  }
}
