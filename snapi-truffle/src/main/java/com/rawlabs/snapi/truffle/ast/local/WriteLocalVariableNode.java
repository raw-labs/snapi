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

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.rawlabs.snapi.frontend.snapi.source.*;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.TypeGuards;

@ImportStatic(TypeGuards.class)
@NodeChild("value")
@NodeField(name = "slot", type = int.class)
@NodeField(name = "snapiType", type = SnapiType.class)
public abstract class WriteLocalVariableNode extends ExpressionNode {

  @Idempotent
  protected abstract int getSlot();

  @Idempotent
  protected abstract SnapiType getSnapiType();

  @Specialization(guards = "isBooleanKind(getSnapiType())")
  protected final boolean doBoolean(VirtualFrame frame, boolean value) {
    frame.getFrameDescriptor().setSlotKind(getSlot(), FrameSlotKind.Boolean);
    frame.setBoolean(getSlot(), value);
    return value;
  }

  @Specialization(guards = "isByteKind(getSnapiType())")
  protected final byte doByte(VirtualFrame frame, byte value) {
    frame.getFrameDescriptor().setSlotKind(getSlot(), FrameSlotKind.Byte);

    frame.setByte(getSlot(), value);
    return value;
  }

  @Specialization(guards = "isShortKind(getSnapiType())")
  protected final short doShort(VirtualFrame frame, short value) {
    frame.getFrameDescriptor().setSlotKind(getSlot(), FrameSlotKind.Int);

    frame.setInt(getSlot(), value);
    return value;
  }

  @Specialization(guards = "isIntKind(getSnapiType())")
  protected final int doInt(VirtualFrame frame, int value) {
    frame.getFrameDescriptor().setSlotKind(getSlot(), FrameSlotKind.Int);

    frame.setInt(getSlot(), value);
    return value;
  }

  @Specialization(guards = "isLongKind(getSnapiType())")
  protected final long doLong(VirtualFrame frame, long value) {
    frame.getFrameDescriptor().setSlotKind(getSlot(), FrameSlotKind.Long);

    frame.setLong(getSlot(), value);
    return value;
  }

  @Specialization(guards = "isFloatKind(getSnapiType())")
  protected final float doFloat(VirtualFrame frame, float value) {
    frame.getFrameDescriptor().setSlotKind(getSlot(), FrameSlotKind.Float);

    frame.setFloat(getSlot(), value);
    return value;
  }

  @Specialization(guards = "isDoubleKind(getSnapiType())")
  protected final double doDouble(VirtualFrame frame, double value) {
    frame.getFrameDescriptor().setSlotKind(getSlot(), FrameSlotKind.Double);

    frame.setDouble(getSlot(), value);
    return value;
  }

  @Specialization(
      replaces = {"doBoolean", "doByte", "doShort", "doInt", "doFloat", "doDouble", "doLong"})
  protected final Object doObject(VirtualFrame frame, Object value) {
    frame.getFrameDescriptor().setSlotKind(getSlot(), FrameSlotKind.Object);

    frame.setObject(getSlot(), value);
    return value;
  }
}
