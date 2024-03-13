/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations;

public class TakeComputeNext {
  private final Object parent;

  private final long takeCount;

  private long currentCount = 0;

  public TakeComputeNext(Object parent, long takeCount) {
    this.parent = parent;
    this.takeCount = takeCount;
  }

  public Object getParent() {
    return parent;
  }

  public long getTakeCount() {
    return takeCount;
  }

  public long getCurrentCount() {
    return currentCount;
  }

  public void incrementCurrentCount() {
    currentCount++;
  }
}
