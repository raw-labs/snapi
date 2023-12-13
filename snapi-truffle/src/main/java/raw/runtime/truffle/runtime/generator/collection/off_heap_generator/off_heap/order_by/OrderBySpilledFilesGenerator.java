package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.StopIterationException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.input_buffer.OrderByInputBuffer;
import raw.runtime.truffle.runtime.list.StringList;

import java.util.ArrayList;
import java.util.Objects;

@ExportLibrary(InteropLibrary.class)
public class OrderBySpilledFilesGenerator implements TruffleObject {
  private final OffHeapGroupByKeys offHeapGroupByKeys;

  private ArrayList<OrderByInputBuffer>
      inputBuffers; // list of Kryo buffers that contain the spilled data.
  private OrderByInputBuffer currentKryoBuffer; // the current buffer being read.

  public OrderBySpilledFilesGenerator(OffHeapGroupByKeys offHeapGroupByKeys) {
    this.offHeapGroupByKeys = offHeapGroupByKeys;
  }

  public void setInputBuffers(ArrayList<OrderByInputBuffer> inputBuffers) {
    this.inputBuffers = inputBuffers;
  }

  public void setCurrentKryoBuffer(OrderByInputBuffer currentKryoBuffer) {
    this.currentKryoBuffer = currentKryoBuffer;
  }

  public OffHeapGroupByKeys getOffHeapGroupByKeys() {
    return offHeapGroupByKeys;
  }

  public ArrayList<OrderByInputBuffer> getInputBuffers() {
    return inputBuffers;
  }

  public OrderByInputBuffer getCurrentKryoBuffer() {
    return currentKryoBuffer;
  }

  public void addInputBuffer(OrderByInputBuffer inputBuffer) {
    this.inputBuffers.add(inputBuffer);
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
