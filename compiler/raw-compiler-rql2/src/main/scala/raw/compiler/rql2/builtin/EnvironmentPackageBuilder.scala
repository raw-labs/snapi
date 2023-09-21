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
import raw.compiler.common.source.Exp
import raw.compiler.rql2.source._

object EnvironmentPackageBuilder {

  object Parameter {
    def apply(tipe: Type, name: Exp): Exp = {
      FunApp(
        Proj(PackageIdnExp("Environment"), "Parameter"),
        Vector(FunAppArg(TypeExp(tipe), None), FunAppArg(name, None))
      )
    }
  }

}
