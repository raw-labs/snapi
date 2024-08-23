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

package com.rawlabs.snapi.truffle.runtime.runtime.exceptions.xml;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.Node;
import com.rawlabs.snapi.truffle.runtime.ast.io.xml.parser.TruffleXmlParser;

public class XmlExpectedNothingException extends XmlParserTruffleException {

  @CompilerDirectives.TruffleBoundary
  public XmlExpectedNothingException(
      String content, TruffleXmlParser parser, Throwable cause, Node location) {
    super(String.format("unexpected value found: '%s'", content), parser, cause, location);
  }

  @CompilerDirectives.TruffleBoundary
  public XmlExpectedNothingException(String content, TruffleXmlParser parser, Node location) {
    super(String.format("unexpected value found: '%s'", content), parser, location);
  }
}
