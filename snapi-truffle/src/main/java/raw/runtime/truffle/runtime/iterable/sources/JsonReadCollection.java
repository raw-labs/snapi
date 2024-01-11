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

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.Bind;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.AbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.sources.JsonReadComputeNext;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.sources.api.SourceContext;

@ExportLibrary(InteropLibrary.class)
public class JsonReadCollection implements TruffleObject {
  private final LocationObject locationObject;
  private final RootCallTarget parseNextRootCallTarget;

  private final SourceContext context;

  private final String encoding;

  public JsonReadCollection(
      LocationObject locationObject,
      String encoding,
      SourceContext context,
      RootCallTarget parseNextRootCallTarget) {
    this.locationObject = locationObject;
    this.parseNextRootCallTarget = parseNextRootCallTarget;
    this.context = context;
    this.encoding = encoding;
  }

  public AbstractGenerator getGenerator() {
    return new AbstractGenerator(
        new JsonReadComputeNext(locationObject, encoding, context, parseNextRootCallTarget));
  }

  // InteropLibrary: Iterable
  @ExportMessage
  boolean hasIterator() {
    return true;
  }

  @ExportMessage
  Object getIterator(
      @Bind("$node") Node thisNode,
      @Cached(inline = true) GeneratorNodes.GeneratorInitNode initNode) {
    Object generator = getGenerator();
    initNode.execute(thisNode, generator);
    return generator;
  }
}
