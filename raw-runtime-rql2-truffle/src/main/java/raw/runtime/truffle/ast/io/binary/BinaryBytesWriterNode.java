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

package raw.runtime.truffle.ast.io.binary;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

import java.io.IOException;
import java.io.OutputStream;

@NodeInfo(shortName = "Binary.BytesWrite")
public class BinaryBytesWriterNode extends StatementNode {

    @CompilerDirectives.TruffleBoundary
    private void doWrite(OutputStream os, byte[] binaryData) {
        try {
            os.write(binaryData);
        } catch (IOException e) {
            throw new RawTruffleRuntimeException(e.getMessage());
        }
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        byte[] binaryData = (byte[]) args[0];
        OutputStream output = (OutputStream) args[1];
        doWrite(output, binaryData);
    }
}

