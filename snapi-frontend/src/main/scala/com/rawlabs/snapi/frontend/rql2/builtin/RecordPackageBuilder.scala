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

import com.rawlabs.snapi.frontend.common.source._
import com.rawlabs.snapi.frontend.rql2.source._

object RecordPackageBuilder {
  object Build {
    def apply(atts: Vector[(String, Exp)]): Exp = {
      FunApp(Proj(PackageIdnExp("Record"), "Build"), atts.map { case (idn, e) => FunAppArg(e, Some(idn)) }.to)
    }
    def apply(atts: Exp*): Exp = {
      FunApp(
        Proj(PackageIdnExp("Record"), "Build"),
        atts.zipWithIndex.map { case (e, idx) => FunAppArg(e, Some(s"_${idx + 1}")) }.to
      )
    }
    def unapply(e: Exp): Option[Vector[(String, Exp)]] = e match {
      case FunApp(Proj(PackageIdnExp("Record"), "Build"), atts) =>
        Some(atts.map { case FunAppArg(e, Some(idn)) => (idn, e) })
      case _ => None
    }
  }

  object AddField {
    def apply(r: Exp, e: Exp, name: String) =
      FunApp(Proj(PackageIdnExp("Record"), "AddField"), Vector(FunAppArg(r, None), FunAppArg(e, Some(name))))
  }

  object Concat {
    def apply(r1: Exp, r2: Exp) =
      FunApp(Proj(PackageIdnExp("Record"), "Concat"), Vector(FunAppArg(r1, None), FunAppArg(r2, None)))
  }

  object GetFieldByIndex {
    def apply(r: Exp, idx: Exp) =
      FunApp(Proj(PackageIdnExp("Record"), "GetFieldByIndex"), Vector(FunAppArg(r, None), FunAppArg(idx, None)))
  }
}
