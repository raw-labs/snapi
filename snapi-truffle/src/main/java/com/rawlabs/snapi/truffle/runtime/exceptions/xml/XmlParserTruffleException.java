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

package com.rawlabs.snapi.truffle.runtime.exceptions.xml;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.Node;
import com.rawlabs.snapi.truffle.ast.io.xml.parser.TruffleXmlParser;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;

public class XmlParserTruffleException extends TruffleRuntimeException {

  @CompilerDirectives.TruffleBoundary
  public XmlParserTruffleException(
      String message, TruffleXmlParser parser, Throwable cause, Node location) {
    super(
        String.format(
            "failed to parse XML (line %d column %d): %s",
            parser.currentLine(), parser.currentColumn(), message),
        cause,
        location);
  }

  @CompilerDirectives.TruffleBoundary
  public XmlParserTruffleException(String message, TruffleXmlParser parser, Node location) {
    super(
        String.format(
            "failed to parse XML (line %d column %d): %s",
            parser.currentLine(), parser.currentColumn(), message),
        location);
  }

  @CompilerDirectives.TruffleBoundary
  public XmlParserTruffleException(Throwable cause, TruffleXmlParser parser) {
    // TODO
    super(cause.getMessage(), cause, null);
  }

  @CompilerDirectives.TruffleBoundary
  public XmlParserTruffleException(int line, int column, Throwable cause, Node location) {
    super(
        String.format(
            "failed to parse XML (line %d column %d): %s", line, column, cause.getMessage()),
        cause,
        location);
  }

  @CompilerDirectives.TruffleBoundary
  public XmlParserTruffleException(
      String message, int line, int column, Throwable cause, Node location) {
    super(
        String.format("failed to parse XML (line %d column %d): %s", line, column, message),
        cause,
        location);
  }
}
