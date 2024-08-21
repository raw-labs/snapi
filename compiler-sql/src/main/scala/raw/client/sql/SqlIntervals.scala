/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.client.sql

import com.rawlabs.compiler.api.RawInterval
import scala.util.matching.Regex

object SqlIntervals {

  val postgresIntervalRegex: Regex =
    """(?:(\d+)\s+years?)?(?:\s*(\d+) mons?)?(?:\s*(\d+) days?)?(?:\s*(\d+):(\d+):(\d+)(?:\.(\d+))?)?""".r

  def padRight(s: String, n: Int): String = {
    val padding = n - s.length
    if (padding <= 0) s
    else s + "0" * padding
  }
  def parseInterval(in: String): RawInterval = {
    in match {
      case postgresIntervalRegex(years, months, days, hours, minutes, seconds, fraction) =>
        val yearsInt = Option(years).map(_.toInt).getOrElse(0)
        val monthsInt = Option(months).map(_.toInt).getOrElse(0)
        val daysInt = Option(days).map(_.toInt).getOrElse(0)
        val hoursInt = Option(hours).map(_.toInt).getOrElse(0)
        val minutesInt = Option(minutes).map(_.toInt).getOrElse(0)
        val secondsInt = Option(seconds).map(_.toInt).getOrElse(0)
        // Padding the fraction to the right and taking the first 3 digits
        // 0.1 => 100, 0.01 => 010, 0.001 => 001
        val fractionInt = Option(fraction).map(x => padRight(x, 3).take(3).toInt).getOrElse(0)
        val weeks = 0
        RawInterval(yearsInt, monthsInt, weeks, daysInt, hoursInt, minutesInt, secondsInt, fractionInt)
      case _ => throw new IllegalArgumentException(s"Invalid interval format: $in")
    }
  }

  def intervalToString(in: RawInterval): String = {

    val time = new StringBuilder()
    val result = new StringBuilder("P")

    val totalMillis = in.seconds * 1000 + in.millis
    val s = totalMillis / 1000

    val ms =
      if (totalMillis >= 0) totalMillis % 1000
      else -totalMillis % 1000

    // P1Y2M4W5DT6H5M7.008S// P1Y2M4W5DT6H5M7.008S
    if (in.hours != 0) time.append(in.hours + "H")
    if (in.minutes != 0) time.append(in.minutes + "M")
    if (s != 0 || ms != 0) time.append(f"$s%d.$ms%03dS")

    if (in.years != 0) result.append(in.years + "Y")

    if (in.months != 0) result.append(in.months + "M")

    if (in.weeks != 0) result.append(in.weeks + "W")

    if (in.days != 0) result.append(in.days + "D")

    if (time.nonEmpty) result.append("T" + time.toString())

    result.toString()
  }
}
