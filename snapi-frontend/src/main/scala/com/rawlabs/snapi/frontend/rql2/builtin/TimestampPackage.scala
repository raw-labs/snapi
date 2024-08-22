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

package com.rawlabs.snapi.frontend.rql2.builtin

import com.rawlabs.compiler.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import com.rawlabs.snapi.frontend.base.source.Type
import com.rawlabs.snapi.frontend.common.source._
import com.rawlabs.snapi.frontend.rql2.source._
import com.rawlabs.snapi.frontend.rql2._
import com.rawlabs.snapi.frontend.rql2.api.{Arg, EntryExtension, ExpParam, PackageExtension, Param, ShortEntryExtension}

import scala.collection.immutable.ListMap

class TimestampPackage extends PackageExtension {

  /**
   * Name of the package.
   */
  override def name: String = "Timestamp"

  /**
   * Package documentation.
   */
  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for the timestamp type."
  )

}

class TimestampBuildEntry
    extends ShortEntryExtension(
      "Timestamp",
      "Build",
      mandatoryParams = Vector(Rql2IntType(), Rql2IntType(), Rql2IntType(), Rql2IntType(), Rql2IntType()),
      returnType = Rql2TimestampType(Set(Rql2IsTryableTypeProperty())),
      docs = EntryDoc(
        "Builds a timestamp value",
        params = List(
          ParamDoc("year", TypeDoc(List("int")), "Year component of the timestamp to build."),
          ParamDoc("month", TypeDoc(List("int")), "Month component of the timestamp to build."),
          ParamDoc("day", TypeDoc(List("int")), "Day component of the timestamp to build."),
          ParamDoc("hours", TypeDoc(List("int")), "Hours component of the timestamp to build."),
          ParamDoc("minutes", TypeDoc(List("int")), "Minutes component of the timestamp to build."),
          ParamDoc("seconds", TypeDoc(List("int")), "Seconds component of the timestamp to build.", isOptional = true),
          ParamDoc(
            "millis",
            TypeDoc(List("int")),
            "Milliseconds component of the timestamp to build.",
            isOptional = true
          )
        ),
        examples = List(
          ExampleDoc("""Timestamp.Build(2022, 1, 15, 9, 30)""", result = Some("2022-01-15 09:30:00")),
          ExampleDoc("""Timestamp.Build(2022, 1, 15, 9, 30, seconds = 20)""", result = Some("2022-01-15 9:30:20")),
          ExampleDoc(
            """Timestamp.Build(2022, 1, 15, 9, 30, seconds = 20, millis = 10)""",
            result = Some("2022-01-15 9:30:20.010")
          )
        )
      ),
      optionalParamsMap = ListMap(
        "seconds" -> ((Rql2IntType(), IntConst("0"))),
        "millis" -> ((Rql2IntType(), IntConst("0")))
      )
    )

class TimestampFromDateEntry
    extends ShortEntryExtension(
      "Timestamp",
      "FromDate",
      Vector(Rql2DateType()),
      Rql2TimestampType(),
      EntryDoc(
        summary = "Builds a timestamp from a date.",
        params = List(
          ParamDoc("date", TypeDoc(List("date")), "The date to convert to timestamp.")
        ),
        examples =
          List(ExampleDoc("""Timestamp.FromDate(Date.Build(1975, 6, 23))""", result = Some("1975-06-23 00:00:00"))),
        ret = Some(ReturnDoc("The timestamp value.", retType = Some(TypeDoc(List("timestamp")))))
      )
    )

class TimestampParseEntry
    extends ShortEntryExtension(
      "Timestamp",
      "Parse",
      mandatoryParams = Vector(Rql2StringType(), Rql2StringType()),
      returnType = Rql2TimestampType(Set(Rql2IsTryableTypeProperty())),
      EntryDoc(
        summary = "Parses a timestamp from a string.",
        description = Some(
          "For more information about format strings see the [Temporal templates documentation](../temporal-templates)."
        ),
        params = List(
          ParamDoc("value", TypeDoc(List("string")), "The string to convert to timestamp."),
          ParamDoc("format", TypeDoc(List("string")), "The format of the timestamp.")
        ),
        examples = List(
          ExampleDoc(
            """Timestamp.Parse("23/06/1975 09:30:15.123", "d/M/yyyy H:m:s.SSS")""",
            result = Some("1975-06-23 09:30:15.123")
          ),
          ExampleDoc(
            """Timestamp.Parse("9:30 23 June 1975", "H:m d MMMM yyyy")""",
            result = Some("1975-06-23 09:30:00")
          )
        ),
        ret = Some(ReturnDoc("The timestamp value.", retType = Some(TypeDoc(List("timestamp")))))
      )
    )

