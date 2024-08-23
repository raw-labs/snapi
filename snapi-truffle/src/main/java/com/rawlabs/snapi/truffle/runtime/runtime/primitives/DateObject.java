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

package com.rawlabs.snapi.truffle.runtime.runtime.primitives;

import static com.rawlabs.snapi.truffle.runtime.runtime.primitives.TruffleTemporalFormatter.DATE_FORMATTER;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.rawlabs.snapi.truffle.runtime.Rql2Language;
import java.time.LocalDate;

@ExportLibrary(InteropLibrary.class)
public final class DateObject implements TruffleObject {

  private final LocalDate date;

  public DateObject(LocalDate date) {
    this.date = date;
  }

  public LocalDate getDate() {
    return date;
  }

  public String toString() {
    return date.format(DATE_FORMATTER);
  }

  @ExportMessage
  boolean isDate() {
    return true;
  }

  @ExportMessage
  LocalDate asDate() {
    return date;
  }

  @ExportMessage
  boolean hasLanguage() {
    return true;
  }

  @ExportMessage
  Class<? extends TruffleLanguage<?>> getLanguage() {
    return Rql2Language.class;
  }

  @ExportMessage
  @TruffleBoundary
  Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
    return "Date";
  }
}
