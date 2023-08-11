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

import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;

@ImportStatic(TypeGuards.class)
@NodeChild("value")
@NodeField(name = "slot", type = int.class)
@NodeField(name = "rql2Type", type = Rql2Type.class)
public abstract class WriteLocalVariableNode extends ExpressionNode {

    protected abstract int getSlot();

    protected abstract Rql2Type getRql2Type();

    @Specialization(guards = "isBooleanKind(getRql2Type())")
    protected final boolean doBoolean(VirtualFrame frame, boolean value) {
        frame.getFrameDescriptor().setSlotKind(getSlot(), FrameSlotKind.Boolean);
        frame.setBoolean(getSlot(), value);
        return value;
    }

    @Specialization(guards = "isByteKind(getRql2Type())")
    protected final byte doByte(VirtualFrame frame, byte value) {
        frame.getFrameDescriptor().setSlotKind(getSlot(), FrameSlotKind.Byte);

        frame.setByte(getSlot(), value);
        return value;
    }

    @Specialization(guards = "isShortKind(getRql2Type())")
    protected final short doShort(VirtualFrame frame, short value) {
        frame.getFrameDescriptor().setSlotKind(getSlot(), FrameSlotKind.Int);

        frame.setInt(getSlot(), value);
        return value;
    }

    @Specialization(guards = "isIntKind(getRql2Type())")
    protected final int doInt(VirtualFrame frame, int value) {
        frame.getFrameDescriptor().setSlotKind(getSlot(), FrameSlotKind.Int);

        frame.setInt(getSlot(), value);
        return value;
    }

    @Specialization(guards = "isLongKind(getRql2Type())")
    protected final long doLong(VirtualFrame frame, long value) {
        frame.getFrameDescriptor().setSlotKind(getSlot(), FrameSlotKind.Long);

        frame.setLong(getSlot(), value);
        return value;
    }

    @Specialization(guards = "isFloatKind(getRql2Type())")
    protected final float doFloat(VirtualFrame frame, float value) {
        frame.getFrameDescriptor().setSlotKind(getSlot(), FrameSlotKind.Float);

        frame.setFloat(getSlot(), value);
        return value;
    }

    @Specialization(guards = "isDoubleKind(getRql2Type())")
    protected final double doDouble(VirtualFrame frame, double value) {
        frame.getFrameDescriptor().setSlotKind(getSlot(), FrameSlotKind.Double);

        frame.setDouble(getSlot(), value);
        return value;
    }

    @Specialization(replaces = {"doBoolean", "doByte", "doShort", "doInt", "doFloat", "doDouble", "doLong"})
    protected final Object doObject(VirtualFrame frame, Object value) {
        frame.getFrameDescriptor().setSlotKind(getSlot(), FrameSlotKind.Object);

        frame.setObject(getSlot(), value);
        return value;
    }

}
