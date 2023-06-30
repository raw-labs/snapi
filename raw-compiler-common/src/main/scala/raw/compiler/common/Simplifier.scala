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

package raw.compiler.common

import org.bitbucket.inkytonik.kiama.rewriting.Strategy
import org.bitbucket.inkytonik.kiama.rewriting.Rewriter._
import raw.compiler.base.ProgramContext
import raw.compiler.common.source._

trait Simplifier extends FreeVars {

  protected def programContext: ProgramContext

  def simplify: Strategy

  final protected lazy val simplificationsCommon: Strategy = rulefs[CommonExp] {
    case _: CommonExp => fail // There are no common simplifications
  }

}
