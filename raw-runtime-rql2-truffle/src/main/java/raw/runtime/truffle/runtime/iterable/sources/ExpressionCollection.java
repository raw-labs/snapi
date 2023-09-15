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

package raw.runtime.truffle.runtime.iterable.sources;

import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.generator.collection.CollectionAbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.ExpressionComputeNext;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;

/*
there will be a filter iterable node expression
takes two arguments
builds the function
  ah, that's where the frame is at?

that node is where the parent is
it gets built cached library et al
so then I have a generator object


 */

@ExportLibrary(IterableLibrary.class)
public final class ExpressionCollection {

  private final Object[] values;

  public ExpressionCollection(Object[] values) {
    this.values = values;
  }

  @ExportMessage
  boolean isIterable() {
    return true;
  }

  @ExportMessage
  Object getGenerator() {
    return new CollectionAbstractGenerator(new ExpressionComputeNext(values));
  }
}
