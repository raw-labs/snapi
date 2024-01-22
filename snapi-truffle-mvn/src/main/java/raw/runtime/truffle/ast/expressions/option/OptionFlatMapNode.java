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

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.tryable_nullable.Nullable;

@NodeInfo(shortName = "Option.FlatMap")
@NodeChild("option")
@NodeChild("function")
@ImportStatic(Nullable.class)
public abstract class OptionFlatMapNode extends ExpressionNode {

  @Specialization(guards = "isNotNull(option)")
  protected Object notNullFlatMap(
      Object option,
      Closure closure,
      @Cached(inline = true) Closure.ClosureExecuteOneNode closureExecuteOneNode) {
    return closureExecuteOneNode.execute(this, closure, option);
  }

  @Specialization(guards = "isNull(option)")
  protected Object optionFlatMap(Object option, Object closure) {
    return option;
  }
}
