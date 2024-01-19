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

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.function.Closure;

@NodeInfo(shortName = "Function.InvokeAfter")
@NodeChild(value = "function")
@NodeChild(value = "sleepTime")
public abstract class FunctionInvokeAfterNode extends ExpressionNode {

  @Specialization
  @TruffleBoundary
  protected Object invokeAfter(
      Closure closure,
      long sleepTime,
      @Cached(inline = true) Closure.ClosureExecuteZeroNode closureExecuteZeroNode) {
    try {
      Thread.sleep(sleepTime);
      return closureExecuteZeroNode.execute(this, closure);
    } catch (InterruptedException e) {
      throw new RawTruffleInternalErrorException(e);
    }
  }
}
