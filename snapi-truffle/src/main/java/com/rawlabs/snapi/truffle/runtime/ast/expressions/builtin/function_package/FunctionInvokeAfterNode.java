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

package com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.function_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.TruffleInternalErrorException;
import com.rawlabs.snapi.truffle.runtime.runtime.function.FunctionExecuteNodes;

@NodeInfo(shortName = "Function.InvokeAfter")
@NodeChild(value = "function")
@NodeChild(value = "sleepTime")
public abstract class FunctionInvokeAfterNode extends ExpressionNode {

  @Specialization
  @TruffleBoundary
  protected Object invokeAfter(
      Object function,
      long sleepTime,
      @Cached(inline = true) FunctionExecuteNodes.FunctionExecuteZero functionExecuteZero) {
    try {
      Thread.sleep(sleepTime);
      return functionExecuteZero.execute(this, function);
    } catch (InterruptedException e) {
      throw new TruffleInternalErrorException(e);
    }
  }
}
