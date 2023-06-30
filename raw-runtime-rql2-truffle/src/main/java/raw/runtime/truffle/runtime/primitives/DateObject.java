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

package raw.runtime.truffle.runtime.primitives;

import com.oracle.truffle.api.interop.TruffleObject;

import java.time.LocalDate;
import java.time.LocalTime;

import static raw.runtime.truffle.runtime.primitives.TruffleTemporalFormatter.DATE_FORMATTER;

public class DateObject implements TruffleObject {

    private final LocalDate date;

    public DateObject(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public String toString() {
        return date.format(DATE_FORMATTER);
    }
}
