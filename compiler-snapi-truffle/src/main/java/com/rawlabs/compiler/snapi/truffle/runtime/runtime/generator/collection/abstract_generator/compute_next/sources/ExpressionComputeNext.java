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

package com.rawlabs.compiler.snapi.truffle.runtime.runtime.generator.collection.abstract_generator.compute_next.sources;

import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.BreakException;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.RawTruffleRuntimeException;

public class ExpressionComputeNext {
  private final Object[] values;
  private int position;

  public ExpressionComputeNext(Object[] values) {
    this.values = values;
    this.position = 0;
  }

  public void incrementPosition() {
    position++;
  }

  public boolean isTerminated() {
    return position >= values.length;
  }

  public Object getCurrent() {
    return values[position];
  }

  public Object next() {
    if (this.isTerminated()) {
      throw new BreakException();
    }
    try {
      return this.getCurrent();
    } catch (RawTruffleRuntimeException e) {
      return new RawTruffleRuntimeException(e.getMessage(), e, null);
    } finally {
      this.incrementPosition();
    }
  }
}
