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

package raw.compiler.rql2.truffle

import com.oracle.truffle.api.frame.FrameDescriptor
import raw.compiler.base.source.Type
import raw.compiler.common.source.Exp
import raw.compiler.rql2.{EntryExtension, Rql2Arg}
import raw.runtime.truffle.{ExpressionNode, RawLanguage}

final case class TruffleArg(e: ExpressionNode, t: Type, idn: Option[String])

trait TruffleEntryExtension { this: EntryExtension =>

  def toTruffle(
      t: Type,
      args: Seq[TruffleArg],
      rawLanguage: RawLanguage
  ): ExpressionNode = {
    ???
  }

  def toTruffle(
      t: Type,
      args: Seq[Rql2Arg],
      emitter: TruffleEmitter
  ): ExpressionNode = {
    toTruffle(t, args.map(arg => TruffleArg(emitter.recurseExp(arg.e), arg.t, arg.idn)), emitter.rawLanguage)
  }

}

trait TruffleEmitter {
  def addScope(): Unit
  def dropScope(): FrameDescriptor
  def recurseExp(in: Exp): ExpressionNode
  def recurseLambda(buildBody: () => ExpressionNode): ExpressionNode

  def rawLanguage: RawLanguage

}
