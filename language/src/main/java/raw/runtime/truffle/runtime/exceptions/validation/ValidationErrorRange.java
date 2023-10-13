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

package raw.runtime.truffle.runtime.exceptions.validation;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

@ExportLibrary(InteropLibrary.class)
public class ValidationErrorRange implements TruffleObject {

  private final ValidationErrorPosition begin;
  private final ValidationErrorPosition end;

  public ValidationErrorRange(ValidationErrorPosition begin, ValidationErrorPosition end) {
    this.begin = begin;
    this.end = end;
  }

  @ExportMessage
  public final boolean hasMembers() {
    return true;
  }

  @ExportMessage
  public final Object readMember(String member) {
    if (member.equals("begin")) return begin;
    else if (member.equals("end")) return end;
    else return null;
  }

  @ExportMessage
  final Object getMembers(boolean includeInternal) throws UnsupportedMessageException {
    return new String[] {"begin", "end"};
  }

  @ExportMessage
  final boolean isMemberReadable(String member) {
    return member.equals("begin") || member.equals("end");
  }
}
