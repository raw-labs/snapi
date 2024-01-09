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

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import raw.runtime.truffle.boundary.RawTruffleBoundaries;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

@ExportLibrary(InteropLibrary.class)
public final class IntervalObject implements TruffleObject {

  private final int years;
  private final int months;
  private final int weeks;
  private final int days;
  private final int hours;
  private final int minutes;
  private final int seconds;
  private final int millis;

  private final Pattern pattern =
      Pattern.compile(
          "^P(?:(\\d+)Y)?(?:(\\d+)M)?(?:(\\d+)W)?(?:(\\d+)D)?(?:T(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)(?:\\.(\\d{1,3}))?S)?)?$");

  public IntervalObject(
      int years, int months, int weeks, int days, int hours, int minutes, int seconds, int millis) {
    this.years = years;
    this.months = months;
    this.weeks = weeks;
    this.days = days;
    this.hours = hours;
    this.minutes = minutes;
    this.seconds = seconds;
    this.millis = millis;
  }

  @TruffleBoundary
  public static IntervalObject fromDuration(Duration duration) {
    return new IntervalObject(0, duration.toMillis());
  }

  public IntervalObject(long months, long millis) {
    long rest;
    this.years = (int) (months / 12);
    this.months = (int) (months % 12);
    this.weeks = 0;
    this.days = (int) (millis / (24 * 3600000));

    rest = millis % (24 * 3600000);
    this.hours = (int) (rest / 3600000);

    rest %= 3600000;
    this.minutes = (int) (rest / 60000);

    rest %= 60000;
    this.seconds = (int) (rest / 1000);

    this.millis = (int) (rest % 1000);
  }

  //  public IntervalObject(Duration duration) {
  //    long millis = duration.getNano() / 1000000 + duration.getSeconds() * 1000;
  //    new IntervalObject(0, millis);
  //  }

  @TruffleBoundary
  private Matcher getMatcher(String interval) {
    return pattern.matcher(interval);
  }

  @TruffleBoundary
  private String matcherGroup(Matcher matcher, int group) {
    return matcher.group(group);
  }

  public IntervalObject(String interval) {

    Matcher matcher = getMatcher(interval);

    if (matcher.matches()) {
      String matchYears = matcherGroup(matcher, 1);
      int years = matchYears == null ? 0 : RawTruffleBoundaries.parseInt(matchYears);

      String matchMonths = matcherGroup(matcher, 2);
      int months = matchMonths == null ? 0 : RawTruffleBoundaries.parseInt(matchMonths);

      String matchWeeks = matcherGroup(matcher, 3);
      int weeks = matchWeeks == null ? 0 : RawTruffleBoundaries.parseInt(matchWeeks);

      String matchDays = matcherGroup(matcher, 4);
      int days = matchDays == null ? 0 : RawTruffleBoundaries.parseInt(matchDays);

      String matchHours = matcherGroup(matcher, 5);
      int hours = matchHours == null ? 0 : RawTruffleBoundaries.parseInt(matchHours);

      String matchMinutes = matcherGroup(matcher, 6);
      int minutes = matchMinutes == null ? 0 : RawTruffleBoundaries.parseInt(matchMinutes);

      String s1 = matcherGroup(matcher, 7);
      String s2 = matcherGroup(matcher, 8);
      String millisStr = matcherGroup(matcher, 9);
      int millisInt;

      if (s1 == null && s2 == null) {
        this.seconds = 0;
        millisInt = 0;
      } else if (s1 != null) {
        this.seconds = RawTruffleBoundaries.parseInt(s1);
        millisInt = 0;
      } else {
        millisInt = RawTruffleBoundaries.parseInt(millisStr);
        // milliseconds will have the same sign as the seconds
        if (millisStr.length() < 3) {
          int multiplier = 1;
          for (int i = 1; i < (3 - millisStr.length() + 1); i++) {
            multiplier = multiplier * 10;
            millisInt = RawTruffleBoundaries.parseInt(millisStr) * multiplier;
          }
        } else {
          millisInt = RawTruffleBoundaries.parseInt(millisStr.substring(0, 3));
        }

        this.seconds = RawTruffleBoundaries.parseInt(s2);
        if (this.seconds < 0) {
          millisInt = -millisInt;
        }
      }

      this.years = years;
      this.months = months;
      this.weeks = weeks;
      this.days = days;
      this.hours = hours;
      this.minutes = minutes;
      this.millis = millisInt;
    } else {
      throw new RawTruffleRuntimeException(
          String.format("could not parse interval from string '%s'", interval));
    }
  }

  public static IntervalObject normalize(
      int years, int months, int weeks, int days, int hours, int minutes, int seconds, int millis) {
    long totalMonths = 12 * (long) years + (long) months;
    long totalDays = 7 * (long) weeks + (long) days;

    long totalMillis =
        24 * 3600000 * totalDays + 3600000L * hours + 60000L * minutes + 1000L * seconds + millis;

    return new IntervalObject(totalMonths, totalMillis);
  }

  public long toMillis() {
    double yearsInDays = 365.25 * years;
    double monthsInDays = (365.25 / 12) * months;

    double totalDays = yearsInDays + monthsInDays + 7 * weeks + days;

    return 24 * 3600000 * (long) totalDays
        + 3600000 * (long) hours
        + 60000 * (long) minutes
        + 1000 * (long) seconds
        + millis;
  }

  public int compareTo(IntervalObject other) {

    long mil1 = this.toMillis();
    long mil2 = other.toMillis();

    return Long.compare(mil1, mil2);
  }

  public int getYears() {
    return years;
  }

  public int getMonths() {
    return months;
  }

  public int getWeeks() {
    return weeks;
  }

  public int getDays() {
    return days;
  }

  public int getHours() {
    return hours;
  }

  public int getMinutes() {
    return minutes;
  }

  public int getSeconds() {
    return seconds;
  }

  public int getMillis() {
    return millis;
  }

  @Override
  public String toString() {
    int ms;
    String time = "";
    String result = "P";

    int totalMillis = seconds * 1000 + millis;
    int s = totalMillis / 1000;

    if (totalMillis >= 0) {
      ms = totalMillis % 1000;
    } else {
      ms = -totalMillis % 1000;
    }

    // P1Y2M4W5DT6H5M7.008S
    if (hours != 0) {
      time += (hours + "H");
    }
    if (minutes != 0) {
      time += (minutes + "M");
    }
    if (s != 0 || ms != 0) {
      time += (s + "." + ms + "%03dS");
    }

    if (years != 0) {
      result += (years + "Y");
    }

    if (months != 0) {
      result += (months + "M");
    }

    if (weeks != 0) {
      result += (weeks + "W");
    }

    if (days != 0) {
      result += (days + "D");
    }

    if (!time.isEmpty()) {
      result += ("T" + time);
    }
    return result;
  }

  @ExportMessage
  final boolean isDuration() {
    return true;
  }

  @ExportMessage
  @TruffleBoundary
  final Duration asDuration() {
    return Duration.ofMillis(toMillis());
  }
}
