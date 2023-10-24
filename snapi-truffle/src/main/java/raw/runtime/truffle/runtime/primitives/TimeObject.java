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

import static raw.runtime.truffle.runtime.primitives.TruffleTemporalFormatter.TIME_FORMATTER;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.time.LocalTime;

@ExportLibrary(InteropLibrary.class)
public final class TimeObject implements TruffleObject {

  private final LocalTime time;

  public TimeObject(LocalTime time) {
    this.time = time;
  }

  public LocalTime getTime() {
    return time;
  }

  public String toString() {
    return time.format(TIME_FORMATTER);
  }

  @ExportMessage
  final boolean isTime() {
    return true;
  }

  @ExportMessage
  final LocalTime asTime() {
    return time;
  }
}
