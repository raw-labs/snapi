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

import com.rawlabs.snapi.frontend.base.source.Type
import com.rawlabs.snapi.frontend.rql2.source._
import com.rawlabs.snapi.frontend.rql2.source._

object TypePackageBuilder {

  def Cast(t: Type, e: Exp): Exp = {
    FunApp(Proj(PackageIdnExp("Type"), "Cast"), Vector(FunAppArg(TypeExp(t), None), FunAppArg(e, None)))
  }

  def ProtectCast(actual: Type, target: Type, e: Exp): Exp = {
    FunApp(
      Proj(PackageIdnExp("Type"), "ProtectCast"),
      Vector(FunAppArg(TypeExp(actual), None), FunAppArg(TypeExp(target), None), FunAppArg(e, None))
    )
  }
  def Empty(t: Type): Exp = {
    FunApp(Proj(PackageIdnExp("Type"), "Empty"), Vector(FunAppArg(TypeExp(t), None)))
  }
}
