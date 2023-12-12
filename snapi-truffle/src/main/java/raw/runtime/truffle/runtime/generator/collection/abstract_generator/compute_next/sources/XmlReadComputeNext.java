package raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.sources;

import com.oracle.truffle.api.RootCallTarget;
import raw.runtime.truffle.ast.io.xml.parser.RawTruffleXmlParser;
import raw.runtime.truffle.ast.io.xml.parser.RawTruffleXmlParserSettings;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.utils.TruffleCharInputStream;
import raw.sources.api.SourceContext;

public class XmlReadComputeNext {
  private final LocationObject locationObject;
  protected final RootCallTarget parseNextRootCallTarget;
  private final SourceContext context;
  private final String encoding;
  private RawTruffleXmlParser parser;
  private final RawTruffleXmlParserSettings settings;

  private TruffleCharInputStream stream;

  public XmlReadComputeNext(
      LocationObject locationObject,
      String encoding,
      SourceContext context,
      RootCallTarget parseNextRootCallTarget,
      RawTruffleXmlParserSettings settings) {
    this.encoding = encoding;
    this.context = context;
    this.settings = settings;
    this.locationObject = locationObject;
    this.parseNextRootCallTarget = parseNextRootCallTarget;
  }

  public void setParser(RawTruffleXmlParser parser) {
    this.parser = parser;
  }

  public void setStream(TruffleCharInputStream stream) {
    this.stream = stream;
  }

  public LocationObject getLocationObject() {
    return locationObject;
  }

  public RootCallTarget getParseNextRootCallTarget() {
    return parseNextRootCallTarget;
  }

  public SourceContext getContext() {
    return context;
  }

  public String getEncoding() {
    return encoding;
  }

  public RawTruffleXmlParser getParser() {
    return parser;
  }

  public RawTruffleXmlParserSettings getSettings() {
    return settings;
  }

  public TruffleCharInputStream getStream() {
    return stream;
  }
}
