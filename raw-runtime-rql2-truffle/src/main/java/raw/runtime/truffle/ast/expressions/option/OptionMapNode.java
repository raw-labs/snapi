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

package raw.runtime.truffle.ast.expressions.option;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.nullable_tryable.NullableTryableLibrary;
import raw.runtime.truffle.runtime.nullable_tryable.RuntimeNullableTryableHandler;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.option.OptionLibrary;

@NodeInfo(shortName = "Option.Map")
@NodeChild("option")
@NodeChild("function")
public abstract class OptionMapNode extends ExpressionNode {

  @Specialization(
      guards = {"options.isOption(option)"},
      limit = "1")
  protected Object optionMap(
      Object option,
      Closure closure,
      @CachedLibrary("option") OptionLibrary options,
      @CachedLibrary(limit = "1") NullableTryableLibrary nullableTryables) {
    if (options.isDefined(option)) {
      Object v = options.get(option);
      Object[] argumentValues = new Object[1];
      argumentValues[0] = v;
      Object result = closure.call(argumentValues);
      RuntimeNullableTryableHandler handler = new RuntimeNullableTryableHandler();
      return nullableTryables.boxOption(handler, result);
    } else {
      return option;
    }
  }
}
