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
import raw.runtime.truffle.ast.io.csv.reader.parser.RawTruffleCsvParserSettings;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.AbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.sources.CsvReadFromStringComputeNext;

@ExportLibrary(InteropLibrary.class)
public class CsvFromStringCollection implements TruffleObject {
  private final String str;
  private final RootCallTarget rowParserCallTarget;

  private final RawTruffleCsvParserSettings settings;

  public CsvFromStringCollection(
      String str, RootCallTarget rowParserCallTarget, RawTruffleCsvParserSettings settings) {
    this.str = str;
    this.rowParserCallTarget = rowParserCallTarget;
    this.settings = settings;
  }

  public AbstractGenerator getGenerator() {
    return new AbstractGenerator(
        new CsvReadFromStringComputeNext(str, rowParserCallTarget, settings));
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
