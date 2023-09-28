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
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension, TruffleShortEntryExtension}
import raw.runtime.truffle.{ExpressionNode, RawLanguage}
import raw.runtime.truffle.ast.expressions.builtin.temporals.interval_package.{
  IntervalBuildNodeGen,
  IntervalDaysNodeGen,
  IntervalFromMillisNodeGen,
  IntervalHoursNodeGen,
  IntervalMillisNodeGen,
  IntervalMinutesNodeGen,
  IntervalMonthsNodeGen,
  IntervalParseNodeGen,
  IntervalSecondsNodeGen,
  IntervalToMillisNodeGen,
  IntervalWeeksNodeGen,
  IntervalYearsNodeGen
}
import raw.runtime.truffle.ast.expressions.literals.IntNode

class TruffleBuildIntervalEntry extends BuildIntervalEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    val y = args.collectFirst { case TruffleArg(e, _, Some("years")) => e }.getOrElse(new IntNode("0"))
    val m = args.collectFirst { case TruffleArg(e, _, Some("months")) => e }.getOrElse(new IntNode("0"))
    val w = args.collectFirst { case TruffleArg(e, _, Some("weeks")) => e }.getOrElse(new IntNode("0"))
    val d = args.collectFirst { case TruffleArg(e, _, Some("days")) => e }.getOrElse(new IntNode("0"))
    val h = args.collectFirst { case TruffleArg(e, _, Some("hours")) => e }.getOrElse(new IntNode("0"))
    val mi = args.collectFirst { case TruffleArg(e, _, Some("minutes")) => e }.getOrElse(new IntNode("0"))
    val s = args.collectFirst { case TruffleArg(e, _, Some("seconds")) => e }.getOrElse(new IntNode("0"))
    val ms = args.collectFirst { case TruffleArg(e, _, Some("millis")) => e }.getOrElse(new IntNode("0"))
    IntervalBuildNodeGen.create(y, m, w, d, h, mi, s, ms);
  }

}

class TruffleIntervalToMillisEntryExtension extends IntervalToMillisEntryExtension with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = IntervalToMillisNodeGen.create(args(0))
}

class TruffleIntervalFromMillisEntryExtension extends IntervalFromMillisEntryExtension with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = IntervalFromMillisNodeGen.create(args(0))
}

class TruffleIntervalParseEntryExtension extends IntervalParseEntryExtension with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = IntervalParseNodeGen.create(args(0))
}

class TruffleIntervalYearsEntry extends IntervalYearsEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = IntervalYearsNodeGen.create(args(0))
}

class TruffleIntervalMonthsEntry extends IntervalMonthsEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = IntervalMonthsNodeGen.create(args(0))
}

class TruffleIntervalWeeksEntry extends IntervalWeeksEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = IntervalWeeksNodeGen.create(args(0))
}

class TruffleIntervalDaysEntry extends IntervalDaysEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = IntervalDaysNodeGen.create(args(0))
}

class TruffleIntervalHoursEntry extends IntervalHoursEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = IntervalHoursNodeGen.create(args(0))
}

class TruffleIntervalMinutesEntry extends IntervalMinutesEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = IntervalMinutesNodeGen.create(args(0))
}

class TruffleIntervalSecondsEntry extends IntervalSecondsEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = IntervalSecondsNodeGen.create(args(0))
}

class TruffleIntervalMillisEntry extends IntervalMillisEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = IntervalMillisNodeGen.create(args(0))
}
