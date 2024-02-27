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

import com.oracle.truffle.api.dsl.Idempotent;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.iterable.operations.JoinCollection;

@NodeInfo(shortName = "Collection.Join")
@NodeChild("left")
@NodeChild("right")
@NodeChild("remap")
@NodeChild("predicate")
@NodeField(name = "rightType", type = Rql2TypeWithProperties.class)
@NodeField(name = "reshapeBeforePredicate", type = Boolean.class)
public abstract class CollectionJoinNode extends ExpressionNode {

  @Idempotent
  protected abstract Rql2TypeWithProperties getRightType();

  @Idempotent
  protected abstract Boolean getReshapeBeforePredicate();

  @Specialization
  protected Object doJoin(
      VirtualFrame frame,
      Object leftIterable,
      Object rightIterable,
      Object remap,
      Object predicate) {
    return new JoinCollection(
        leftIterable,
        rightIterable,
        remap,
        predicate,
        getRightType(),
        getReshapeBeforePredicate(),
        RawContext.get(this).getSourceContext(),
        RawLanguage.get(this),
        frame.materialize());
  }
}
