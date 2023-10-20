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

import raw.compiler.rql2.source._

import java.time.LocalDateTime

object TimestampPackageBuilder {

  object FromDate {
    def apply(e: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Timestamp"), "FromDate"), Vector(FunAppArg(e, None)))
    }
  }

  object FromLocalDateTime {
    def apply(d: LocalDateTime): Exp = {
      FunApp(
        Proj(PackageIdnExp("Timestamp"), "Build"),
        Vector(
          FunAppArg(IntConst(d.getYear.toString), None),
          FunAppArg(IntConst(d.getMonthValue.toString), None),
          FunAppArg(IntConst(d.getDayOfMonth.toString), None),
          FunAppArg(IntConst(d.getHour.toString), None),
          FunAppArg(IntConst(d.getMinute.toString), None),
          FunAppArg(IntConst(d.getSecond.toString), Some("seconds")),
          FunAppArg(IntConst((d.getNano / 1000000).toString), Some("millis"))
        )
      )
    }
  }

}
