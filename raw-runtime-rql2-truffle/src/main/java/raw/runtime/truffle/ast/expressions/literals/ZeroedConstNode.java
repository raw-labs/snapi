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

package raw.runtime.truffle.ast.expressions.literals;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.primitives.DateObject;
import raw.runtime.truffle.runtime.primitives.IntervalObject;
import raw.runtime.truffle.runtime.primitives.TimeObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ZeroedConstNode extends ExpressionNode {

    Rql2Type type;

    public ZeroedConstNode(Rql2Type type) {
        this.type = type;
    }

    @Override
    public final Object executeGeneric(VirtualFrame virtualFrame) {
        if (this.type instanceof Rql2ByteType) {
            return (byte) 0;
        } else if (this.type instanceof Rql2ShortType) {
            return (short) 0;
        } else if (this.type instanceof Rql2IntType) {
            return 0;
        } else if (this.type instanceof Rql2LongType) {
            return (long) 0;
        } else if (this.type instanceof Rql2FloatType) {
            return (float) 0;
        } else if (this.type instanceof Rql2DoubleType) {
            return (double) 0;
        } else if (this.type instanceof Rql2BoolType) {
            return false;
        } else {
            return null;
        }
    }
}
