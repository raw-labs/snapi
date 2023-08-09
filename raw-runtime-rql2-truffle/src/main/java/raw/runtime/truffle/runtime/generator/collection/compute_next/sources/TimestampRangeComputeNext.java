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

package raw.runtime.truffle.runtime.generator.collection.compute_next.sources;

import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.time.LocalDateTime;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.generator.collection.compute_next.ComputeNextLibrary;
import raw.runtime.truffle.runtime.primitives.IntervalObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;

@ExportLibrary(ComputeNextLibrary.class)
public class TimestampRangeComputeNext {
  private final LocalDateTime end;
  private final IntervalObject step;
  private LocalDateTime current;

  public TimestampRangeComputeNext(
      TimestampObject start, TimestampObject end, IntervalObject step) {
    this.current = start.getTimestamp();
    this.end = end.getTimestamp();
    this.step = step;
  }

  @ExportMessage
  void init() {}

  @ExportMessage
  void close() {}

  @ExportMessage
  public boolean isComputeNext() {
    return true;
  }

  @ExportMessage
  Object computeNext() {
    if (current.isBefore(end)) {
      TimestampObject r = new TimestampObject(current);
      current =
          current
              .plusYears(step.getYears())
              .plusMonths(step.getMonths())
              .plusWeeks(step.getWeeks())
              .plusDays(step.getDays())
              .plusHours(step.getHours())
              .plusMinutes(step.getMinutes())
              .plusSeconds(step.getSeconds())
              .plusNanos(1000000L * step.getMillis());
      return r;
    } else {
      throw new BreakException();
    }
  }
}
