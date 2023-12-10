package raw.runtime.truffle.runtime.generator.collection.compute_next.sources;

import com.oracle.truffle.api.CompilerDirectives;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.primitives.IntervalObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;

import java.time.LocalDateTime;

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

  public Object next() {
    if (current.isBefore(end)) {
      TimestampObject r = new TimestampObject(current);
      addTime();
      return r;
    } else {
      throw new BreakException();
    }
  }

  @CompilerDirectives.TruffleBoundary
  private void addTime() {
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
  }
}
