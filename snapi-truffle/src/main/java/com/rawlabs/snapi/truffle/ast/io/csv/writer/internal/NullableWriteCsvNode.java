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

package com.rawlabs.snapi.truffle.ast.io.csv.writer.internal;

import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ProgramStatementNode;
import com.rawlabs.snapi.truffle.ast.StatementNode;
import com.rawlabs.snapi.truffle.ast.tryable_nullable.TryableNullableNodes;
import com.rawlabs.snapi.truffle.ast.tryable_nullable.TryableNullableNodesFactory;
import com.rawlabs.snapi.truffle.runtime.exceptions.csv.CsvWriterTruffleException;
import java.io.IOException;

@NodeInfo(shortName = "NullableWriteCsv")
public class NullableWriteCsvNode extends StatementNode {

  @Child private DirectCallNode valueWriter;

  @Child
  private TryableNullableNodes.IsNullNode isNullNode =
      TryableNullableNodesFactory.IsNullNodeGen.create();

  public NullableWriteCsvNode(ProgramStatementNode valueWriter) {
    this.valueWriter = DirectCallNode.create(valueWriter.getCallTarget());
  }

  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    Object nullable = args[0];
    CsvGenerator generator = (CsvGenerator) args[1];
    if (!isNullNode.execute(this, nullable)) {
      valueWriter.call(nullable, generator);
    } else {
      doWriteNull(generator);
    }
  }

  @TruffleBoundary
  private void doWriteNull(CsvGenerator gen) {
    try {
      gen.writeString("null");
    } catch (IOException e) {
      throw new CsvWriterTruffleException(e.getMessage(), e, this);
    }
  }
}
