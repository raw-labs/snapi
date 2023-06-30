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

package raw.runtime.truffle.ast.expressions.builtin.temporals.time_package;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.primitives.TimeObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

@NodeInfo(shortName = "Time.Build")
@NodeChild("h")
@NodeChild("m")
@NodeChild("s")
@NodeChild("ms")
public abstract class TimeBuildNode extends ExpressionNode {

    @Specialization
    public Object buildTime(int h, int m, int s, int ms) {
        try {
            return ObjectTryable.BuildSuccess(new TimeObject(LocalTime.of(h, m, s, (int) TimeUnit.MILLISECONDS.toNanos(ms))));
        } catch (DateTimeException e) {
            return ObjectTryable.BuildFailure(e.getMessage());
        }
    }

}
