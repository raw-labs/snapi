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

package raw.runtime.truffle.ast.io.xml;

import com.ctc.wstx.api.WstxInputProperties;
import com.ctc.wstx.stax.WstxInputFactory;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.codehaus.stax2.XMLInputFactory2;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.xml.XmlReaderRawTruffleException;
import raw.runtime.truffle.utils.RawTruffleCharStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class XmlParserNodes {

  @NodeInfo(shortName = "Parser.Initialize")
  @GenerateUncached
  public abstract static class InitXmlParserNode extends Node {

    public abstract XMLStreamReader execute(Object stream);

    private WstxInputFactory getFactory() {
      WstxInputFactory factory = new WstxInputFactory();
      // Enable multiple roots
      factory.setProperty(
          WstxInputProperties.P_INPUT_PARSING_MODE, WstxInputProperties.PARSING_MODE_DOCUMENTS);
      factory.setProperty(XMLInputFactory2.P_LAZY_PARSING, true);
      factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
      return factory;
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    XMLStreamReader initParserFromStream(RawTruffleCharStream stream) {
      WstxInputFactory factory = getFactory();
      try {
        return factory.createXMLStreamReader(stream.getReader());
      } catch (XMLStreamException e) {
        throw new XmlReaderRawTruffleException(e.getMessage(), e, this);
      }
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    XMLStreamReader initParserFromString(String str) {
      WstxInputFactory factory = getFactory();
      try {
        return factory.createXMLStreamReader(
            new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)));
      } catch (XMLStreamException e) {
        throw new XmlReaderRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "Parser.Close")
  @GenerateUncached
  public abstract static class CloseXmlParserNode extends Node {

    public abstract void execute(XMLStreamReader xmlStreamReader);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void closeParserSilently(XMLStreamReader xmlStreamReader) {
      if (xmlStreamReader != null) {
        try {
          xmlStreamReader.close();
        } catch (XMLStreamException e) {
          throw new RawTruffleRuntimeException(e.getMessage(), this);
        }
      }
    }
  }

  @NodeInfo(shortName = "Parser.SkipOne")
  @GenerateUncached
  public abstract static class SkipOneXmlParserNode extends Node {

    public abstract int execute(XMLStreamReader xmlStreamReader);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    int skipOne(XMLStreamReader xmlStreamReader) {
      boolean skip = true;
      int token = -1;
      try {
        while (skip && xmlStreamReader.hasNext()) {
          token = xmlStreamReader.next();
          assert (token != XMLStreamConstants.ATTRIBUTE);

          boolean toSkip =
              token == XMLStreamConstants.SPACE
                  || token == XMLStreamConstants.PROCESSING_INSTRUCTION
                  || token == XMLStreamConstants.COMMENT
                  || token == XMLStreamConstants.END_DOCUMENT
                  || token == XMLStreamConstants.START_DOCUMENT
                  || token == XMLStreamConstants.DTD;
          skip =
              toSkip
                  || (token == XMLStreamConstants.CHARACTERS
                      && xmlStreamReader.getText().isBlank());
        }
        return token;
      } catch (XMLStreamException e) {
        throw new XmlReaderRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "Parser.SkipChild")
  @GenerateUncached
  public abstract static class SkipChildXmlParserNode extends Node {

    public abstract void execute(XMLStreamReader xmlStreamReader);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void skipChild(XMLStreamReader xmlStreamReader) {
      int depth = 1;
      try {
        while (depth > 0 && xmlStreamReader.hasNext()) {
          int token = xmlStreamReader.next();
          if (token == XMLStreamConstants.START_ELEMENT) depth += 1;
          else if (token == XMLStreamConstants.END_ELEMENT) depth -= 1;
        }
        assert (xmlStreamReader.hasNext());
      } catch (XMLStreamException e) {
        throw new XmlReaderRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "Parser.SkipObj")
  @GenerateUncached
  public abstract static class SkipObjXmlParserNode extends Node {

    public abstract void execute(XMLStreamReader xmlStreamReader);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void skipObj(
        XMLStreamReader xmlStreamReader,
        @Cached("create()") SkipOneXmlParserNode skipOne,
        @Cached("create()") SkipOneXmlParserNode skipObj) {
      try {
        while (xmlStreamReader.hasNext()) {
          int ev = xmlStreamReader.getEventType();
          switch (ev) {
            case XMLStreamConstants.START_ELEMENT:
              skipOne.execute(xmlStreamReader);
              skipObj.execute(xmlStreamReader);
              break;
            case XMLStreamConstants.CHARACTERS
                | XMLStreamConstants.ENTITY_REFERENCE
                | XMLStreamConstants.CDATA:
              break;
            case XMLStreamConstants.END_ELEMENT:
              return;
          }
        }
      } catch (XMLStreamException e) {
        throw new XmlReaderRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "Parser.GetTextTag")
  @GenerateUncached
  public abstract static class GetTextTagXmlParserNode extends Node {

    public abstract void execute(XMLStreamReader xmlStreamReader);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void skipObj(
        XMLStreamReader xmlStreamReader,
        @Cached("create()") SkipOneXmlParserNode skipOne,
        @Cached("create()") SkipOneXmlParserNode skipObj) {
      try {
        while (xmlStreamReader.hasNext()) {
          int ev = xmlStreamReader.getEventType();
          switch (ev) {
            case XMLStreamConstants.START_ELEMENT:
              skipOne.execute(xmlStreamReader);
              skipObj.execute(xmlStreamReader);
              break;
            case XMLStreamConstants.CHARACTERS
                | XMLStreamConstants.ENTITY_REFERENCE
                | XMLStreamConstants.CDATA:
              break;
            case XMLStreamConstants.END_ELEMENT:
              return;
          }
        }
      } catch (XMLStreamException e) {
        throw new XmlReaderRawTruffleException(e.getMessage(), e, this);
      }
    }
  }
}
