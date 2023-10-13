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
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.math.BigDecimal;
import raw.runtime.truffle.RawLanguage;

@ExportLibrary(InteropLibrary.class)
public class DecimalObject implements TruffleObject {
  private final BigDecimal bigDecimal;

  public DecimalObject(BigDecimal bigDecimal) {
    this.bigDecimal = bigDecimal;
  }

  public BigDecimal getBigDecimal() {
    return bigDecimal;
  }

  @ExportMessage
  boolean hasLanguage() {
    return true;
  }

  @ExportMessage
  Class<? extends TruffleLanguage<?>> getLanguage() {
    return RawLanguage.class;
  }

  @ExportMessage
  @CompilerDirectives.TruffleBoundary
  Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
    return "Decimal";
  }

  @ExportMessage
  boolean isString() {
    return true;
  }

  @ExportMessage
  @CompilerDirectives.TruffleBoundary
  String asString() {
    return bigDecimal.toString();
  }
}
