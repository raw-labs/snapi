package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by;

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

// A simple in-memory generator over the map. Internally iterates through the
// key set and returns items from the values list, one by one.
@ExportLibrary(InteropLibrary.class)
public class OrderByMemoryGenerator implements TruffleObject {
  private final OffHeapGroupByKeys offHeapGroupByKeys;

  private final Iterator<Object[]> keysIterator; // the iterator from the in-memory map keys.
  private Iterator<Object> values = null; // the current iterator over the grouped rows

  public OrderByMemoryGenerator(OffHeapGroupByKeys offHeapGroupByKeys) {
    this.offHeapGroupByKeys = offHeapGroupByKeys;
    this.keysIterator = offHeapGroupByKeys.getMemMap().keySet().iterator();
  }

  public void setValues(Iterator<Object> values) {
    this.values = values;
  }

  public OffHeapGroupByKeys getOffHeapGroupByKeys() {
    return offHeapGroupByKeys;
  }

  public Iterator<Object[]> getKeysIterator() {
    return keysIterator;
  }

  public Iterator<Object> getValues() {
    return values;
  }

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
