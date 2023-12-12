package raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.sources;

import com.oracle.truffle.api.RootCallTarget;
import raw.runtime.truffle.ast.io.csv.reader.parser.RawTruffleCsvParser;
import raw.runtime.truffle.ast.io.csv.reader.parser.RawTruffleCsvParserSettings;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.utils.TruffleCharInputStream;
import raw.sources.api.SourceContext;

public class CsvReadComputeNext {
  private final LocationObject location;
  private final RootCallTarget rowParserCallTarget;
  private final SourceContext context;
  private final String encoding;
  private final RawTruffleCsvParserSettings settings;
  private TruffleCharInputStream stream;
  private RawTruffleCsvParser parser;

  public CsvReadComputeNext(
      LocationObject location,
      SourceContext context,
      RootCallTarget rowParserCallTarget,
      String encoding,
      RawTruffleCsvParserSettings settings) {
    this.context = context;
    this.location = location;
    this.rowParserCallTarget = rowParserCallTarget;
    this.encoding = encoding;
    this.settings = settings;
  }

  public void setStream(TruffleCharInputStream stream) {
    this.stream = stream;
  }

  public void setParser(RawTruffleCsvParser parser) {
    this.parser = parser;
  }

  public LocationObject getLocation() {
    return location;
  }

  public RootCallTarget getRowParserCallTarget() {
    return rowParserCallTarget;
  }

  public SourceContext getContext() {
    return context;
  }

  public String getEncoding() {
    return encoding;
  }

  public RawTruffleCsvParserSettings getSettings() {
    return settings;
  }

  public TruffleCharInputStream getStream() {
    return stream;
  }

  public RawTruffleCsvParser getParser() {
    return parser;
  }
}
