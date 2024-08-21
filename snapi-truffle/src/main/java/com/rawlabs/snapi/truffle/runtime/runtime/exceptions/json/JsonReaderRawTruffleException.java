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

package com.rawlabs.snapi.truffle.runtime.runtime.exceptions.json;

import com.fasterxml.jackson.core.JsonParser;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.nodes.Node;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.RawTruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.utils.TruffleCharInputStream;

public class JsonReaderRawTruffleException extends RawTruffleRuntimeException {
  @TruffleBoundary
  public JsonReaderRawTruffleException(String message, Throwable cause, Node location) {
    super(message, cause, location);
  }

  @TruffleBoundary
  public JsonReaderRawTruffleException(Throwable cause, Node location) {
    super("failed to read JSON", cause, location);
  }

  @TruffleBoundary
  public JsonReaderRawTruffleException(
      JsonParser parser, TruffleCharInputStream stream, Throwable cause, Node location) {
    super(createMessage(cause.getCause().getMessage(), parser, stream), cause, location);
  }

  @TruffleBoundary
  public JsonReaderRawTruffleException(
      String message,
      JsonParser parser,
      TruffleCharInputStream stream,
      Throwable cause,
      Node location) {
    super(createMessage(message, parser, stream), cause, location);
  }

  private static String createMessage(
      String message, JsonParser parser, TruffleCharInputStream stream) {
    StringBuilder sb = new StringBuilder();
    sb.append("failed to read JSON");
    if (parser != null) {
      sb.append(
          String.format(
              " (line %d column %d)",
              parser.getCurrentLocation().getLineNr(), parser.getCurrentLocation().getColumnNr()));
    }
    if (stream != null) {
      sb.append(String.format(" (%s)", stream.positionDescription()));
    }
    if (!message.isEmpty()) {
      sb.append(": ");
      sb.append(message);
    }
    return sb.toString();
  }
}
