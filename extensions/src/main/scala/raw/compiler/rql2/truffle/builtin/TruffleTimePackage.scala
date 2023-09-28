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

import raw.compiler.rql2.builtin._
import raw.compiler.rql2.truffle._
import raw.runtime.truffle.ExpressionNode
import raw.runtime.truffle.ast.expressions.builtin.temporals.time_package.{
  TimeAddIntervalNodeGen,
  TimeBuildNodeGen,
  TimeHourNodeGen,
  TimeMillisNodeGen,
  TimeMinuteNodeGen,
  TimeNowNodeGen,
  TimeParseNodeGen,
  TimeSecondNodeGen,
  TimeSubtractIntervalNodeGen,
  TimeSubtractNodeGen
}

class TruffleTimeBuildEntry extends TimeBuildEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode =
    TimeBuildNodeGen.create(args.head, args(1), args(2), args(3))
}

class TruffleTimeParseEntry extends TimeParseEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimeParseNodeGen.create(args.head, args(1))
}

class TruffleTimeNowEntry extends TimeNowEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimeNowNodeGen.create()
}

class TruffleTimeHourEntry extends TimeHourEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimeHourNodeGen.create(args.head)
}

class TruffleTimeMinuteEntry extends TimeMinuteEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimeMinuteNodeGen.create(args.head)
}

class TruffleTimeSecondEntry extends TimeSecondEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimeSecondNodeGen.create(args.head)
}

class TruffleTimeMillisEntry extends TimeMillisEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimeMillisNodeGen.create(args.head)
}

class TruffleTimeSubtractEntry extends TimeSubtractEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimeSubtractNodeGen.create(args.head, args(1))
}

class TruffleTimeAddIntervalEntry extends TimeAddIntervalEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimeAddIntervalNodeGen.create(args.head, args(1))
}

class TruffleTimeSubtractIntervalEntry extends TimeSubtractIntervalEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode =
    TimeSubtractIntervalNodeGen.create(args.head, args(1))
}
