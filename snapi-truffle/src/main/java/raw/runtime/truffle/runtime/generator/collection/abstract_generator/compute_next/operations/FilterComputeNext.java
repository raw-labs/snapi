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

package raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations;

import com.oracle.truffle.api.frame.MaterializedFrame;

public class FilterComputeNext {
  private final Object parent;
  private final Object predicate;
  private final MaterializedFrame frame;
  private final int generatorSlot;

  private final int functionSlot;

  private final int resultSlot;

  public FilterComputeNext(
      Object parent,
      Object predicate,
      MaterializedFrame frame,
      int generatorSlot,
      int functionSlot,
      int resultSlot) {
    this.parent = parent;
    this.predicate = predicate;
    this.frame = frame;
    this.generatorSlot = generatorSlot;
    this.functionSlot = functionSlot;
    this.resultSlot = resultSlot;
  }

  public Object getParent() {
    return parent;
  }

  public Object getPredicate() {
    return predicate;
  }

  public MaterializedFrame getFrame() {
    return frame;
  }

  public int getGeneratorSlot() {
    return generatorSlot;
  }

  public int getFunctionSlot() {
    return functionSlot;
  }

  public int getResultSlot() {
    return resultSlot;
  }
}
