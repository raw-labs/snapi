package raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.sources;

import com.oracle.truffle.api.RootCallTarget;
import raw.runtime.truffle.ast.io.csv.reader.parser.RawTruffleCsvParser;
import raw.runtime.truffle.ast.io.csv.reader.parser.RawTruffleCsvParserSettings;
import raw.runtime.truffle.utils.RawTruffleCharStream;
import raw.runtime.truffle.utils.RawTruffleStringCharStream;

public class CsvReadFromStringComputeNext {
  private final RawTruffleCharStream stream;
  private final RootCallTarget rowParserCallTarget;
  private final RawTruffleCsvParserSettings settings;
  private RawTruffleCsvParser parser;

  public CsvReadFromStringComputeNext(
      String str, RootCallTarget rowParserCallTarget, RawTruffleCsvParserSettings settings) {
    this.rowParserCallTarget = rowParserCallTarget;
    this.settings = settings;
    this.stream = new RawTruffleStringCharStream(str);
  }

  public void setParser(RawTruffleCsvParser parser) {
    this.parser = parser;
  }

  public RawTruffleCharStream getStream() {
    return stream;
  }

  public RootCallTarget getRowParserCallTarget() {
    return rowParserCallTarget;
  }

  public RawTruffleCsvParserSettings getSettings() {
    return settings;
  }

  public RawTruffleCsvParser getParser() {
    return parser;
  }
}
