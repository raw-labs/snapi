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
import raw.compiler.rql2.source._
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension}
import raw.runtime.truffle.{ExpressionNode, RawLanguage}
import raw.runtime.truffle.ast.expressions.builtin.location_package.{
  LocationBuildNode,
  LocationDescribeNodeGen,
  LocationLlNodeGen,
  LocationLsNodeGen
}
import raw.runtime.truffle.ast.expressions.literals.IntNode

class TruffleLocationBuildEntry extends LocationBuildEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    val keys = args.collect { case TruffleArg(_, _, Some(idn)) => idn.replace("_", "-") }
    val values = args.collect { case TruffleArg(e, _, Some(_)) => e }
    val types = args.collect { case TruffleArg(_, t, Some(_)) => t.asInstanceOf[Rql2TypeWithProperties] }
    new LocationBuildNode(
      args(0).e,
      keys.toArray,
      values.toArray,
      types.toArray
    )
  }

}

class TruffleLocationDescribeEntry extends LocationDescribeEntry with TruffleEntryExtension {
  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    val sampleSize = args
      .collectFirst { case arg if arg.idn.contains("sampleSize") => arg.e }
      .getOrElse(new IntNode(Integer.MIN_VALUE.toString))
    LocationDescribeNodeGen.create(args(0).e, sampleSize)
  }
}

class TruffleLocationLsEntry extends LocationLsEntry with TruffleEntryExtension {
  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    LocationLsNodeGen.create(args(0).e)
  }
}

class TruffleLocationLlEntry extends LocationLlEntry with TruffleEntryExtension {
  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    LocationLlNodeGen.create(args(0).e)
  }
}
