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

package com.rawlabs.snapi.truffle.runtime.generator.collection.abstract_generator.compute_next.sources;

public class UnionComputeNext {
  private final Object[] inputs;
  private int index;
  private Object currentGenerator = null;

  public UnionComputeNext(Object[] inputs) {
    this.inputs = inputs;
    this.index = 0;
  }

  public void setCurrentGenerator(Object currentGenerator) {
    this.currentGenerator = currentGenerator;
  }

  public Object getCurrentGenerator() {
    return currentGenerator;
  }

  public boolean isTerminated() {
    return index >= inputs.length;
  }

  public Object getIterable() {
    return inputs[index];
  }

  public void incrementIndex() {
    index++;
  }
}
