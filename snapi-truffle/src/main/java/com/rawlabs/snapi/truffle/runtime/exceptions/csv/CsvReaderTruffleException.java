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

public class CsvReaderTruffleException extends TruffleRuntimeException {

  @CompilerDirectives.TruffleBoundary
  public CsvReaderTruffleException(
      String message,
      TruffleCsvParser parser,
      TruffleCharStream stream,
      Throwable cause,
      Node location) {
    super(createMessage(message, parser, stream), cause, location);
  }

  @CompilerDirectives.TruffleBoundary
  public CsvReaderTruffleException(
      String message, TruffleCsvParser parser, TruffleCharStream stream) {
    super(createMessage(message, parser, stream));
  }

  @CompilerDirectives.TruffleBoundary
  public CsvReaderTruffleException(
      String message, TruffleCharStream stream, Throwable cause, Node location) {
    super(message, cause, location);
  }

  @CompilerDirectives.TruffleBoundary
  public CsvReaderTruffleException(TruffleCharStream stream, Throwable cause) {
    super(createMessage(cause.getMessage(), null, stream), cause, null);
  }

  @CompilerDirectives.TruffleBoundary
  public CsvReaderTruffleException(TruffleCharStream stream, Throwable cause, Node location) {
    super(createMessage(cause.getMessage(), null, stream), cause, location);
  }

  @CompilerDirectives.TruffleBoundary
  private static String createMessage(
      String message, TruffleCsvParser parser, TruffleCharStream stream) {
    StringBuilder sb = new StringBuilder();
    sb.append("failed to read CSV");
    if (parser != null) {
      sb.append(
          String.format(
              " (line %d column %d)", parser.currentTokenLine(), parser.currentTokenColumn()));
    }
    if (stream != null) {
      String position = stream.positionDescription();
      if (position != null) {
        sb.append(String.format(" (%s)", position));
      }
      sb.append(": ");
    }
    sb.append(message);
    return sb.toString();
  }
}
