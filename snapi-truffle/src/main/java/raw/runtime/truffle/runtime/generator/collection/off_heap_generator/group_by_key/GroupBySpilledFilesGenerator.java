package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.group_by_key;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.StopIterationException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.util.ArrayList;
import java.util.Objects;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.group_by_key.OffHeapGroupByKey;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.input_buffer.InputBuffer;
import raw.runtime.truffle.runtime.list.StringList;

@ExportLibrary(InteropLibrary.class)
public class GroupBySpilledFilesGenerator implements TruffleObject {

  private final OffHeapGroupByKey offHeapGroupByKey;
  private ArrayList<InputBuffer>
      inputBuffers; // list of Kryo buffers that contain the spilled data.

  public GroupBySpilledFilesGenerator(OffHeapGroupByKey offHeapGroupByKey) {
    this.offHeapGroupByKey = offHeapGroupByKey;
  }

  public void setInputBuffers(ArrayList<InputBuffer> inputBuffers) {
    this.inputBuffers = inputBuffers;
  }

  public void addInputBuffer(InputBuffer inputBuffer) {
    this.inputBuffers.add(inputBuffer);
  }

  public ArrayList<InputBuffer> getInputBuffers() {
    return inputBuffers;
  }

  public OffHeapGroupByKey getOffHeapGroupByKey() {
    return offHeapGroupByKey;
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
