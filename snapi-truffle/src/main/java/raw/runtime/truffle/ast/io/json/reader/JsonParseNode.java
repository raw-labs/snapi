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

package raw.runtime.truffle.ast.io.json.reader;

import com.fasterxml.jackson.core.JsonParser;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodes.*;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.json.JsonReaderRawTruffleException;

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
      throw new JsonReaderRawTruffleException();
    } finally {
      if (parser != null) {
        closeParserNode.execute(this, parser);
      }
    }
  }
}
