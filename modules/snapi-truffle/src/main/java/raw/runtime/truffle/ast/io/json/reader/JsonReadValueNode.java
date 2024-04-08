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
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodes.CloseJsonParserNode;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodes.InitJsonParserNode;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodes.NextTokenJsonParserNode;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.utils.TruffleCharInputStream;
import raw.runtime.truffle.utils.TruffleInputStream;
import raw.sources.api.SourceContext;

@NodeInfo(shortName = "Json.ReadArray")
public class JsonReadValueNode extends ExpressionNode {

  @Child private ExpressionNode locationExp;

  @Child private ExpressionNode encodingExp;

  @Child private DirectCallNode childDirectCall;

  @Child
  private InitJsonParserNode initParserNode = JsonParserNodesFactory.InitJsonParserNodeGen.create();

  @Child
  private CloseJsonParserNode closeParserNode =
      JsonParserNodesFactory.CloseJsonParserNodeGen.create();

  @Child
  private NextTokenJsonParserNode nextTokenNode =
      JsonParserNodesFactory.NextTokenJsonParserNodeGen.create();

  private final SourceContext sourceContext = RawContext.get(this).getSourceContext();

  private JsonParser parser;

  public JsonReadValueNode(
      ExpressionNode locationExp, ExpressionNode encodingExp, RootCallTarget readRootCallTarget) {
    this.locationExp = locationExp;
    this.encodingExp = encodingExp;
    this.childDirectCall = DirectCallNode.create(readRootCallTarget);
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    try {
      LocationObject locationObject = (LocationObject) locationExp.executeGeneric(virtualFrame);
      String encoding = (String) encodingExp.executeGeneric(virtualFrame);

      TruffleInputStream truffleInputStream = new TruffleInputStream(locationObject, sourceContext);
      TruffleCharInputStream stream = new TruffleCharInputStream(truffleInputStream, encoding);

      parser = initParserNode.execute(this, stream);
      nextTokenNode.execute(this, parser);

      return this.childDirectCall.call(parser);
    } finally {
      closeParserNode.execute(this, parser);
      parser = null;
    }
  }
}
