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

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.tryable_nullable.TryableNullableNodes;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.tryable.TryableLibrary;

@NodeInfo(shortName = "Tryable.Map")
@NodeChild("tryable")
@NodeChild("function")
public abstract class TryableMapNode extends ExpressionNode {

  @Specialization(guards = "tryables.isTryable(tryable)", limit = "1")
  protected Object doObject(
      Object tryable,
      Object closure,
      @Cached("create()") TryableNullableNodes.BoxTryableNode boxTryable,
      @CachedLibrary("tryable") TryableLibrary tryables,
      @CachedLibrary("closure") InteropLibrary interops) {
    if (tryables.isSuccess(tryable)) {
      Object v = tryables.success(tryable);
      Object[] argumentValues = new Object[1];
      argumentValues[0] = v;
      Object result = null;
      try {
        result = interops.execute(closure, argumentValues);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      return boxTryable.execute(result);
    } else {
      return tryable;
    }
  }
}
