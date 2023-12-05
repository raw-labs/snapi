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
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.compute_next.ComputeNextLibrary;

public final class ExpressionComputeNext {

  private final Object[] values;
  private int position;

  public ExpressionComputeNext(Object[] values) {
    this.values = values;
    this.position = 0;
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
    if (position >= values.length) {
      throw new BreakException();
    }
    try {
      return values[position];
    } catch (RawTruffleRuntimeException e) {
      return new RawTruffleRuntimeException(e.getMessage());
    } finally {
      position++;
    }
  }
}
