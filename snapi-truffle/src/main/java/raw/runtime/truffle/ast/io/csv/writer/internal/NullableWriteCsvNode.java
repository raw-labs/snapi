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
import raw.runtime.truffle.tryable_nullable.Nullable;

@NodeInfo(shortName = "NullableWriteCsv")
public class NullableWriteCsvNode extends StatementNode {

  @Child private DirectCallNode valueWriter;

  public NullableWriteCsvNode(ProgramStatementNode valueWriter) {
    this.valueWriter = DirectCallNode.create(valueWriter.getCallTarget());
  }

  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    Object nullable = args[0];
    CsvGenerator generator = (CsvGenerator) args[1];
    if (Nullable.isNotNull(nullable)) {
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
      throw new CsvWriterRawTruffleException(e.getMessage(), e, this);
    }
  }
}
