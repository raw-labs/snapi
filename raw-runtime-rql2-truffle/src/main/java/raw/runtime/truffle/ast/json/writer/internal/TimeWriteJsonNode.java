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

package raw.runtime.truffle.ast.json.writer.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.runtime.exceptions.json.JsonWriterRawTruffleException;
import raw.runtime.truffle.runtime.primitives.TimeObject;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@NodeInfo(shortName = "TimeWriteJson")
public class TimeWriteJsonNode extends StatementNode {

  // two different formatters, depending on whether there are milliseconds or not.
  private final DateTimeFormatter fmtWithMS = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
  private final DateTimeFormatter fmtWithoutMS = DateTimeFormatter.ofPattern("HH:mm:ss");

  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    this.doWrite((TimeObject) args[0], (JsonGenerator) args[1]);
  }

  @CompilerDirectives.TruffleBoundary
  private void doWrite(TimeObject value, JsonGenerator gen) {
    try {
      LocalTime ts = value.getTime();
      // .format throws DateTimeException if its internal StringBuilder throws an IOException.
      // We consider it as an internal error and let it propagate.
      if (ts.getNano() != 0) {
        gen.writeString(fmtWithMS.format(ts));
      } else {
        gen.writeString(fmtWithoutMS.format(ts));
      }
    } catch (IOException e) {
      throw new JsonWriterRawTruffleException(e.getMessage(), this);
    }
  }
}
