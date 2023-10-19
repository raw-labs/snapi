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
import raw.runtime.truffle.runtime.tryable.TryableLibrary;

@NodeInfo(shortName = "Tryable.FlatMap")
@NodeChild("tryable")
@NodeChild("function")
public abstract class TryableFlatMapNode extends ExpressionNode {

  //    here add more guads to try to find object value and then do executeLong instead and call
  // LongTryable
  //    that's the only thing I think
  //    guarguars is tryable and object isSccess and type is null kind of thin

  @Specialization(guards = "tryables.isTryable(tryable)", limit = "1")
  protected Object doObject(
      Object tryable,
      Object closure,
      @CachedLibrary("tryable") TryableLibrary tryables,
      @CachedLibrary("closure") InteropLibrary interops) {
    if (tryables.isSuccess(tryable)) {
      Object v = tryables.success(tryable);
      Object[] argumentValues = new Object[1];
      argumentValues[0] = v;
      try {
        return interops.execute(closure, argumentValues);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
    } else {
      return tryable;
    }
  }
}
