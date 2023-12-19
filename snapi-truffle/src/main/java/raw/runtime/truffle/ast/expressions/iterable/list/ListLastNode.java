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

import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.list.ListLibrary;
import raw.runtime.truffle.runtime.primitives.NullObject;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "List.Last")
@NodeChild("list")
public abstract class ListLastNode extends ExpressionNode {

  @Specialization(limit = "3")
  protected Object doLast(Object list, @CachedLibrary("list") ListLibrary lists) {
    if (lists.size(list) == 0) {
      return NullObject.INSTANCE;
    }
    return lists.get(list, (int) lists.size(list) - 1);
  }
}
