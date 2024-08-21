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

import com.rawlabs.compiler.snapi.base.source.Type
import com.rawlabs.compiler.snapi.common.source._
import com.rawlabs.compiler.snapi.rql2.source._

object ListPackageBuilder {

  object Build {
    def apply(es: Exp*): Exp = {
      FunApp(Proj(PackageIdnExp("List"), "Build"), es.map(e => FunAppArg(e, None)).to)
    }
    def unapply(e: Exp): Option[Seq[Exp]] = e match {
      case FunApp(Proj(PackageIdnExp("List"), "Build"), es) => Some(es.map(_.e))
      case _ => None
    }
  }

  object Transform {
    def apply(x: Exp, f: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("List"), "Transform"), Vector(FunAppArg(x, None), FunAppArg(f, None)))
    }
  }

  object Filter {
    def apply(x: Exp, f: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("List"), "Filter"), Vector(FunAppArg(x, None), FunAppArg(f, None)))
    }
  }

  object UnsafeFrom {
    def apply(x: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("List"), "UnsafeFrom"), Vector(FunAppArg(x, None)))
    }
  }

  object First {
    def apply(x: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("List"), "First"), Vector(FunAppArg(x, None)))
    }
  }

  object Last {
    def apply(x: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("List"), "Last"), Vector(FunAppArg(x, None)))
    }
  }

  object Empty {
    def apply(t: Type): Exp = {
      FunApp(Proj(PackageIdnExp("List"), "Empty"), Vector(FunAppArg(TypeExp(t), None)))
    }
  }

  object Exists {
    def apply(list: Exp, predicate: Exp) = {
      FunApp(Proj(PackageIdnExp("List"), "Exists"), Vector(FunAppArg(list, None), FunAppArg(predicate, None)))
    }
  }
}
