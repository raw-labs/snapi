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

object LocationPackageBuilder {

  object FromString {
    def apply(url: Exp): Exp = {
      FunApp(
        Proj(PackageIdnExp("Location"), "FromString"),
        Vector(FunAppArg(url, None))
      )
    }
  }

}
