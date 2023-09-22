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

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.DirectCallNode;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.ast.io.csv.reader.CsvParserNodes;
import raw.runtime.truffle.ast.io.csv.reader.parser.RawTruffleCsvParser;
import raw.runtime.truffle.ast.io.csv.reader.parser.RawTruffleCsvParserSettings;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.csv.CsvParserRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.csv.CsvReaderRawTruffleException;
import raw.runtime.truffle.runtime.generator.collection.compute_next.ComputeNextLibrary;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.utils.TruffleCharInputStream;
import raw.runtime.truffle.utils.TruffleInputStream;

@ExportLibrary(ComputeNextLibrary.class)
public class CsvReadComputeNext {

  private final LocationObject location;
  private RawTruffleCsvParser parser;
  private final DirectCallNode rowParser;
  private final RuntimeContext context;

  private TruffleCharInputStream stream;
  private final String encoding;
  private final RawTruffleCsvParserSettings settings;

  public CsvReadComputeNext(
      LocationObject location,
      RuntimeContext context,
      DirectCallNode rowParser,
      String encoding,
      RawTruffleCsvParserSettings settings) {
    this.context = context;
    this.location = location;
    this.rowParser = rowParser;
    this.encoding = encoding;
    this.settings = settings;
  }

  @ExportMessage
  void init(
      @Cached("create()") CsvParserNodes.InitCsvParserNode initParser,
      @Cached.Shared("closeParser") @Cached("create()")
          CsvParserNodes.CloseCsvParserNode closeParser) {
    try {
      TruffleInputStream truffleInputStream = new TruffleInputStream(location, context);
      stream = new TruffleCharInputStream(truffleInputStream, encoding);
      parser = initParser.execute(stream, settings);
    } catch (RawTruffleRuntimeException ex) {
      closeParser.execute(parser);
      throw ex;
    }
    parser.skipHeaderLines();
  }

  @ExportMessage
  void close(
      @Cached.Shared("closeParser") @Cached("create()")
          CsvParserNodes.CloseCsvParserNode closeParser) {
    closeParser.execute(parser);
  }

  @ExportMessage
  public boolean isComputeNext() {
    return true;
  }

  @ExportMessage
  Object computeNext() {
    if (parser.done()) {
      throw new BreakException();
    }
    try {
      return rowParser.call(parser);
    } catch (CsvParserRawTruffleException e) {
      // wrap any error with the stream location
      throw new CsvReaderRawTruffleException(e.getMessage(), null, stream);
    }
  }
}
