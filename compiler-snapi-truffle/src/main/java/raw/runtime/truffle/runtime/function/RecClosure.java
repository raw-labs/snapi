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

// A recursive function closure
// Duplicate of Closure.java. It is differentiated in order to apply recursion optimizations in the
// future.
@ExportLibrary(InteropLibrary.class)
public class RecClosure implements TruffleObject {

  public static final int INLINE_CACHE_SIZE = 3;
  private final Function function;
  private final MaterializedFrame frame;
  private final Object[] defaultArguments;
  private static final String GET_DEFAULT_PREFIX = "default_";

  // for regular recClosures. The 'frame' has to be a materialized one to make sure it can be stored
  // and used later.
  public RecClosure(Function function, Object[] defaultArguments, MaterializedFrame frame) {
    assert function != null;
    this.function = function;
    this.frame = frame;
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

  // Execute function for interop
  @ExportMessage
  abstract static class Execute {

    @Specialization(
        limit = "INLINE_CACHE_SIZE",
        guards = "recClosure.getCallTarget() == cachedTarget")
    protected static Object doDirect(
        RecClosure recClosure,
        Object[] arguments,
        @Cached("recClosure.getCallTarget()") RootCallTarget cachedTarget,
        @Cached("create(cachedTarget)") DirectCallNode callNode) {
      Object[] finalArgs = new Object[recClosure.getArgNames().length + 1];
      finalArgs[0] = recClosure.frame;
      System.arraycopy(
          recClosure.defaultArguments, 0, finalArgs, 1, recClosure.getArgNames().length);
      setArgs(recClosure, arguments, finalArgs);
      return callNode.call(finalArgs);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        RecClosure recClosure, Object[] arguments, @Cached IndirectCallNode callNode) {
      Object[] finalArgs = new Object[recClosure.getArgNames().length + 1];
      finalArgs[0] = recClosure.frame;
      System.arraycopy(
          recClosure.defaultArguments, 0, finalArgs, 1, recClosure.getArgNames().length);
      setArgs(recClosure, arguments, finalArgs);

      return callNode.call(recClosure.getCallTarget(), finalArgs);
    }

    private static void setArgs(RecClosure recClosure, Object[] arguments, Object[] finalArgs) {
      String[] namedArgsNames = new String[arguments.length];
      String[] argNames = recClosure.getArgNames();
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
    return member.startsWith(RecClosure.GET_DEFAULT_PREFIX);
  }

  @CompilerDirectives.TruffleBoundary
  private String substring(String member, int from) {
    return member.substring(from);
  }

  // A node that executes a closure without optional arguments
  @NodeInfo(shortName = "RecClosure.Execute")
  @GenerateUncached
  @GenerateInline
  public abstract static class RecClosureExecuteWithNamesNode extends Node {

    public abstract Object execute(
        Node node, RecClosure recClosure, String[] namedArgNames, Object[] arguments);

    private static void setArgsWithNames(
        RecClosure recClosure, String[] namedArgNames, Object[] arguments, Object[] finalArgs) {
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
          for (; idx < recClosure.getArgNames().length; idx++) {
            if (Objects.equals(currentArgName, recClosure.getArgNames()[idx])) {
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

    // A node to execute a closure without params
    // It is used for reducing multiple Object[] allocations
    @Specialization(guards = "recClosure.getCallTarget() == cachedTarget", limit = "3")
    protected static Object doDirect(
        RecClosure recClosure,
        String[] namedArgNames,
        Object[] arguments,
        @Cached("recClosure.getCallTarget()") RootCallTarget cachedTarget,
        @Cached("create(cachedTarget)") DirectCallNode callNode) {
      Object[] finalArgs = getObjectArray(recClosure.getArgNames().length);
      finalArgs[0] = recClosure.frame;
      System.arraycopy(
          recClosure.defaultArguments, 0, finalArgs, 1, recClosure.getArgNames().length);
      setArgsWithNames(recClosure, namedArgNames, arguments, finalArgs);
      return callNode.call(finalArgs);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        RecClosure recClosure,
        String[] namedArgNames,
        Object[] arguments,
        @Cached(inline = false) IndirectCallNode callNode) {
      Object[] finalArgs = getObjectArray(recClosure.getArgNames().length);
      finalArgs[0] = recClosure.frame;
      System.arraycopy(
          recClosure.defaultArguments, 0, finalArgs, 1, recClosure.getArgNames().length);
      setArgsWithNames(recClosure, namedArgNames, arguments, finalArgs);
      return callNode.call(recClosure.getCallTarget(), finalArgs);
    }
  }

  // A node to execute a closure without params
  // It is used for reducing multiple Object[] allocations
  @NodeInfo(shortName = "RecClosure.ExecuteZero")
  @GenerateUncached
  @GenerateInline
  public abstract static class RecClosureExecuteZeroNode extends Node {

    public abstract Object execute(Node node, RecClosure recClosure);

    @Specialization(guards = "recClosure.getCallTarget() == cachedTarget", limit = "3")
    protected static Object doDirect(
        RecClosure recClosure,
        @Cached("recClosure.getCallTarget()") RootCallTarget cachedTarget,
        @Cached("create(cachedTarget)") DirectCallNode callNode) {
      return callNode.call(recClosure.frame);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        RecClosure recClosure, @Cached(inline = false) IndirectCallNode callNode) {
      return callNode.call(recClosure.getCallTarget(), recClosure.frame);
    }
  }

  // A node to execute a closure with 1 param
  // It is used for reducing multiple Object[] allocations
  @NodeInfo(shortName = "RecClosure.ExecuteOne")
  @GenerateUncached
  @GenerateInline
  public abstract static class RecClosureExecuteOneNode extends Node {

    public abstract Object execute(Node node, RecClosure recClosure, Object argument);

    @Specialization(guards = "recClosure.getCallTarget() == cachedTarget", limit = "8")
    protected static Object doDirect(
        RecClosure recClosure,
        Object argument,
        @Cached("recClosure.getCallTarget()") RootCallTarget cachedTarget,
        @Cached("create(cachedTarget)") DirectCallNode callNode) {
      return callNode.call(recClosure.frame, argument);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        RecClosure recClosure, Object argument, @Cached(inline = false) IndirectCallNode callNode) {
      return callNode.call(recClosure.getCallTarget(), recClosure.frame, argument);
    }
  }

  // A node to execute a closure with 2 params
  // It is used for reducing multiple Object[] allocations
  @NodeInfo(shortName = "RecClosure.ExecuteTwo")
  @GenerateUncached
  @GenerateInline
  public abstract static class RecClosureExecuteTwoNode extends Node {

    public abstract Object execute(
        Node node, RecClosure recClosure, Object argument1, Object argument2);

    @Specialization(guards = "recClosure.getCallTarget() == cachedTarget", limit = "3")
    protected static Object doDirect(
        RecClosure recClosure,
        Object argument1,
        Object argument2,
        @Cached("recClosure.getCallTarget()") RootCallTarget cachedTarget,
        @Cached("create(cachedTarget)") DirectCallNode callNode) {
      return callNode.call(recClosure.frame, argument1, argument2);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        RecClosure recClosure,
        Object argument1,
        Object argument2,
        @Cached(inline = false) IndirectCallNode callNode) {
      return callNode.call(recClosure.getCallTarget(), recClosure.frame, argument1, argument2);
    }
  }
}
