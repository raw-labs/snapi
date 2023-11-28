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

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.Cached;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import raw.runtime.truffle.runtime.list.StringList;

@ExportLibrary(InteropLibrary.class)
public class Closure implements TruffleObject {

  public static final int INLINE_CACHE_SIZE = 3;
  private final Function function;
  private final MaterializedFrame frame;
  private final Object[] defaultArguments;
  private String[] namedArgNames = null;
  private final Map<String, Object> namedArgs = new HashMap<>();
  private static final String GET_DEFAULT_PREFIX = "default_";

  // for regular closures. The 'frame' has to be a materialized one to make sure it can be stored
  // and used later.
  public Closure(Function function, Object[] defaultArguments, MaterializedFrame frame) {
    assert function != null;
    this.function = function;
    this.frame = frame;
    this.defaultArguments = defaultArguments;
    for (int i = 0; i < defaultArguments.length; i++) {
      if (defaultArguments[i] != null) {
        namedArgs.put(function.getArgNames()[i], defaultArguments[i]);
      }
    }
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
      Object[] args =
          closure.getNamedArgNames() == null
              ? getArgs(closure, arguments)
              : getNamedArgs(closure, closure.getNamedArgNames(), arguments);

      return callNode.call(args);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        Closure closure, Object[] arguments, @Cached IndirectCallNode callNode) {

      Object[] args =
          closure.getNamedArgNames() == null
              ? getArgs(closure, arguments)
              : getNamedArgs(closure, closure.getNamedArgNames(), arguments);

      return callNode.call(closure.getCallTarget(), args);
    }

    private static Object[] getArgs(Closure closure, Object[] arguments) {
      String[] namedArgsNames = new String[arguments.length];
      String[] argNames = closure.getArgNames();
      System.arraycopy(argNames, 0, namedArgsNames, 0, namedArgsNames.length);
      return getNamedArgs(closure, namedArgsNames, arguments);
    }

    // Don't explode loop, graph becomes too big.
    private static Object[] getNamedArgs(
        Closure closure, String[] namedArgsNames, Object[] arguments) {
      Object[] args = new Object[closure.getArgNames().length + 1];
      args[0] = closure.frame;
      String[] argNames = closure.getArgNames();
      // first fill in the default arguments (nulls if no default).
      System.arraycopy(closure.defaultArguments, 0, args, 1, argNames.length);
      for (int i = 0; i < namedArgsNames.length; i++) {
        String currentArgName = namedArgsNames[i];
        if (currentArgName == null) {
          // no arg name was provided, use the index.
          args[i + 1] = arguments[i];
        } else {
          // an arg name, ignore the current index 'i' and instead walk the arg names to find
          // the
          // real, and fill it in.

          int idx = 0;
          for (; idx < argNames.length; idx++) {
            if (Objects.equals(currentArgName, argNames[idx])) {
              break;
            }
          }
          args[idx + 1] = arguments[i];
        }
      }

      return args;
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
      return namedArgs.containsKey(argName);
    } else {
      return false;
    }
  }

  @ExportMessage
  final Object getMembers(boolean includeInternal) {
    return new StringList(
        namedArgs.keySet().stream().map(s -> GET_DEFAULT_PREFIX + s).toArray(String[]::new));
  }

  @ExportMessage
  final Object invokeMember(String member, Object... arguments)
      throws UnknownIdentifierException, ArityException {
    if (member.startsWith(GET_DEFAULT_PREFIX)) {
      if (arguments.length > 0) {
        throw ArityException.create(0, 0, arguments.length);
      }
      String argName = member.substring(GET_DEFAULT_PREFIX.length());
      return namedArgs.get(argName);
    } else {
      throw UnknownIdentifierException.create(member);
    }
  }
}
