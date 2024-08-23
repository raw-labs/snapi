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
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.StatementNode;
import com.rawlabs.snapi.truffle.runtime.exceptions.csv.CsvWriterTruffleException;
import com.rawlabs.snapi.truffle.runtime.primitives.TimestampObject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NodeInfo(shortName = "TimestampWriteCsv")
public class TimestampWriteCsvNode extends StatementNode {

  // two different formatters, depending on whether there are milliseconds or not.
  private final DateTimeFormatter fmtWithoutMS =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
  private final DateTimeFormatter fmtWithMS =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

  @Override
  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    TimestampObject value = (TimestampObject) args[0];
    CsvGenerator generator = (CsvGenerator) args[1];
    doWrite(value, generator);
  }

  @TruffleBoundary
  private void doWrite(TimestampObject value, CsvGenerator gen) {
    try {
      LocalDateTime ts = value.getTimestamp();
      // .format throws DateTimeException if its internal StringBuilder throws an IOException.
      // We consider it as an internal error and let it propagate.
      if (ts.getNano() != 0) {
        gen.writeString(fmtWithMS.format(ts));
      } else {
        gen.writeString(fmtWithoutMS.format(ts));
      }
    } catch (IOException e) {
      throw new CsvWriterTruffleException(e.getMessage(), e, this);
    }
  }
}