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
import raw.runtime.truffle.runtime.iterable.sources.ExpressionCollection;

@NodeInfo(shortName = "Collection.Build")
public class CollectionBuildNode extends ExpressionNode {

  private final ExpressionNode[] exps;

  public CollectionBuildNode(ExpressionNode[] exps) {
    this.exps = exps;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return new ExpressionCollection(exps, frame);
  }
}
