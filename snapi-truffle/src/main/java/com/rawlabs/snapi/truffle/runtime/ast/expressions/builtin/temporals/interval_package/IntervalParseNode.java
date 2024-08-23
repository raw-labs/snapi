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

package com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.temporals.interval_package;

import static com.rawlabs.snapi.truffle.runtime.boundary.TruffleBoundaries.parseInt;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.boundary.TruffleBoundaries;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.ErrorObject;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.IntervalObject;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NodeInfo(shortName = "Interval.Parse")
@NodeChild("format")
@ImportStatic(TruffleBoundaries.class)
public abstract class IntervalParseNode extends ExpressionNode {

  private int intOrDefault(String toParse) {
    try {
      return parseInt(toParse);
    } catch (NumberFormatException ex) {
      return 0;
    }
  }

  @Specialization
  @TruffleBoundary
  public Object parse(String format) {
    try {
      Pattern pattern =
          Pattern.compile(
              "P(?:(-?\\d+)Y)?(?:(-?\\d+)M)?(?:(-?\\d+)W)?(?:(-?\\d+)D)?T?(?:(-?\\d+)H)?(?:(-?\\d+)M)?(?:(-?\\d+)S|(-?\\d+)\\.(\\d+)S)?");
      Matcher matcher = pattern.matcher(format);

      // Legacy logic from scala compiler
      if (matcher.find()) {
        String y = matcher.group(1);
        String m = matcher.group(2);
        String w = matcher.group(3);
        String d = matcher.group(4);
        String h = matcher.group(5);
        String mi = matcher.group(6);
        String s1 = matcher.group(7);
        String s2 = matcher.group(8);
        String mil = matcher.group(9);

        int seconds;
        int millis;
        if (s1 == null && s2 == null) {
          seconds = 0;
          millis = 0;
        } else if (s1 != null) {
          seconds = parseInt(s1);
          millis = 0;
        } else {
          int milliseconds;
          // if the length is smaller than 3 we have to add a multiplier
          if (mil.length() < 3) {
            int multiplier = 1;
            for (int i = 0; i <= 3 - mil.length(); i++) {
              multiplier *= 10;
            }
            milliseconds = parseInt(mil) * multiplier;
          } else {
            milliseconds = parseInt(mil.substring(0, 3));
          }
          long parsedSeconds = parseInt(s2);

          seconds = parseInt(s2);
          if (parsedSeconds >= 0) {
            millis = milliseconds;
          } else {
            millis = -milliseconds;
          }
        }
        return new IntervalObject(
            this.intOrDefault(y),
            this.intOrDefault(m),
            this.intOrDefault(w),
            this.intOrDefault(d),
            this.intOrDefault(h),
            this.intOrDefault(mi),
            seconds,
            millis);
      } else {
        throw new ParseException("Couldn't parse interval", 0);
      }
    } catch (ParseException e) {
      return new ErrorObject(e.getMessage());
    }
  }
}
