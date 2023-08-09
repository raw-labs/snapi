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
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.iterable.operations.OrderByCollection;

@NodeInfo(shortName = "Collection.OrderBy")
public class CollectionOrderByNode extends ExpressionNode {
  private final ExpressionNode input;
  private final ExpressionNode[] keyFuns;
  private final ExpressionNode[] orderings;
  private final Rql2TypeWithProperties[] keyTypes;
  private final Rql2TypeWithProperties valueType;

  public CollectionOrderByNode(
      ExpressionNode input,
      ExpressionNode[] keyFuns,
      ExpressionNode[] orderings,
      Rql2TypeWithProperties[] keyTypes,
      Rql2TypeWithProperties valueType) {
    this.input = input;
    this.keyFuns = keyFuns;
    this.orderings = orderings;
    this.keyTypes = keyTypes;
    this.valueType = valueType;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object iterable = input.executeGeneric(frame);
    int[] orders = new int[orderings.length];
    for (int i = 0; i < orderings.length; i++) {
      try {
        String order = orderings[i].executeString(frame);
        if (order.equalsIgnoreCase("ASC")) orders[i] = 1;
        else if (order.equalsIgnoreCase("DESC")) orders[i] = -1;
        else throw new RawTruffleRuntimeException("Invalid ordering: " + order);
      } catch (UnexpectedResultException e) {
        throw new RawTruffleRuntimeException(e.getMessage());
      }
    }
    Closure[] keyFunctions = new Closure[this.keyFuns.length];
    for (int i = 0; i < this.keyFuns.length; i++) {
      keyFunctions[i] = (Closure) this.keyFuns[i].executeGeneric(frame);
    }
    return new OrderByCollection(
        iterable,
        keyFunctions,
        orders,
        keyTypes,
        valueType,
        RawLanguage.get(this),
        RawContext.get(this).getRuntimeContext());
  }
}
