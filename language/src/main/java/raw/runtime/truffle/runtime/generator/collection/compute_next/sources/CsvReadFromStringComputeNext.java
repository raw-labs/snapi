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
import raw.runtime.truffle.ast.io.csv.reader.CsvParserNodes;
import raw.runtime.truffle.ast.io.csv.reader.parser.RawTruffleCsvParser;
import raw.runtime.truffle.ast.io.csv.reader.parser.RawTruffleCsvParserSettings;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.csv.CsvParserRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.csv.CsvReaderRawTruffleException;
import raw.runtime.truffle.runtime.generator.collection.compute_next.ComputeNextLibrary;
import raw.runtime.truffle.utils.RawTruffleCharStream;
import raw.runtime.truffle.utils.RawTruffleStringCharStream;

@ExportLibrary(ComputeNextLibrary.class)
public class CsvReadFromStringComputeNext {

  private final RawTruffleCharStream stream;
  private RawTruffleCsvParser parser;
  protected final RootNode rowParserNode;
  private final RawTruffleCsvParserSettings settings;

  public CsvReadFromStringComputeNext(
      String str, RootNode rowParserNode, RawTruffleCsvParserSettings settings) {
    this.rowParserNode = rowParserNode;
    this.settings = settings;
    this.stream = new RawTruffleStringCharStream(str);
  }

  @ExportMessage
  void init(
      @Cached("create()") CsvParserNodes.InitCsvParserNode initParser,
      @Cached.Shared("closeParser") @Cached("create()")
          CsvParserNodes.CloseCsvParserNode closeParser) {
    try {
      parser = initParser.execute(stream, settings);
    } catch (CsvReaderRawTruffleException ex) {
      CsvReaderRawTruffleException newEx =
          new CsvReaderRawTruffleException(ex.getMessage(), null, stream);
      closeParser.execute(parser);
      throw newEx;
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
  Object computeNext(
      @Cached(value = "this.rowParserNode.getCallTarget()", allowUncached = true)
          RootCallTarget cachedTarget,
      @Cached(value = "create(cachedTarget)", allowUncached = true) DirectCallNode rowParser) {
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
