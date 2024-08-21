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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.io.xml.parser;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.temporals.DateTimeFormatCache;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.RawTruffleInternalErrorException;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.xml.XmlParserRawTruffleException;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.xml.XmlReaderRawTruffleException;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.DateObject;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.DecimalObject;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.TimeObject;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.TimestampObject;
import com.rawlabs.compiler.snapi.truffle.runtime.utils.RawTruffleCharStream;
import com.rawlabs.compiler.snapi.truffle.runtime.utils.RawTruffleStringCharStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Vector;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.commons.text.StringEscapeUtils;

public class RawTruffleXmlParser {

  private int currentLine;
  private int currentColumn;
  private final XMLStreamReader xmlStreamReader;
  private final RawTruffleCharStream stream;
  private int currentToken;
  private String currentTag;
  private boolean currentTokenValid = false;
  private final DateTimeFormatter dateFormatter, timeFormatter, timestampFormatter;
  private final RawTruffleXmlParserSettings settings;

  @TruffleBoundary
  public int currentLine() {
    return xmlStreamReader.getLocation().getLineNumber();
  }

  @TruffleBoundary
  public int currentColumn() {
    return xmlStreamReader.getLocation().getColumnNumber();
  }

  @TruffleBoundary
  public RawTruffleXmlParser(RawTruffleCharStream stream, RawTruffleXmlParserSettings settings) {
    this.stream = stream;
    this.dateFormatter = DateTimeFormatCache.get(settings.dateFormat);
    this.timeFormatter = DateTimeFormatCache.get(settings.timeFormat);
    this.timestampFormatter = DateTimeFormatCache.get(settings.timestampFormat);
    this.settings = settings;
    try {
      xmlStreamReader =
          RawTruffleXmlParserFactory.singleton().createXMLStreamReader(stream.getReader());
    } catch (XMLStreamException e) {
      // TODO !!!!!!!!!!!!!!!!!!!!! Like in CSV/Json
      throw new XmlReaderRawTruffleException("Error creating XMLStreamReader", e, null);
    }
  }

  private final StringBuilder stringBuilder = new StringBuilder();

  public static RawTruffleXmlParser create(
      RawTruffleCharStream stream, RawTruffleXmlParserSettings settings) {
    return new RawTruffleXmlParser(stream, settings);
  }

  public RawTruffleXmlParser duplicateFor(String text) {
    RawTruffleCharStream subStream = new RawTruffleStringCharStream(text);
    return new RawTruffleXmlParser(subStream, settings);
  }

  @TruffleBoundary
  public void assertCurrentTokenIsStartTag() {
    assertCurrentToken(XMLStreamReader.START_ELEMENT);
  }

  @TruffleBoundary
  public void expectEndTag(String tag) {
    int currentToken = xmlStreamReader.getEventType();
    if (currentToken != XMLStreamReader.END_ELEMENT) {
      recordPosition();
      throw new XmlParserRawTruffleException(
          "expected "
              + eventToStr(XMLStreamReader.END_ELEMENT, tag)
              + " but got "
              + eventToStr(currentToken, currentTag),
          this,
          null);
    }
  }

  private String eventToStr(int token, String tagName) {
    switch (token) {
      case XMLStreamConstants.START_ELEMENT:
        if (tagName != null) return "start-element" + " <" + tagName + ">";
        else return "start-element";
      case XMLStreamConstants.END_ELEMENT:
        if (tagName != null) return "end-element" + " </" + tagName + ">";
        else return "end-element";
      case XMLStreamConstants.CHARACTERS:
        return "characters";
      case XMLStreamConstants.ENTITY_DECLARATION:
        return "entity-declaration";
      case XMLStreamConstants.ENTITY_REFERENCE:
        return "entity-reference";
      case XMLStreamConstants.PROCESSING_INSTRUCTION:
        return "precessing-instruction";
      case XMLStreamConstants.START_DOCUMENT:
        return "start-document";
      case XMLStreamConstants.END_DOCUMENT:
        return "end-document";
      case XMLStreamConstants.ATTRIBUTE:
        return "attribute";
      default:
        // TODO log something
        throw new RawTruffleInternalErrorException();
    }
  }

  @TruffleBoundary
  public boolean onEndTag() {
    try {
      assert (currentTokenValid || !xmlStreamReader.hasNext());
      return currentToken == XMLStreamReader.END_ELEMENT;
    } catch (IllegalStateException | XMLStreamException ex) {
      throw new XmlReaderRawTruffleException(ex, stream, null);
    }
  }

  @TruffleBoundary
  public boolean onStartTag() {
    assert (currentTokenValid);
    return currentToken == XMLStreamReader.START_ELEMENT;
  }

  @TruffleBoundary
  public String getCurrentName() {
    assert (currentTokenValid);
    return xmlStreamReader.getLocalName();
  }

