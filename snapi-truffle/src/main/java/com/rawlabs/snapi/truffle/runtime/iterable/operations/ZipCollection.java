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

package com.rawlabs.snapi.truffle.runtime.iterable.operations;

import com.oracle.truffle.api.dsl.Bind;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;
import com.rawlabs.snapi.truffle.Rql2Language;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.iterable.IterableNodes;

@ExportLibrary(InteropLibrary.class)
public class ZipCollection implements TruffleObject {
  final Object parentIterable1;
  final Object parentIterable2;

  public ZipCollection(Object iterable1, Object iterable2, Rql2Language language) {
    this.parentIterable1 = iterable1;
    this.parentIterable2 = iterable2;
  }

  public Object getParentIterable1() {
    return parentIterable1;
  }

  public Object getParentIterable2() {
    return parentIterable2;
  }

  @ExportMessage
  boolean hasIterator() {
    return true;
  }

  @ExportMessage
  Object getIterator(
      @Bind("$node") Node thisNode,
      @Cached(inline = true) IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) GeneratorNodes.GeneratorInitNode initNode) {
    Object generator = getGeneratorNode.execute(thisNode, this);
    initNode.execute(thisNode, generator);
    return generator;
  }
}
