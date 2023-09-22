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
import raw.runtime.truffle.ast.expressions.tryable.{TryableFlatMapNodeGen, TryableIsFailureNodeGen, TryableIsSuccessNodeGen, TryableMapNodeGen, TryableUnsafeGetNodeGen}

class TruffleTryTransformEntry extends TryTransformEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    TryableMapNodeGen.create(args(0).e, args(1).e)
  }

}

class TruffleTryIsErrorEntry extends TryIsErrorEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    TryableIsFailureNodeGen.create(args(0).e)
  }

}

class TruffleTryIsSuccessEntry extends TryIsSuccessEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    TryableIsSuccessNodeGen.create(args(0).e)
  }

}

class TruffleTryFlatMapEntry extends TryFlatMapEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    TryableFlatMapNodeGen.create(args(0).e, args(1).e)
  }

}

class TruffleTryUnsafeGetEntry extends TryUnsafeGetEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    TryableUnsafeGetNodeGen.create(args(0).e)
  }
}
