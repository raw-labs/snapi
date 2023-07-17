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

package raw.runtime.truffle.ast.io;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ast.ProgramStatementNode;
import raw.runtime.truffle.runtime.option.OptionLibrary;

import java.io.OutputStream;

@NodeInfo(shortName = "Binary.NullableWrite")
public class NullableStreamWriterNode extends StatementNode {

  @Child
  private DirectCallNode innerWriter;

  @Child
  private OptionLibrary options = OptionLibrary.getFactory().createDispatched(1);

  public NullableStreamWriterNode(ProgramStatementNode innerWriter) {
    this.innerWriter = DirectCallNode.create(innerWriter.getCallTarget());
  }

  @Override
  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    Object nullable = args[0];
    OutputStream output = (OutputStream) args[1];
    if (options.isDefined(nullable)) {
      // the nullable is defined, write its bytes using the inner writer (the plain binary writer)
      innerWriter.call(options.get(nullable), output);
    }  // else don't write anything.
  }

}

