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

package raw.runtime.truffle.runtime.option;

import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

@ExportLibrary(OptionLibrary.class)
public final class EmptyOption implements TruffleObject {

  public EmptyOption() {}

  @ExportMessage
  boolean isOption() {
    return true;
  }

  @ExportMessage
  public Object get() {
    throw new AssertionError("Calling get() on EmptyOption.");
  }

  @ExportMessage
  public void set(Object value) {
    throw new AssertionError("Calling set() on EmptyOption.");
  }

  @ExportMessage
  public boolean isDefined() {
    return false;
  }
}
