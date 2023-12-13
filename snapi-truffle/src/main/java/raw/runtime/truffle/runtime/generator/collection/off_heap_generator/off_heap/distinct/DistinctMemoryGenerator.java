package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.distinct;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.StopIterationException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.list.StringList;

import java.util.Iterator;
import java.util.Objects;

@ExportLibrary(InteropLibrary.class)
public class DistinctMemoryGenerator implements TruffleObject {
  private final Iterator<Object> items;

  public DistinctMemoryGenerator(OffHeapDistinct offHeapDistinct) {
    this.items = offHeapDistinct.getIndex().iterator();
  }

  public Iterator<Object> getItems() {
    return items;
  }

  // InteropLibrary: Iterator

  @ExportMessage
  final boolean isIterator() {
    return true;
  }

  @ExportMessage
  final boolean hasIteratorNextElement(@CachedLibrary("this") GeneratorLibrary generatorLibrary)
      throws UnsupportedMessageException {
    return generatorLibrary.hasNext(this);
  }

  @ExportMessage
  final Object getIteratorNextElement(@CachedLibrary("this") GeneratorLibrary generatorLibrary)
      throws UnsupportedMessageException, StopIterationException {
    return generatorLibrary.next(this);
  }

  @ExportMessage
  final boolean hasMembers() {
    return true;
  }

  @ExportMessage
  final Object getMembers(boolean includeInternal) {
    return new StringList(new String[] {"close"});
  }

  @ExportMessage
  final boolean isMemberInvocable(String member) {
    return Objects.equals(member, "close");
  }

  @ExportMessage
  final Object invokeMember(
      String member, Object[] args, @CachedLibrary("this") GeneratorLibrary generatorLibrary) {
    assert (Objects.equals(member, "close"));
    generatorLibrary.close(this);
    return 0;
  }
}
