package raw.runtime.truffle.runtime.function;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateInline;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.util.ArrayList;
import java.util.Objects;
import raw.runtime.truffle.runtime.list.StringList;

@ExportLibrary(InteropLibrary.class)
public class NonClosure implements TruffleObject {

  public static final int INLINE_CACHE_SIZE = 3;
  private final Function function;
  private final Object[] defaultArguments;
  private final VirtualFrame frame;
  private static final String GET_DEFAULT_PREFIX = "default_";

  public NonClosure(Function function, Object[] defaultArguments, VirtualFrame frame) {
    assert function != null;
    this.frame = frame;
    this.function = function;
    this.defaultArguments = defaultArguments;
  }

  public String getName() {
    return function.getName();
  }

  public RootCallTarget getCallTarget() {
    return function.getCallTarget();
  }

  public String[] getArgNames() {
    return function.getArgNames();
  }

  @ExportMessage
  abstract static class Execute {

    // Expects virtual frame to be already set
    @Specialization(
        limit = "INLINE_CACHE_SIZE",
        guards = "nonClosure.getCallTarget() == cachedTarget")
    protected static Object doDirect(
        NonClosure nonClosure,
        Object[] arguments,
        @Cached("nonClosure.getCallTarget()") RootCallTarget cachedTarget,
        @Cached("create(cachedTarget)") DirectCallNode callNode) {
      setArgs(nonClosure, arguments, arguments);
      return callNode.call(arguments);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        NonClosure nonClosure, Object[] arguments, @Cached IndirectCallNode callNode) {
      setArgs(nonClosure, arguments, arguments);
      return callNode.call(nonClosure.getCallTarget(), arguments);
    }

    private static void setArgs(NonClosure nonClosure, Object[] arguments, Object[] finalArgs) {
      String[] namedArgsNames = new String[arguments.length];
      String[] argNames = nonClosure.getArgNames();
      System.arraycopy(argNames, 0, namedArgsNames, 0, namedArgsNames.length);

      for (int i = 0; i < namedArgsNames.length; i++) {
        for (int j = 0; j < argNames.length; j++) {
          if (Objects.equals(namedArgsNames[i], argNames[j])) {
            finalArgs[j + 1] = arguments[i];
            break;
          }
        }
      }
    }
  }

  @ExportMessage
  boolean isExecutable() {
    return true;
  }

  @ExportMessage
  final boolean hasMembers() {
    return true;
  }

  @ExportMessage
  final boolean isMemberInvocable(String member) {
    if (member.startsWith(GET_DEFAULT_PREFIX)) {
      String argName = member.substring(GET_DEFAULT_PREFIX.length());
      for (int i = 0; i < defaultArguments.length; i++) {
        if (defaultArguments[i] != null && argName.equals(function.getArgNames()[i])) {
          return true;
        }
      }
    }
    return false;
  }

  @ExportMessage
  @CompilerDirectives.TruffleBoundary
  final Object getMembers(boolean includeInternal) {
    ArrayList<String> keys = new ArrayList<>();
    for (int i = 0; i < defaultArguments.length; i++) {
      if (defaultArguments[i] != null) {
        keys.add(GET_DEFAULT_PREFIX + function.getArgNames()[i]);
      }
    }
    return new StringList(keys.toArray(String[]::new));
  }

  @ExportMessage
  final Object invokeMember(String member, Object... arguments)
      throws UnknownIdentifierException, ArityException {
    if (startsWith(member)) {
      if (arguments.length > 0) {
        throw ArityException.create(0, 0, arguments.length);
      }
      String argName = substring(member, GET_DEFAULT_PREFIX.length());

      for (int i = 0; i < defaultArguments.length; i++) {
        if (defaultArguments[i] != null && argName.equals(function.getArgNames()[i])) {
          return defaultArguments[i];
        }
      }
    }
    throw UnknownIdentifierException.create(member);
  }

  @CompilerDirectives.TruffleBoundary
  private boolean startsWith(String member) {
    return member.startsWith(NonClosure.GET_DEFAULT_PREFIX);
  }

  @CompilerDirectives.TruffleBoundary
  private String substring(String member, int from) {
    return member.substring(from);
  }

  @NodeInfo(shortName = "NonClosure.Execute")
  @GenerateUncached
  @GenerateInline
  public abstract static class NonClosureExecuteWithNamesNode extends Node {

    public abstract Object execute(
        Node node, NonClosure nonClosure, String[] namedArgNames, Object[] arguments);

