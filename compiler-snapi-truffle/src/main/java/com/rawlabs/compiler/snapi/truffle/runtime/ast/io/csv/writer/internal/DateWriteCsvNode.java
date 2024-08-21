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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.io.csv.writer.internal;

import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import com.rawlabs.compiler.snapi.truffle.runtime.StatementNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.csv.CsvWriterRawTruffleException;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.DateObject;

@NodeInfo(shortName = "DateWriteCsv")
public class DateWriteCsvNode extends StatementNode {

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Override
  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    DateObject value = (DateObject) args[0];
    CsvGenerator generator = (CsvGenerator) args[1];
    doWrite(value, generator);
  }

  @TruffleBoundary
  private void doWrite(DateObject value, CsvGenerator gen) {
    try {
      // .format throws DateTimeException if its internal StringBuilder throws an IOException.
      // We consider it as an internal error and let it propagate.
      gen.writeString(formatter.format(value.getDate()));
    } catch (IOException e) {
      throw new CsvWriterRawTruffleException(e.getMessage(), e, this);
    }
  }
}
