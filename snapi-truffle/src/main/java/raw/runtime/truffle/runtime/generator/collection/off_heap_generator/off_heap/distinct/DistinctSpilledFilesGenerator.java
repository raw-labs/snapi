package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.distinct;

import com.esotericsoftware.kryo.io.Input;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.StopIterationException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.list.StringList;

import java.util.ArrayList;
import java.util.Objects;

@ExportLibrary(InteropLibrary.class)
public class DistinctSpilledFilesGenerator implements TruffleObject {
  private final OffHeapDistinct offHeapDistinct;
  private ArrayList<Input> kryoBuffers; // list of Kryo buffers that contain the spilled data.
  private ArrayList<Object> headKeys; // list of keys that are currently at the head of each buffer.

  public DistinctSpilledFilesGenerator(OffHeapDistinct offHeapDistinct) {
    this.offHeapDistinct = offHeapDistinct;
  }

  public ArrayList<Input> getKryoBuffers() {
    return kryoBuffers;
  }

  public void setKryoBuffers(ArrayList<Input> kryoBuffers) {
    this.kryoBuffers = kryoBuffers;
  }

  public ArrayList<Object> getHeadKeys() {
    return headKeys;
  }

  public void setHeadKeys(ArrayList<Object> headKeys) {
    this.headKeys = headKeys;
  }

  public OffHeapDistinct getOffHeapDistinct() {
    return offHeapDistinct;
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
