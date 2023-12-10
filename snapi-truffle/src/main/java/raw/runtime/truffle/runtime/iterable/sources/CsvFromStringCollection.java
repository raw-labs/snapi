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
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.CsvReadFromStringComputeNext;

@ExportLibrary(InteropLibrary.class)
public class CsvFromStringCollection implements TruffleObject {
  private final String str;
  private final RootCallTarget rowParserCallTarget;

  private final RawTruffleCsvParserSettings settings;

  public CsvFromStringCollection(
      String str, RootCallTarget rowParserCallTarget, RawTruffleCsvParserSettings settings) {
    this.str = str;
    this.rowParserCallTarget = rowParserCallTarget;
    this.settings = settings;
  }

  public AbstractGenerator getGenerator() {
    return new AbstractGenerator(
        new CsvReadFromStringComputeNext(str, rowParserCallTarget, settings));
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
