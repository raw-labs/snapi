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

import com.typesafe.scalalogging.StrictLogging
import org.bitbucket.inkytonik.kiama.relation.Bridge
import raw.compiler.base.source.RawBridge
import raw.compiler.common.source._

trait FreeVars extends StrictLogging {

  def freeVars(n: SourceNode): Set[String] = n match {
    case IdnExp(IdnUse(idn)) => Set(idn)
    case n => n.productIterator.flatMap(resolve).toSet
  }

  private def resolve(n: Any): Set[String] = n match {
    case n1: SourceNode => freeVars(n1)
    case _: Bridge[_] => Set.empty
    case _: RawBridge[_] => Set.empty
    case p: Product => p.productIterator.flatMap(resolve).toSet
    case t: Traversable[_] => t.flatMap(resolve).toSet
    case _: String | _: Integer | _: Boolean => Set.empty
  }

}
