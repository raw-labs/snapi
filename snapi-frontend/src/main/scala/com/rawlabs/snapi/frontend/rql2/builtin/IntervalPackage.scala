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

import com.rawlabs.compiler.api.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import com.rawlabs.snapi.frontend.base.source.Type
import com.rawlabs.snapi.frontend.rql2._
import com.rawlabs.snapi.frontend.rql2.api.{Arg, EntryExtension, ExpParam, PackageExtension, Param, ShortEntryExtension}
import com.rawlabs.snapi.frontend.rql2.source._

class IntervalPackage extends PackageExtension {

  override def name: String = "Interval"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for the interval type."
  )

}

class BuildIntervalEntry extends EntryExtension {

  override def packageName: String = "Interval"

  override def entryName: String = "Build"

  override def docs: EntryDoc = EntryDoc(
    "Creates an interval.",
    params = List(
      ParamDoc(
        "years",
        typeDoc = TypeDoc(List("int")),
        description = """Number of years in the interval.""",
        isOptional = true
      ),
      ParamDoc(
        "months",
        typeDoc = TypeDoc(List("int")),
        description = """Number of months in the interval.""",
        isOptional = true
      ),
      ParamDoc(
        "weeks",
        typeDoc = TypeDoc(List("int")),
        description = """Number of weeks in the interval.""",
        isOptional = true
      ),
      ParamDoc(
        "days",
        typeDoc = TypeDoc(List("int")),
        description = """Number of days in the interval.""",
        isOptional = true
      ),
      ParamDoc(
        "hours",
        typeDoc = TypeDoc(List("int")),
        description = """Number of hours in the interval.""",
        isOptional = true
      ),
      ParamDoc(
        "minutes",
        typeDoc = TypeDoc(List("int")),
        description = """Number of minutes in the interval.""",
        isOptional = true
      ),
      ParamDoc(
        "seconds",
        typeDoc = TypeDoc(List("int")),
        description = """Number of seconds in the interval.""",
        isOptional = true
      ),
      ParamDoc(
        "millis",
        typeDoc = TypeDoc(List("int")),
        description = """Number of milli-seconds in the interval.""",
        isOptional = true
      )
    ),
    examples = List(
      ExampleDoc("""Interval.Build(hours = 1, minutes = 30)"""),
      ExampleDoc("""Interval.Build(years  = 3, months = 6, days = 5)""")
    ),
    ret = Some(
      ReturnDoc(
        """The interval representation of the value.""",
        Some(TypeDoc(List("interval")))
      )
    )
  )

  override def nrMandatoryParams: Int = 0

  override def optionalParams: Option[Set[String]] = Some(
    Set(
      "years",
      "months",
      "weeks",
      "days",
      "hours",
      "minutes",
      "seconds",
      "millis"
    )
  )

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    Right(ExpParam(Rql2IntType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2IntervalType())
  }

}

class IntervalToMillisEntryExtension
    extends ShortEntryExtension(
      "Interval",
      "ToMillis",
      mandatoryParams = Vector(Rql2IntervalType()),
      returnType = Rql2LongType(),
      EntryDoc(
        summary = "Converts a interval to the corresponding number of milliseconds",
        examples = List(
          ExampleDoc("""Interval.ToMillis(Interval.Build(minutes=1))""", result = Some("60000")),
          ExampleDoc(
            """Interval.ToMillis(Interval.Build(days=1))""",
            result = Some("86400000")
          )
        ),
        params = List(
          ParamDoc("value", TypeDoc(List("interval")), "The interval to be converted to milliseconds.")
        ),
        ret = Some(
          ReturnDoc(
            """The number of milliseconds in the interval.""",
            Some(TypeDoc(List("long")))
          )
        )
      )
    )

class IntervalFromMillisEntryExtension
    extends ShortEntryExtension(
      "Interval",
      "FromMillis",
      mandatoryParams = Vector(Rql2LongType()),
      returnType = Rql2IntervalType(),
      EntryDoc(
        summary = "Converts a number of milliseconds to the corresponding interval",
        examples = List(
          ExampleDoc("""Interval.FromMillis(60000)""", result = Some("Interval(0,0,0,0,0,1,0,0)"))
        ),
        params = List(
          ParamDoc("mills", TypeDoc(List("long")), "The number of milliseconds to be converted to interval.")
        ),
        ret = Some(
          ReturnDoc(
            """The interval representation of the value.""",
            Some(TypeDoc(List("interval")))
          )
        )
      )
    )

class IntervalParseEntryExtension
    extends ShortEntryExtension(
      "Interval",
      "Parse",
      mandatoryParams = Vector(Rql2StringType()),
      returnType = Rql2IntervalType(Set(Rql2IsTryableTypeProperty())),
      EntryDoc(
        """"Parses an interval from a string.""",
        info = Some(
          """The interval format for the string has to be the ISO-8601 for durations, see https://en.wikipedia.org/wiki/ISO_8601#Durations."""
        ),
        examples = List(
          ExampleDoc(
            """Interval.Parse("P1Y2M") // interval 1 year, 2 months""",
            result = Some("Interval(1,2,0,0,0,0,0,0)")
          ),
          ExampleDoc(
            """Interval.Parse("PT1M2S") // interval 1 minute, 2 seconds""",
            result = Some("Interval(0,0,0,0,0,1,2,0)")
          ),
          ExampleDoc(
            """Interval.Parse("P1Y2M3DT4H5M6.007S") // interval 1 year, 2 months, 3 days, 4 hours, 5 minutes, 6 seconds, 7 milliseconds""",
            result = Some("Interval(1,2,0,3,4,5,6,7)")
          )
        ),
        params = List(
          ParamDoc("str", TypeDoc(List("string")), "The string to be parsed as interval.")
        ),
        ret = Some(
          ReturnDoc(
            """The interval representation of the string.""",
            Some(TypeDoc(List("interval")))
          )
        )
      )
    )

