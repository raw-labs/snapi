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

package raw.runtime.truffle.ast.expressions.tryable;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.option.EmptyOption;
import raw.runtime.truffle.runtime.option.OptionLibrary;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;
import raw.runtime.truffle.runtime.tryable.TryableLibrary;

@NodeInfo(shortName = "TryableNullable.FlatMap")
@NodeChild("tryable")
@NodeChild("function")
public abstract class TryableNullableFlatMapNode extends ExpressionNode {

  private final Object[] argumentValues = new Object[1];
  private final ObjectTryable successNone = ObjectTryable.BuildSuccess(new EmptyOption());

  @Specialization(limit = "1")
  @CompilerDirectives.TruffleBoundary
  protected Object doTryable(
      Object tryable,
      Object closure,
      @CachedLibrary("tryable") TryableLibrary tryables,
      @CachedLibrary("closure") InteropLibrary interops,
      @CachedLibrary(limit = "1") OptionLibrary nullables) {
    if (tryables.isSuccess(tryable)) {
      Object n = tryables.success(tryable);
      if (nullables.isDefined(n)) {
        Object v = nullables.get(n);
        argumentValues[0] = v;
        try {
          return interops.execute(closure, argumentValues);
        } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
          throw new RawTruffleRuntimeException("failed to execute function");
        }
      } else {
        return successNone;
      }
    } else {
      return tryable;
    }
  }
}
