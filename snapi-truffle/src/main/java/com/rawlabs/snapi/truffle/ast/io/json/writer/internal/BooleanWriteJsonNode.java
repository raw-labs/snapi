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

package com.rawlabs.snapi.truffle.ast.io.json.writer.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.StatementNode;
import com.rawlabs.snapi.truffle.ast.io.json.writer.JsonWriteNodes;
import com.rawlabs.snapi.truffle.ast.io.json.writer.JsonWriteNodesFactory;

@NodeInfo(shortName = "BooleanWriteJson")
public class BooleanWriteJsonNode extends StatementNode {

  @Child
  JsonWriteNodes.WriteBooleanJsonWriterNode writeBoolean =
      JsonWriteNodesFactory.WriteBooleanJsonWriterNodeGen.create();

  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    writeBoolean.execute(this, (boolean) args[0], (JsonGenerator) args[1]);
  }
}
