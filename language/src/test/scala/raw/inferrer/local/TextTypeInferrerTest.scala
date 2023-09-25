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

package raw.inferrer.local

import com.typesafe.scalalogging.StrictLogging
import org.scalatest.funsuite.AnyFunSuite
import raw.inferrer.api._

class TextTypeInferrerTest extends AnyFunSuite with StrictLogging with TextTypeInferrer {

  val nulls = Seq("null")
  def assertType(values: Seq[String], expected: SourceType) = {
    val inferred = values.foldLeft(SourceNothingType(): SourceType) {
      case (acc: SourceNullableType, value) if nulls.contains(value) => SourceNullableType.cloneAsNullable(acc)
      case (_: SourceNothingType, value) if nulls.contains(value) => SourceNullType()
      case (acc, value) => getType(value, acc)
    }

    assert(inferred == expected)
  }

  test("infer dates with format") {
    assertType(Seq("1975-06-23", "2022-06-19", "1980-12-01"), SourceDateType(Some("yyyy-M-d"), false))
    assertType(Seq("23/06/1975", "19/06/2022", "01/12/1980"), SourceDateType(Some("d/M/yyyy"), false))
    assertType(Seq("01-06-22", "24-12-19", "01-01-18"), SourceDateType(Some("d-M-yy"), false))
    assertType(Seq("23 June 1975", "12 December 2019", "26 February 2022"), SourceDateType(Some("d MMMM yyyy"), false))
    assertType(Seq("1975 June 23", "2019 December 12", "2022 February 26"), SourceDateType(Some("yyyy MMMM d"), false))

    // nullables
    assertType(Seq("null", "2022-06-19", "1980-12-01"), SourceDateType(Some("yyyy-M-d"), true))
    assertType(Seq("23/06/1975", "null", "01/12/1980"), SourceDateType(Some("d/M/yyyy"), true))
    assertType(Seq("01-06-22", "24-12-19", "null"), SourceDateType(Some("d-M-yy"), true))
  }

  test("infer times with format AM PM") {
    assertType(Seq("12:00 AM", "11:30 PM", "11:00 AM"), SourceTimeType(Some("h:m a"), false))
    assertType(Seq("12:00:15 AM", "11:30:25 PM", "11:00:42 AM"), SourceTimeType(Some("h:m:s a"), false))
    assertType(Seq("12:00 AM", "11:30 PM", "null"), SourceTimeType(Some("h:m a"), true))

    assertType(
      Seq("01-06-22 12:30 AM", "24-12-19 11:45 PM", "01-01-18 10:00 AM"),
      SourceTimestampType(Some("d-M-yy h:m a"), false)
    )
    assertType(
      Seq("1975 June 23 12:30 AM", "2019 December 12 11:45 PM", "2022 February 26 09:34 AM"),
      SourceTimestampType(Some("yyyy MMMM d h:m a"), false)
    )
    assertType(
      Seq("01-06-22 12:30 AM", "24-12-19 11:45 PM", "null"),
      SourceTimestampType(Some("d-M-yy h:m a"), true)
    )
  }

  test("infer times with format") {
    assertType(Seq("12:00", "23:30", "11:00"), SourceTimeType(Some("H:m"), false))
    assertType(Seq("12:00:15", "23:30:25", "11:00:42"), SourceTimeType(Some("H:m:s"), false))
    assertType(Seq("12:00:15.123", "23:30:25.456", "11:00:42.789"), SourceTimeType(Some("H:m:s.SSS"), false))

    // nullables
    assertType(Seq("null", "23:30", "11:00"), SourceTimeType(Some("H:m"), true))
    assertType(Seq("12:00:15", "null", "11:00:42"), SourceTimeType(Some("H:m:s"), true))
  }

  test("infer timestamps with format") {
    assertType(
      Seq("1975-06-23T23:30:15.123", "2022-06-19T12:40:12.456", "1980-12-01T09:23:16.876"),
      SourceTimestampType(Some("yyyy-M-d'T'HH:mm[:ss[.SSS]]"), false)
    )
    assertType(
      Seq("23/06/1975 12:30", "19/06/2022 23:45", "01/12/1980 09:32"),
      SourceTimestampType(Some("d/M/yyyy H:m"), false)
    )

    assertType(
      Seq("23 June 1975 23:30", "12 December 2019 12:45", "26 February 2022 09:11"),
      SourceTimestampType(Some("d MMMM yyyy H:m"), false)
    )

    // nullables
    assertType(
      Seq("null", "2022-06-19T12:40:12.456", "1980-12-01T09:23:16.876"),
      SourceTimestampType(Some("yyyy-M-d'T'HH:mm[:ss[.SSS]]"), true)
    )
    assertType(
      Seq("23/06/1975 12:30", "null", "01/12/1980 09:32"),
      SourceTimestampType(Some("d/M/yyyy H:m"), true)
    )

  }

  test("if date format changes infer string") {
    assertType(Seq("23/06/1975", "12 December 2019", "01-12-1980"), SourceStringType(false))
    assertType(Seq("01-06-22", "24-12-19", "123"), SourceStringType(false))
    assertType(Seq("23 June 1975", "12 December 2019", "Hello!"), SourceStringType(false))

    // nullables
    assertType(Seq("1975-06-23", "null", "23 June 1975"), SourceStringType(true))
  }

  test("if time format changes infer string") {
    assertType(Seq("12:00", "23:30", "11:00 AM"), SourceStringType(false))
    assertType(Seq("12:00:15", "23:30:25", "123"), SourceStringType(false))
    assertType(Seq("12:00 AM", "11:30 PM", "11:00.000"), SourceStringType(false))

    // nullables
    assertType(Seq("null", "23:30", "11:00 AM"), SourceStringType(true))
  }

  test("if timestamp format changes infer string") {
    assertType(
      Seq("1975-06-23T23:30:15.123", "2022-06-19T12:40:12.456", "1975 June 23 12:30 AM"),
      SourceStringType(false)
    )
    assertType(Seq("23/06/1975 12:30", "1975 June 23 12:30 AM", "01/12/1980 09:32"), SourceStringType(false))
    assertType(Seq("01-06-22 12:30 AM", "24-12-19 11:45 PM", "123"), SourceStringType(false))
    assertType(Seq("23 June 1975 23:30", "Hello", "26 February 2022 09:11"), SourceStringType(false))

    // nullables
    assertType(Seq("null", "2022-06-19T12:40:12.456", "26 February 2022 09:11"), SourceStringType(true))
    assertType(Seq("01-06-22 12:30 AM", "26 February 2022 09:11", "null"), SourceStringType(true))
    assertType(Seq("23/06/1975 12:30", "null", "123"), SourceStringType(true))

  }

}
