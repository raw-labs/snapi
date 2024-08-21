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

package com.rawlabs.snapi.truffle.runtime.ast.io.json.reader.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.ast.io.json.reader.JsonParserNodes;

@NodeInfo(shortName = "IntParseJson")
public abstract class IntParseJsonNode extends ExpressionNode {

  @Specialization
  protected int doParse(
      VirtualFrame frame, @Cached(inline = true) JsonParserNodes.ParseIntJsonParserNode parse) {
    Object[] args = frame.getArguments();
    JsonParser parser = (JsonParser) args[0];
    return parse.execute(this, parser);
  }
}
