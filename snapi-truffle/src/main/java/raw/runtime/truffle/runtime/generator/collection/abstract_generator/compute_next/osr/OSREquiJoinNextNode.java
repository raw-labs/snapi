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

/// *
// * Copyright 2023 RAW Labs S.A.
// *
// * Use of this software is governed by the Business Source License
// * included in the file licenses/BSL.txt.
// *
// * As of the Change Date specified in that file, in accordance with
// * the Business Source License, use of this software will be governed
// * by the Apache License, Version 2.0, included in the file
// * licenses/APL.txt.
// */
//
// package raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.osr;
//
// import com.oracle.truffle.api.CompilerDirectives;
// import com.oracle.truffle.api.frame.VirtualFrame;
// import com.oracle.truffle.api.nodes.Node;
// import com.oracle.truffle.api.nodes.RepeatingNode;
// import raw.runtime.truffle.runtime.exceptions.BreakException;
// import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
// import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;
// import
// raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations.EquiJoinComputeNext;
// import raw.runtime.truffle.runtime.operators.OperatorNodes;
// import raw.runtime.truffle.runtime.operators.OperatorNodesFactory;
//
// public class OSREquiJoinNextNode extends Node implements RepeatingNode {
//
//  @Child
//  private GeneratorNodes.GeneratorHasNextNode hasNextNode =
//      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();
//
//  @Child
//  private GeneratorNodes.GeneratorNextNode nextNode =
//      GeneratorNodesFactory.GeneratorNextNodeGen.create();
//
//  @Child OperatorNodes.CompareNode compareKey = OperatorNodesFactory.CompareNodeGen.create();
//
//  @CompilerDirectives.CompilationFinal private EquiJoinComputeNext computeNext;
//
//  public void init(EquiJoinComputeNext computeNext) {
//    this.computeNext = computeNext;
//  }
//
//  // keep iterating until we find matching keys
//  public boolean executeRepeating(VirtualFrame frame) {
//    if (computeNext.getLeftKey() == null || computeNext.getRightKey() == null) {
//      if (computeNext.getLeftKey() == null) {
//        if (hasNextNode.execute(this, computeNext.getLeftMapGenerator())) {
//          computeNext.setLeftEntry(
//              (Object[]) nextNode.execute(this, computeNext.getLeftMapGenerator()));
//          computeNext.setLeftKey(computeNext.getLeftEntry()[0]);
//        } else {
//          throw new BreakException();
//        }
//      }
//
//      if (computeNext.getRightKey() == null) {
//        if (hasNextNode.execute(this, computeNext.getRightMapGenerator())) {
//          computeNext.setRightEntry(
//              (Object[]) nextNode.execute(this, computeNext.getRightMapGenerator()));
//          computeNext.setRightKey(computeNext.getRightEntry()[0]);
//        } else {
//          throw new BreakException();
//        }
//      }
//
//      int compare = compareKey.execute(this, computeNext.getLeftKey(), computeNext.getRightKey());
//      // if keys aren't equal, reset the smallest of both (it will be read in the next
//      // iteration and
//      // will be larger)
//      if (compare < 0) {
//        computeNext.setLeftKey(null);
//      } else if (compare > 0) {
//        computeNext.setRightKey(null);
//      } else {
//        // keys are equal, prepare to do the cartesian product between both.
//        // leftRows and rightRows are the arrays of rows with the same key.
//        // We'll iterate over them to produce the cartesian product.
//        computeNext.setLeftRows((Object[]) computeNext.getLeftEntry()[1]);
//        computeNext.setRightRows((Object[]) computeNext.getRightEntry()[1]);
//        computeNext.setLeftIndex(0);
//        computeNext.setRightIndex(0);
//        return false;
//      }
//      return true;
//    }
//    return false;
//  }
// }
