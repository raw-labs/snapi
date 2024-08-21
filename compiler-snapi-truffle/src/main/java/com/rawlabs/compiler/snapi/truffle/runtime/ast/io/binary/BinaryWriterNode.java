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

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.RawContext;
import com.rawlabs.compiler.snapi.truffle.runtime.StatementNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.ProgramStatementNode;
import java.io.OutputStream;

@NodeInfo(shortName = "Binary.Write")
public class BinaryWriterNode extends StatementNode {

  @Child private ExpressionNode binaryNode;

  @Child private DirectCallNode innerWriter;

  private final OutputStream os = RawContext.get(this).getOutput();

  public BinaryWriterNode(ExpressionNode binaryNode, ProgramStatementNode innerWriter) {
    this.innerWriter = DirectCallNode.create(innerWriter.getCallTarget());
    this.binaryNode = binaryNode;
  }

  @Override
  public void executeVoid(VirtualFrame frame) {
    Object binaryObject = binaryNode.executeGeneric(frame);
    innerWriter.call(binaryObject, os);
  }
}
