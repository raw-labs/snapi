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

public class UnnestComputeNext {
  final Object parent;
  final Object transform;

  Object currentGenerator = null;

  public UnnestComputeNext(Object parent, Object transform) {
    this.parent = parent;
    this.transform = transform;
  }

  public void setCurrentGenerator(Object currentGenerator) {
    this.currentGenerator = currentGenerator;
  }

  public Object getParent() {
    return parent;
  }

  public Object getTransform() {
    return transform;
  }

  public Object getCurrentGenerator() {
    return currentGenerator;
  }
}
