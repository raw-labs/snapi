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

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.iterable.operations.TakeCollection;

@NodeInfo(shortName = "Collection.Take")
@NodeChild("iterable")
@NodeChild("takeCount")
public abstract class CollectionTakeNode extends ExpressionNode {
  @Specialization
  protected Object doTake(Object iterable, long takeCount) {
    return new TakeCollection(iterable, takeCount);
  }
}
