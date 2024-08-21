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

import java.time.LocalDate

object DatePackageBuilder {

  object FromTimestamp {
    def apply(e: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Date"), "FromTimestamp"), Vector(FunAppArg(e, None)))
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
