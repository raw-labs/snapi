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

package raw.runtime.truffle.ast.expressions.builtin.function_package;

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
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.function.Closure;

@NodeInfo(shortName = "Function.InvokeAfter")
@NodeChild(value = "function")
@NodeChild(value = "sleepTime")
public abstract class FunctionInvokeAfterNode extends ExpressionNode {

  @Specialization(limit = "3")
  @CompilerDirectives.TruffleBoundary
  protected Object invokeAfter(
      Object closure, long sleepTime, @CachedLibrary("closure") InteropLibrary interops) {
    try {
      Thread.sleep(sleepTime);
      return interops.execute(closure);
    } catch (InterruptedException
        | UnsupportedMessageException
        | UnsupportedTypeException
        | ArityException e) {
      throw new RawTruffleInternalErrorException(e);
    }
  }
}
