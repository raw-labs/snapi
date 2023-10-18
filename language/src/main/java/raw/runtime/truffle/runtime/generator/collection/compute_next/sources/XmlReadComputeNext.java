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
import raw.runtime.truffle.ast.io.xml.parser.RawTruffleXmlParser;
import raw.runtime.truffle.ast.io.xml.parser.RawTruffleXmlParserSettings;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.xml.XmlParserRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.xml.XmlReaderRawTruffleException;
import raw.runtime.truffle.runtime.generator.collection.compute_next.ComputeNextLibrary;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.utils.TruffleCharInputStream;
import raw.runtime.truffle.utils.TruffleInputStream;
import raw.sources.api.SourceContext;

@ExportLibrary(ComputeNextLibrary.class)
public class XmlReadComputeNext {

  private final LocationObject locationObject;
  protected final RootCallTarget parseNextRootCallTarget;
  private final SourceContext context;
  private final String encoding;
  private RawTruffleXmlParser parser;
  private final RawTruffleXmlParserSettings settings;

  private TruffleCharInputStream stream;

  public XmlReadComputeNext(
      LocationObject locationObject,
      String encoding,
      SourceContext context,
      RootCallTarget parseNextRootCallTarget,
      RawTruffleXmlParserSettings settings) {
    this.encoding = encoding;
    this.context = context;
    this.settings = settings;
    this.locationObject = locationObject;
    this.parseNextRootCallTarget = parseNextRootCallTarget;
  }

  @ExportMessage
  void init() {
    try {
      TruffleInputStream truffleInputStream = new TruffleInputStream(locationObject, context);
      stream = new TruffleCharInputStream(truffleInputStream, encoding);
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
      @Cached(value = "this.parseNextRootCallTarget", allowUncached = true, neverDefault = true)
          RootCallTarget cachedTarget,
      @Cached(value = "create(cachedTarget)", allowUncached = true)
          DirectCallNode parseNextCallNode) {
    if (parser.onEndTag()) {
      throw new BreakException();
    } else {
      try {
        return parseNextCallNode.call(parser);
      } catch (XmlParserRawTruffleException e) {
        throw new XmlReaderRawTruffleException(e, stream, null);
      }
    }
  }
}
