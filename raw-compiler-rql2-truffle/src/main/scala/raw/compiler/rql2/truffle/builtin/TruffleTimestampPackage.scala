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
import raw.compiler.rql2.source._
import raw.compiler.rql2.builtin._
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension, TruffleShortEntryExtension}
import raw.runtime.truffle.ExpressionNode
import raw.runtime.truffle.ast.expressions.builtin.temporals.interval_package.IntervalBuildNodeGen
import raw.runtime.truffle.ast.expressions.builtin.temporals.timestamp_package.{
  TimestampAddIntervalNodeGen,
  TimestampBuildNodeGen,
  TimestampDayNodeGen,
  TimestampFromDateNodeGen,
  TimestampFromUnixTimestampNodeGen,
  TimestampHourNodeGen,
  TimestampMillisNodeGen,
  TimestampMinuteNodeGen,
  TimestampMonthNodeGen,
  TimestampNowNodeGen,
  TimestampParseNodeGen,
  TimestampRangeNodeGen,
  TimestampSecondNodeGen,
  TimestampSubtractIntervalNodeGen,
  TimestampSubtractNodeGen,
  TimestampTimeBucketIntervalNodeGen,
  TimestampTimeBucketStringNodeGen,
  TimestampToUnixTimestampNodeGen,
  TimestampYearNodeGen
}
import raw.runtime.truffle.ast.expressions.literals.IntNode

class TruffleTimestampBuildEntry extends TimestampBuildEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = {
    TimestampBuildNodeGen.create(
      args(0),
      args(1),
      args(2),
      args(3),
      args(4),
      args(5),
      args(6)
    )
  }
}

class TruffleTimestampFromDateEntry extends TimestampFromDateEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimestampFromDateNodeGen.create(args(0))
}

class TruffleTimestampParseEntry extends TimestampParseEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimestampParseNodeGen.create(args(0), args(1))
}

class TruffleTimestampNowEntry extends TimestampNowEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimestampNowNodeGen.create()
}

class TruffleTimestampRangeEntry extends TimestampRangeEntry with TruffleEntryExtension {
  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    val start = args(0).e
    val end = args(1).e
    val step = args
      .collectFirst { case arg if arg.idn.contains("step") => arg.e }
      .getOrElse(
        IntervalBuildNodeGen.create(
          new IntNode("0"),
          new IntNode("0"),
          new IntNode("0"),
          new IntNode("1"),
          new IntNode("0"),
          new IntNode("0"),
          new IntNode("0"),
          new IntNode("0")
        )
      )
    TimestampRangeNodeGen.create(start, end, step)
  }

}

class TruffleTimestampYearEntry extends TimestampYearEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimestampYearNodeGen.create(args(0))
}

class TruffleTimestampMonthEntry extends TimestampMonthEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimestampMonthNodeGen.create(args(0))
}

class TruffleTimestampDayEntry extends TimestampDayEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimestampDayNodeGen.create(args(0))
}

class TruffleTimestampHourEntry extends TimestampHourEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimestampHourNodeGen.create(args(0))
}

class TruffleTimestampMinuteEntry extends TimestampMinuteEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimestampMinuteNodeGen.create(args(0))
}

class TruffleTimestampSecondEntry extends TimestampSecondEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimestampSecondNodeGen.create(args(0))
}

class TruffleTimestampMillisEntry extends TimestampMillisEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimestampMillisNodeGen.create(args(0))
}

class TruffleTimestampFromUnixTimestampEntry extends TimestampFromUnixTimestampEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimestampFromUnixTimestampNodeGen.create(args(0))
}

class TruffleTimestampToUnixTimestampEntry extends TimestampToUnixTimestampEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimestampToUnixTimestampNodeGen.create(args(0))
}

class TruffleTimestampTimeBucketEntry extends TimestampTimeBucketEntry with TruffleEntryExtension {
  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    args(0).t match {
      case Rql2StringType(_) => TimestampTimeBucketStringNodeGen.create(args(0).e, args(1).e)
      case Rql2IntervalType(_) => TimestampTimeBucketIntervalNodeGen.create(args(0).e, args(1).e)
    }
  }

}

class TruffleTimestampSubtractEntry extends TimestampSubtractEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = TimestampSubtractNodeGen.create(args(0), args(1))
}

class TruffleTimestampAddIntervalEntry extends TimestampAddIntervalEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode =
    TimestampAddIntervalNodeGen.create(args(0), args(1))
}

class TruffleTimestampSubtractIntervalEntry extends TimestampSubtractIntervalEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode =
    TimestampSubtractIntervalNodeGen.create(args(0), args(1))
}
