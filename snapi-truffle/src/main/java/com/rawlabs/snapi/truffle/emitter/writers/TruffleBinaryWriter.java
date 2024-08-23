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

package com.rawlabs.snapi.truffle.emitter.writers;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.rawlabs.snapi.frontend.rql2.source.Rql2BinaryType;
import com.rawlabs.snapi.frontend.rql2.source.Rql2IsNullableTypeProperty;
import com.rawlabs.snapi.frontend.rql2.source.Rql2IsTryableTypeProperty;
import com.rawlabs.snapi.frontend.rql2.source.Rql2StringType;
import com.rawlabs.snapi.truffle.Rql2Language;
import com.rawlabs.snapi.truffle.ast.ProgramStatementNode;
import com.rawlabs.snapi.truffle.ast.io.binary.BinaryBytesWriterNode;
import com.rawlabs.snapi.truffle.ast.io.binary.NullableBinaryWriterNode;
import com.rawlabs.snapi.truffle.ast.io.binary.TryableBinaryWriterNode;

public class TruffleBinaryWriter {

  public static ProgramStatementNode getBinaryWriterNode(
      Rql2BinaryType type, Rql2Language lang, FrameDescriptor frameDescriptor) {
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
      Rql2StringType type, Rql2Language lang, FrameDescriptor frameDescriptor) {
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
