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
import com.rawlabs.snapi.frontend.rql2.api.{PackageExtension, ShortEntryExtension}
import com.rawlabs.snapi.frontend.rql2.source._

import scala.collection.immutable.ListMap

class TimePackage extends PackageExtension {

  /**
   * Name of the package.
   */
  override def name: String = "Time"

  /**
   * Package documentation.
   */
  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for the time type."
  )

}

class TimeBuildEntry
    extends ShortEntryExtension(
      "Time",
      "Build",
      mandatoryParams = Vector(Rql2IntType(), Rql2IntType()),
      returnType = Rql2TimeType(Set(Rql2IsTryableTypeProperty())),
      docs = EntryDoc(
        "Builds a time value.",
        params = List(
          ParamDoc("hours", TypeDoc(List("int")), "Hours component of the time to build."),
          ParamDoc("minutes", TypeDoc(List("int")), "Minutes component of the time to build."),
          ParamDoc("seconds", TypeDoc(List("int")), "Seconds component of the time to build.", isOptional = true),
          ParamDoc(
            "milliseconds",
            TypeDoc(List("int")),
            "Milliseconds component of the time to build.",
            isOptional = true
          )
        ),
        examples = List(
          ExampleDoc("""Time.Build(9, 30)""", result = Some("9:30:00.000")),
          ExampleDoc("""Time.Build(9, 30, seconds = 20)""", result = Some("9:30:20.000")),
          ExampleDoc("""Time.Build(9, 30, seconds = 20, millis = 10)""", result = Some("9:30:20.010"))
        ),
        ret = Some(ReturnDoc("The time built from the given components.", retType = Some(TypeDoc(List("time")))))
      ),
      optionalParamsMap = ListMap(
        "seconds" -> ((Rql2IntType(), IntConst("0"))),
        "millis" -> ((Rql2IntType(), IntConst("0")))
      )
    )

class TimeParseEntry
    extends ShortEntryExtension(
      "Time",
      "Parse",
      mandatoryParams = Vector(Rql2StringType(), Rql2StringType()),
      returnType = Rql2TimeType(Set(Rql2IsTryableTypeProperty())),
      EntryDoc(
        summary = "Parses a time from a string.",
        description = Some(
          "For more information about format strings see the [Temporal templates documentation](../temporal-templates)."
        ),
        params = List(
          ParamDoc("value", TypeDoc(List("string")), "The string to convert to time."),
          ParamDoc("format", TypeDoc(List("string")), "The format of the time.")
        ),
        examples = List(
          ExampleDoc("""Time.Parse("09:12:23.450", "H:m:s.SSS")""", result = Some("""9:12:23.450""""")),
          ExampleDoc("""Time.Parse("09:12 PM", "h:m a")""", result = Some("""21:12:00.000"""""))
        ),
        ret = Some(ReturnDoc("The time parsed from the given string.", retType = Some(TypeDoc(List("time")))))
      )
    )

class TimeNowEntry
    extends ShortEntryExtension(
      "Time",
      "Now",
      mandatoryParams = Vector(),
      returnType = Rql2TimeType(),
      EntryDoc(
        summary = "Returns the current time.",
        examples = List(ExampleDoc("""Time.Now()""")),
        ret = Some(ReturnDoc("The current time.", retType = Some(TypeDoc(List("time")))))
      )
    )

class TimeHourEntry
    extends ShortEntryExtension(
      "Time",
      "Hour",
      mandatoryParams = Vector(Rql2TimeType()),
      returnType = Rql2IntType(),
      EntryDoc(
        summary = "Returns the hours component of a time.",
        params = List(
          ParamDoc("time", TypeDoc(List("time")), "The time from which the hours component is extracted.")
        ),
        examples = List(ExampleDoc("""Time.Hour(Time.Build(9, 30))""", result = Some("9"))),
        ret = Some(ReturnDoc("The hours component of the given time.", retType = Some(TypeDoc(List("int")))))
      )
    )

class TimeMinuteEntry
    extends ShortEntryExtension(
      "Time",
      "Minute",
      mandatoryParams = Vector(Rql2TimeType()),
      returnType = Rql2IntType(),
      EntryDoc(
        summary = "Returns the minutes component of a time.",
        params = List(
          ParamDoc("time", TypeDoc(List("time")), "The time from which the minutes component is extracted.")
        ),
        examples = List(ExampleDoc("""Time.Minute(Time.Build(9, 30))""", result = Some("30"))),
        ret = Some(ReturnDoc("The minutes component of the given time.", retType = Some(TypeDoc(List("int")))))
      )
    )

