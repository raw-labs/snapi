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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.temporals.time_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.temporals.DateTimeFormatCache;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.ErrorObject;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.TimeObject;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@NodeInfo(shortName = "Time.Parse")
@NodeChild("str")
@NodeChild("format")
public abstract class TimeParseNode extends ExpressionNode {

  @Specialization
  @TruffleBoundary
  public Object parse(String str, String format) {
    try {
      DateTimeFormatter formatter = DateTimeFormatCache.get(format);
      return new TimeObject(LocalTime.parse(str, formatter));
    } catch (IllegalArgumentException ex) {
      return new ErrorObject("invalid time template: " + format);
    } catch (DateTimeParseException ex) {
      return new ErrorObject(
          String.format("string '%s' does not match time template '%s'", str, format));
    }
  }
}
