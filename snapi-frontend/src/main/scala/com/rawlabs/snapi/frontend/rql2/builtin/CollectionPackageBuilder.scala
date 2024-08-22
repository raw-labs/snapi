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

object CollectionPackageBuilder {
  object Transform {
    def apply(x: Exp, f: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "Transform"), Vector(FunAppArg(x, None), FunAppArg(f, None)))
    }
  }

  object Filter {
    def apply(x: Exp, f: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "Filter"), Vector(FunAppArg(x, None), FunAppArg(f, None)))
    }
  }

  object From {
    def apply(x: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "From"), Vector(FunAppArg(x, None)))
    }
  }

  object TupleAvg {
    def apply(x: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "TupleAvg"), Vector(FunAppArg(x, None)))
    }
  }

  object First {
    def apply(x: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "First"), Vector(FunAppArg(x, None)))
    }
  }

  object Last {
    def apply(x: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "Last"), Vector(FunAppArg(x, None)))
    }
  }

  object Union {
    def apply(collections: Vector[Exp]): Exp = {
      val args = collections.map(x => FunAppArg(x, None))
      FunApp(Proj(PackageIdnExp("Collection"), "Union"), args)
    }
  }

  object Exists {
    def apply(list: Exp, predicate: Exp) = {
      FunApp(Proj(PackageIdnExp("Collection"), "Exists"), Vector(FunAppArg(list, None), FunAppArg(predicate, None)))
    }
  }

  object Distinct {
    def apply(x: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "Distinct"), Vector(FunAppArg(x, None)))
    }
  }

  object Empty {
    def apply(t: Type): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "Empty"), Vector(FunAppArg(TypeExp(t), None)))
    }
  }

  object EquiJoin {
    def apply(left: Exp, right: Exp, leftK: Exp, rightK: Exp): Exp = {
      val args = Vector(left, right, leftK, rightK).map(v => FunAppArg(v, None))
      FunApp(Proj(PackageIdnExp("Collection"), "EquiJoin"), args)
    }
  }

  object Join {
    def apply(left: Exp, right: Exp, filter: Exp): Exp = {
      val args = Vector(left, right, filter).map(v => FunAppArg(v, None))
      FunApp(Proj(PackageIdnExp("Collection"), "Join"), args)
    }
  }

  object InternalEquiJoin {
    def apply(left: Exp, right: Exp, leftK: Exp, rightK: Exp, remapF: Exp): Exp = {
      val args = Vector(left, right, leftK, rightK, remapF).map(v => FunAppArg(v, None))
      FunApp(Proj(PackageIdnExp("Collection"), "InternalEquiJoin"), args)
    }
  }

  object InternalJoin {
    def apply(left: Exp, right: Exp, remap: Exp, filter: Exp): Exp = {
      val args = Vector(left, right, remap, filter).map(v => FunAppArg(v, None))
      FunApp(Proj(PackageIdnExp("Collection"), "InternalJoin"), args)
    }
  }

  object Explode {
    def apply(left: Exp, right: Exp): Exp = {
      val args = Vector(left, right).map(v => FunAppArg(v, None))
      FunApp(Proj(PackageIdnExp("Collection"), "Explode"), args)
    }
  }

  object Unnest {
    def apply(in: Exp, f: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "Unnest"), Vector(FunAppArg(in, None), FunAppArg(f, None)))
    }
  }

  object OrderBy {
    def apply(c: Exp, keysAndOrders: Vector[Exp]): Exp = {
      val args = (c +: keysAndOrders).map(e => FunAppArg(e, None))
      FunApp(Proj(PackageIdnExp("Collection"), "OrderBy"), args)
    }
  }

  object Zip {
    def apply(list1: Exp, list2: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "Zip"), Vector(FunAppArg(list1, None), FunAppArg(list2, None)))
    }
  }

}
