package raw.runtime.truffle.runtime.iterable.sources;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.ExpressionComputeNext;
import raw.runtime.truffle.runtime.generator.collection_old.CollectionAbstractGenerator;

@ExportLibrary(InteropLibrary.class)
public class ExpressionCollection implements TruffleObject {

  private final Object[] values;

  public ExpressionCollection(Object[] values) {
    this.values = values;
  }

  public CollectionAbstractGenerator getGenerator() {
    return new CollectionAbstractGenerator(new ExpressionComputeNext(values));
  }

  // InteropLibrary: Iterable
  @ExportMessage
  boolean hasIterator() {
    return true;
  }

  @ExportMessage
  Object getIterator() {
    return getGenerator();
  }
}
