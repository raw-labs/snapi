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

import static raw.runtime.truffle.ast.osr.AuxiliarySlots.*;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.iterable.operations.EquiJoinCollection;

@NodeInfo(shortName = "Collection.EquiJoin")
public class CollectionEquiJoinNode extends ExpressionNode {
  @Child private ExpressionNode left;
  @Child private ExpressionNode right;
  @Child private ExpressionNode leftKeyFun;
  @Child private ExpressionNode rightKeyFun;
  @Child private ExpressionNode remapFun;

  private final Rql2TypeWithProperties leftValueType;
  private final Rql2TypeWithProperties rightValueType;
  private final Rql2TypeWithProperties keyType;

  @CompilerDirectives.CompilationFinal private int computeNextSlot = -1;
  @CompilerDirectives.CompilationFinal private int shouldContinueSlot = -1;
  @CompilerDirectives.CompilationFinal private int generatorSlot = -1;
  @CompilerDirectives.CompilationFinal private int keyFunctionSlot = -1;
  @CompilerDirectives.CompilationFinal private int mapSlot = -1;

  public CollectionEquiJoinNode(
      ExpressionNode left,
      ExpressionNode right,
      ExpressionNode leftKeyFun,
      ExpressionNode rightKeyFun,
      Rql2TypeWithProperties keyType,
      Rql2TypeWithProperties leftValueType,
      Rql2TypeWithProperties rightValueType,
      ExpressionNode remapFun) {
    this.remapFun = remapFun;
    this.keyType = keyType;
    // left
    this.left = left;
    this.leftKeyFun = leftKeyFun;
    this.leftValueType = leftValueType;
    // right
    this.right = right;
    this.rightKeyFun = rightKeyFun;
    this.rightValueType = rightValueType;
  }

  private void setSlots(VirtualFrame frame) {
    computeNextSlot = getComputeNextSlot(frame.getFrameDescriptor());
    shouldContinueSlot = getShouldContinueSlot(frame.getFrameDescriptor());
    generatorSlot = getGeneratorSlot(frame.getFrameDescriptor());
    keyFunctionSlot = getFunctionSlot(frame.getFrameDescriptor());
    mapSlot = getMapSlot(frame.getFrameDescriptor());
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object leftIterable = left.executeGeneric(frame);
    Object leftKeyF = leftKeyFun.executeGeneric(frame);
    Object rightIterable = right.executeGeneric(frame);
    Object rightKeyF = rightKeyFun.executeGeneric(frame);
    Object remapF = remapFun.executeGeneric(frame);

    if (computeNextSlot == -1) {
      setSlots(frame);
    }

    return new EquiJoinCollection(
        leftIterable,
        leftKeyF,
        leftValueType,
        rightIterable,
        rightKeyF,
        rightValueType,
        keyType,
        remapF,
        frame.materialize(),
        computeNextSlot,
        shouldContinueSlot,
        generatorSlot,
        keyFunctionSlot,
        mapSlot);
  }
}
