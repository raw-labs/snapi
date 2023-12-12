package raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.sources;

import com.fasterxml.jackson.core.JsonParser;
import com.oracle.truffle.api.RootCallTarget;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.utils.TruffleCharInputStream;
import raw.runtime.truffle.utils.TruffleInputStream;
import raw.sources.api.SourceContext;

public class JsonReadComputeNext {
  private final LocationObject locationObject;
  private final RootCallTarget parseNextCallTarget;
  private final SourceContext context;
  private final String encoding;

  private TruffleCharInputStream stream;

  private JsonParser parser;

  public JsonReadComputeNext(
      LocationObject locationObject,
      String encoding,
      SourceContext context,
      RootCallTarget parseNextRootCallTarget) {
    this.encoding = encoding;
    this.context = context;
    this.locationObject = locationObject;
    this.parseNextCallTarget = parseNextRootCallTarget;
  }

  public void setStream(TruffleCharInputStream stream) {
    this.stream = stream;
  }

  public void setParser(JsonParser parser) {
    this.parser = parser;
  }

  public RootCallTarget getParseNextCallTarget() {
    return parseNextCallTarget;
  }

  public JsonParser getParser() {
    return parser;
  }

  public TruffleCharInputStream getStream() {
    return stream;
  }

  public LocationObject getLocationObject() {
    return locationObject;
  }

  public SourceContext getContext() {
    return context;
  }

  public String getEncoding() {
    return encoding;
  }
}
