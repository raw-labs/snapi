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

package com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives;

import com.oracle.truffle.api.dsl.Bind;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.temporals.interval_package.IntervalNodes;
import java.time.Duration;

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
  final Duration asDuration(
      @Bind("$node") Node thisNode,
      @Cached(inline = true) IntervalNodes.IntervalToMillisStaticNode toMillisNode) {
    return Duration.ofMillis(toMillisNode.execute(thisNode, this));
  }
}
