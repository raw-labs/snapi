package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.StopIterationException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.util.ArrayList;
import java.util.Objects;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.input_buffer.GroupByInputBuffer;
import raw.runtime.truffle.runtime.list.StringList;

@ExportLibrary(InteropLibrary.class)
public class GroupBySpilledFilesGenerator implements TruffleObject {

  private final OffHeapGroupByKey offHeapGroupByKey;
  private ArrayList<GroupByInputBuffer>
      inputBuffers; // list of Kryo buffers that contain the spilled data.

  public GroupBySpilledFilesGenerator(OffHeapGroupByKey offHeapGroupByKey) {
    this.offHeapGroupByKey = offHeapGroupByKey;
  }

  public void setInputBuffers(ArrayList<GroupByInputBuffer> inputBuffers) {
    this.inputBuffers = inputBuffers;
  }

  public void addInputBuffer(GroupByInputBuffer inputBuffer) {
    this.inputBuffers.add(inputBuffer);
  }

  public ArrayList<GroupByInputBuffer> getInputBuffers() {
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
  final boolean hasIteratorNextElement(@Cached GeneratorNodes.GeneratorHasNextNode hasNextNode)
          throws UnsupportedMessageException {
    return hasNextNode.execute(this);
  }

  @ExportMessage
  final Object getIteratorNextElement(@Cached GeneratorNodes.GeneratorNextNode nextNode)
          throws UnsupportedMessageException, StopIterationException {
    return nextNode.execute(this);
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
          String member, Object[] args, @Cached GeneratorNodes.GeneratorCloseNode closeNode) {
    assert (Objects.equals(member, "close"));
    closeNode.execute(this);
    return 0;
  }
}
