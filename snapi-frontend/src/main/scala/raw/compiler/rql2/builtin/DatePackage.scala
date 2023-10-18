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

package raw.compiler.rql2.builtin

import raw.client.api._
import raw.compiler.rql2.api.{PackageExtension, ShortEntryExtension}
import raw.compiler.rql2.source._

class DatePackage extends PackageExtension {

  /**
   * Name of the package.
   */
  override def name: String = "Date"

  /**
   * Package documentation.
   */
  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for the date type."
  )

}

class DateBuildEntry
    extends ShortEntryExtension(
      "Date",
      "Build",
      Vector(Rql2IntType(), Rql2IntType(), Rql2IntType()),
      Rql2DateType(Set(Rql2IsTryableTypeProperty())),
      docs = EntryDoc(
        "Builds a date value.",
        examples = List(ExampleDoc("""Date.Build(2022, 1, 15)""", result = Some("15th January 2022"))),
        params = List(
          ParamDoc("year", TypeDoc(List("int")), "Year component of the date to build."),
          ParamDoc("month", TypeDoc(List("int")), "Month component of the date to build."),
          ParamDoc("day", TypeDoc(List("int")), "Day component of the date to build.")
        ),
        ret = Some(ReturnDoc("The date value built from the given components.", retType = Some(TypeDoc(List("date")))))
      )
    )

class DateFromEpochDayEntry
    extends ShortEntryExtension(
      "Date",
      "FromEpochDay",
      Vector(Rql2LongType()),
      Rql2DateType(),
      EntryDoc(
        summary = "Builds a date by adding the number of days from 1970-01-01 (Unix epoch).",
        examples = List(
          ExampleDoc("""Date.FromEpochDay(0)""", result = Some("1970-01-01")),
          ExampleDoc("""Date.FromEpochDay(1000)""", result = Some("1972-09-27"))
        ),
        params = List(
          ParamDoc("epochDays", TypeDoc(List("long")), "The number of days since 1970-01-01 (Unix epoch).")
        ),
        ret =
          Some(ReturnDoc("The date value built from the given number of days.", retType = Some(TypeDoc(List("date")))))
      )
    )

class DateFromTimestampEntry
    extends ShortEntryExtension(
      "Date",
      "FromTimestamp",
      Vector(Rql2TimestampType()),
      Rql2DateType(),
      EntryDoc(
        summary = "Builds a date from a timestamp.",
        examples = List(
          ExampleDoc("""Date.FromTimestamp(Timestamp.Build(1975, 6, 23, 9, 30))""", result = Some("""1975-06-23"""))
        ),
        params = List(
          ParamDoc("timestamp", TypeDoc(List("timestamp")), "The timestamp to convert to date.")
        ),
        ret = Some(ReturnDoc("The date value built from the given timestamp.", retType = Some(TypeDoc(List("date")))))
      )
    )

class DateParseEntry
    extends ShortEntryExtension(
      "Date",
      "Parse",
      Vector(Rql2StringType(), Rql2StringType()),
      Rql2DateType(Set(Rql2IsTryableTypeProperty())),
      EntryDoc(
        summary = "Parses a date from a string.",
        description = Some(
          "For more information about format strings see the [Temporal templates documentation](../temporal-templates)."
        ),
        examples = List(
          ExampleDoc("""Date.Parse("2018-02-01", "yyyy-MM-dd")""", result = Some("""2018-02-01""")),
          ExampleDoc("""Date.Parse("23 June 1975", "d MMMM yyyy")""", result = Some("""1975-06-23"""))
        ),
        params = List(
          ParamDoc("value", TypeDoc(List("string")), "The string to convert to date."),
          ParamDoc("format", TypeDoc(List("string")), "The format of the date.")
        ),
        ret = Some(ReturnDoc("The date value built from the given string.", retType = Some(TypeDoc(List("date")))))
      )
    )

class DateNowEntry
    extends ShortEntryExtension(
      "Date",
      "Now",
      Vector(),
      Rql2DateType(),
      EntryDoc(
        summary = "Returns the current date.",
        ret = Some(ReturnDoc("The current date.", retType = Some(TypeDoc(List("date")))))
      )
    )

