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

package com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.temporals.timestamp_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.temporals.DateTimeFormatCache;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.ErrorObject;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.TimestampObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@NodeInfo(shortName = "Timestamp.Parse")
@NodeChild("str")
@NodeChild("format")
public abstract class TimestampParseNode extends ExpressionNode {

  @Specialization
  @TruffleBoundary
  public Object parse(String str, String format) {
    try {
      DateTimeFormatter formatter = DateTimeFormatCache.get(format);
      return new TimestampObject(LocalDateTime.parse(str, formatter));
    } catch (IllegalArgumentException ex) {
      return new ErrorObject("invalid timestamp template: " + format);
    } catch (DateTimeParseException ex) {
      return new ErrorObject(
          String.format("string '%s' does not match timestamp template '%s'", str, format));
    }
  }
}
