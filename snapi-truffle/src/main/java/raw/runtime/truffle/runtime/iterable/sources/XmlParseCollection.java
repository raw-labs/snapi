package raw.runtime.truffle.runtime.iterable.sources;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.ast.io.xml.parser.RawTruffleXmlParserSettings;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.AbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.sources.XmlParseComputeNext;
import raw.sources.api.SourceContext;

@ExportLibrary(InteropLibrary.class)
public class XmlParseCollection implements TruffleObject {
  private final String text;
  private final RootCallTarget parseNextRootCallTarget;
  private final RawTruffleXmlParserSettings settings;

  public XmlParseCollection(
      String text,
      SourceContext context,
      RootCallTarget parseNextRootCallTarget,
      RawTruffleXmlParserSettings settings) {
    this.text = text;
    this.parseNextRootCallTarget = parseNextRootCallTarget;
    this.settings = settings;
  }

  public AbstractGenerator getGenerator() {
    return new AbstractGenerator(new XmlParseComputeNext(text, parseNextRootCallTarget, settings));
  }

  // InteropLibrary: Iterable
  @ExportMessage
  boolean hasIterator() {
    return true;
  }

  @ExportMessage
  Object getIterator(@Cached GeneratorNodes.GeneratorInitNode initNode) {
    Object generator = getGenerator();
    initNode.execute(generator);
    return generator;
  }
}
