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

package raw.compiler.rql2.truffle.builtin

import raw.compiler.base.source.Type
import raw.compiler.common.source.{IterableType, TryType}
import raw.compiler.rql2.builtin.ToCommonInternalEntry
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension}
import raw.runtime.truffle.ExpressionNode

class TruffleToCommonInternalEntry extends ToCommonInternalEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    args(0).t match {
      case TryType(IterableType(_)) => ???
      case IterableType(_) => ???
      case TryType(_) => ???
      case _ => args(0).e
    }
  }

}
