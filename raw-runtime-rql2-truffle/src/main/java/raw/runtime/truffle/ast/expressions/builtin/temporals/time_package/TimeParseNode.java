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

package raw.runtime.truffle.ast.expressions.builtin.temporals.time_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.builtin.temporals.DateTimeFormatCache;
import raw.runtime.truffle.runtime.primitives.TimeObject;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;

@NodeInfo(shortName = "Time.Parse")
@NodeChild("str")
@NodeChild("format")
public abstract class TimeParseNode extends ExpressionNode {

  @Specialization
  @CompilerDirectives.TruffleBoundary
  public Object parse(String str, String format) {
    try {
      DateTimeFormatter formatter = DateTimeFormatCache.get(format);
      return ObjectTryable.BuildSuccess(new TimeObject(LocalTime.parse(str, formatter)));
    } catch (IllegalArgumentException ex) {
      return ObjectTryable.BuildFailure("invalid time template: " + format);
    } catch (DateTimeParseException ex) {
      return ObjectTryable.BuildFailure(
          String.format("string '%s' does not match time template '%s'", str, format));
    }
  }
}
