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
import raw.runtime.truffle.runtime.primitives.TimestampObject;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@NodeInfo(shortName = "Timestamp.TimeBucketString")
@NodeChild("value")
@NodeChild("timestamp")
public abstract class TimestampTimeBucketStringNode extends ExpressionNode {
    @Specialization
    protected TimestampObject timestampTimeBucket(String value, TimestampObject timestampObj) {

        LocalDateTime timestamp = timestampObj.getTimestamp();
        LocalDateTime result;

        switch (value.toLowerCase()) {
            case "microseconds":
            case "micros":
                int nanosMic = (timestamp.getNano() / 1000) * 1000;
                result = LocalDateTime.of(timestamp.getYear(), timestamp.getMonthValue(), timestamp.getDayOfMonth(), timestamp.getHour(), timestamp.getMinute(), timestamp.getSecond(), nanosMic);
                break;
            case "milliseconds":
            case "millis":
                int nanosMil = (timestamp.getNano() / 1000000) * 1000000;
                result = LocalDateTime.of(timestamp.getYear(), timestamp.getMonthValue(), timestamp.getDayOfMonth(), timestamp.getHour(), timestamp.getMinute(), timestamp.getSecond(), nanosMil);
                break;
            case "second":
                result = LocalDateTime.of(timestamp.getYear(), timestamp.getMonthValue(), timestamp.getDayOfMonth(), timestamp.getHour(), timestamp.getMinute(), timestamp.getSecond());
                break;
            case "minute":
                result = LocalDateTime.of(timestamp.getYear(), timestamp.getMonthValue(), timestamp.getDayOfMonth(), timestamp.getHour(), timestamp.getMinute());
                break;
            case "hour":
                result = LocalDateTime.of(timestamp.getYear(), timestamp.getMonthValue(), timestamp.getDayOfMonth(), timestamp.getHour(), 0);
                break;
            case "day":
                result = LocalDateTime.of(timestamp.getYear(), timestamp.getMonthValue(), timestamp.getDayOfMonth(), 0, 0);
                break;
            case "week":
                LocalDateTime d = timestamp.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
                result = LocalDateTime.of(d.getYear(), d.getMonthValue(), d.getDayOfMonth(), 0, 0);
                break;
            case "month":
                result = LocalDateTime.of(timestamp.getYear(), timestamp.getMonthValue(), 1, 0, 0);
                break;
            case "quarter":
                int month = ((timestamp.getMonthValue() - 1) / 3) * 3 + 1;
                result = LocalDateTime.of(timestamp.getYear(), month, 1, 0, 0);
                break;
            case "year":
                result = LocalDateTime.of(timestamp.getYear(), 1, 1, 0, 0);
                break;
            case "decade":
                int yearDec = (timestamp.getYear() / 10) * 10;
                result = LocalDateTime.of(yearDec, 1, 1, 0, 0);
                break;
            case "century":
                int yearCent = ((timestamp.getYear() - 1) / 100) * 100 + 1;
                result = LocalDateTime.of(yearCent, 1, 1, 0, 0);
                break;
            case "millennium":
                int yearMil = ((timestamp.getYear() - 1) / 1000) * 1000 + 1;
                result = LocalDateTime.of(yearMil, 1, 1, 0, 0);
                break;
            default:
                throw new RawTruffleRuntimeException("intervals in time_bucket cannot have years or months mixed with other fields.", this);
        }

        return new TimestampObject(result);
    }
}
