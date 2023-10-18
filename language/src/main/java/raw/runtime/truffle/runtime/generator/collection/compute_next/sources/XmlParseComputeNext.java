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

package raw.runtime.truffle.runtime.generator.collection.compute_next.sources;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.RootNode;
import raw.runtime.truffle.ast.io.xml.parser.RawTruffleXmlParser;
import raw.runtime.truffle.ast.io.xml.parser.RawTruffleXmlParserSettings;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.compute_next.ComputeNextLibrary;
import raw.runtime.truffle.utils.RawTruffleStringCharStream;
import raw.sources.api.SourceContext;

@ExportLibrary(ComputeNextLibrary.class)
public class XmlParseComputeNext {

  private final String text;
  protected final RootNode parseNextRootNode;
  private final SourceContext context;
  private RawTruffleXmlParser parser;
  private final RawTruffleXmlParserSettings settings;

  private RawTruffleStringCharStream stream;

  public XmlParseComputeNext(
      String text,
      SourceContext context,
      RootNode parseNextRootNode,
      RawTruffleXmlParserSettings settings) {
    this.context = context;
    this.text = text;
    this.settings = settings;
    this.parseNextRootNode = parseNextRootNode;
  }

  @ExportMessage
  void init() {
    try {
      stream = new RawTruffleStringCharStream(text);
      parser = RawTruffleXmlParser.create(stream, settings);
      // move from null to the first token
      int token = parser.nextToken(); // consume START_OBJECT
      parser.assertCurrentTokenIsStartTag(); // because it's the top level object
    } catch (RawTruffleRuntimeException ex) {
      if (parser != null) parser.close();
      throw ex;
    }
  }

  @ExportMessage
  void close() {
    if (parser != null) parser.close();
  }

  @ExportMessage
  public boolean isComputeNext() {
    return true;
  }

  @ExportMessage
  Object computeNext(
      @Cached(value = "this.parseNextRootNode.getCallTarget()", allowUncached = true)
          RootCallTarget cachedTarget,
      @Cached(value = "create(cachedTarget)", allowUncached = true)
          DirectCallNode parseNextCallNode) {
    if (parser.onEndTag()) {
      throw new BreakException();
    } else {
      return parseNextCallNode.call(parser);
    }
  }
}
