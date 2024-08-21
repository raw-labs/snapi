/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.compiler.snapi.truffle.runtime.ast.osr.bodies;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.rawlabs.compiler.snapi.rql2.source.Rql2Type;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.TypeGuards;
import java.util.ArrayList;

public class OSRToArrayBodyNode extends ExpressionNode {

  private final Rql2Type resultType;

  private final int listSlot;

  private final int currentIdxSlot;

  private final int resultSlot;

  public OSRToArrayBodyNode(Rql2Type resultType, int listSlot, int currentIdxSlot, int resultSlot) {
    this.resultType = resultType;
    this.currentIdxSlot = currentIdxSlot;
    this.resultSlot = resultSlot;
    this.listSlot = listSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    int currentIdx = frame.getInt(currentIdxSlot);
    Object result = frame.getObject(resultSlot);
    @SuppressWarnings("unchecked")
    ArrayList<Object> llist = (ArrayList<Object>) frame.getObject(listSlot);

    if (TypeGuards.isByteKind(resultType)) {
      ((byte[]) result)[currentIdx] = (byte) llist.get(currentIdx);
    } else if (TypeGuards.isShortKind(resultType)) {
      ((short[]) result)[currentIdx] = (short) llist.get(currentIdx);
    } else if (TypeGuards.isIntKind(resultType)) {
      ((int[]) result)[currentIdx] = (int) llist.get(currentIdx);
    } else if (TypeGuards.isLongKind(resultType)) {
      ((long[]) result)[currentIdx] = (long) llist.get(currentIdx);
    } else if (TypeGuards.isFloatKind(resultType)) {
      ((float[]) result)[currentIdx] = (float) llist.get(currentIdx);
    } else if (TypeGuards.isDoubleKind(resultType)) {
      ((double[]) result)[currentIdx] = (double) llist.get(currentIdx);
    } else if (TypeGuards.isBooleanKind(resultType)) {
      ((boolean[]) result)[currentIdx] = (boolean) llist.get(currentIdx);
    } else if (TypeGuards.isStringKind(resultType)) {
      ((String[]) result)[currentIdx] = (String) llist.get(currentIdx);
    } else {
      ((Object[]) result)[currentIdx] = llist.get(currentIdx);
    }
    frame.setInt(currentIdxSlot, currentIdx + 1);
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
