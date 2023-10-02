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

import com.oracle.truffle.api.frame.FrameDescriptor
import raw.compiler.rql2.builtin.{BinaryBase64Entry, BinaryReadEntry, FromStringBinaryEntryExtension}
import raw.compiler.rql2.source.{Rql2BinaryType, Rql2IsNullableTypeProperty, Rql2IsTryableTypeProperty, Rql2StringType}
import raw.compiler.rql2.truffle.TruffleShortEntryExtension
import raw.runtime.truffle.ast.ProgramStatementNode
//import raw.runtime.truffle.ast.expressions.builtin.binary_package.{
//  BinaryBase64NodeGen,
//  BinaryFromStringNodeGen,
//  BinaryReadNodeGen
//}
import raw.runtime.truffle.ast.io.binary.{BinaryBytesWriterNode, NullableBinaryWriterNode, TryableBinaryWriterNode}
import raw.runtime.truffle.{ExpressionNode, RawLanguage}
//
//class TruffleFromStringBinaryEntryExtension extends FromStringBinaryEntryExtension with TruffleShortEntryExtension {
//
//  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = BinaryFromStringNodeGen.create(args.head)
//
//}
//
//class TruffleBinaryReadEntry extends BinaryReadEntry with TruffleShortEntryExtension {
//
//  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = BinaryReadNodeGen.create(args.head)
//
//}
//
//class TruffleBinaryBase64Entry extends BinaryBase64Entry with TruffleShortEntryExtension {
//  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = BinaryBase64NodeGen.create(args.head)
//}
//
object TruffleBinaryWriter {

  def apply(t: Rql2BinaryType, lang: RawLanguage): ProgramStatementNode = {
    val frameDescriptor = new FrameDescriptor()

    // The generated writer program depends on the type properties.
    if (t.props.isEmpty) {
      // No properties, just write the bytes.
      new ProgramStatementNode(lang, frameDescriptor, new BinaryBytesWriterNode())
    } else if (t.props.contains(Rql2IsTryableTypeProperty())) {
      // Tryable binary: wrap the inner writer (plain or nullable) in a tryable writer that throws in case of failure.
      val innerType = t.cloneAndRemoveProp(Rql2IsTryableTypeProperty()).asInstanceOf[Rql2BinaryType]
      val innerWriter = TruffleBinaryWriter(innerType, lang)
      new ProgramStatementNode(lang, frameDescriptor, new TryableBinaryWriterNode(innerWriter))
    } else {
      // Nullable binary: wrap the inner writer (plain) in a nullable writer that writes nothing if the value is null.
      val innerType = t.cloneAndRemoveProp(Rql2IsNullableTypeProperty()).asInstanceOf[Rql2BinaryType]
      val innerWriter = TruffleBinaryWriter(innerType, lang)
      new ProgramStatementNode(lang, frameDescriptor, new NullableBinaryWriterNode(innerWriter))
    }

  }

  def apply(t: Rql2StringType, lang: RawLanguage): ProgramStatementNode = {
    val frameDescriptor = new FrameDescriptor()
    // The generated writer program depends on the type properties.
    if (t.props.isEmpty) {
      // No properties, just write the bytes.
      new ProgramStatementNode(lang, frameDescriptor, new BinaryBytesWriterNode())
    } else if (t.props.contains(Rql2IsTryableTypeProperty())) {
      // Tryable binary: wrap the inner writer (plain or nullable) in a tryable writer that throws in case of failure.
      val innerType = t.cloneAndRemoveProp(Rql2IsTryableTypeProperty()).asInstanceOf[Rql2StringType]
      val innerWriter = TruffleBinaryWriter(innerType, lang)
      new ProgramStatementNode(lang, frameDescriptor, new TryableBinaryWriterNode(innerWriter))
    } else {
      // Nullable binary: wrap the inner writer (plain) in a nullable writer that writes nothing if the value is null.
      val innerType = t.cloneAndRemoveProp(Rql2IsNullableTypeProperty()).asInstanceOf[Rql2StringType]
      val innerWriter = TruffleBinaryWriter(innerType, lang)
      new ProgramStatementNode(lang, frameDescriptor, new NullableBinaryWriterNode(innerWriter))
    }

  }

}
