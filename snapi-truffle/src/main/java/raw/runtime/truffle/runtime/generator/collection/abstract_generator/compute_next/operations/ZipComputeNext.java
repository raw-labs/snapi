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

import raw.runtime.truffle.RawLanguage;

public class ZipComputeNext {
  private final Object parent1;

  private final Object parent2;

  public ZipComputeNext(Object parent1, Object parent2) {
    this.parent1 = parent1;
    this.parent2 = parent2;
  }

  public Object getParent1() {
    return parent1;
  }

  public Object getParent2() {
    return parent2;
  }
}
