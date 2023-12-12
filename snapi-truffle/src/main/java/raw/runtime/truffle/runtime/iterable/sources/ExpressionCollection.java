package raw.runtime.truffle.runtime.iterable.sources;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.AbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.sources.ExpressionComputeNext;

@ExportLibrary(InteropLibrary.class)
public class ExpressionCollection implements TruffleObject {

  private final Object[] values;

  public ExpressionCollection(Object[] values) {
    this.values = values;
  }

  public AbstractGenerator getGenerator() {
    return new AbstractGenerator(new ExpressionComputeNext(values));
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
