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
import raw.compiler.common.source._
import raw.compiler.rql2.ProgramContext
import raw.compiler.rql2.builtin.{HttpCallEntry, HttpReadEntry, HttpUrlDecode, HttpUrlEncode}
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension, TruffleShortEntryExtension}
import raw.runtime.truffle.ExpressionNode

class TruffleHttpReadEntry extends HttpReadEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = ???
}

abstract class TruffleHttpCallEntry(method: String) extends HttpCallEntry(method) with TruffleEntryExtension {
  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = ???

}

class TruffleHttpGetEntry extends TruffleHttpCallEntry("get")

class TruffleHttpPostEntry extends TruffleHttpCallEntry("post")

class TruffleHttpPutEntry extends TruffleHttpCallEntry("put")

class TruffleHttpDeleteEntry extends TruffleHttpCallEntry("delete")

class TruffleHttpHeadEntry extends TruffleHttpCallEntry("head")

class TruffleHttpPatchEntry extends TruffleHttpCallEntry("patch")

class TruffleHttpOptionsEntry extends TruffleHttpCallEntry("options")

class TruffleHttpUrlEncode extends HttpUrlEncode with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = ???
}

class TruffleHttpUrlDecode extends HttpUrlDecode with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = ???
}
