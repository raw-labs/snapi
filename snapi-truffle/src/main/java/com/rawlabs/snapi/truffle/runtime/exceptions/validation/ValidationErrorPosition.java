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

package com.rawlabs.snapi.truffle.runtime.exceptions.validation;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.rawlabs.snapi.truffle.runtime.list.StringList;

@ExportLibrary(InteropLibrary.class)
public class ValidationErrorPosition implements TruffleObject {

  private final int line;
  private final int column;

  public ValidationErrorPosition(int line, int column) {
    this.line = line;
    this.column = column;
  }

  @ExportMessage
  public final boolean hasMembers() {
    return true;
  }

  @ExportMessage
  public final Object readMember(String member) {
    if (member.equals("line")) return line;
    else if (member.equals("column")) return column;
    else return null;
  }

  @ExportMessage
  final Object getMembers(boolean includeInternal) throws UnsupportedMessageException {
    return new StringList(new String[] {"line", "column"});
  }

  @ExportMessage
  final boolean isMemberReadable(String member) {
    return member.equals("line") || member.equals("column");
  }
}
