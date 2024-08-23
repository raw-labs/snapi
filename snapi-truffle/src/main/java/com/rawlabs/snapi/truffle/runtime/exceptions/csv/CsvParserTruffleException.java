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

package com.rawlabs.snapi.truffle.runtime.exceptions.csv;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.Node;
import com.rawlabs.snapi.truffle.ast.io.csv.reader.parser.TruffleCsvParser;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.utils.TruffleCharStream;

public class CsvParserTruffleException extends TruffleRuntimeException {

  @CompilerDirectives.TruffleBoundary
  public CsvParserTruffleException(Throwable cause, Node location) {
    super(cause.getMessage(), cause, location);
  }

  @CompilerDirectives.TruffleBoundary
  public CsvParserTruffleException(int line, int column, Throwable cause, Node location) {
    super(
        String.format(
            "failed to parse CSV (line %d, col %d), %s", line, column, cause.getMessage()),
        cause,
        location);
  }

  @CompilerDirectives.TruffleBoundary
  public CsvParserTruffleException(
      String message, int line, int column, Throwable cause, Node location) {
    super(
        String.format("failed to parse CSV (line %d, col %d), %s", line, column, message),
        cause,
        location);
  }

  @CompilerDirectives.TruffleBoundary
  public CsvParserTruffleException(
      String message,
      TruffleCsvParser p,
      TruffleCharStream stream,
      Throwable cause,
      Node location) {
    super(
        String.format(
            "failed to parse CSV (%s: line %d, col %d), %s",
            stream.positionDescription(), p.currentTokenLine(), p.currentTokenColumn(), message),
        cause,
        location);
  }

  @CompilerDirectives.TruffleBoundary
  public CsvParserTruffleException(
      String message, TruffleCsvParser p, TruffleCharStream stream, Node location) {
    super(
        String.format(
            "failed to parse CSV (%s: line %d, col %d), %s",
            stream.positionDescription(), p.currentTokenLine(), p.currentTokenColumn(), message),
        location);
  }

  @CompilerDirectives.TruffleBoundary
  public CsvParserTruffleException(
      String message, TruffleCsvParser p, Throwable cause, Node location) {
    super(
        String.format(
            "failed to parse CSV (line %d, col %d), %s",
            p.currentTokenLine(), p.currentTokenColumn(), message),
        cause,
        location);
  }

  @CompilerDirectives.TruffleBoundary
  public CsvParserTruffleException(TruffleCsvParser p, Throwable cause, Node location) {
    this(cause.getMessage(), p.currentTokenLine(), p.currentTokenColumn(), cause, location);
  }
}
