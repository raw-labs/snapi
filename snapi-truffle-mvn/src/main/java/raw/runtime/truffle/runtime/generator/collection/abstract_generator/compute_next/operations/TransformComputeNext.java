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

import raw.runtime.truffle.runtime.function.Closure;

public class TransformComputeNext {
  final Object parent;
  final Closure transform;

  public TransformComputeNext(Object parent, Closure transform) {
    this.parent = parent;
    this.transform = transform;
  }

  public Object getParent() {
    return parent;
  }

  public Closure getTransform() {
    return transform;
  }
}
