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
import raw.compiler.rql2.builtin.{RegexFirstMatchInEntry, RegexGroupsEntry, RegexMatchesEntry, RegexReplaceEntry}
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension}
import raw.runtime.truffle.{ExpressionNode, RawLanguage}
import raw.runtime.truffle.ast.expressions.builtin.regex_package._

class TruffleRegexReplaceEntry extends RegexReplaceEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    RegexReplaceNodeGen.create(args(0).e, args(1).e, args(2).e)
  }

}

class TruffleRegexMatchesEntry extends RegexMatchesEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    RegexMatchesNodeGen.create(args(0).e, args(1).e)
  }
}

class TruffleRegexFirstMatchInEntry extends RegexFirstMatchInEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    RegexFirstMatchInNodeGen.create(args(0).e, args(1).e)

  }

}

class TruffleRegexGroupsEntry extends RegexGroupsEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    RegexGroupsNodeGen.create(args(0).e, args(1).e)
  }
}
