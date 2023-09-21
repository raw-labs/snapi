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

import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import java.util.Objects;

public class Closure {
  private final Function function;
  private final MaterializedFrame frame;
  private final Object[] defaultArguments;

  // for regular closures. The 'frame' has to be a materialized one to make sure it can be stored
  // and used later.
  public Closure(Function function, Object[] defaultArguments, MaterializedFrame frame) {
    this.function = function;
    this.frame = frame;
    this.defaultArguments = defaultArguments;
  }

  // "plain" call, no named arguments. That's used internally by '.Filter', '.GroupBy', etc.
  public Object call(Object... arguments) {
    Object[] args = new Object[function.argNames.length + 1];
    args[0] = frame;
    System.arraycopy(arguments, 0, args, 1, arguments.length);
    return function.execute(args);
  }

  // for top-level functions. The internal 'frame' is null because it's never used to fetch values
  // of free-variables.
  public Closure(Function function, Object[] defaultArguments) {
    this(function, defaultArguments, null);
  }

  // call with named arguments. That's used by the invoke or other ways a function can be called
  // with named arguments.
  @ExplodeLoop
  public Object callWithNames(String[] argNames, Object... arguments) {
    Object[] args = new Object[function.argNames.length + 1];
    args[0] = frame;
    // first fill in the default arguments (nulls if no default).
    System.arraycopy(defaultArguments, 0, args, 1, function.argNames.length);
    for (int i = 0; i < argNames.length; i++) {
      if (argNames[i] == null) {
        // no arg name was provided, use the index.
        args[i + 1] = arguments[i];
      } else {
        // an arg name, ignore the current index 'i' and instead walk the arg names to find
        // the
        // real, and fill it in.

        int idx = 0;
        for (; idx < argNames.length; idx++) {
          if (Objects.equals(argNames[i], function.argNames[idx])) {
            break;
          }
        }
        args[idx + 1] = arguments[i];
      }
    }
    return function.execute(args);
  }
}
