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
import raw.runtime.truffle.ExpressionNode
import raw.runtime.truffle.ast.expressions.tryable.{
  TryableFailureNodeGen,
  TryableFailureWithTypeNodeGen,
  TryableGetFailureNodeGen
}

class TruffleErrorBuildEntry extends ErrorBuildEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    TryableFailureNodeGen.create(args(0).e)
  }

}

class TruffleErrorBuildWithTypeEntry extends ErrorBuildWithTypeEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    TryableFailureWithTypeNodeGen.create(args(0).e, args(1).e)
  }

}

class TruffleErrorGetEntry extends ErrorGetEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    TryableGetFailureNodeGen.create(args(0).e)
  }

}
