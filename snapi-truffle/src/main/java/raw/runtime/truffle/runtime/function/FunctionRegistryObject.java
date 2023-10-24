package raw.runtime.truffle.runtime.function;


import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.list.StringList;

import java.util.HashMap;
import java.util.Map;

@ExportLibrary(InteropLibrary.class)
public class FunctionRegistryObject implements TruffleObject {

  private final Map<String, Closure> functions = new HashMap<>();

  @CompilerDirectives.TruffleBoundary
  public Closure get(String name) {
    return functions.get(name);
  }

  @CompilerDirectives.TruffleBoundary
  public void put(String name, Closure closure) {
    functions.put(name, closure);
  }

  @ExportMessage
  boolean hasMembers() {
    return true;
  }

  @ExportMessage
  final Object getMembers(boolean includeInternal) {
    return new StringList(functions.keySet().toArray(String[]::new));
  }

  @ExportMessage final boolean isMemberReadable(String member) { return functions.containsKey(member); }

  @ExportMessage
  final Object readMember(String member) {
    return functions.get(member);
  }

  @ExportMessage
  final boolean hasLanguage() {
    return true;
  }

  @ExportMessage final Class<? extends TruffleLanguage<?>> getLanguage() { return RawLanguage.class; }

  @ExportMessage
  final Object toDisplayString(boolean allowSideEffects) {
    return "RawScope";
  }

  @ExportMessage
  boolean isScope() {
    return true;
  }

}
