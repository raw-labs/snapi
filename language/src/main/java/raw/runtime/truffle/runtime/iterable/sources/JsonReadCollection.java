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
import com.oracle.truffle.api.nodes.DirectCallNode;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.runtime.generator.collection.CollectionAbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.JsonReadComputeNext;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.sources.api.SourceContext;

@ExportLibrary(IterableLibrary.class)
public class JsonReadCollection {

  private final LocationObject locationObject;
  private final DirectCallNode parseNextRootNode;

  private final SourceContext context;

  private final String encoding;

  public JsonReadCollection(
      LocationObject locationObject,
      String encoding,
      SourceContext context,
      DirectCallNode parseNextRootNode) {
    this.locationObject = locationObject;
    this.parseNextRootNode = parseNextRootNode;
    this.context = context;
    this.encoding = encoding;
  }

  @ExportMessage
  boolean isIterable() {
    return true;
  }

  @ExportMessage
  Object getGenerator() {
    return new CollectionAbstractGenerator(
        new JsonReadComputeNext(locationObject, encoding, context, parseNextRootNode));
  }
}
