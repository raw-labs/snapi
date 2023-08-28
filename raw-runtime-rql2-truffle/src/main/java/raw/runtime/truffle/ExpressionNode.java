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

package raw.runtime.truffle;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import raw.runtime.truffle.runtime.primitives.*;
import raw.runtime.truffle.runtime.record.RecordObject;

import java.math.BigDecimal;

@TypeSystemReference(RawTypes.class)
@GenerateWrapper
public abstract class ExpressionNode extends StatementNode {

    private boolean hasExpressionTag;

    public abstract Object executeGeneric(VirtualFrame virtualFrame);

    public void executeVoid(VirtualFrame virtualFrame) {
        executeGeneric(virtualFrame);
    }

    public boolean executeBoolean(VirtualFrame virtualFrame) throws UnexpectedResultException {
        return RawTypesGen.expectBoolean(executeGeneric(virtualFrame));
    }

    public byte executeByte(VirtualFrame virtualFrame) throws UnexpectedResultException {
        return RawTypesGen.expectByte(executeGeneric(virtualFrame));
    }

    public short executeShort(VirtualFrame virtualFrame) throws UnexpectedResultException {
        return RawTypesGen.expectShort(executeGeneric(virtualFrame));
    }

    public int executeInt(VirtualFrame virtualFrame) throws UnexpectedResultException {
        return RawTypesGen.expectInteger(executeGeneric(virtualFrame));
    }

    public long executeLong(VirtualFrame virtualFrame) throws UnexpectedResultException {
        return RawTypesGen.expectLong(executeGeneric(virtualFrame));
    }

    public float executeFloat(VirtualFrame virtualFrame) throws UnexpectedResultException {
        return RawTypesGen.expectFloat(executeGeneric(virtualFrame));
    }

    public double executeDouble(VirtualFrame virtualFrame) throws UnexpectedResultException {
        return RawTypesGen.expectDouble(executeGeneric(virtualFrame));
    }

    public byte[] executeBinary(VirtualFrame virtualFrame) throws UnexpectedResultException {
        return RawTypesGen.expectByteArray(executeGeneric(virtualFrame));
    }

    public BigDecimal executeBigDecimal(VirtualFrame virtualFrame)
            throws UnexpectedResultException {
        return RawTypesGen.expectBigDecimal(executeGeneric(virtualFrame));
    }

    public DateObject executeDate(VirtualFrame virtualFrame) throws UnexpectedResultException {
        return RawTypesGen.expectDateObject(executeGeneric(virtualFrame));
    }

    public IntervalObject executeInterval(VirtualFrame virtualFrame)
            throws UnexpectedResultException {
        return RawTypesGen.expectIntervalObject(executeGeneric(virtualFrame));
    }

    public TimeObject executeTime(VirtualFrame virtualFrame) throws UnexpectedResultException {
        return RawTypesGen.expectTimeObject(executeGeneric(virtualFrame));
    }

    public TimestampObject executeTimestamp(VirtualFrame virtualFrame)
            throws UnexpectedResultException {
        return RawTypesGen.expectTimestampObject(executeGeneric(virtualFrame));
    }

    public String executeString(VirtualFrame virtualFrame) throws UnexpectedResultException {
        return RawTypesGen.expectString(executeGeneric(virtualFrame));
    }

    public LocationObject executeLocation(VirtualFrame virtualFrame)
            throws UnexpectedResultException {
        return RawTypesGen.expectLocationObject(executeGeneric(virtualFrame));
    }

    public RecordObject executeRecord(VirtualFrame virtualFrame) throws UnexpectedResultException {
        return RawTypesGen.expectRecordObject(executeGeneric(virtualFrame));
    }

    @Override
    public WrapperNode createWrapper(ProbeNode probe) {
        return new ExpressionNodeWrapper(this, probe);
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        if (tag == StandardTags.ExpressionTag.class) {
            return hasExpressionTag;
        }
        return super.hasTag(tag);
    }

    public final void addExpressionTag() {
        hasExpressionTag = true;
    }
}
