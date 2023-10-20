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

import raw.compiler.common.source._
import raw.compiler.rql2.source._

import java.time.{LocalDate, LocalTime}

object TimePackageBuilder {

  object FromLocalTime {
    def apply(t: LocalTime): Exp = {
      FunApp(
        Proj(PackageIdnExp("Time"), "Build"),
        Vector(
          FunAppArg(IntConst(t.getHour.toString), None),
          FunAppArg(IntConst(t.getMinute.toString), None),
          FunAppArg(IntConst(t.getSecond.toString), Some("seconds")),
          FunAppArg(IntConst((t.getNano / 1000000).toString), Some("millis"))
        )
      )
    }
  }

  object FromLocalDate {
    def apply(d: LocalDate): Exp = {
      FunApp(
        Proj(PackageIdnExp("Date"), "Build"),
        Vector(
          FunAppArg(IntConst(d.getYear.toString), None),
          FunAppArg(IntConst(d.getMonthValue.toString), None),
          FunAppArg(IntConst(d.getDayOfMonth.toString), None)
        )
      )
    }
  }
}
