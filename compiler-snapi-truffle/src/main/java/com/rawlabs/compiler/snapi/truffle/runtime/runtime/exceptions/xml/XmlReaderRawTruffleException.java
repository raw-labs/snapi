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

package com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.xml;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.Node;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.io.xml.parser.RawTruffleXmlParser;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.RawTruffleRuntimeException;
import com.rawlabs.compiler.snapi.truffle.runtime.utils.RawTruffleCharStream;

public class XmlReaderRawTruffleException extends RawTruffleRuntimeException {

  @CompilerDirectives.TruffleBoundary
  public XmlReaderRawTruffleException(String message, Throwable cause, Node location) {
    super(message, cause, location);
  }

  @CompilerDirectives.TruffleBoundary
  public XmlReaderRawTruffleException(
      String message, RawTruffleCharStream stream, Throwable cause, Node location) {
    super(createMessage(message, stream, null), cause, location);
  }

  @CompilerDirectives.TruffleBoundary
  public XmlReaderRawTruffleException(String message, RawTruffleCharStream stream, Node location) {
    super(createMessage(message, stream, null), location);
  }

  @CompilerDirectives.TruffleBoundary
  public XmlReaderRawTruffleException(Throwable cause, RawTruffleCharStream stream, Node location) {
    super(createMessage(cause.getMessage(), stream, null), cause, location);
  }

  @CompilerDirectives.TruffleBoundary
  public XmlReaderRawTruffleException(
      Throwable cause, RawTruffleXmlParser parser, RawTruffleCharStream stream, Node location) {
    super(createMessage(cause.getMessage(), stream, parser), cause, location);
  }

  private static String createMessage(
      String customMessage, RawTruffleCharStream stream, RawTruffleXmlParser parser) {
    StringBuilder builder = new StringBuilder("failed to read XML");
    if (parser != null) {
      builder.append(
          String.format(" (line %d column %d)", parser.currentLine(), parser.currentColumn()));
    }
    String position = stream.positionDescription();
    if (position != null) {
      builder.append(String.format(" (%s)", position));
    }
    builder.append(": ").append(customMessage);
    return builder.toString();
  }
}
