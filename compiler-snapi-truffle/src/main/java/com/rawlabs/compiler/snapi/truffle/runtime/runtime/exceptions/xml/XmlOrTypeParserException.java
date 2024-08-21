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
import java.util.List;

public class XmlOrTypeParserException extends XmlParserRawTruffleException {

  @CompilerDirectives.TruffleBoundary
  public XmlOrTypeParserException(
      List<String> parseErrors, RawTruffleXmlParser parser, Throwable cause, Node location) {
    super(createMessage(parseErrors), parser, cause, location);
  }

  @CompilerDirectives.TruffleBoundary
  public XmlOrTypeParserException(
      List<String> parseErrors, RawTruffleXmlParser parser, Node location) {
    super(createMessage(parseErrors), parser, location);
  }

  private static String createMessage(List<String> messages) {
    StringBuilder sb = new StringBuilder();
    sb.append("failed to parse or type:\n");

    for (int i = 0; i < messages.size(); i++) {
      sb.append(String.format("\t %d: %s", i, messages.get(i).replaceAll("\n", "\n\t")));
    }

    return sb.toString();
  }
}
