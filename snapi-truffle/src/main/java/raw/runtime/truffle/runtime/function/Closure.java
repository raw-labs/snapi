/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.runtime.truffle.runtime.function;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateInline;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.MaterializedFrame;
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
public class Closure implements TruffleObject {

  public static final int INLINE_CACHE_SIZE = 3;
  private final Function function;
  private final MaterializedFrame frame;
  private final Object[] defaultArguments;
  private String[] namedArgNames = null;
  private static final String GET_DEFAULT_PREFIX = "default_";

  // for regular closures. The 'frame' has to be a materialized one to make sure it can be stored
  // and used later.
  public Closure(Function function, Object[] defaultArguments, MaterializedFrame frame) {
    assert function != null;
    this.function = function;
    this.frame = frame;
    this.defaultArguments = defaultArguments;
  }

  // for top-level functions. The internal 'frame' is null because it's never used to fetch values
  // of free-variables.
  public Closure(Function function, Object[] defaultArguments) {
    this(function, defaultArguments, null);
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

  public void setNamedArgNames(String[] namedArgNames) {
    this.namedArgNames = namedArgNames;
  }

  public String[] getNamedArgNames() {
    return this.namedArgNames;
  }

  @ExportMessage
  abstract static class Execute {

    @Specialization(limit = "INLINE_CACHE_SIZE", guards = "closure.getCallTarget() == cachedTarget")
    protected static Object doDirect(
        Closure closure,
        Object[] arguments,
        @Cached("closure.getCallTarget()") RootCallTarget cachedTarget,
        @Cached("create(cachedTarget)") DirectCallNode callNode) {
      Object[] finalArgs = new Object[closure.getArgNames().length + 1];
      finalArgs[0] = closure.frame;
      System.arraycopy(closure.defaultArguments, 0, finalArgs, 1, closure.getArgNames().length);
      setArgs(closure, arguments, finalArgs);
      return callNode.call(finalArgs);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        Closure closure, Object[] arguments, @Cached IndirectCallNode callNode) {
      Object[] finalArgs = new Object[closure.getArgNames().length + 1];
      finalArgs[0] = closure.frame;
      System.arraycopy(closure.defaultArguments, 0, finalArgs, 1, closure.getArgNames().length);
      setArgs(closure, arguments, finalArgs);

      return callNode.call(closure.getCallTarget(), finalArgs);
    }

    private static void setArgs(Closure closure, Object[] arguments, Object[] finalArgs) {
      String[] namedArgsNames = new String[arguments.length];
      String[] argNames = closure.getArgNames();
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
    return member.startsWith(Closure.GET_DEFAULT_PREFIX);
  }

  @CompilerDirectives.TruffleBoundary
  private String substring(String member, int from) {
    return member.substring(from);
  }

  @NodeInfo(shortName = "Closure.Execute")
  @GenerateUncached
  @GenerateInline
  public abstract static class ClosureExecuteWithNamesNode extends Node {

    public abstract Object execute(Node node, Closure closure, Object[] arguments);

    private static void setArgsWithNames(Closure closure, Object[] arguments, Object[] finalArgs) {
      for (int i = 0; i < closure.getNamedArgNames().length; i++) {
        String currentArgName = closure.getNamedArgNames()[i];
        if (currentArgName == null) {
          // no arg name was provided, use the index.
          finalArgs[i + 1] = arguments[i];
        } else {
          // an arg name, ignore the current index 'i' and instead walk the arg names to find
          // the
          // real, and fill it in.

          int idx = 0;
          for (; idx < closure.getArgNames().length; idx++) {
            if (Objects.equals(currentArgName, closure.getArgNames()[idx])) {
              break;
            }
          }
          finalArgs[idx + 1] = arguments[i];
        }
      }
    }

    @Specialization(guards = "closure.getCallTarget() == cachedTarget", limit = "3")
    protected static Object doDirect(
        Closure closure,
        Object[] arguments,
        @Cached("closure.getCallTarget()") RootCallTarget cachedTarget,
        @Cached("create(cachedTarget)") DirectCallNode callNode) {
      Object[] finalArgs = new Object[closure.getArgNames().length + 1];
      finalArgs[0] = closure.frame;
      System.arraycopy(closure.defaultArguments, 0, finalArgs, 1, closure.getArgNames().length);
      setArgsWithNames(closure, arguments, finalArgs);
      return callNode.call(finalArgs);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        Closure closure, Object[] arguments, @Cached(inline = false) IndirectCallNode callNode) {
      Object[] finalArgs = new Object[closure.getArgNames().length + 1];
      finalArgs[0] = closure.frame;
      System.arraycopy(closure.defaultArguments, 0, finalArgs, 1, closure.getArgNames().length);
      setArgsWithNames(closure, arguments, finalArgs);
      return callNode.call(closure.getCallTarget(), finalArgs);
    }
  }

  @NodeInfo(shortName = "Closure.ExecuteZero")
  @GenerateUncached
  @GenerateInline
  public abstract static class ClosureExecuteZeroNode extends Node {

    public abstract Object execute(Node node, Closure closure);

    @Specialization(guards = "closure.getCallTarget() == cachedTarget", limit = "3")
    protected static Object doDirect(
        Closure closure,
        @Cached("closure.getCallTarget()") RootCallTarget cachedTarget,
        @Cached("create(cachedTarget)") DirectCallNode callNode) {
      return callNode.call(closure.frame);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        Closure closure, @Cached(inline = false) IndirectCallNode callNode) {
      return callNode.call(closure.getCallTarget(), closure.frame);
    }
  }

  @NodeInfo(shortName = "Closure.ExecuteOne")
  @GenerateUncached
  @GenerateInline
  public abstract static class ClosureExecuteOneNode extends Node {

    public abstract Object execute(Node node, Closure closure, Object argument);

    @Specialization(guards = "closure.getCallTarget() == cachedTarget", limit = "8")
    protected static Object doDirect(
        Closure closure,
        Object argument,
        @Cached("closure.getCallTarget()") RootCallTarget cachedTarget,
        @Cached("create(cachedTarget)") DirectCallNode callNode) {
      return callNode.call(closure.frame, argument);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        Closure closure, Object argument, @Cached(inline = false) IndirectCallNode callNode) {
      return callNode.call(closure.getCallTarget(), closure.frame, argument);
    }
  }

  @NodeInfo(shortName = "Closure.ExecuteTwo")
  @GenerateUncached
  @GenerateInline
  public abstract static class ClosureExecuteTwoNode extends Node {

    public abstract Object execute(Node node, Closure closure, Object argument1, Object argument2);

    @Specialization(guards = "closure.getCallTarget() == cachedTarget", limit = "3")
    protected static Object doDirect(
        Closure closure,
        Object argument1,
        Object argument2,
        @Cached("closure.getCallTarget()") RootCallTarget cachedTarget,
        @Cached("create(cachedTarget)") DirectCallNode callNode) {
      return callNode.call(closure.frame, argument1, argument2);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        Closure closure,
        Object argument1,
        Object argument2,
        @Cached(inline = false) IndirectCallNode callNode) {
      return callNode.call(closure.getCallTarget(), closure.frame, argument1, argument2);
    }
  }
}
