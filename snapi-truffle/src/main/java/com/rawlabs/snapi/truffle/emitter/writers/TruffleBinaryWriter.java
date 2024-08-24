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
import com.rawlabs.snapi.frontend.snapi.source.SnapiBinaryType;
import com.rawlabs.snapi.frontend.snapi.source.SnapiIsNullableTypeProperty;
import com.rawlabs.snapi.frontend.snapi.source.SnapiIsTryableTypeProperty;
import com.rawlabs.snapi.frontend.snapi.source.SnapiStringType;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.ProgramStatementNode;
import com.rawlabs.snapi.truffle.ast.io.binary.BinaryBytesWriterNode;
import com.rawlabs.snapi.truffle.ast.io.binary.NullableBinaryWriterNode;
import com.rawlabs.snapi.truffle.ast.io.binary.TryableBinaryWriterNode;

public class TruffleBinaryWriter {

  public static ProgramStatementNode getBinaryWriterNode(
      SnapiBinaryType type, SnapiLanguage lang, FrameDescriptor frameDescriptor) {
    if (type.props().isEmpty()) {
      return new ProgramStatementNode(lang, frameDescriptor, new BinaryBytesWriterNode());
    } else if (type.props().contains(SnapiIsTryableTypeProperty.apply())) {
      SnapiBinaryType innerType =
          (SnapiBinaryType) type.cloneAndRemoveProp(SnapiIsTryableTypeProperty.apply());
      ProgramStatementNode innerWriter =
          TruffleBinaryWriter.getBinaryWriterNode(innerType, lang, frameDescriptor);
      return new ProgramStatementNode(
          lang, frameDescriptor, new TryableBinaryWriterNode(innerWriter));
    } else {
      SnapiBinaryType innerType =
          (SnapiBinaryType) type.cloneAndRemoveProp(SnapiIsNullableTypeProperty.apply());
      ProgramStatementNode innerWriter =
          TruffleBinaryWriter.getBinaryWriterNode(innerType, lang, frameDescriptor);
      return new ProgramStatementNode(
          lang, frameDescriptor, new NullableBinaryWriterNode(innerWriter));
    }
  }

  public static ProgramStatementNode getBinaryWriterNode(
      SnapiStringType type, SnapiLanguage lang, FrameDescriptor frameDescriptor) {
    if (type.props().isEmpty()) {
      return new ProgramStatementNode(lang, frameDescriptor, new BinaryBytesWriterNode());
    } else if (type.props().contains(SnapiIsTryableTypeProperty.apply())) {
      SnapiStringType innerType =
          (SnapiStringType) type.cloneAndRemoveProp(SnapiIsTryableTypeProperty.apply());
      ProgramStatementNode innerWriter =
          TruffleBinaryWriter.getBinaryWriterNode(innerType, lang, frameDescriptor);
      return new ProgramStatementNode(
          lang, frameDescriptor, new TryableBinaryWriterNode(innerWriter));
    } else {
      SnapiStringType innerType =
          (SnapiStringType) type.cloneAndRemoveProp(SnapiIsNullableTypeProperty.apply());
      ProgramStatementNode innerWriter =
          TruffleBinaryWriter.getBinaryWriterNode(innerType, lang, frameDescriptor);
      return new ProgramStatementNode(
          lang, frameDescriptor, new NullableBinaryWriterNode(innerWriter));
    }
  }
}
