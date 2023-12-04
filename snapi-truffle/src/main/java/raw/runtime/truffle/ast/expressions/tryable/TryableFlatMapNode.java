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

@NodeInfo(shortName = "Tryable.FlatMap")
@NodeChild("tryable")
@NodeChild("function")
@ImportStatic(Tryable.class)
public abstract class TryableFlatMapNode extends ExpressionNode {

  //    here add more guads to try to find object value and then do executeLong instead and call
  // LongTryable
  //    that's the only thing I think
  //    guarguars is tryable and object isSccess and type is null kind of thin

  @Specialization(limit = "1", guards = "isSuccess(tryable)")
  protected Object doObjectIsSuccess(
      Object tryable, Object closure, @CachedLibrary("closure") InteropLibrary interops) {
    //    argumentValues[0] = tryable;
    try {
      return interops.execute(closure, tryable);
    } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
      throw new RawTruffleRuntimeException("failed to execute function");
    }
  }

  @Specialization(guards = "isFailure(tryable)")
  protected Object doObjectFailure(Object tryable, Object closure) {
    return tryable;
  }
}
