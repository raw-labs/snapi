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

package raw.runtime.truffle.ast.expressions.builtin.temporals.interval_package;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import raw.runtime.truffle.boundary.RawTruffleBoundaries;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.primitives.IntervalObject;

public class IntervalNodes {

  @NodeInfo(shortName = "Interval.FromDuration")
  @GenerateUncached
  @GenerateInline
  public abstract static class IntervalFromDurationNode extends Node {

    public abstract IntervalObject execute(Node node, Duration duration);

    @Specialization
    static IntervalObject countZero(
        Node node,
        Duration duration,
        @Bind("$node") Node thisNode,
        @Cached IntervalBuildNode buildNode) {
      return buildNode.execute(thisNode, 0, duration.toMillis());
    }
  }

  @NodeInfo(shortName = "Interval.FromDuration")
  @GenerateUncached
  @GenerateInline
  public abstract static class IntervalBuildNode extends Node {

    public abstract IntervalObject execute(Node node, long months, long millis);

    @Specialization
    static IntervalObject build(Node node, long inMonths, long inMillis) {
      long rest;
      int years = (int) (inMonths / 12);
      int months = (int) (inMonths % 12);
      int weeks = 0;
      int days = (int) (inMillis / (24 * 3600000));

      rest = inMillis % (24 * 3600000);
      int hours = (int) (rest / 3600000);

      rest %= 3600000;
      int minutes = (int) (rest / 60000);

      rest %= 60000;
      int seconds = (int) (rest / 1000);

      int millis = (int) (rest % 1000);

      return new IntervalObject(years, months, weeks, days, hours, minutes, seconds, millis);
    }
  }

  @NodeInfo(shortName = "Interval.FromDuration")
  @GenerateUncached
  @GenerateInline
  public abstract static class IntervalBuildFromStringNode extends Node {

    public abstract IntervalObject execute(Node node, String interval);

    private static final Pattern pattern =
        Pattern.compile(
            "^P(?:(\\d+)Y)?(?:(\\d+)M)?(?:(\\d+)W)?(?:(\\d+)D)?(?:T(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)(?:\\.(\\d{1,3}))?S)?)?$");

    @Specialization
    static IntervalObject build(Node node, String interval) {

      Matcher matcher = pattern.matcher(interval);

      int seconds;
      int years;
      int months;
      int weeks;
      int days;
      int hours;
      int minutes;
      int millis;

      if (matcher.matches()) {
        String matchYears = matcher.group(1);
        years = matchYears == null ? 0 : RawTruffleBoundaries.parseInt(matchYears);

        String matchMonths = matcher.group(2);
        months = matchMonths == null ? 0 : RawTruffleBoundaries.parseInt(matchMonths);

        String matchWeeks = matcher.group(3);
        weeks = matchWeeks == null ? 0 : RawTruffleBoundaries.parseInt(matchWeeks);

        String matchDays = matcher.group(4);
        days = matchDays == null ? 0 : RawTruffleBoundaries.parseInt(matchDays);

        String matchHours = matcher.group(5);
        hours = matchHours == null ? 0 : RawTruffleBoundaries.parseInt(matchHours);

        String matchMinutes = matcher.group(6);
        minutes = matchMinutes == null ? 0 : RawTruffleBoundaries.parseInt(matchMinutes);

        String s1 = matcher.group(7);
        String s2 = matcher.group(8);
        String millisStr = matcher.group(9);

        if (s1 == null && s2 == null) {
          seconds = 0;
          millis = 0;
        } else if (s1 != null) {
          seconds = RawTruffleBoundaries.parseInt(s1);
          millis = 0;
        } else {
          millis = RawTruffleBoundaries.parseInt(millisStr);
          // milliseconds will have the same sign as the seconds
          if (millisStr.length() < 3) {
            int multiplier = 1;
            for (int i = 1; i < (3 - millisStr.length() + 1); i++) {
              multiplier = multiplier * 10;
              millis = RawTruffleBoundaries.parseInt(millisStr) * multiplier;
            }
          } else {
            millis = RawTruffleBoundaries.parseInt(millisStr.substring(0, 3));
          }

          seconds = RawTruffleBoundaries.parseInt(s2);
          if (seconds < 0) {
            millis = -millis;
          }
        }

        return new IntervalObject(years, months, weeks, days, hours, minutes, seconds, millis);
      } else {
        throw new RawTruffleRuntimeException(
            String.format("could not parse interval from string '%s'", interval));
      }
    }
  }

  @NodeInfo(shortName = "Interval.Normalize")
  @GenerateUncached
  @GenerateInline
  public abstract static class IntervalNormalizeNode extends Node {

    public abstract IntervalObject execute(
        Node node,
        int years,
        int months,
        int weeks,
        int days,
        int hours,
        int minutes,
        int seconds,
        int millis);

    @Specialization
    static IntervalObject normalize(
        Node node,
        int years,
        int months,
        int weeks,
        int days,
        int hours,
        int minutes,
        int seconds,
        int millis,
        @Bind("$node") Node thisNode,
        @Cached IntervalBuildNode buildNode) {
      long totalMonths = 12 * (long) years + (long) months;
      long totalDays = 7 * (long) weeks + (long) days;

      long totalMillis =
          24 * 3600000 * totalDays + 3600000L * hours + 60000L * minutes + 1000L * seconds + millis;
      return buildNode.execute(thisNode, totalMonths, totalMillis);
    }
  }

  @NodeInfo(shortName = "Interval.ToMillis")
  @GenerateUncached
  @GenerateInline
  public abstract static class IntervalToMillisStaticNode extends Node {

    public abstract long execute(Node node, IntervalObject interval);

    @Specialization
    static long toMillis(Node node, IntervalObject interval) {
      double yearsInDays = 365.25 * interval.getYears();
      double monthsInDays = (365.25 / 12) * interval.getMonths();

      double totalDays = yearsInDays + monthsInDays + 7 * interval.getWeeks() + interval.getDays();

      return 24 * 3600000 * (long) totalDays
          + 3600000 * (long) interval.getHours()
          + 60000 * (long) interval.getMinutes()
          + 1000 * (long) interval.getSeconds()
          + interval.getMillis();
    }
  }

  @NodeInfo(shortName = "Interval.Compare")
  @GenerateUncached
  @GenerateInline
  public abstract static class IntervalCompareNode extends Node {

    public abstract int execute(Node node, IntervalObject interval1, IntervalObject interval2);

    @Specialization
    static int compare(
        Node node,
        IntervalObject interval1,
        IntervalObject interval2,
        @Bind("$node") Node thisNode,
        @Cached IntervalToMillisStaticNode toMillisNode) {
      long mil1 = toMillisNode.execute(thisNode, interval1);
      long mil2 = toMillisNode.execute(thisNode, interval1);

      return Long.compare(mil1, mil2);
    }
  }
}
