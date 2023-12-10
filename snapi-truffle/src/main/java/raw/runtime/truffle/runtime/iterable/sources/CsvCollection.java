package raw.runtime.truffle.runtime.iterable.sources;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.ast.io.csv.reader.parser.RawTruffleCsvParserSettings;
import raw.runtime.truffle.runtime.generator.collection.AbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.AbstractGeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.CsvReadComputeNext;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.sources.api.SourceContext;

@ExportLibrary(InteropLibrary.class)
public class CsvCollection implements TruffleObject {
  private final LocationObject location;
  private final RootCallTarget rowParserCallTarget;

  private final SourceContext context;

  private final String encoding;
  private final RawTruffleCsvParserSettings settings;

  public CsvCollection(
      LocationObject location,
      SourceContext context,
      RootCallTarget rowParserCallTarget,
      String encoding,
      RawTruffleCsvParserSettings settings) {
    this.location = location;
    this.rowParserCallTarget = rowParserCallTarget;
    this.context = context;
    this.encoding = encoding;
    this.settings = settings;
  }

  public AbstractGenerator getGenerator() {
    return new AbstractGenerator(
        new CsvReadComputeNext(location, context, rowParserCallTarget, encoding, settings));
  }

  // InteropLibrary: Iterable
  @ExportMessage
  boolean hasIterator() {
    return true;
  }

  @ExportMessage
  Object getIterator(@Cached AbstractGeneratorNodes.AbstractGeneratorInitNode initNode) {
    Object generator = getGenerator();
    initNode.execute(generator);
    return generator;
  }
}
