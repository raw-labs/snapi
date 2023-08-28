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

package raw.runtime.truffle.ast.expressions.builtin.temporals.timestamp_package;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.iterable.sources.RangeCollection;
import raw.runtime.truffle.runtime.primitives.IntervalObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;

@NodeInfo(shortName = "Timestamp.Range")
@NodeChild("start")
@NodeChild("end")
@NodeChild("step")
public abstract class TimestampRangeNode extends ExpressionNode {

    private static final IntervalObject zero = new IntervalObject(0, 0, 0, 0, 0, 0, 0, 0);

    @Specialization
    protected Object doRange(TimestampObject start, TimestampObject end, IntervalObject step) {
        if (step.compareTo(zero) <= 0)
            throw new RawTruffleRuntimeException("range step has to be strictly positive", this);
        return new RangeCollection(start, end, step);
    }
}
