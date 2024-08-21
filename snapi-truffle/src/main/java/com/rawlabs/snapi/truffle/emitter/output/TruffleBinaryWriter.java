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

package com.rawlabs.snapi.truffle.emitter.output;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.rawlabs.compiler.snapi.rql2.source.Rql2BinaryType;
import com.rawlabs.compiler.snapi.rql2.source.Rql2IsNullableTypeProperty;
import com.rawlabs.compiler.snapi.rql2.source.Rql2IsTryableTypeProperty;
import com.rawlabs.compiler.snapi.rql2.source.Rql2StringType;
import com.rawlabs.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.snapi.truffle.runtime.ast.ProgramStatementNode;
import com.rawlabs.snapi.truffle.runtime.ast.io.binary.BinaryBytesWriterNode;
import com.rawlabs.snapi.truffle.runtime.ast.io.binary.NullableBinaryWriterNode;
import com.rawlabs.snapi.truffle.runtime.ast.io.binary.TryableBinaryWriterNode;

public class TruffleBinaryWriter {

  public static ProgramStatementNode getBinaryWriterNode(
      Rql2BinaryType type, RawLanguage lang, FrameDescriptor frameDescriptor) {
    if (type.props().isEmpty()) {
      return new ProgramStatementNode(lang, frameDescriptor, new BinaryBytesWriterNode());
    } else if (type.props().contains(Rql2IsTryableTypeProperty.apply())) {
      Rql2BinaryType innerType =
          (Rql2BinaryType) type.cloneAndRemoveProp(Rql2IsTryableTypeProperty.apply());
      ProgramStatementNode innerWriter =
          TruffleBinaryWriter.getBinaryWriterNode(innerType, lang, frameDescriptor);
      return new ProgramStatementNode(
          lang, frameDescriptor, new TryableBinaryWriterNode(innerWriter));
    } else {
      Rql2BinaryType innerType =
          (Rql2BinaryType) type.cloneAndRemoveProp(Rql2IsNullableTypeProperty.apply());
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
          (Rql2StringType) type.cloneAndRemoveProp(Rql2IsTryableTypeProperty.apply());
      ProgramStatementNode innerWriter =
          TruffleBinaryWriter.getBinaryWriterNode(innerType, lang, frameDescriptor);
      return new ProgramStatementNode(
          lang, frameDescriptor, new TryableBinaryWriterNode(innerWriter));
    } else {
      Rql2StringType innerType =
          (Rql2StringType) type.cloneAndRemoveProp(Rql2IsNullableTypeProperty.apply());
      ProgramStatementNode innerWriter =
          TruffleBinaryWriter.getBinaryWriterNode(innerType, lang, frameDescriptor);
      return new ProgramStatementNode(
          lang, frameDescriptor, new NullableBinaryWriterNode(innerWriter));
    }
  }
}
