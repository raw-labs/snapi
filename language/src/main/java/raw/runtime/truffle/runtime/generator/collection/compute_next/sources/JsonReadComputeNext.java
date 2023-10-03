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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.DirectCallNode;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodes;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.json.JsonReaderRawTruffleException;
import raw.runtime.truffle.runtime.generator.collection.compute_next.ComputeNextLibrary;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.utils.TruffleCharInputStream;
import raw.runtime.truffle.utils.TruffleInputStream;
import raw.sources.api.SourceContext;

@ExportLibrary(ComputeNextLibrary.class)
public class JsonReadComputeNext {

  private final LocationObject locationObject;
  private JsonParser parser;
  private final DirectCallNode parseNextCallNode;
  private final SourceContext context;
  private final String encoding;

  private TruffleCharInputStream stream;

  public JsonReadComputeNext(
      LocationObject locationObject,
      String encoding,
      SourceContext context,
      DirectCallNode parseNextCallNode) {
    this.encoding = encoding;
    this.context = context;
    this.locationObject = locationObject;
    this.parseNextCallNode = parseNextCallNode;
  }

  @ExportMessage
  void init(
      @Cached("create()") JsonParserNodes.InitJsonParserNode initParser,
      @Cached.Shared("closeParser") @Cached("create()")
          JsonParserNodes.CloseJsonParserNode closeParser,
      @Cached("create()") JsonParserNodes.NextTokenJsonParserNode nextToken) {
    try {
      TruffleInputStream truffleInputStream = new TruffleInputStream(locationObject, context);
      stream = new TruffleCharInputStream(truffleInputStream, encoding);
      this.parser = initParser.execute(stream);
      // move from null to the first token
      nextToken.execute(parser);
      // the first token is START_ARRAY so skip it
      nextToken.execute(parser);
    } catch (JsonReaderRawTruffleException ex) {
      JsonReaderRawTruffleException newEx =
          new JsonReaderRawTruffleException(ex.getMessage(), parser, stream);
      closeParser.execute(parser);
      throw newEx;
    } catch (RawTruffleRuntimeException ex) {
      closeParser.execute(parser);
      throw ex;
    }
  }

  @ExportMessage
  void close(
      @Cached.Shared("closeParser") @Cached("create()")
          JsonParserNodes.CloseJsonParserNode closeParser) {
    closeParser.execute(parser);
  }

  @ExportMessage
  public boolean isComputeNext() {
    return true;
  }

  @ExportMessage
  Object computeNext(@Cached JsonParserNodes.CurrentTokenJsonParserNode currentToken) {
    try {
      JsonToken token = currentToken.execute(parser);
      if (token != JsonToken.END_ARRAY && token != null) {
        return parseNextCallNode.call(parser);
      } else {
        throw new BreakException();
      }
    } catch (JsonReaderRawTruffleException e) {
      throw new JsonReaderRawTruffleException(e.getMessage(), stream);
    }
  }
}
