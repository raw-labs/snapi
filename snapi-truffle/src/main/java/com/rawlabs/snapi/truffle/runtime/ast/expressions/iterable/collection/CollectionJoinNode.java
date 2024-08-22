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

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.frontend.rql2.source.Rql2TypeWithProperties;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.StaticInitializers;
import com.rawlabs.snapi.truffle.runtime.runtime.iterable.operations.JoinCollection;

@NodeInfo(shortName = "Collection.Join")
@NodeChild("left")
@NodeChild("right")
@NodeChild("remap")
@NodeChild("predicate")
@NodeField(name = "rightType", type = Rql2TypeWithProperties.class)
@NodeField(name = "reshapeBeforePredicate", type = Boolean.class)
@NodeField(name = "computeNextSlot", type = int.class)
@NodeField(name = "shouldContinueSlot", type = int.class)
@NodeField(name = "resultSlot", type = int.class)
@NodeField(name = "generatorSlot", type = int.class)
@NodeField(name = "outputBufferSlot", type = int.class)
@ImportStatic(StaticInitializers.class)
public abstract class CollectionJoinNode extends ExpressionNode {

  @Idempotent
  protected abstract Rql2TypeWithProperties getRightType();

  @Idempotent
  protected abstract Boolean getReshapeBeforePredicate();

  @Idempotent
  protected abstract int getComputeNextSlot();

  @Idempotent
  protected abstract int getShouldContinueSlot();

  @Idempotent
  protected abstract int getResultSlot();

  @Idempotent
  protected abstract int getGeneratorSlot();

  @Idempotent
  protected abstract int getOutputBufferSlot();

  @Specialization
  protected static Object doJoin(
      VirtualFrame frame,
      Object leftIterable,
      Object rightIterable,
      Object remap,
      Object predicate,
      @Bind("$node") Node thisNode,
      @Cached(value = "getKryoOutputBufferSize(thisNode)", neverDefault = true)
          int kryoOutputBufferSize) {
    CollectionJoinNode joinNode = (CollectionJoinNode) thisNode;
    return new JoinCollection(
        leftIterable,
        rightIterable,
        remap,
        predicate,
        joinNode.getRightType(),
        joinNode.getReshapeBeforePredicate(),
        kryoOutputBufferSize,
        frame.materialize(),
        joinNode.getComputeNextSlot(),
        joinNode.getShouldContinueSlot(),
        joinNode.getResultSlot(),
        joinNode.getGeneratorSlot(),
        joinNode.getOutputBufferSlot());
  }
}
