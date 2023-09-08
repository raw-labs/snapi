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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.TruffleObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

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

    this.millis = (int) (rest %= 1000);
  }

  @CompilerDirectives.TruffleBoundary
  public IntervalObject(String interval) {

    Matcher matcher = pattern.matcher(interval);

    if (matcher.matches()) {

      int years = matcher.group(1) == null ? 0 : Integer.parseInt(matcher.group(1));
      int months = matcher.group(2) == null ? 0 : Integer.parseInt(matcher.group(2));
      int weeks = matcher.group(3) == null ? 0 : Integer.parseInt(matcher.group(3));
      int days = matcher.group(4) == null ? 0 : Integer.parseInt(matcher.group(4));
      int hours = matcher.group(5) == null ? 0 : Integer.parseInt(matcher.group(5));
      int minutes = matcher.group(6) == null ? 0 : Integer.parseInt(matcher.group(6));
      String s1 = matcher.group(7);
      String s2 = matcher.group(8);
      String millisStr = matcher.group(9);
      int millisInt;

      if (s1 == null && s2 == null) {
        this.seconds = 0;
        millisInt = 0;
      } else if (s1 != null) {
        this.seconds = Integer.parseInt(s1);
        millisInt = 0;
      } else {
        millisInt = Integer.parseInt(millisStr);
        // milliseconds will have the same sign as the seconds
        if (millisStr.length() < 3) {
          int multiplier = 1;
          for (int i = 1; i < (3 - millisStr.length() + 1); i++) {
            multiplier = multiplier * 10;
            millisInt = Integer.parseInt(millisStr) * multiplier;
          }
        } else {
          millisInt = Integer.parseInt(millisStr.substring(0, 3));
        }

        this.seconds = Integer.parseInt(s2);
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

    if (mil1 == mil2) return 0;
    else if (mil1 > mil2) return 1;
    else return -1;
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

    if (!time.equals("")) {
      result += ("T" + time);
    }
    return result;
  }
}
