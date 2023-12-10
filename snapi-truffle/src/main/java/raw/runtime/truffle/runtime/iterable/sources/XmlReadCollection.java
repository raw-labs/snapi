package raw.runtime.truffle.runtime.iterable.sources;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.ast.io.xml.parser.RawTruffleXmlParserSettings;
import raw.runtime.truffle.runtime.generator.collection.AbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.AbstractGeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.XmlReadComputeNext;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.sources.api.SourceContext;

@ExportLibrary(InteropLibrary.class)
public class XmlReadCollection implements TruffleObject {
  private final LocationObject locationObject;
  private final RootCallTarget parseNextRootCallTarget;
  private RawTruffleXmlParserSettings settings;
  private final SourceContext context;

  private final String encoding;

  public XmlReadCollection(
      LocationObject locationObject,
      String encoding,
      SourceContext context,
      RootCallTarget parseNextRootCallTarget,
      RawTruffleXmlParserSettings settings) {
    this.locationObject = locationObject;
    this.parseNextRootCallTarget = parseNextRootCallTarget;
    this.settings = settings;
    this.context = context;
    this.encoding = encoding;
  }

  public AbstractGenerator getGenerator() {
    return new AbstractGenerator(
        new XmlReadComputeNext(
            locationObject, encoding, context, parseNextRootCallTarget, settings));
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
