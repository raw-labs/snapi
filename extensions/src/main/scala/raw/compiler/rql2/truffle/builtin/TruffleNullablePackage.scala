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
import raw.compiler.rql2.builtin._
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension}
import raw.runtime.truffle.{ExpressionNode, RawLanguage}
import raw.runtime.truffle.ast.expressions.option.{OptionIsDefinedNodeGen, OptionMapNodeGen, OptionNoneNode, OptionSomeNodeGen, OptionUnsafeGetNodeGen}
import raw.runtime.truffle.ast.expressions.unary.NotNodeGen

class TruffleNullableEmptyEntry extends NullableEmptyEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    new OptionNoneNode(t)
  }

}

class TruffleNullableBuildEntry extends NullableBuildEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    OptionSomeNodeGen.create(args(0).e)
  }

}

class TruffleNullableIsNullEntry extends NullableIsNullEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    NotNodeGen.create(OptionIsDefinedNodeGen.create(args(0).e))
  }

}

class TruffleNullableUnsafeGetEntry extends NullableUnsafeGetEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    OptionUnsafeGetNodeGen.create(args(0).e)
  }

}

class TruffleNullableTransformEntry extends NullableTransformEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    OptionMapNodeGen.create(args(0).e, args(1).e)
  }

}
