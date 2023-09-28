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
import raw.runtime.truffle.ast.expressions.builtin.math_package.{MathAbsNodeGen, MathAcosNodeGen, MathAsinNodeGen, MathAtanNodeGen, MathAtn2NodeGen, MathCeilingNodeGen, MathCosNodeGen, MathCotNodeGen, MathDegreesNodeGen, MathExpNodeGen, MathFloorNodeGen, MathLog10NodeGen, MathLogNodeGen, MathPowerNodeGen, MathRadiansNodeGen, MathSignNodeGen, MathSinNodeGen, MathSqrtNodeGen, MathSquareNodeGen, MathTanNodeGen}
import raw.runtime.truffle.ast.expressions.literals.DoubleNode

class TruffleMathPiEntry extends MathPiEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = new DoubleNode(Math.PI.toString())
}

class TruffleMathRandomEntry extends MathRandomEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = new DoubleNode(Math.random().toString())
}

class TruffleMathPowerEntry extends MathPowerEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = MathPowerNodeGen.create(args(0), args(1))
}

class TruffleMathAtn2Entry extends MathAtn2Entry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = MathAtn2NodeGen.create(args(0), args(1))
}

class TruffleMathAbsEntry extends MathAbsEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    MathAbsNodeGen.create(args(0).e)
  }

}

class TruffleMathAcosEntry extends MathAcosEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = MathAcosNodeGen.create(args(0))
}

class TruffleMathAsinEntry extends MathAsinEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = MathAsinNodeGen.create(args(0))
}

class TruffleMathAtanEntry extends MathAtanEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = MathAtanNodeGen.create(args(0))
}

class TruffleMathCeilingEntry extends MathCeilingEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = MathCeilingNodeGen.create(args(0))
}

class TruffleMathCosEntry extends MathCosEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = MathCosNodeGen.create(args(0))
}

class TruffleMathCotEntry extends MathCotEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = MathCotNodeGen.create(args(0))
}

class TruffleMathDegreesEntry extends MathDegreesEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = MathDegreesNodeGen.create(args(0))
}

class TruffleMathExpEntry extends MathExpEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = MathExpNodeGen.create(args(0))
}

class TruffleMathLogEntry extends MathLogEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = MathLogNodeGen.create(args(0))
}

class TruffleMathLog10Entry extends MathLog10Entry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = MathLog10NodeGen.create(args(0))
}

class TruffleMathRadiansEntry extends MathRadiansEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = MathRadiansNodeGen.create(args(0))
}

class TruffleMathSignEntry extends MathSignEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = MathSignNodeGen.create(args(0))
}

class TruffleMathSinEntry extends MathSinEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = MathSinNodeGen.create(args(0))
}

class TruffleMathSqrtEntry extends MathSqrtEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = MathSqrtNodeGen.create(args(0))
}

class TruffleMathTanEntry extends MathTanEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = MathTanNodeGen.create(args(0))
}

class TruffleMathSquareEntry extends MathSquareEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = MathSquareNodeGen.create(args(0))
}

class TruffleMathFloorEntry extends MathFloorEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = MathFloorNodeGen.create(args(0))
}
