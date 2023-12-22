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
import raw.runtime.truffle.tryable_nullable.TryableNullable;

@NodeInfo(shortName = "TryableNullable.FlatMap")
@NodeChild("tryable")
@NodeChild("function")
@ImportStatic(TryableNullable.class)
public abstract class TryableNullableFlatMapNode extends ExpressionNode {

  @Specialization(guards = "isValue(maybeTryableNullable)", limit = "1")
  protected Object doTryableValue(
      Object maybeTryableNullable,
      Object closure,
      @CachedLibrary("closure") InteropLibrary interops) {
    try {
      return interops.execute(closure, maybeTryableNullable);
    } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
      throw new RawTruffleRuntimeException("failed to execute function");
    }
  }

  @Specialization(guards = "!isValue(maybeTryableNullable)")
  protected Object doTryableNotValue(Object maybeTryableNullable, Object closure) {
    return maybeTryableNullable;
  }
}
