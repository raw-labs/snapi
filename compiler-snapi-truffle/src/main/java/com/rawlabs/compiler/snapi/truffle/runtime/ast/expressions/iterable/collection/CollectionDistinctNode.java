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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.iterable.collection;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.rql2.source.Rql2TypeWithProperties;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.iterable.operations.DistinctCollection;

@NodeInfo(shortName = "Collection.Distinct")
@NodeChild("input")
@NodeField(name = "valueType", type = Rql2TypeWithProperties.class)
@NodeField(name = "generatorSlot", type = int.class)
@NodeField(name = "offHeapDistinctSlot", type = int.class)
public abstract class CollectionDistinctNode extends ExpressionNode {

  @Idempotent
  protected abstract Rql2TypeWithProperties getValueType();

  @Idempotent
  protected abstract int getGeneratorSlot();

  @Idempotent
  protected abstract int getOffHeapDistinctSlot();

  @Specialization
  protected Object doDistinct(VirtualFrame frame, Object iterable) {
    return new DistinctCollection(
        iterable,
        getValueType(),
        frame.materialize(),
        getGeneratorSlot(),
        getOffHeapDistinctSlot());
  }
}
