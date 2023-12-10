package raw.runtime.truffle.runtime.generator.collection.compute_next.sources;

import com.oracle.truffle.api.RootCallTarget;
import raw.runtime.truffle.ast.io.xml.parser.RawTruffleXmlParser;
import raw.runtime.truffle.ast.io.xml.parser.RawTruffleXmlParserSettings;
import raw.runtime.truffle.utils.RawTruffleStringCharStream;
import raw.sources.api.SourceContext;

public class XmlParseComputeNext {
  private final String text;
  protected final RootCallTarget parseNextRootCallTarget;
  private final RawTruffleXmlParserSettings settings;
  private RawTruffleXmlParser parser;

  private RawTruffleStringCharStream stream;

  public XmlParseComputeNext(
      String text, RootCallTarget parseNextRootCallTarget, RawTruffleXmlParserSettings settings) {
    this.text = text;
    this.settings = settings;
    this.parseNextRootCallTarget = parseNextRootCallTarget;
  }

  public void setParser(RawTruffleXmlParser parser) {
    this.parser = parser;
  }

  public void setStream(RawTruffleStringCharStream stream) {
    this.stream = stream;
  }

  public RootCallTarget getParseNextRootCallTarget() {
    return parseNextRootCallTarget;
  }

  public RawTruffleXmlParser getParser() {
    return parser;
  }

  public RawTruffleStringCharStream getStream() {
    return stream;
  }

  public String getText() {
    return text;
  }

  public RawTruffleXmlParserSettings getSettings() {
    return settings;
  }
}
