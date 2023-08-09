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
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.generator.collection.compute_next.ComputeNextLibrary;

@ExportLibrary(ComputeNextLibrary.class)
public class IntRangeComputeNext {
  private final int end;
  private final int step;
  private int position;

  public IntRangeComputeNext(int start, int end, int step) {
    this.end = end;
    this.step = step;
    this.position = start - step;
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
    int current;
    try {
      current = Math.addExact(position, step);
    } catch (ArithmeticException e) {
      throw new BreakException();
    }
    if (current >= end) {
      throw new BreakException();
    }
    position = current;
    return position;
  }
}
