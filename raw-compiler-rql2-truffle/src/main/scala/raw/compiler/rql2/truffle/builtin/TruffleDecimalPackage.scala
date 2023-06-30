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
import raw.compiler.rql2.builtin.{DecimalFromEntry, DecimalRoundEntry}
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension, TruffleShortEntryExtension}
import raw.runtime.truffle.ExpressionNode
import raw.runtime.truffle.ast.expressions.builtin.numeric.decimal_package.{DecimalFromNodeGen, DecimalRoundNodeGen}

class TruffleDecimalFromEntry extends DecimalFromEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    DecimalFromNodeGen.create(args(0).e)
  }

}

class TruffleDecimalRoundEntry extends DecimalRoundEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = DecimalRoundNodeGen.create(args(0), args(1))
}
