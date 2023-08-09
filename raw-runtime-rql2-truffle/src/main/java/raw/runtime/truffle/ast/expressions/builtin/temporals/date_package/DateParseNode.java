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

package raw.runtime.truffle.ast.expressions.builtin.temporals.date_package;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.builtin.temporals.DateTimeFormatCache;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.primitives.DateObject;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;

@NodeInfo(shortName = "Date.Parse")
@NodeChild("str")
@NodeChild("format")
public abstract class DateParseNode extends ExpressionNode {

  @Specialization
  public Object parse(String str, String format) {
    try {
      DateTimeFormatter formatter = DateTimeFormatCache.get(format);
      return ObjectTryable.BuildSuccess(new DateObject(LocalDate.parse(str, formatter)));
    } catch (IllegalArgumentException ex) {
      return ObjectTryable.BuildFailure("invalid date template: " + format);
    } catch (DateTimeParseException ex) {
      return ObjectTryable.BuildFailure(
          String.format("string '%s' does not match date template '%s'", str, format));
    }
  }
}
