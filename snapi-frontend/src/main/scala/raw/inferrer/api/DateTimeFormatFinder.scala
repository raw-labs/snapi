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

package raw.inferrer.api

import java.time._
import java.time.format.{DateTimeFormatter, DateTimeParseException}

object DateTimeFormatFinder {
  final private val timeFormats = Seq("H:m", "h:m a", "H:m:s", "h:m:s a", "H:m:s.SSS", "h:m:s.SSS a", "HHmmss")

  // TODO:  To be able to year 99 as 1999 use a DateTimeFormatBuilder as shown in:
  // https://stackoverflow.com/questions/32505490/how-to-change-the-base-date-for-parsing-two-letter-years-with-java-8-datetimefor
  //DateTimeFormatterBuilder appendValueReduced(ChronoField.YEAR_OF_ERA, 2, 4, LocalDate.now().minusYears(80))
  // There were problems with building the timestamp format after
  final private val dateFormats = Seq(
    "d-M-yyyy",
    "d/M/yyyy",
    "d-M-yy",
    "d/M/yy",
    "yyyy-M-d",
    "yyyy/M/d",
    "yy-M-d",
    "yy/M/d",
    "yyMMdd",
    "yyyyMMdd",
    "MMMM d yyyy",
    "MMMM d yy",
    "MMM-d-yyyy",
    "MMM-d-yy",
    "MMM d yyyy",
    "MMM d yy",
    "d MMMM yyyy",
    "d MMMM yy",
    "d MMM yyyy",
    "d-MMM-yyyy",
    "d-MMM-yy",
    "yyyy MMMM d",
    "yy MMMM d",
    "yyyy-MMM-d",
    "yy-MMM-d",
    "yyyy MMM d",
    "yy MMM d",
    "yyyy d MMMM",
    "yy d MMMM",
    "yyyy-d-MMM",
    "yy-d-MMM",
    "yyyy d MMM",
    "yy d MMM"
  )

  final private val timeFormatters = timeFormats.map(x => x -> DateTimeFormatter.ofPattern(x)).toMap
  final private val dateFormatters = dateFormats.map(x => x -> DateTimeFormatter.ofPattern(x)).toMap
  final private val timestampFormatters = {
    // All combinations of date and time formats + our default format with 'T' between
    val timestampFormats =
      (for (df <- dateFormats; tf <- timeFormats) yield s"$df $tf") :+ "yyyy-M-d'T'HH:mm[:ss[.SSS]]"
    timestampFormats.map(x => x -> DateTimeFormatter.ofPattern(x)).toMap
  }

  def getDate(str: String): Option[(String, LocalDate)] = {
    for (f <- dateFormatters) {
      try {
        return Some(f._1 -> LocalDate.parse(str, f._2))
      } catch {
        case _: DateTimeParseException =>
      }
    }
    None
  }

  def getTimestamp(str: String): Option[(String, LocalDateTime)] = {
    for (f <- timestampFormatters) {
      try {
        return Some(f._1 -> LocalDateTime.parse(str, f._2))
      } catch {
        case _: DateTimeParseException =>
      }
    }
    None
  }

  def getTime(str: String): Option[(String, LocalTime)] = {
    for (f <- timeFormatters) {
      try {
        return Some(f._1 -> LocalTime.parse(str, f._2))
      } catch {
        case _: DateTimeParseException =>
      }
    }
    None
  }

  def tryDateFormat(value: String, format: String): Boolean = {
    try {
      dateFormatters(format).parse(value)
      return true
    } catch {
      case _: DateTimeParseException =>
    }
    false
  }

  def tryTimeFormat(value: String, format: String): Boolean = {
    try {
      timeFormatters(format).parse(value)
      return true
    } catch {
      case _: DateTimeParseException =>
    }
    false
  }

  def tryTimestampFormat(value: String, format: String): Boolean = {
    try {
      timestampFormatters(format).parse(value)
      return true
    } catch {
      case _: DateTimeParseException =>
    }
    false
  }

}
