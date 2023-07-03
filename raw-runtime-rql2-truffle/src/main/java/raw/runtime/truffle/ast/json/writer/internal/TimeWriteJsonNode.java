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

  private final DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH:mm:ss");
  private final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    this.doWrite((TimeObject) args[0], (JsonGenerator) args[1]);
  }

  @CompilerDirectives.TruffleBoundary
  private void doWrite(TimeObject value, JsonGenerator gen) {
    try {
      LocalTime ts = value.getTime();
      if (ts.getNano() != 0) {
        gen.writeString(formatter2.format(ts));
      } else {
        gen.writeString(formatter1.format(ts));
      }
    } catch (IOException e) {
      throw new JsonWriterRawTruffleException(e.getMessage(), this);
    }
  }
}
