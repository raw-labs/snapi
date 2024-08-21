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

package com.rawlabs.compiler.snapi.rql2.builtin

import com.rawlabs.compiler.snapi.common.source._
import com.rawlabs.compiler.snapi.rql2.source._

object IntervalPackageBuilder {

  object Build {
    def apply(e: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Interval"), "Build"), Vector(FunAppArg(e, None)))
    }
  }

  object FromRawInterval {
    def apply(years: Int, months: Int, weeks: Int, days: Int, hours: Int, minutes: Int, seconds: Int, millis: Int)
        : Exp = {
      FunApp(
        Proj(PackageIdnExp("Interval"), "Build"),
        Vector(
          FunAppArg(IntConst(years.toString), Some("years")),
          FunAppArg(IntConst(months.toString), Some("months")),
          FunAppArg(IntConst(days.toString), Some("days")),
          FunAppArg(IntConst(hours.toString), Some("hours")),
          FunAppArg(IntConst(minutes.toString), Some("minutes")),
          FunAppArg(IntConst(seconds.toString), Some("seconds")),
          FunAppArg(IntConst(millis.toString), Some("millis"))
        )
      )

    }
  }

}
