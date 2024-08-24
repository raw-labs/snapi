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

package com.rawlabs.snapi.truffle.ast.local;

import com.oracle.truffle.api.dsl.Idempotent;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.rawlabs.snapi.frontend.snapi.source.*;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.TypeGuards;

@ImportStatic(TypeGuards.class)
@NodeField(name = "slot", type = int.class)
@NodeField(name = "snapiType", type = SnapiType.class)
public abstract class ReadLocalVariableNode extends ExpressionNode {

  @Idempotent
  protected abstract int getSlot();

  @Idempotent
  protected abstract SnapiType getSnapiType();

  @Specialization(guards = "isBooleanKind(getSnapiType())")
  protected final boolean doBoolean(VirtualFrame frame) {
    return frame.getBoolean(getSlot());
  }

  @Specialization(guards = "isByteKind(getSnapiType())")
  protected final byte doByte(VirtualFrame frame) {
    return frame.getByte(getSlot());
  }

  @Specialization(guards = "isShortKind(getSnapiType())")
  protected final short doShort(VirtualFrame frame) {
    return (short) frame.getInt(getSlot());
  }

  @Specialization(guards = "isIntKind(getSnapiType())")
  protected final int doInt(VirtualFrame frame) {
    return frame.getInt(getSlot());
  }

  @Specialization(guards = "isLongKind(getSnapiType())")
  protected final long doLong(VirtualFrame frame) {
    return frame.getLong(getSlot());
  }

  @Specialization(guards = "isFloatKind(getSnapiType())")
  protected final float doFloat(VirtualFrame frame) {
    return frame.getFloat(getSlot());
  }

  @Specialization(guards = "isDoubleKind(getSnapiType())")
  protected final double doDouble(VirtualFrame frame) {
    return frame.getDouble(getSlot());
  }

  @Specialization(
      replaces = {"doBoolean", "doByte", "doShort", "doInt", "doFloat", "doDouble", "doLong"})
  protected final Object doObject(VirtualFrame frame) {
    return frame.getObject(getSlot());
  }
}
