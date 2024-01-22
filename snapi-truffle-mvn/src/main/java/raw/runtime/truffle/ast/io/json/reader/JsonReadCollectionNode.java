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

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.runtime.iterable.sources.JsonReadCollection;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.sources.api.SourceContext;

@NodeInfo(shortName = "Json.ReadCollection")
public class JsonReadCollectionNode extends ExpressionNode {
  @Child private ExpressionNode locationExp;

  @Child private ExpressionNode encodingExp;

  private final RootCallTarget rootCallTarget;

  public JsonReadCollectionNode(
      ExpressionNode locationExp, ExpressionNode encodingExp, ProgramExpressionNode readerNode) {
    this.locationExp = locationExp;
    this.encodingExp = encodingExp;
    this.rootCallTarget = readerNode.getCallTarget();
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    LocationObject locationObject = (LocationObject) locationExp.executeGeneric(virtualFrame);
    String encoding = (String) encodingExp.executeGeneric(virtualFrame);

    SourceContext context = RawContext.get(this).getSourceContext();
    return new JsonReadCollection(locationObject, encoding, context, rootCallTarget);
  }
}
