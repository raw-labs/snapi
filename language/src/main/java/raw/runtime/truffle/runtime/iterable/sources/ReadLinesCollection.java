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
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.runtime.generator.collection.CollectionAbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.ReadLinesComputeNext;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.utils.TruffleCharInputStream;
import raw.sources.api.SourceContext;

@ExportLibrary(IterableLibrary.class)
public class ReadLinesCollection {

  TruffleCharInputStream stream;
  SourceContext context;

  public ReadLinesCollection(TruffleCharInputStream stream, SourceContext context) {
    this.stream = stream;
    this.context = context;
  }

  @ExportMessage
  boolean isIterable() {
    return true;
  }

  @ExportMessage
  Object getGenerator() {
    return new CollectionAbstractGenerator(new ReadLinesComputeNext(stream));
  }
}