  @TruffleBoundary
  public void skipTag() {
    int depth = 0;
    try {
      while (xmlStreamReader.hasNext()) {
        int token = xmlStreamReader.getEventType();
        if (token == XMLStreamConstants.START_ELEMENT) depth += 1;
        else if (token == XMLStreamConstants.END_ELEMENT) depth -= 1;
        nextToken();
        if (depth == 0) break;
      }
    } catch (XMLStreamException ex) {
      throw new XmlParserRawTruffleException(ex, this);
    }
  }

  @TruffleBoundary
  public void finishConsuming() {
    int depth = 1;
    try {
      while (xmlStreamReader.hasNext()) {
        int token = xmlStreamReader.getEventType();
        if (token == XMLStreamConstants.START_ELEMENT) depth += 1;
        else if (token == XMLStreamConstants.END_ELEMENT) depth -= 1;
        nextToken();
        if (depth == 0) break;
      }
    } catch (XMLStreamException ex) {
      throw new XmlReaderRawTruffleException(ex, stream, null);
    }
  }

  @TruffleBoundary
  public Vector<String> attributes() {
    Vector<String> names = new Vector<>();
    try {
      int n = xmlStreamReader.getAttributeCount();
      for (int i = 0; i < n; i++) {
        names.add(xmlStreamReader.getAttributeLocalName(i));
      }
      return names;
    } catch (IllegalStateException ex) {
      throw new XmlReaderRawTruffleException(ex, stream, null);
    }
  }

  @TruffleBoundary
  public int nextToken() {
    boolean skip = true;
    int token = -1;
    try {
      while (skip && xmlStreamReader.hasNext()) {
        token = xmlStreamReader.next();
        assert (token != XMLStreamConstants.ATTRIBUTE);
        skip =
            token == XMLStreamConstants.SPACE
                || token == XMLStreamConstants.PROCESSING_INSTRUCTION
                || token == XMLStreamConstants.COMMENT
                || token == XMLStreamConstants.END_DOCUMENT
                || token == XMLStreamConstants.START_DOCUMENT
                || token == XMLStreamConstants.DTD
                || (token == XMLStreamConstants.CHARACTERS && xmlStreamReader.getText().isBlank());
      }
      currentTokenValid = !skip;
      if (currentTokenValid) {
        currentToken = token;
        if (onStartTag() || onEndTag()) currentTag = xmlStreamReader.getLocalName();
      }
    } catch (XMLStreamException e) {
      // TODO more details?
      recordPosition();
      throw new XmlReaderRawTruffleException(e, this, stream, null);
    }
    return currentToken;
  }

  private void assertCurrentToken(int expectedToken) {
    int currentToken = xmlStreamReader.getEventType();
    if (currentToken != expectedToken) {
      // TODO !!!!!!!!!!!!!!!!!!!!!!!!!!
      recordPosition();
      throw new XmlReaderRawTruffleException(
          "Expected token " + expectedToken + " but found " + currentToken, stream, null);
    }
  }

  // String

  @TruffleBoundary
  public String getText() {
    stringBuilder.setLength(0);
    while (xmlStreamReader.getEventType() == XMLStreamConstants.ENTITY_REFERENCE
        || xmlStreamReader.getEventType() == XMLStreamConstants.CHARACTERS
        || xmlStreamReader.getEventType() == XMLStreamConstants.CDATA) {
      stringBuilder.append(xmlStreamReader.getText());
      nextToken();
    }
    return stringBuilder.toString();
  }

  @TruffleBoundary
  private void recordPosition() {
    currentLine = xmlStreamReader.getLocation().getLineNumber();
    currentColumn = xmlStreamReader.getLocation().getColumnNumber();
  }

  @TruffleBoundary
  public String getAsString() {
    String tag = xmlStreamReader.getLocalName();
    assertCurrentTokenIsStartTag();
    nextToken(); // skip the field name
    recordPosition();
    String text = getText();
    expectEndTag(tag); // don't skip the end tag but make sure it is there and it's the one we need
    return text;
  }

  @TruffleBoundary
  public String getStringAttribute(int index) {
    recordPosition();
    return xmlStreamReader.getAttributeValue(index);
  }

  @TruffleBoundary
  public byte byteFrom(String content) {
    try {
      return Byte.parseByte(content.strip());
    } catch (NumberFormatException e) {
      throw new XmlParserRawTruffleException(
          "cannot cast '" + content + "' to byte", this, e, null);
    }
  }

  @TruffleBoundary
  public short shortFrom(String content) {
    try {
      return Short.parseShort(content.strip());
    } catch (NumberFormatException e) {
      throw new XmlParserRawTruffleException(
          "cannot cast '" + content + "' to short", this, e, null);
    }
  }

  @TruffleBoundary
  public int intFrom(String content) {
    try {
      return Integer.parseInt(content.strip());
    } catch (NumberFormatException e) {
      throw new XmlParserRawTruffleException("cannot cast '" + content + "' to int", this, e, null);
    }
  }

