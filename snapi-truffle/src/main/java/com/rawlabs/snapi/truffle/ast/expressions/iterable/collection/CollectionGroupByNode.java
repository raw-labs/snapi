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

package com.rawlabs.snapi.truffle.ast.expressions.iterable.collection;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.frontend.snapi.source.SnapiTypeWithProperties;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.iterable.operations.GroupByCollection;

@NodeInfo(shortName = "Collection.GroupBy")
@NodeChild("iterable")
@NodeChild("keyFun")
@NodeField(name = "keyType", type = SnapiTypeWithProperties.class)
@NodeField(name = "rowType", type = SnapiTypeWithProperties.class)
@NodeField(name = "generatorSlot", type = int.class)
@NodeField(name = "functionSlot", type = int.class)
@NodeField(name = "mapSlot", type = int.class)
public abstract class CollectionGroupByNode extends ExpressionNode {

  @Idempotent
  protected abstract SnapiTypeWithProperties getKeyType();

  @Idempotent
  protected abstract SnapiTypeWithProperties getRowType();

  @Idempotent
  protected abstract int getGeneratorSlot();

  @Idempotent
  protected abstract int getFunctionSlot();

  @Idempotent
  protected abstract int getMapSlot();

  @Specialization
  protected Object doGroup(VirtualFrame frame, Object iterable, Object keyFun) {
    return new GroupByCollection(
        iterable,
        keyFun,
        getKeyType(),
        getRowType(),
        frame.materialize(),
        getGeneratorSlot(),
        getFunctionSlot(),
        getMapSlot());
  }
}
