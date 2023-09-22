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
import raw.compiler.rql2.builtin.{LongFromEntry, LongRangeEntry}
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension}
import raw.runtime.truffle.{ExpressionNode, RawLanguage}
import raw.runtime.truffle.ast.expressions.builtin.numeric.long_package.{LongFromNodeGen, LongRangeNodeGen}
import raw.runtime.truffle.ast.expressions.literals.LongNode

class TruffleLongFromEntry extends LongFromEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    LongFromNodeGen.create(args(0).e);
  }

}

class TruffleLongRangeEntry extends LongRangeEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    val start = args(0).e
    val end = args(1).e
    val step = args.collectFirst { case arg if arg.idn.contains("step") => arg.e }.getOrElse(new LongNode("1"))
    LongRangeNodeGen.create(start, end, step);
  }

}
