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

package com.rawlabs.snapi.truffle.runtime.ast.io.json.writer.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.StatementNode;
import com.rawlabs.snapi.truffle.runtime.ast.io.json.writer.JsonWriteNodes;
import com.rawlabs.snapi.truffle.runtime.ast.io.json.writer.JsonWriteNodesFactory;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.TimestampObject;

@NodeInfo(shortName = "TimestampWriteJson")
public class TimestampWriteJsonNode extends StatementNode {

  @Child
  JsonWriteNodes.WriteTimestampJsonWriterNode writeTimestamp =
      JsonWriteNodesFactory.WriteTimestampJsonWriterNodeGen.create();

  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    writeTimestamp.execute(this, (TimestampObject) args[0], (JsonGenerator) args[1]);
  }
}
