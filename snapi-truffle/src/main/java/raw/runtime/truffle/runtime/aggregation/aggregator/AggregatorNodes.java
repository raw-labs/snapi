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

package raw.runtime.truffle.runtime.aggregation.aggregator;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.operators.OperatorNodes;
import raw.runtime.truffle.runtime.primitives.NullObject;
import raw.runtime.truffle.tryable_nullable.Nullable;

public class AggregatorNodes {
  @NodeInfo(shortName = "Aggregator.Zero")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(Aggregators.class)
  public abstract static class Zero extends Node {

    public abstract Object execute(Node node, byte aggregatorType);

    @Specialization(guards = "aggregatorType == COUNT")
    static Object countZero(Node node, byte aggregatorType) {
      return 0L;
    }

    @Specialization(guards = "aggregatorType == MAX")
    static Object maxZero(Node node, byte aggregatorType) {
      return NullObject.INSTANCE;
    }

    @Specialization(guards = "aggregatorType == MIN")
    static Object minZero(Node node, byte aggregatorType) {
      return NullObject.INSTANCE;
    }

    @Specialization(guards = "aggregatorType == SUM")
    static Object sumZero(Node node, byte aggregatorType) {
      return NullObject.INSTANCE;
    }
  }

  @NodeInfo(shortName = "Aggregator.Merge")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(Aggregators.class)
  public abstract static class Merge extends Node {

    public abstract Object execute(Node node, byte aggregatorType, Object current, Object next);

    @Specialization(guards = "aggregatorType == COUNT")
    static long mergeCount(Node node, byte aggregatorType, long current, Object next) {
      return current + 1;
    }

    @Specialization(guards = "aggregatorType == MAX")
    static Object mergeMax(
        Node node,
        byte aggregatorType,
        Object current,
        Object next,
        @Cached @Cached.Shared("compare") OperatorNodes.CompareNode compare) {
      if (Nullable.isNotNull(current)) {
        if (Nullable.isNotNull(next)) {
          // if both are defined, pick the largest
          if (compare.execute(current, next) > 0) {
            return current;
          } else {
            return next;
          }
        } else {
          // if only current is defined, return it
          return current;
        }
      } else {
        // left is not defined, return right (perhaps not defined either, but fine)
        return next;
      }
    }

    @Specialization(guards = "aggregatorType == MIN")
    static Object mergeMin(
        Node node,
        byte aggregatorType,
        Object current,
        Object next,
        @Cached @Cached.Shared("compare") OperatorNodes.CompareNode compare) {
      if (Nullable.isNotNull(current)) {
        if (Nullable.isNotNull(next)) {
          // if both are defined, pick the smallest
          if (compare.execute(current, next) < 0) {
            return current;
          } else {
            return next;
          }
        } else {
          // if only current is defined, return it
          return current;
        }
      } else {
        // left is not defined, return right (perhaps not defined either, but fine)
        return next;
      }
    }

    @Specialization(guards = "aggregatorType == SUM")
    static Object mergeSum(
        Node node,
        byte aggregatorType,
        Object current,
        Object next,
        @Bind("$node") Node thisNode,
        @Cached OperatorNodes.AddNode add) {
      return add.execute(thisNode, current, next);
    }
  }
}
