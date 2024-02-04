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

package raw.runtime.truffle.ast.expressions.iterable.list.osr;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import java.util.ArrayList;
import raw.compiler.rql2.source.Rql2Type;
import raw.runtime.truffle.ast.TypeGuards;

public class OSRToArrayNode extends Node implements RepeatingNode {

  private ArrayList<Object> llist;

  private final Rql2Type resultType;

  private int currentIdx;

  private Object result;

  public OSRToArrayNode(Rql2Type resultType) {
    this.resultType = resultType;
  }

  public void init(ArrayList<Object> llist) {
    currentIdx = 0;
    this.llist = llist;
    result = StaticArrayBuilder.build(llist.size(), resultType);
  }

  public boolean executeRepeating(VirtualFrame frame) {
    // ignored
    return false;
  }

  public boolean shouldContinue(Object returnValue) {
    return returnValue == this.initialLoopStatus() || currentIdx < llist.size();
  }

  public Object executeRepeatingWithValue(VirtualFrame frame) {
    if (currentIdx == llist.size()) {
      return result;
    }
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
    currentIdx++;
    return result;
  }
}