class TimeSecondEntry
    extends ShortEntryExtension(
      "Time",
      "Second",
      mandatoryParams = Vector(Rql2TimeType()),
      returnType = Rql2IntType(),
      EntryDoc(
        summary = "Returns the seconds component of a time.",
        params = List(
          ParamDoc("time", TypeDoc(List("time")), "The time from which the seconds component is extracted.")
        ),
        examples = List(ExampleDoc("""Time.Seconds(Time.Build(9, 30, seconds=15))""", result = Some("15"))),
        ret = Some(ReturnDoc("The seconds component of the given time.", retType = Some(TypeDoc(List("int")))))
      )
    )

class TimeMillisEntry
    extends ShortEntryExtension(
      "Time",
      "Millis",
      mandatoryParams = Vector(Rql2TimeType()),
      returnType = Rql2IntType(),
      EntryDoc(
        summary = "Returns the milliseconds component of a time.",
        params = List(
          ParamDoc("time", TypeDoc(List("time")), "The time from which the milliseconds component is extracted.")
        ),
        examples = List(ExampleDoc("""Time.Millis(Time.Build(9, 30, millis=123))""", result = Some("123"))),
        ret = Some(ReturnDoc("The milliseconds component of the given time.", retType = Some(TypeDoc(List("int")))))
      )
    )

class TimeSubtractEntry
    extends ShortEntryExtension(
      "Time",
      "Subtract",
      mandatoryParams = Vector(Rql2TimeType(), Rql2TimeType()),
      returnType = Rql2IntervalType(),
      EntryDoc(
        summary = "Subtracts two times.",
        params = List(
          ParamDoc("time1", TypeDoc(List("time")), "Time to be subtracted (minuend)."),
          ParamDoc("time2", TypeDoc(List("time")), "Time to subtract (subtrahend).")
        ),
        examples = List(
          ExampleDoc(
            """let
              |  t1 = Time.Build(9, 30),
              |  t2 = Time.Build(0, 0)
              |in
              |  Time.Subtract(t1, t2)""".stripMargin,
            result = Some("""interval: hours=9, minutes=30"""")
          )
        ),
        ret = Some(ReturnDoc("The interval between the two times.", retType = Some(TypeDoc(List("interval")))))
      )
    )

class TimeAddIntervalEntry
    extends ShortEntryExtension(
      "Time",
      "AddInterval",
      mandatoryParams = Vector(Rql2TimeType(), Rql2IntervalType()),
      returnType = Rql2TimeType(),
      EntryDoc(
        summary = """Adds an interval to a time.""",
        warning = Some("Note that years, months, weeks and days of interval will be ignored."),
        params = List(
          ParamDoc("time", TypeDoc(List("time")), "Start time."),
          ParamDoc("interval", TypeDoc(List("interval")), "interval to add.")
        ),
        examples = List(
          ExampleDoc(
            """let
              |  t = Time.Build(0, 0),
              |  i = Interval.Build(hours = 9, minutes = 30)
              |in
              |  Time.AddInterval(t, i)""".stripMargin,
            result = Some("09:30")
          )
        ),
        ret = Some(
          ReturnDoc(
            "The time resulting from adding the interval to the start time.",
            retType = Some(TypeDoc(List("time")))
          )
        )
      )
    )

class TimeSubtractIntervalEntry
    extends ShortEntryExtension(
      "Time",
      "SubtractInterval",
      mandatoryParams = Vector(Rql2TimeType(), Rql2IntervalType()),
      returnType = Rql2TimeType(),
      EntryDoc(
        summary = """Subtracts an interval to a time.""",
        warning = Some("""Note that years, months, weeks and days of interval will be ignored."""),
        params = List(
          ParamDoc("time", TypeDoc(List("time")), "Start time."),
          ParamDoc("interval", TypeDoc(List("interval")), "Interval to subtract.")
        ),
        examples = List(
          ExampleDoc(
            """let
              |  t = Time.Build(9, 30),
              |  i = Interval.Build(hours = 9, minutes = 30)
              |in
              |  Time.SubtractInterval(t, i)""".stripMargin,
            result = Some("00:00")
          )
        ),
        ret = Some(
          ReturnDoc(
            "The time resulting from subtracting the interval to the start time.",
            retType = Some(TypeDoc(List("time")))
          )
        )
      )
    )