    private static void setArgsWithNames(
        NonClosure nonClosure, String[] namedArgNames, Object[] arguments, Object[] finalArgs) {
      for (int i = 0; i < namedArgNames.length; i++) {
        String currentArgName = namedArgNames[i];
        if (currentArgName == null) {
          // no arg name was provided, use the index.
          finalArgs[i + 1] = arguments[i];
        } else {
          // an arg name, ignore the current index 'i' and instead walk the arg names to find
          // the
          // real, and fill it in.

          int idx = 0;
          for (; idx < nonClosure.getArgNames().length; idx++) {
            if (Objects.equals(currentArgName, nonClosure.getArgNames()[idx])) {
              break;
            }
          }
          finalArgs[idx + 1] = arguments[i];
        }
      }
    }

    public static Object[] getObjectArray(int size) {
      return new Object[size + 1];
    }

    @Specialization(guards = "nonClosure.getCallTarget() == cachedTarget", limit = "3")
    protected static Object doDirect(
        Node node,
        NonClosure nonClosure,
        String[] namedArgNames,
        Object[] arguments,
        @Cached("nonClosure.getCallTarget()") RootCallTarget cachedTarget,
        @Cached("create(cachedTarget)") DirectCallNode callNode) {
      Object[] finalArgs = getObjectArray(nonClosure.getArgNames().length);
      finalArgs[0] = nonClosure.frame;
      System.arraycopy(
          nonClosure.defaultArguments, 0, finalArgs, 1, nonClosure.getArgNames().length);
      setArgsWithNames(nonClosure, namedArgNames, arguments, finalArgs);
      return callNode.call(finalArgs);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        Node node,
        NonClosure nonClosure,
        String[] namedArgNames,
        Object[] arguments,
        @Cached(inline = false) IndirectCallNode callNode) {
      Object[] finalArgs = getObjectArray(nonClosure.getArgNames().length);
      finalArgs[0] = nonClosure.frame;
      System.arraycopy(
          nonClosure.defaultArguments, 0, finalArgs, 1, nonClosure.getArgNames().length);
      setArgsWithNames(nonClosure, namedArgNames, arguments, finalArgs);
      return callNode.call(nonClosure.getCallTarget(), finalArgs);
    }
  }

  @NodeInfo(shortName = "NonClosure.ExecuteZero")
  @GenerateUncached
  @GenerateInline
  public abstract static class NonClosureExecuteZeroNode extends Node {

    public abstract Object execute(Node node, NonClosure nonClosure);

    @Specialization(guards = "nonClosure.getCallTarget() == cachedTarget", limit = "3")
    protected static Object doDirect(
        Node node,
        NonClosure nonClosure,
        @Cached("nonClosure.getCallTarget()") RootCallTarget cachedTarget,
        @Cached("create(cachedTarget)") DirectCallNode callNode) {
      return callNode.call(nonClosure.frame);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        Node node, NonClosure nonClosure, @Cached(inline = false) IndirectCallNode callNode) {
      return callNode.call(nonClosure.getCallTarget(), nonClosure.frame);
    }
  }

  @NodeInfo(shortName = "NonClosure.ExecuteOne")
  @GenerateUncached
  @GenerateInline
  public abstract static class NonClosureExecuteOneNode extends Node {

    public abstract Object execute(Node node, NonClosure nonClosure, Object argument);

    @Specialization(guards = "nonClosure.getCallTarget() == cachedTarget", limit = "8")
    protected static Object doDirect(
        Node node,
        NonClosure nonClosure,
        Object argument,
        @Cached("nonClosure.getCallTarget()") RootCallTarget cachedTarget,
        @Cached("create(cachedTarget)") DirectCallNode callNode) {
      return callNode.call(nonClosure.frame, argument);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        Node node,
        NonClosure nonClosure,
        Object argument,
        @Cached(inline = false) IndirectCallNode callNode) {
      return callNode.call(nonClosure.getCallTarget(), nonClosure.frame, argument);
    }
  }

  @NodeInfo(shortName = "NonClosure.ExecuteTwo")
  @GenerateUncached
  @GenerateInline
  public abstract static class NonClosureExecuteTwoNode extends Node {

    public abstract Object execute(
        Node node, NonClosure nonClosure, Object argument1, Object argument2);

    @Specialization(guards = "nonClosure.getCallTarget() == cachedTarget", limit = "3")
    protected static Object doDirect(
        Node node,
        NonClosure nonClosure,
        Object argument1,
        Object argument2,
        @Cached("nonClosure.getCallTarget()") RootCallTarget cachedTarget,
        @Cached("create(cachedTarget)") DirectCallNode callNode) {
      return callNode.call(nonClosure.frame, argument1, argument2);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        Node node,
        NonClosure nonClosure,
        Object argument1,
        Object argument2,
        @Cached(inline = false) IndirectCallNode callNode) {
      return callNode.call(nonClosure.getCallTarget(), nonClosure.frame, argument1, argument2);
    }
  }
}
