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

import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.compute_next.ComputeNextLibrary;

@ExportLibrary(ComputeNextLibrary.class)
public final class ExpressionComputeNext {

  private final ExpressionNode[] exps;
  private final MaterializedFrame frame;
  private int position;

  public ExpressionComputeNext(ExpressionNode[] exps, MaterializedFrame frame) {
    this.frame = frame;
    this.exps = exps;
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
    if (position >= exps.length) {
      throw new BreakException();
    }
    try {
      return exps[position].executeGeneric(frame);
    } catch (RawTruffleRuntimeException e) {
      return new RawTruffleRuntimeException(e.getMessage());
    } finally {
      position++;
    }
  }
}
