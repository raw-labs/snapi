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

package raw.runtime.truffle.runtime.exceptions.csv;

import com.oracle.truffle.api.nodes.Node;
import raw.runtime.truffle.ast.io.csv.reader.parser.RawTruffleCsvParser;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.utils.RawTruffleCharStream;

public class CsvParserRawTruffleException extends RawTruffleRuntimeException {

  public CsvParserRawTruffleException(Throwable cause, Node location) {
    super(cause.getMessage(), cause, location);
  }

  public CsvParserRawTruffleException(int line, int column, Throwable cause, Node location) {
    super(
        String.format(
            "failed to parse CSV (line %d, col %d), %s", line, column, cause.getMessage()),
        cause,
        location);
  }

  public CsvParserRawTruffleException(
      String message, int line, int column, Throwable cause, Node location) {
    super(
        String.format("failed to parse CSV (line %d, col %d), %s", line, column, message),
        cause,
        location);
  }

  public CsvParserRawTruffleException(
      String message, RawTruffleCsvParser p, RawTruffleCharStream stream, Node location) {
    super(
        String.format(
            "failed to parse CSV (%s: line %d, col %d), %s",
            stream.positionDescription(), p.currentTokenLine(), p.currentTokenColumn(), message),
        location);
  }

  public CsvParserRawTruffleException(
      String message, RawTruffleCsvParser p, Throwable cause, Node location) {
    super(
        String.format(
            "failed to parse CSV (line %d, col %d), %s",
            p.currentTokenLine(), p.currentTokenColumn(), message),
        cause,
        location);
  }

  public CsvParserRawTruffleException(String message, RawTruffleCsvParser p, Node location) {
    super(
        String.format(
            "failed to parse CSV (line %d, col %d), %s",
            p.currentTokenLine(), p.currentTokenColumn(), message),
        null,
        location);
  }

  public CsvParserRawTruffleException(RawTruffleCsvParser p, Throwable cause, Node location) {
    this(cause.getMessage(), p.currentTokenLine(), p.currentTokenColumn(), cause, location);
  }
}
