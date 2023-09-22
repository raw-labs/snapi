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

import com.oracle.truffle.api.nodes.Node;
import raw.runtime.truffle.ast.io.xml.parser.RawTruffleXmlParser;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.utils.RawTruffleCharStream;

public class XmlReaderRawTruffleException extends RawTruffleRuntimeException {

  public XmlReaderRawTruffleException(String message, Throwable cause, Node location) {
    super(message, cause, location);
  }

  public XmlReaderRawTruffleException(String message, RawTruffleCharStream stream, Node location) {
    super(createMessage(message, stream, null), null, location);
  }

  public XmlReaderRawTruffleException(Throwable cause, RawTruffleCharStream stream, Node location) {
    super(createMessage(cause.getMessage(), stream, null), cause, location);
  }

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
