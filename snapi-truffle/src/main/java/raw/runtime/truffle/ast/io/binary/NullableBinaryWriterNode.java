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
import java.io.OutputStream;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ast.ProgramStatementNode;
import raw.runtime.truffle.tryable_nullable.Nullable;

@NodeInfo(shortName = "Binary.NullableWrite")
public class NullableBinaryWriterNode extends StatementNode {

  @Child private DirectCallNode innerWriter;

  public NullableBinaryWriterNode(ProgramStatementNode innerWriter) {
    this.innerWriter = DirectCallNode.create(innerWriter.getCallTarget());
  }

  @Override
  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    Object nullable = args[0];
    OutputStream output = (OutputStream) args[1];
    if (Nullable.isNotNull(nullable)) {
      // the nullable is defined, write its bytes using the inner writer (the plain binary
      // writer)
      innerWriter.call(nullable, output);
    } // else don't write anything.
  }
}
