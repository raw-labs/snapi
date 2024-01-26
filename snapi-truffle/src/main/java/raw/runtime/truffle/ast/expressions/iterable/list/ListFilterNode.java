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

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.util.ArrayList;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.list.*;
import raw.runtime.truffle.tryable_nullable.TryableNullable;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "List.Filter")
@NodeChild("list")
@NodeChild("function")
public abstract class ListFilterNode extends ExpressionNode {

  @Specialization
  protected static RawArrayList doFilter(
      Object list,
      Object function,
      @Bind("this") Node thisNode,
      @Cached(inline = true) IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) ListNodes.ToIterableNode toIterableNode,
      @Cached(inline = true) GeneratorNodes.GeneratorCloseNode generatorCloseNode,
      @Cached(inline = true) GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode) {
    ArrayList<Object> llist = new ArrayList<>();
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      generatorInitNode.execute(thisNode, generator);
      while (generatorHasNextNode.execute(thisNode, generator)) {
        Object v = generatorNextNode.execute(thisNode, generator);
        Boolean predicate = null;
        predicate =
            TryableNullable.handlePredicate(
                functionExecuteOneNode.execute(thisNode, function, v), false);
        if (predicate) {
          llist.add(v);
        }
      }
      return new RawArrayList(llist);
    } finally {
      generatorCloseNode.execute(thisNode, generator);
    }
  }
}