class DateYearEntry
    extends ShortEntryExtension(
      "Date",
      "Year",
      Vector(Rql2DateType()),
      Rql2IntType(),
      EntryDoc(
        summary = "Returns the year component of the date.",
        examples = List(ExampleDoc("""Date.Year(Date.Build(1975, 6, 23))""", result = Some("1975"))),
        params = List(
          ParamDoc("date", TypeDoc(List("date")), "The date from which the year component is extracted.")
        ),
        ret = Some(ReturnDoc("The year component of the date.", retType = Some(TypeDoc(List("int")))))
      )
    )

class DateMonthEntry
    extends ShortEntryExtension(
      "Date",
      "Month",
      Vector(Rql2DateType()),
      Rql2IntType(),
      EntryDoc(
        summary = "Returns the month component of the date.",
        examples = List(ExampleDoc("""Date.Month(Date.Build(1975, 6, 23))""", result = Some("6"))),
        params = List(
          ParamDoc("date", TypeDoc(List("date")), "The date from which the month component is extracted.")
        ),
        ret = Some(ReturnDoc("The month component of the date.", retType = Some(TypeDoc(List("int")))))
      )
    )

class DateDayEntry
    extends ShortEntryExtension(
      "Date",
      "Day",
      Vector(Rql2DateType()),
      Rql2IntType(),
      EntryDoc(
        summary = "Returns the day component of the date.",
        examples = List(ExampleDoc("""Date.Day(Date.Build(1975, 6, 23))""", result = Some("23"))),
        params = List(
          ParamDoc("date", TypeDoc(List("date")), "The date from which the day component is extracted.")
        ),
        ret = Some(ReturnDoc("The day component of the date.", retType = Some(TypeDoc(List("int")))))
      )
    )

class DateSubtractEntry
    extends ShortEntryExtension(
      "Date",
      "Subtract",
      Vector(Rql2DateType(), Rql2DateType()),
      Rql2IntervalType(),
      EntryDoc(
        summary = "Subtracts two dates.",
        examples = List(
          ExampleDoc(
            """let
              |  d1 = Date.Build(2019, 3, 4),
              |  d2 = Date.Build(2018, 1, 1)
              |in
              |  Date.Subtract(d1, d2)""".stripMargin,
            result = Some("""interval: years=1, months=2, days=3""")
          )
        ),
        params = List(
          ParamDoc("date1", TypeDoc(List("date")), "date to be subtracted (minuend)."),
          ParamDoc("date2", TypeDoc(List("date")), "date to subtract (subtrahend).")
        ),
        ret = Some(ReturnDoc("The interval between the two dates.", retType = Some(TypeDoc(List("interval")))))
      )
    )

class DateAddIntervalEntry
    extends ShortEntryExtension(
      "Date",
      "AddInterval",
      mandatoryParams = Vector(Rql2DateType(), Rql2IntervalType()),
      returnType = Rql2TimestampType(),
      EntryDoc(
        summary = "Adds an interval to a date.",
        examples = List(
          ExampleDoc(
            """let
              |  d = Date.Build(2018, 1, 1),
              |  i = Interval.Build(years = 1, months = 2, days = 3, hours = 9, minutes = 30)
              |in
              |  Date.AddInterval(d, i)""".stripMargin,
            result = Some("""2019-03-04 09:30""")
          )
        ),
        params = List(
          ParamDoc("date", TypeDoc(List("date")), "Start date."),
          ParamDoc("interval", TypeDoc(List("interval")), "interval to add.")
        ),
        ret = Some(
          ReturnDoc("The date resulting from adding the interval to the date.", retType = Some(TypeDoc(List("date"))))
        )
      )
    )

class DateSubtractIntervalEntry
    extends ShortEntryExtension(
      "Date",
      "SubtractInterval",
      mandatoryParams = Vector(Rql2DateType(), Rql2IntervalType()),
      returnType = Rql2TimestampType(),
      EntryDoc(
        summary = "Subtracts an interval to a date.",
        examples = List(
          ExampleDoc(
            """let
              |  d = Date.Build(2019, 3, 4),
              |  i = Interval.Build(years = 1, months = 2, days = 3, hours = 9, minutes = 30)
              |in
              |  Date.SubtractInterval(d, i)""".stripMargin,
            result = Some("""2018-01-01 00:00""")
          )
        ),
        params = List(
          ParamDoc("date", TypeDoc(List("date")), "Start date."),
          ParamDoc("interval", TypeDoc(List("interval")), "Interval to subtract.")
        ),
        ret = Some(
          ReturnDoc(
            "The date resulting from subtracting the interval to the date.",
            retType = Some(TypeDoc(List("date")))
          )
        )
      )
    )
