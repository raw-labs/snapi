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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.io.binary;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.truffle.runtime.StatementNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.RawTruffleRuntimeException;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.BinaryObject;
import java.io.IOException;
import java.io.OutputStream;

@NodeInfo(shortName = "Binary.BytesWrite")
public class BinaryBytesWriterNode extends StatementNode {

  @TruffleBoundary
  private void doWrite(Object[] args) {
    byte[] binaryData;
    if (args[0] instanceof BinaryObject) {
      binaryData = ((BinaryObject) args[0]).getBytes();
    } else {
      binaryData = ((String) args[0]).getBytes();
    }
    OutputStream output = (OutputStream) args[1];
    try {
      output.write(binaryData);
    } catch (IOException e) {
      throw new RawTruffleRuntimeException(e.getMessage(), e, this);
    }
  }

  @Override
  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    doWrite(args);
  }
}
