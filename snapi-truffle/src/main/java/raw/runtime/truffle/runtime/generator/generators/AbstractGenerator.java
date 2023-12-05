package raw.runtime.truffle.runtime.generator.generators;

import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

public class AbstractGenerator {

  private Object next = null;

  private boolean isTerminated = false;

  private final Object computeNext;

  private RawTruffleRuntimeException exception = null;

  public AbstractGenerator(Object computeNext) {
    this.computeNext = computeNext;
  }

  public Object getComputeNext() {
    return computeNext;
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
}
