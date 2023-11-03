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

import raw.compiler.base.source.Type
import raw.compiler.common.source._
import raw.compiler.rql2.source._

object ErrorPackageBuilder {

  object BuildWithType {
    def apply(t: Type, e: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Error"), "BuildWithType"), Vector(FunAppArg(TypeExp(t), None), FunAppArg(e, None)))
    }
  }

  object Get {
    def apply(e: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Error"), "Get"), Vector(FunAppArg(e, None)))
    }
  }

}
