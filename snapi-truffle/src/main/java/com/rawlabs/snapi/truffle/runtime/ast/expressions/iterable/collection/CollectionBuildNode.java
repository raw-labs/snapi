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

package com.rawlabs.snapi.truffle.runtime.ast.expressions.iterable.collection;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.runtime.iterable.sources.ExpressionCollection;

@NodeInfo(shortName = "Collection.Build")
public class CollectionBuildNode extends ExpressionNode {

  @Children private final ExpressionNode[] exps;

  public CollectionBuildNode(ExpressionNode[] exps) {
    this.exps = exps;
  }

  @Override
  @ExplodeLoop
  public Object executeGeneric(VirtualFrame frame) {
    Object[] values = new Object[exps.length];
    for (int i = 0; i < exps.length; i++) {
      values[i] = exps[i].executeGeneric(frame);
    }
    return new ExpressionCollection(values);
  }
}
