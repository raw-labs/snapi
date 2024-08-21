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

package raw.runtime.truffle.ast.local;

import com.oracle.truffle.api.dsl.Idempotent;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.rawlabs.compiler.snapi.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;

@ImportStatic(TypeGuards.class)
@NodeField(name = "slot", type = int.class)
@NodeField(name = "rql2Type", type = Rql2Type.class)
public abstract class ReadLocalVariableNode extends ExpressionNode {

  @Idempotent
  protected abstract int getSlot();

  @Idempotent
  protected abstract Rql2Type getRql2Type();

  @Specialization(guards = "isBooleanKind(getRql2Type())")
  protected final boolean doBoolean(VirtualFrame frame) {
    return frame.getBoolean(getSlot());
  }

  @Specialization(guards = "isByteKind(getRql2Type())")
  protected final byte doByte(VirtualFrame frame) {
    return frame.getByte(getSlot());
  }

  @Specialization(guards = "isShortKind(getRql2Type())")
  protected final short doShort(VirtualFrame frame) {
    return (short) frame.getInt(getSlot());
  }

  @Specialization(guards = "isIntKind(getRql2Type())")
  protected final int doInt(VirtualFrame frame) {
    return frame.getInt(getSlot());
  }

  @Specialization(guards = "isLongKind(getRql2Type())")
  protected final long doLong(VirtualFrame frame) {
    return frame.getLong(getSlot());
  }

  @Specialization(guards = "isFloatKind(getRql2Type())")
  protected final float doFloat(VirtualFrame frame) {
    return frame.getFloat(getSlot());
  }

  @Specialization(guards = "isDoubleKind(getRql2Type())")
  protected final double doDouble(VirtualFrame frame) {
    return frame.getDouble(getSlot());
  }

  @Specialization(
      replaces = {"doBoolean", "doByte", "doShort", "doInt", "doFloat", "doDouble", "doLong"})
  protected final Object doObject(VirtualFrame frame) {
    return frame.getObject(getSlot());
  }
}
