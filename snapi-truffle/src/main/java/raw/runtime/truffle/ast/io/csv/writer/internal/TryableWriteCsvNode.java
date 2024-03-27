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

package raw.runtime.truffle.ast.io.csv.writer.internal;

import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.io.IOException;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ast.ProgramStatementNode;
import raw.runtime.truffle.runtime.exceptions.csv.CsvWriterRawTruffleException;
import raw.runtime.truffle.tryable_nullable.TryableNullableNodes;
import raw.runtime.truffle.tryable_nullable.TryableNullableNodesFactory;

@NodeInfo(shortName = "TryableWriteCsv")
public class TryableWriteCsvNode extends StatementNode {

  @Child private DirectCallNode valueWriter;

  @Child
  private TryableNullableNodes.IsErrorNode isErrorNode =
      TryableNullableNodesFactory.IsErrorNodeGen.create();

  @Child
  private TryableNullableNodes.GetErrorNode getFailureNode =
      TryableNullableNodesFactory.GetErrorNodeGen.create();

  public TryableWriteCsvNode(ProgramStatementNode valueWriter) {
    this.valueWriter = DirectCallNode.create(valueWriter.getCallTarget());
  }

  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    Object tryable = args[0];
    CsvGenerator generator = (CsvGenerator) args[1];
    if (!isErrorNode.execute(this, tryable)) {
      valueWriter.call(tryable, generator);
    } else {
      doWriteError(getFailureNode.execute(this, tryable), generator);
    }
  }

  @TruffleBoundary
  private void doWriteError(String message, CsvGenerator gen) {
    try {
      gen.writeString(message);
    } catch (IOException e) {
      throw new CsvWriterRawTruffleException(e.getMessage(), e, this);
    }
  }
}
