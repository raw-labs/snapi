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

object ShortPackageBuilder {

  object From {
    def apply(e: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Short"), "From"), Vector(FunAppArg(e, None)))
    }
  }
}
