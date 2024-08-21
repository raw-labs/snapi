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

import static java.lang.Math.addExact;

import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.BreakException;

public class LongRangeComputeNext {
  private final long end;
  private final long step;
  private long position;

  public LongRangeComputeNext(long start, long end, long step) {
    this.end = end;
    this.step = step;
    this.position = start - step;
  }

  public long next() {
    long current;
    try {
      current = addExact(position, step);
    } catch (ArithmeticException e) {
      throw new BreakException();
    }
    if (current >= end) {
      throw new BreakException();
    }
    position = current;
    return current;
  }
}
