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

package raw.runtime.truffle.runtime.exceptions.xml;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.Node;
import raw.runtime.truffle.ast.io.xml.parser.RawTruffleXmlParser;

public class XmlExpectedNothingException extends XmlParserRawTruffleException {

  @CompilerDirectives.TruffleBoundary
  public XmlExpectedNothingException(
      String content, RawTruffleXmlParser parser, Throwable cause, Node location) {
    super(String.format("unexpected value found: '%s'", content), parser, cause, location);
  }

  @CompilerDirectives.TruffleBoundary
  public XmlExpectedNothingException(String content, RawTruffleXmlParser parser, Node location) {
    super(String.format("unexpected value found: '%s'", content), parser, location);
  }
}
