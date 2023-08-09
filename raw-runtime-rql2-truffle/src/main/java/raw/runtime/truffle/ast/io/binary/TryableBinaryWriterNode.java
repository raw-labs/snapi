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

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ast.ProgramStatementNode;
import raw.runtime.truffle.runtime.exceptions.binary.BinaryWriterRawTruffleException;
import raw.runtime.truffle.runtime.tryable.TryableLibrary;

import java.io.OutputStream;

@NodeInfo(shortName = "Binary.TryableWrite")
public class TryableBinaryWriterNode extends StatementNode {

  @Child private DirectCallNode innerWriter;

  @Child private TryableLibrary tryables = TryableLibrary.getFactory().createDispatched(1);

  public TryableBinaryWriterNode(ProgramStatementNode innerWriter) {
    this.innerWriter = DirectCallNode.create(innerWriter.getCallTarget());
  }

  @Override
  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    Object tryable = args[0];
    OutputStream output = (OutputStream) args[1];
    if (tryables.isSuccess(tryable)) {
      // the tryable is a success, write its bytes using the inner writer.
      innerWriter.call(tryables.success(tryable), output);
    } else {
      // else throw.
      throw new BinaryWriterRawTruffleException(tryables.failure(tryable), this);
    }
  }
}
