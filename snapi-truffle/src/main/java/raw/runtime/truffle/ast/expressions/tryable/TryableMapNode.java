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

import com.oracle.truffle.api.dsl.ImportStatic;
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
import raw.runtime.truffle.tryable_nullable.Tryable;

@NodeInfo(shortName = "Tryable.Map")
@NodeChild("tryable")
@NodeChild("function")
@ImportStatic(Tryable.class)
public abstract class TryableMapNode extends ExpressionNode {

  private final Object[] argumentValues = new Object[1];

  @Specialization(guards = "isSuccess(tryable)", limit = "1")
  protected Object doObjectIsSuccess(
      Object tryable, Object closure, @CachedLibrary("closure") InteropLibrary interops) {
    argumentValues[0] = tryable;
    Object result = null;
    try {
      result = interops.execute(closure, argumentValues);
    } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
      throw new RawTruffleRuntimeException("failed to execute function");
    }
    return result;
  }

  @Specialization(limit = "1")
  protected Object doObjectIsFailure(
      Object tryable, Object closure, @CachedLibrary("closure") InteropLibrary interops) {
    return tryable;
  }
}
