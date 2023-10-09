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

package raw.compiler.snapi.truffle.builtin.binary_extension;

import com.oracle.truffle.api.frame.FrameDescriptor;
import raw.compiler.rql2.source.Rql2BinaryType;
import raw.compiler.rql2.source.Rql2IsNullableTypeProperty;
import raw.compiler.rql2.source.Rql2IsTryableTypeProperty;
import raw.compiler.rql2.source.Rql2StringType;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.ProgramStatementNode;
import raw.runtime.truffle.ast.io.binary.BinaryBytesWriterNode;
import raw.runtime.truffle.ast.io.binary.NullableBinaryWriterNode;
import raw.runtime.truffle.ast.io.binary.TryableBinaryWriterNode;

public class TruffleBinaryWriter {

  public static ProgramStatementNode getBinaryWriterNode(
      Rql2BinaryType type, RawLanguage lang, FrameDescriptor frameDescriptor) {
    if (type.props().isEmpty()) {
      return new ProgramStatementNode(lang, frameDescriptor, new BinaryBytesWriterNode());
    } else if (type.props().contains(Rql2IsTryableTypeProperty.apply())) {
      Rql2BinaryType innerType =
          (Rql2BinaryType) type.cloneAndAddProp(Rql2IsTryableTypeProperty.apply());
      ProgramStatementNode innerWriter =
          TruffleBinaryWriter.getBinaryWriterNode(innerType, lang, frameDescriptor);
      return new ProgramStatementNode(
          lang, frameDescriptor, new TryableBinaryWriterNode(innerWriter));
    } else {
      Rql2BinaryType innerType =
          (Rql2BinaryType) type.cloneAndAddProp(Rql2IsNullableTypeProperty.apply());
      ProgramStatementNode innerWriter =
          TruffleBinaryWriter.getBinaryWriterNode(innerType, lang, frameDescriptor);
      return new ProgramStatementNode(
          lang, frameDescriptor, new NullableBinaryWriterNode(innerWriter));
    }
  }

  public static ProgramStatementNode getBinaryWriterNode(
      Rql2StringType type, RawLanguage lang, FrameDescriptor frameDescriptor) {
    if (type.props().isEmpty()) {
      return new ProgramStatementNode(lang, frameDescriptor, new BinaryBytesWriterNode());
    } else if (type.props().contains(Rql2IsTryableTypeProperty.apply())) {
      Rql2StringType innerType =
          (Rql2StringType) type.cloneAndAddProp(Rql2IsTryableTypeProperty.apply());
      ProgramStatementNode innerWriter =
          TruffleBinaryWriter.getBinaryWriterNode(innerType, lang, frameDescriptor);
      return new ProgramStatementNode(
          lang, frameDescriptor, new TryableBinaryWriterNode(innerWriter));
    } else {
      Rql2StringType innerType =
          (Rql2StringType) type.cloneAndAddProp(Rql2IsNullableTypeProperty.apply());
      ProgramStatementNode innerWriter =
          TruffleBinaryWriter.getBinaryWriterNode(innerType, lang, frameDescriptor);
      return new ProgramStatementNode(
          lang, frameDescriptor, new NullableBinaryWriterNode(innerWriter));
    }
  }
}
