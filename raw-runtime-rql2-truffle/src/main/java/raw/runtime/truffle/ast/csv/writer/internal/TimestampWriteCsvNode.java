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

package raw.runtime.truffle.ast.csv.writer.internal;

import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.csv.CsvWriterRawTruffleException;
import raw.runtime.truffle.runtime.primitives.TimestampObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NodeInfo(shortName = "TimestampWriteCsv")
public class TimestampWriteCsvNode extends StatementNode {

  private final DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
  private final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

  @Override
  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    TimestampObject value = (TimestampObject) args[0];
    CsvGenerator generator = (CsvGenerator) args[1];
    doWrite(value, generator);
  }

  @CompilerDirectives.TruffleBoundary
  private void doWrite(TimestampObject value, CsvGenerator gen) {
    try {
      LocalDateTime ts = value.getTimestamp();
      if (ts.getNano() != 0) {
        gen.writeString(formatter2.format(ts));
      } else {
        gen.writeString(formatter1.format(ts));
      }
    } catch (IOException e) {
      throw new CsvWriterRawTruffleException(e.getMessage(), e, this);
    }
  }
}
