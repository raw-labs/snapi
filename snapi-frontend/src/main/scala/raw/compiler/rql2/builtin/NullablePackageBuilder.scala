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
import raw.compiler.rql2.source._

object NullablePackageBuilder {

  object Empty {
    def apply(t: Type): Exp = {
      FunApp(Proj(PackageIdnExp("Nullable"), "Empty"), Vector(FunAppArg(TypeExp(t), None)))
    }
  }

  object UnsafeGet {
    def apply(e: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Nullable"), "UnsafeGet"), Vector(FunAppArg(e, None)))
    }
  }

  object Build {
    def apply(e: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Nullable"), "Build"), Vector(FunAppArg(e, None)))
    }
  }

  object Transform {
    def apply(f: Exp, e: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Nullable"), "Transform"), Vector(FunAppArg(f, None), FunAppArg(e, None)))
    }
  }
}
