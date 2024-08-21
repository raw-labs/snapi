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

package com.rawlabs.compiler.snapi.truffle.runtime.runtime.iterable.sources;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.Bind;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.rdbms.JdbcExceptionHandler;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.generator.collection.abstract_generator.AbstractGenerator;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.generator.collection.abstract_generator.compute_next.sources.JdbcQueryComputeNext;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.LocationObject;
import com.rawlabs.utils.core.RawSettings;

@ExportLibrary(InteropLibrary.class)
public class JdbcQueryCollection implements TruffleObject {
  private final LocationObject dbLocation;
  private final String query;
  private final RawSettings rawSettings;
  private final RootCallTarget rowParserCallTarget;

  private final JdbcExceptionHandler exceptionHandler;

  public JdbcQueryCollection(
      LocationObject dbLocation,
      String query,
      RawSettings rawSettings,
      RootCallTarget rowParserCallTarget,
      JdbcExceptionHandler exceptionHandler) {
    this.dbLocation = dbLocation;
    this.query = query;
    this.rawSettings = rawSettings;
    this.rowParserCallTarget = rowParserCallTarget;
    this.exceptionHandler = exceptionHandler;
  }

  public AbstractGenerator getGenerator() {
    return new AbstractGenerator(
        new JdbcQueryComputeNext(
            dbLocation, query, rawSettings, rowParserCallTarget, exceptionHandler));
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
