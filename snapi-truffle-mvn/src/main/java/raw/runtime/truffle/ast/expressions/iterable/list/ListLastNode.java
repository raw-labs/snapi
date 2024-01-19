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

package raw.runtime.truffle.ast.expressions.iterable.list;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.list.ListNodes;
import raw.runtime.truffle.runtime.primitives.NullObject;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "List.Last")
@NodeChild("list")
public abstract class ListLastNode extends ExpressionNode {

  @Specialization
  protected Object doLast(
      Object list,
      @Cached(inline = true) ListNodes.SizeNode sizeNode,
      @Cached(inline = true) ListNodes.GetNode getNode) {
    long size = sizeNode.execute(this, list);
    if (size == 0) {
      return NullObject.INSTANCE;
    }
    return getNode.execute(this, list, (int) size - 1);
  }
}
