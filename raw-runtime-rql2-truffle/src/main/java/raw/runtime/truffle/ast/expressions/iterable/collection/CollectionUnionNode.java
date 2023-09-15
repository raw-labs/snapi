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

package raw.runtime.truffle.ast.expressions.iterable.collection;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.iterable.sources.UnionCollection;

@NodeInfo(shortName = "Collection.Union")
public class CollectionUnionNode extends ExpressionNode {

  @Children ExpressionNode[] inputs;

  public CollectionUnionNode(ExpressionNode[] inputs) {
    this.inputs = inputs;
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    return new UnionCollection(inputs, virtualFrame.materialize());
  }
}
