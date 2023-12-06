package raw.runtime.truffle.runtime.generator.collection;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

@ExportLibrary(InteropLibrary.class)
public class AbstractGenerator implements TruffleObject {

  private final Object nextGenerator;

  private Object next = null;

  private boolean isTerminated = false;


  private RawTruffleRuntimeException exception = null;

  public AbstractGenerator(Object computeNext) {
    this.nextGenerator = computeNext;
  }

  public Object getNextGenerator() {
    return nextGenerator;
  }

  public boolean isTerminated() {
    return isTerminated;
  }

  public void setTerminated(boolean isTerminated) {
    this.isTerminated = isTerminated;
  }

  public Object getNext() {
    return next;
  }

  public void setNext(Object next) {
    this.next = next;
  }

  public RawTruffleRuntimeException getException() {
    return exception;
  }

  public void setException(RawTruffleRuntimeException exception) {
    this.exception = exception;
  }

  public boolean hasException() {
    return exception != null;
  }

  // InteropLibrary: Iterator

  // (AZ) TO DO when all the nodes are created
  //  @ExportMessage
  //  final boolean isIterator() {
  //    return true;
  //  }
  //
  //  @ExportMessage
  //  final boolean hasIteratorNextElement(@CachedLibrary("this") GeneratorLibrary generatorLibrary)
  //          throws UnsupportedMessageException {
  //    return generatorLibrary.hasNext(this);
  //  }
  //
  //  @ExportMessage
  //  final Object getIteratorNextElement(@CachedLibrary("this") GeneratorLibrary generatorLibrary)
  //          throws UnsupportedMessageException, StopIterationException {
  //    return generatorLibrary.next(this);
  //  }
  //
  //  @ExportMessage
  //  final boolean hasMembers() {
  //    return true;
  //  }
  //
  //  @ExportMessage
  //  final Object getMembers(boolean includeInternal) {
  //    return new StringList(new String[] {"close"});
  //  }
  //
  //  @ExportMessage
  //  final boolean isMemberInvocable(String member) {
  //    return Objects.equals(member, "close");
  //  }
  //
  //  @ExportMessage
  //  final Object invokeMember(
  //          String member, Object[] args, @CachedLibrary("this") GeneratorLibrary
  // generatorLibrary) {
  //    assert (Objects.equals(member, "close"));
  //    generatorLibrary.close(this);
  //    return 0;
  //  }
}
