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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.io.json.reader;

import com.fasterxml.jackson.core.JsonParser;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.io.json.reader.JsonParserNodes.*;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.RawTruffleRuntimeException;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.json.JsonReaderRawTruffleException;

@NodeInfo(shortName = "ParseJson")
@NodeChild(value = "str")
@NodeField(name = "childCallTarget", type = RootCallTarget.class)
public abstract class JsonParseNode extends ExpressionNode {

  @Idempotent
  protected abstract RootCallTarget getChildCallTarget();

  @Specialization
  protected Object doParse(
      String str,
      @Cached(inline = true) InitJsonParserNode initParserNode,
      @Cached(inline = true) CloseJsonParserNode closeParserNode,
      @Cached(inline = true) NextTokenJsonParserNode nextTokenNode,
      @Cached("create(getChildCallTarget())") DirectCallNode childDirectCall) {
    JsonParser parser = null;
    try {
      parser = initParserNode.execute(this, str);
      nextTokenNode.execute(this, parser);
      return childDirectCall.call(parser);
    } catch (RawTruffleRuntimeException e) {
      throw new JsonReaderRawTruffleException(e.getMessage(), parser, null, e, this);
    } finally {
      if (parser != null) {
        closeParserNode.execute(this, parser);
      }
    }
  }
}
