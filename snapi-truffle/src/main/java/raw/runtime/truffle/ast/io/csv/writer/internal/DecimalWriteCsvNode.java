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
import com.oracle.truffle.api.nodes.NodeInfo;
import java.io.IOException;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.runtime.exceptions.csv.CsvWriterRawTruffleException;
import raw.runtime.truffle.runtime.primitives.DecimalObject;

@NodeInfo(shortName = "DecimalWriteCsv")
public class DecimalWriteCsvNode extends StatementNode {

  @Override
  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    DecimalObject value = (DecimalObject) args[0];
    CsvGenerator generator = (CsvGenerator) args[1];
    doWrite(value, generator);
  }

  @TruffleBoundary
  private void doWrite(DecimalObject value, CsvGenerator gen) {
    try {
      gen.writeNumber(value.getBigDecimal());
    } catch (IOException e) {
      throw new CsvWriterRawTruffleException(e.getMessage(), e, this);
    }
  }
}
