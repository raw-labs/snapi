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

import raw.compiler.rql2.builtin.{BinaryBase64Entry, BinaryReadEntry, FromStringBinaryEntryExtension}
import raw.compiler.rql2.truffle.TruffleShortEntryExtension
import raw.runtime.truffle.ExpressionNode
import raw.runtime.truffle.ast.expressions.builtin.binary_package.{
  BinaryBase64NodeGen,
  BinaryFromStringNode,
  BinaryFromStringNodeGen,
  BinaryReadNodeGen
}
import raw.runtime.truffle.ast.expressions.literals.DoubleNode

class TruffleFromStringBinaryEntryExtension extends FromStringBinaryEntryExtension with TruffleShortEntryExtension {

  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = BinaryFromStringNodeGen.create(args(0))

}

class TruffleBinaryReadEntry extends BinaryReadEntry with TruffleShortEntryExtension {

  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = BinaryReadNodeGen.create(args(0))

}

class TruffleBinaryBase64Entry extends BinaryBase64Entry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = BinaryBase64NodeGen.create(args(0))
}
