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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.io.json.writer.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.truffle.runtime.StatementNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.io.json.writer.JsonWriteNodes;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.io.json.writer.JsonWriteNodesFactory;

@NodeInfo(shortName = "FloatWriteJson")
public class FloatWriteJsonNode extends StatementNode {

  @Child
  JsonWriteNodes.WriteFloatJsonWriterNode writeFloat =
      JsonWriteNodesFactory.WriteFloatJsonWriterNodeGen.create();

  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    writeFloat.execute(this, (float) args[0], (JsonGenerator) args[1]);
  }
}
