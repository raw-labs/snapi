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

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.DirectCallNode;
import raw.runtime.truffle.ast.io.xml.parser.RawTruffleXmlParserSettings;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.generator.collection.CollectionAbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.XmlParseComputeNext;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.sources.api.SourceContext;

@ExportLibrary(IterableLibrary.class)
@ExportLibrary(InteropLibrary.class)
public class XmlParseCollection implements TruffleObject {

  private final String text;
  private final DirectCallNode parseNextRootNode;
  private final RawTruffleXmlParserSettings settings;
  private final SourceContext context;

  public XmlParseCollection(
      String text,
      SourceContext context,
      DirectCallNode parseNextRootNode,
      RawTruffleXmlParserSettings settings) {
    this.text = text;
    this.parseNextRootNode = parseNextRootNode;
    this.context = context;
    this.settings = settings;
  }

  @ExportMessage
  boolean isIterable() {
    return true;
  }

  @ExportMessage
  Object getGenerator() {
    return new CollectionAbstractGenerator(
        new XmlParseComputeNext(text, context, parseNextRootNode, settings));
  }

  // InteropLibrary: Iterable

  @ExportMessage
  boolean hasIterator() {
    return true;
  }

  private final GeneratorLibrary generatorLibrary = GeneratorLibrary.getFactory().createDispatched(1);

  @ExportMessage
  Object getIterator(@CachedLibrary("this") IterableLibrary iterables) {
    Object generator = iterables.getGenerator(this);
    generatorLibrary.init(generator);
    return generator;
  }

}
