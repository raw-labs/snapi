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

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.frontend.snapi.source.SnapiTypeWithProperties;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.iterable.operations.EquiJoinCollection;

@NodeInfo(shortName = "Collection.EquiJoin")
public class CollectionEquiJoinNode extends ExpressionNode {
  @Child private ExpressionNode left;
  @Child private ExpressionNode right;
  @Child private ExpressionNode leftKeyFun;
  @Child private ExpressionNode rightKeyFun;
  @Child private ExpressionNode remapFun;

  private final SnapiTypeWithProperties leftValueType;
  private final SnapiTypeWithProperties rightValueType;
  private final SnapiTypeWithProperties keyType;

  private final int computeNextSlot;
  private final int shouldContinueSlot;
  private final int generatorSlot;
  private final int keyFunctionSlot;
  private final int mapSlot;

  public CollectionEquiJoinNode(
          ExpressionNode left,
          ExpressionNode right,
          ExpressionNode leftKeyFun,
          ExpressionNode rightKeyFun,
          SnapiTypeWithProperties keyType,
          SnapiTypeWithProperties leftValueType,
          SnapiTypeWithProperties rightValueType,
          ExpressionNode remapFun,
          int computeNextSlot,
          int shouldContinueSlot,
          int generatorSlot,
          int keyFunctionSlot,
          int mapSlot) {
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

    this.computeNextSlot = computeNextSlot;
    this.shouldContinueSlot = shouldContinueSlot;
    this.generatorSlot = generatorSlot;
    this.keyFunctionSlot = keyFunctionSlot;
    this.mapSlot = mapSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object leftIterable = left.executeGeneric(frame);
    Object leftKeyF = leftKeyFun.executeGeneric(frame);
    Object rightIterable = right.executeGeneric(frame);
    Object rightKeyF = rightKeyFun.executeGeneric(frame);
    Object remapF = remapFun.executeGeneric(frame);

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