class IntervalYearsEntry
    extends ShortEntryExtension(
      "Interval",
      "Years",
      mandatoryParams = Vector(Rql2IntervalType()),
      returnType = Rql2IntType(),
      EntryDoc(
        summary = """"Gets the years part of an interval.""",
        examples = List(
          ExampleDoc("""Interval.Years(Interval.Build(years = 2, days = 20))""", result = Some("2"))
        ),
        params = List(
          ParamDoc("value", TypeDoc(List("interval")), "The interval to get the years from.")
        ),
        ret = Some(
          ReturnDoc(
            """The number of years in the interval.""",
            Some(TypeDoc(List("int")))
          )
        )
      )
    )

class IntervalMonthsEntry
    extends ShortEntryExtension(
      "Interval",
      "Months",
      mandatoryParams = Vector(Rql2IntervalType()),
      returnType = Rql2IntType(),
      EntryDoc(
        summary = """"Gets the months part of an interval.""",
        examples = List(
          ExampleDoc("""Interval.Months(Interval.Build(years = 2, months = 10))""", result = Some("10"))
        ),
        params = List(
          ParamDoc("value", TypeDoc(List("interval")), "The interval to get the months from.")
        ),
        ret = Some(
          ReturnDoc(
            """The number of months in the interval.""",
            Some(TypeDoc(List("int")))
          )
        )
      )
    )

class IntervalWeeksEntry
    extends ShortEntryExtension(
      "Interval",
      "Weeks",
      mandatoryParams = Vector(Rql2IntervalType()),
      returnType = Rql2IntType(),
      EntryDoc(
        summary = """"Gets the weeks part of an interval.""",
        examples = List(
          ExampleDoc("""Interval.Weeks(Interval.Build(years = 2, weeks = 4))""", result = Some("4"))
        ),
        params = List(
          ParamDoc("value", TypeDoc(List("interval")), "The interval to get the weeks from.")
        ),
        ret = Some(
          ReturnDoc(
            """The number of weeks in the interval.""",
            Some(TypeDoc(List("int")))
          )
        )
      )
    )

class IntervalDaysEntry
    extends ShortEntryExtension(
      "Interval",
      "Days",
      mandatoryParams = Vector(Rql2IntervalType()),
      returnType = Rql2IntType(),
      EntryDoc(
        summary = """"Gets the days part of an interval.""",
        examples = List(
          ExampleDoc("""Interval.Days(Interval.Build(years = 2, days = 20))""", result = Some("20"))
        ),
        params = List(
          ParamDoc("value", TypeDoc(List("interval")), "The interval to get the days from.")
        ),
        ret = Some(
          ReturnDoc(
            """The number of days in the interval.""",
            Some(TypeDoc(List("int")))
          )
        )
      )
    )

class IntervalHoursEntry
    extends ShortEntryExtension(
      "Interval",
      "Hours",
      mandatoryParams = Vector(Rql2IntervalType()),
      returnType = Rql2IntType(),
      EntryDoc(
        summary = """"Gets the hours part of an interval.""",
        examples = List(
          ExampleDoc("""Interval.Hours(Interval.Build(years = 2, hours = 12))""", result = Some("12"))
        ),
        params = List(
          ParamDoc("value", TypeDoc(List("interval")), "The interval to get the hours from.")
        ),
        ret = Some(
          ReturnDoc(
            """The number of hours in the interval.""",
            Some(TypeDoc(List("int")))
          )
        )
      )
    )

class IntervalMinutesEntry
    extends ShortEntryExtension(
      "Interval",
      "Minutes",
      mandatoryParams = Vector(Rql2IntervalType()),
      returnType = Rql2IntType(),
      EntryDoc(
        summary = """"Gets the minutes part of an interval.""",
        examples = List(
          ExampleDoc("""Interval.Minutes(Interval.Build(years = 2, minutes = 15))""", result = Some("15"))
        ),
        params = List(
          ParamDoc("value", TypeDoc(List("interval")), "The interval to get the minutes from.")
        ),
        ret = Some(
          ReturnDoc(
            """The number of minutes in the interval.""",
            Some(TypeDoc(List("int")))
          )
        )
      )
    )

class IntervalSecondsEntry
    extends ShortEntryExtension(
      "Interval",
      "Seconds",
      mandatoryParams = Vector(Rql2IntervalType()),
      returnType = Rql2IntType(),
      EntryDoc(
        summary = """"Gets the seconds part of an interval.""",
        examples = List(
          ExampleDoc("""Interval.Seconds(Interval.Build(years = 2, seconds = 45))""", result = Some("45"))
        ),
        params = List(
          ParamDoc("value", TypeDoc(List("interval")), "The interval to get the seconds from.")
        ),
        ret = Some(
          ReturnDoc(
            """The number of seconds in the interval.""",
            Some(TypeDoc(List("int")))
          )
        )
      )
    )

class IntervalMillisEntry
    extends ShortEntryExtension(
      "Interval",
      "Millis",
      mandatoryParams = Vector(Rql2IntervalType()),
      returnType = Rql2IntType(),
      EntryDoc(
        summary = """"Gets the milliseconds part of an interval.""",
        examples = List(
          ExampleDoc("""Interval.Millis(Interval.Build(years = 2, millis = 230))""", result = Some("230"))
        ),
        params = List(
          ParamDoc("value", TypeDoc(List("interval")), "The interval to get the milli-seconds from.")
        ),
        ret = Some(
          ReturnDoc(
            """The number of milliseconds in the interval.""",
            Some(TypeDoc(List("int")))
          )
        )
      )
    )