  @TruffleBoundary
  public long longFrom(String content) {
    try {
      return Long.parseLong(content.strip());
    } catch (NumberFormatException e) {
      throw new XmlParserRawTruffleException(
          "cannot cast '" + content + "' to long", this, e, null);
    }
  }

  @TruffleBoundary
  public float floatFrom(String content) {
    try {
      return Float.parseFloat(content.strip());
    } catch (NumberFormatException e) {
      throw new XmlParserRawTruffleException(
          "cannot cast '" + content + "' to float", this, e, null);
    }
  }

  @TruffleBoundary
  public double doubleFrom(String content) {
    try {
      return Double.parseDouble(content.strip());
    } catch (NumberFormatException e) {
      throw new XmlParserRawTruffleException(
          "cannot cast '" + content + "' to double", this, e, null);
    }
  }

  @TruffleBoundary
  public DecimalObject decimalFrom(String content) {
    try {
      return new DecimalObject(new BigDecimal(content.strip()));
    } catch (NumberFormatException e) {
      throw new XmlParserRawTruffleException(
          "cannot cast '" + content + "' to decimal", this, e, null);
    }
  }

  @TruffleBoundary
  public boolean boolFrom(String content) {
    String normalized = content.strip().toLowerCase();
    if (Objects.equals(normalized, "true")) {
      return true;
    } else if (Objects.equals(normalized, "false")) {
      return false;
    } else {
      throw new XmlParserRawTruffleException(
          "cannot cast '" + content + "' to boolean", this, null);
    }
  }

  @TruffleBoundary
  public DateObject dateFrom(String content) {
    try {
      return new DateObject(LocalDate.parse(content.strip(), dateFormatter));
    } catch (DateTimeParseException ex) {
      throw new XmlParserRawTruffleException(
          String.format(
              "string '%s' does not match date template '%s'", content, settings.dateFormat),
          this,
          ex,
          null);
    }
  }

  @TruffleBoundary
  public TimeObject timeFrom(String content) {
    try {
      return new TimeObject(LocalTime.parse(content.strip(), timeFormatter));
    } catch (DateTimeParseException ex) {
      throw new XmlParserRawTruffleException(
          String.format(
              "string '%s' does not match time template '%s'", content, settings.timeFormat),
          this,
          ex,
          null);
    }
  }

  @TruffleBoundary
  public TimestampObject timestampFrom(String content) {
    try {
      return new TimestampObject(LocalDateTime.parse(content.strip(), timestampFormatter));
    } catch (DateTimeParseException ex) {
      throw new XmlParserRawTruffleException(
          String.format(
              "string '%s' does not match timestamp template '%s'",
              content, settings.timestampFormat),
          this,
          ex,
          null);
    }
  }

  @TruffleBoundary
  public void close() {
    try {
      xmlStreamReader.close();
    } catch (XMLStreamException e) {
      // TODO !!!!!!!!!!!!!!!!!!!!! Like in CSV/Json
      throw new XmlReaderRawTruffleException("Error closing XMLStreamReader", e, null);
    }
  }

  @TruffleBoundary
  public String elementAsString() {
    StringBuilder s = new StringBuilder();
    String currentTagName;
    int depth;
    if (xmlStreamReader.getEventType() == XMLStreamConstants.START_ELEMENT) {
      currentTagName = xmlStreamReader.getLocalName();
      int attributeCount = xmlStreamReader.getAttributeCount();
      s.append("<").append(currentTagName);
      for (int i = 0; i < attributeCount; i++) {
        s.append(" ")
            .append(xmlStreamReader.getAttributeLocalName(i))
            .append("=\"")
            .append(xmlStreamReader.getAttributeValue(i))
            .append("\"");
      }
      s.append(">");
      depth = 1;
    } else {
      depth = 0;
    }

    try {
      while (depth > 0 && xmlStreamReader.hasNext()) {
        int token = xmlStreamReader.next();
        switch (token) {
          case XMLStreamConstants.START_ELEMENT:
            currentTagName = xmlStreamReader.getLocalName();
            s.append("<").append(currentTagName);
            int attributeCount = xmlStreamReader.getAttributeCount();
            for (int i = 0; i < attributeCount; i++) {
              s.append(" ")
                  .append(xmlStreamReader.getAttributeLocalName(i))
                  .append("=\"")
                  .append(xmlStreamReader.getAttributeValue(i))
                  .append("\"");
            }
            s.append(">");
            depth += 1;
            break;
          case XMLStreamConstants.END_ELEMENT:
            s.append("</").append(xmlStreamReader.getLocalName()).append(">");
            depth -= 1;
            break;
          case XMLStreamConstants.ENTITY_REFERENCE:
          case XMLStreamConstants.CHARACTERS:
          case XMLStreamConstants.CDATA:
            s.append(StringEscapeUtils.escapeXml11(xmlStreamReader.getText()));
        }
      }
    } catch (XMLStreamException e) {
      throw new XmlParserRawTruffleException(e, this);
    }
    return s.toString();
  }
}
