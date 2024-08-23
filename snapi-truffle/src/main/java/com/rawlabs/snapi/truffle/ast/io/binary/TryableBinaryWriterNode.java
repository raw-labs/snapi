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

package com.rawlabs.snapi.truffle.ast.io.binary;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.StatementNode;
import com.rawlabs.snapi.truffle.ast.ProgramStatementNode;
import com.rawlabs.snapi.truffle.runtime.exceptions.binary.BinaryWriterTruffleException;
import com.rawlabs.snapi.truffle.ast.tryable_nullable.TryableNullableNodes;
import com.rawlabs.snapi.truffle.ast.tryable_nullable.TryableNullableNodesFactory;
import java.io.OutputStream;

@NodeInfo(shortName = "Binary.TryableWrite")
public class TryableBinaryWriterNode extends StatementNode {

  @Child private DirectCallNode innerWriter;

  @Child
  private TryableNullableNodes.IsErrorNode isErrorNode =
      TryableNullableNodesFactory.IsErrorNodeGen.create();

  @Child
  private TryableNullableNodes.GetErrorNode getFailureNode =
      TryableNullableNodesFactory.GetErrorNodeGen.create();

  public TryableBinaryWriterNode(ProgramStatementNode innerWriter) {
    this.innerWriter = DirectCallNode.create(innerWriter.getCallTarget());
  }

  @Override
  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    Object tryable = args[0];
    OutputStream output = (OutputStream) args[1];
    if (!isErrorNode.execute(this, tryable)) {
      // the tryable is a success, write its bytes using the inner writer.
      innerWriter.call(tryable, output);
    } else {
      // else throw.
      throw new BinaryWriterTruffleException(getFailureNode.execute(this, tryable), this);
    }
  }
}
