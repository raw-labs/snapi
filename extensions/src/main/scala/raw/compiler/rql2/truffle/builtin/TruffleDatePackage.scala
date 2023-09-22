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
import raw.compiler.rql2.truffle.TruffleShortEntryExtension
import raw.runtime.truffle.ExpressionNode
import raw.runtime.truffle.ast.expressions.builtin.temporals.date_package.{
  DateAddIntervalNodeGen,
  DateBuildNodeGen,
  DateDayNodeGen,
  DateFromEpochDayNodeGen,
  DateFromTimestampNodeGen,
  DateMonthNodeGen,
  DateNowNodeGen,
  DateParseNodeGen,
  DateSubtractIntervalNodeGen,
  DateSubtractNodeGen,
  DateYearNodeGen
}

class TruffleDateBuildEntry extends DateBuildEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = DateBuildNodeGen.create(args(0), args(1), args(2))
}

class TruffleDateFromEpochDayEntry extends DateFromEpochDayEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = DateFromEpochDayNodeGen.create(args(0))
}

class TruffleDateFromTimestampEntry extends DateFromTimestampEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = DateFromTimestampNodeGen.create(args(0))
}

class TruffleDateParseEntry extends DateParseEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = DateParseNodeGen.create(args(0), args(1))
}

class TruffleDateNowEntry extends DateNowEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = DateNowNodeGen.create()
}

class TruffleDateYearEntry extends DateYearEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = DateYearNodeGen.create(args(0))
}

class TruffleDateMonthEntry extends DateMonthEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = DateMonthNodeGen.create(args(0))
}

class TruffleDateDayEntry extends DateDayEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = DateDayNodeGen.create(args(0))
}

class TruffleDateSubtractEntry extends DateSubtractEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = DateSubtractNodeGen.create(args(0), args(1))
}

class TruffleDateAddIntervalEntry extends DateAddIntervalEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = DateAddIntervalNodeGen.create(args(0), args(1))
}

class TruffleDateSubtractIntervalEntry extends DateSubtractIntervalEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode =
    DateSubtractIntervalNodeGen.create(args(0), args(1))
}
