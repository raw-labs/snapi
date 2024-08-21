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

package com.rawlabs.compiler.snapi.utils

import org.bitbucket.inkytonik.kiama.==>
import org.bitbucket.inkytonik.kiama.rewriting.Rewriter.id
import org.bitbucket.inkytonik.kiama.rewriting.Strategy
import org.bitbucket.inkytonik.kiama.util.Collections.Factory

import scala.language.higherKinds

trait ExtraRewriters {

  def ifSet(flag: Boolean, s: Strategy): Strategy = {
    if (flag) s else id
  }

  /**
   * Only collect nodes of base type N.
   * Required to ensure safe collection within the tree (since trees have a base type).
   * (And for general-purposed use, can always set N as Any.)
   */
  def collectNodes[N: Manifest, CC[X] <: Iterable[X], U](f: N ==> U)(implicit cbf: Factory[U, CC[U]]): Any => CC[U] = {
    org.bitbucket.inkytonik.kiama.rewriting.Rewriter.collect[CC, U] {
      case n: N if f.isDefinedAt(n) => f(n)
    }
  }

}