class TimestampNowEntry
    extends ShortEntryExtension(
      "Timestamp",
      "Now",
      mandatoryParams = Vector(),
      returnType = Rql2TimestampType(),
      EntryDoc(
        summary = "Returns the current timestamp.",
        examples = List(ExampleDoc("""Timestamp.Now()""")),
        ret = Some(ReturnDoc("The current timestamp.", retType = Some(TypeDoc(List("timestamp")))))
      )
    )

class TimestampRangeEntry extends EntryExtension {

  override def packageName: String = "Timestamp"

  override def entryName: String = "Range"

  override def docs: EntryDoc = EntryDoc(
    summary = "Builds a collection of timestamps between two specified timestamps.",
    params = List(
      ParamDoc("start", TypeDoc(List("timestamp")), "The starting value."),
      ParamDoc("end", TypeDoc(List("timestamp")), "The end value (not included)."),
      ParamDoc(
        "step",
        TypeDoc(List("interval")),
        "The step value (default: Interval.Build(days=1)).",
        isOptional = true
      )
    ),
    examples = List(
      ExampleDoc(
        """Timestamp.Range(Timestamp.Build(1975, 6, 23, 9, 30), Timestamp.Build(1975, 6, 28, 9, 30), step=Interval.Build(days=2))"""
      )
    ),
    ret = Some(ReturnDoc("The collection of timestamps.", retType = Some(TypeDoc(List("collection(timestamp)")))))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(Rql2TimestampType()))
  }

  override def optionalParams: Option[Set[String]] = Some(Set("step"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    assert(idn == "step")
    Right(ExpParam(Rql2IntervalType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2IterableType(Rql2TimestampType()))
  }

}

class TimestampYearEntry
    extends ShortEntryExtension(
      "Timestamp",
      "Year",
      mandatoryParams = Vector(Rql2TimestampType()),
      returnType = Rql2IntType(),
      EntryDoc(
        summary = "Returns the year component of the timestamp.",
        params = List(
          ParamDoc("timestamp", TypeDoc(List("timestamp")), "The timestamp from which the year component is extracted.")
        ),
        examples = List(ExampleDoc("""Timestamp.Year(Timestamp.Build(1975, 6, 23, 9, 30))""", result = Some("1975"))),
        ret = Some(ReturnDoc("The year component of the timestamp.", retType = Some(TypeDoc(List("int")))))
      )
    )

class TimestampMonthEntry
    extends ShortEntryExtension(
      "Timestamp",
      "Month",
      mandatoryParams = Vector(Rql2TimestampType()),
      returnType = Rql2IntType(),
      EntryDoc(
        summary = "Returns the month component of the timestamp.",
        params = List(
          ParamDoc(
            "timestamp",
            TypeDoc(List("timestamp")),
            "The timestamp from which the month component is extracted."
          )
        ),
        examples = List(ExampleDoc("""Timestamp.Month(Timestamp.Build(1975, 6, 23, 9, 30))""", result = Some("6"))),
        ret = Some(ReturnDoc("The month component of the timestamp.", retType = Some(TypeDoc(List("int")))))
      )
    )

class TimestampDayEntry
    extends ShortEntryExtension(
      "Timestamp",
      "Day",
      mandatoryParams = Vector(Rql2TimestampType()),
      returnType = Rql2IntType(),
      EntryDoc(
        summary = "Returns the day component of the timestamp.",
        params = List(
          ParamDoc("timestamp", TypeDoc(List("timestamp")), "The timestamp from which the day component is extracted.")
        ),
        examples = List(ExampleDoc("""Timestamp.Day(Timestamp.Build(1975, 6, 23, 9, 30))""", result = Some("23"))),
        ret = Some(ReturnDoc("The day component of the timestamp.", retType = Some(TypeDoc(List("int")))))
      )
    )

class TimestampHourEntry
    extends ShortEntryExtension(
      "Timestamp",
      "Hour",
      mandatoryParams = Vector(Rql2TimestampType()),
      returnType = Rql2IntType(),
      EntryDoc(
        summary = "Returns the hours component of the timestamp.",
        params = List(
          ParamDoc(
            "timestamp",
            TypeDoc(List("timestamp")),
            "The timestamp from which the hours component is extracted."
          )
        ),
        examples = List(ExampleDoc("""Timestamp.Hour(Timestamp.Build(1975, 6, 23, 9, 30))""", result = Some("9"))),
        ret = Some(ReturnDoc("The hours component of the timestamp.", retType = Some(TypeDoc(List("int")))))
      )
    )

class TimestampMinuteEntry
    extends ShortEntryExtension(
      "Timestamp",
      "Minute",
      mandatoryParams = Vector(Rql2TimestampType()),
      returnType = Rql2IntType(),
      EntryDoc(
        summary = "Returns minutes part of the timestamp as an integer.",
        params = List(
          ParamDoc(
            "timestamp",
            TypeDoc(List("timestamp")),
            "The timestamp from which the minutes component is extracted."
          )
        ),
        examples = List(ExampleDoc("""Timestamp.Minute(Timestamp.Build(1975, 6, 23, 9, 30))""", result = Some("30"))),
        ret = Some(ReturnDoc("The minutes component of the timestamp.", retType = Some(TypeDoc(List("int")))))
      )
    )

class TimestampSecondEntry
    extends ShortEntryExtension(
      "Timestamp",
      "Second",
      mandatoryParams = Vector(Rql2TimestampType()),
      returnType = Rql2IntType(),
      EntryDoc(
        summary = "Returns the seconds component of the timestamp.",
        params = List(
          ParamDoc(
            "timestamp",
            TypeDoc(List("timestamp")),
            "The timestamp from which the seconds component is extracted."
          )
        ),
        examples = List(
          ExampleDoc("""Timestamp.Second(Timestamp.Build(1975, 6, 23, 9, 30, seconds = 15))""", result = Some("15"))
        ),
        ret = Some(ReturnDoc("The seconds component of the timestamp.", retType = Some(TypeDoc(List("int")))))
      )
    )

class TimestampMillisEntry
    extends ShortEntryExtension(
      "Timestamp",
      "Millis",
      mandatoryParams = Vector(Rql2TimestampType()),
      returnType = Rql2IntType(),
      EntryDoc(
        summary = "Returns the milliseconds component of the timestamp.",
        params = List(
          ParamDoc(
            "timestamp",
            TypeDoc(List("timestamp")),
            "The timestamp from which the year milliseconds is extracted."
          )
        ),
        examples = List(
          ExampleDoc(
            """Timestamp.Millis(Timestamp.Build(1975, 6, 23, 9, 30, seconds = 15, millis = 123)) // 123""",
            result = Some("123")
          )
        ),
        ret = Some(ReturnDoc("The milliseconds component of the timestamp.", retType = Some(TypeDoc(List("int")))))
      )
    )

class TimestampFromUnixTimestampEntry
    extends ShortEntryExtension(
      "Timestamp",
      "FromUnixTimestamp",
      mandatoryParams = Vector(Rql2LongType()),
      returnType = Rql2TimestampType(),
      EntryDoc(
        summary = "Builds a timestamp from a Unix epoch (number of seconds since 01-01-1970).",
        params = List(
          ParamDoc(
            "epoch",
            TypeDoc(List("long")),
            "Unix epoch (number of seconds since 01-01-1970)."
          )
        ),
        examples = List(ExampleDoc("""Timestamp.FromUnixTimestamp(1517443320)""", result = Some("2018-02-1 01:02:00"))),
        ret =
          Some(ReturnDoc("The timestamp corresponding to the Unix epoch.", retType = Some(TypeDoc(List("timestamp")))))
      )
    )

class TimestampToUnixTimestampEntry
    extends ShortEntryExtension(
      "Timestamp",
      "ToUnixTimestamp",
      mandatoryParams = Vector(Rql2TimestampType()),
      returnType = Rql2LongType(),
      EntryDoc(
        summary = "Converts a timestamp into the corresponding Unix epoch (number of seconds since 01-01-1970).",
        params = List(ParamDoc("timestamp", TypeDoc(List("timestamp")), "Timestamp to convert to Unix epoch.")),
        examples = List(
          ExampleDoc(
            """Timestamp.ToUnixTimestamp(Timestamp.Build(2018, 2, 1, 1, 2))""",
            result = Some("1517443320")
          )
        ),
        ret = Some(
          ReturnDoc(
            "The Unix epoch corresponding to the timestamp.",
            retType = Some(TypeDoc(List("long")))
          )
        )
      )
    )

class TimestampTimeBucketEntry extends EntryExtension {

  override def packageName: String = "Timestamp"

  override def entryName: String = "TimeBucket"

  /**
   * Documentation.
   */
  override def docs: EntryDoc = EntryDoc(
    summary = """Truncates a timestamp to the specified precision.""",
    info = Some("""Valid string values for precision are:
      |- milliseconds
      |- second
      |- minute
      |- hour
      |- day
      |- week
      |- month
      |- quarter
      |- year
      |- decade
      |- century
      |- millennium.""".stripMargin),
    params = List(
      ParamDoc(
        "value",
        TypeDoc(List("interval", "string")),
        "Interval or string definition to which the timestamp will be truncated."
      ),
      ParamDoc("timestamp.", TypeDoc(List("timestamp")), "The timestamp to truncate.")
    ),
    examples = List(
      ExampleDoc(
        """Timestamp.TimeBucket(Interval.Build(millis = 100), Timestamp.Build(2007, 3, 14, 1, 2, seconds=3, millis=4))""",
        result = Some("2007-03-14 01:02:03.000")
      ),
      ExampleDoc(
        """Timestamp.TimeBucket(Interval.Build(years = 2), Timestamp.Build(2007, 3, 14, 1, 2, seconds=3, millis=4))""",
        result = Some("2006-01-01 00:00:00")
      ),
      ExampleDoc(
        """Timestamp.TimeBucket("hour", Timestamp.Build(2007, 3, 14, 1, 2, seconds=3, millis=4))""",
        result = Some("2007-03-14 01:00:00")
      ),
      ExampleDoc(
        """Timestamp.TimeBucket("year", Timestamp.Build(2007, 3, 14, 1, 2, seconds=3, millis=4))""",
        result = Some("2007-01-01 00:00:00")
      )
    ),
    ret = Some(ReturnDoc("The truncated timestamp.", retType = Some(TypeDoc(List("timestamp")))))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    if (idx == 0) Right(ExpParam(OneOfType(Rql2StringType(), Rql2IntervalType())))
    else Right(ExpParam(Rql2TimestampType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2TimestampType())
  }

}

class TimestampSubtractEntry
    extends ShortEntryExtension(
      "Timestamp",
      "Subtract",
      mandatoryParams = Vector(Rql2TimestampType(), Rql2TimestampType()),
      returnType = Rql2IntervalType(),
      EntryDoc(
        summary = "Subtracts two timestamps.",
        params = List(
          ParamDoc("timestamp1", TypeDoc(List("timestamp")), "Timestamp to be subtracted (minuend)."),
          ParamDoc("timestamp2", TypeDoc(List("timestamp")), "Timestamp to subtract (subtrahend).")
        ),
        examples = List(
          ExampleDoc(
            """let
              |  t1 = Timestamp.Build(2019, 3, 4, 9, 30),
              |  t2 = Timestamp.Build(2018, 1, 1, 0, 0)
              |in
              |  Timestamp.Subtract(t1, t2)""".stripMargin,
            result = Some("interval: years=1, months=2, days=3, hours=9, minutes=30")
          )
        ),
        ret = Some(
          ReturnDoc(
            "The interval between the two timestamps.",
            retType = Some(TypeDoc(List("interval")))
          )
        )
      )
    )

class TimestampAddIntervalEntry
    extends ShortEntryExtension(
      "Timestamp",
      "AddInterval",
      mandatoryParams = Vector(Rql2TimestampType(), Rql2IntervalType()),
      returnType = Rql2TimestampType(),
      EntryDoc(
        summary = "Adds an interval to a timestamp.",
        params = List(
          ParamDoc("timestamp", TypeDoc(List("timestamp")), "Start timestamp."),
          ParamDoc("interval", TypeDoc(List("interval")), "interval to add.")
        ),
        examples = List(
          ExampleDoc(
            """let
              |  t = Timestamp.Build(2018, 1, 1, 0, 0),
              |  i = Interval.Build(years = 1, months = 2, days = 3, hours = 9, minutes = 30)
              |in
              |  Timestamp.AddInterval(t, i)""".stripMargin,
            result = Some("2019-03-04 09:30:00")
          )
        ),
        ret = Some(
          ReturnDoc(
            "The timestamp with the interval added.",
            retType = Some(TypeDoc(List("timestamp")))
          )
        )
      )
    )

class TimestampSubtractIntervalEntry
    extends ShortEntryExtension(
      "Timestamp",
      "SubtractInterval",
      mandatoryParams = Vector(Rql2TimestampType(), Rql2IntervalType()),
      returnType = Rql2TimestampType(),
      EntryDoc(
        summary = "Subtracts an interval to a timestamp.",
        params = List(
          ParamDoc("timestamp", TypeDoc(List("timestamp")), "Start timestamp."),
          ParamDoc("interval", TypeDoc(List("interval")), "Interval to subtract.")
        ),
        examples = List(
          ExampleDoc(
            """let
              |  t = Timestamp.Build(2019, 3, 4, 9, 30),
              |  i = Interval.Build(years = 1, months = 2, days = 3, hours = 9, minutes = 30)
              |in
              |  Timestamp.Subtract(t, i)""".stripMargin,
            result = Some("2018-01-01 00:00:00")
          )
        ),
        ret = Some(
          ReturnDoc(
            "The timestamp with the interval subtracted.",
            retType = Some(TypeDoc(List("timestamp")))
          )
        )
      )
    )
