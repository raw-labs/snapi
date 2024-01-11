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

import com.oracle.truffle.api.dsl.Bind;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.list.ListNodes;
import raw.runtime.truffle.tryable_nullable.TryableNullable;

@NodeInfo(shortName = "List.Exists")
@NodeChild("list")
@NodeChild("function")
public abstract class ListExistsNode extends ExpressionNode {

  @Specialization(limit = "3")
  protected static boolean doList(
      Object list,
      Object closure,
      @Bind("this") Node thisNode,
      @Cached(inline = true) IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) GeneratorNodes.GeneratorCloseNode generatorCloseNode,
      @Cached(inline = true) ListNodes.ToIterableNode toIterableNode,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      generatorInitNode.execute(thisNode, generator);
      while (generatorHasNextNode.execute(thisNode, generator)) {
        boolean predicate =
            TryableNullable.handlePredicate(
                interops.execute(closure, generatorNextNode.execute(thisNode, generator)), false);
        if (predicate) {
          return true;
        }
      }
      return false;
    } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
      throw new RawTruffleRuntimeException("failed to execute function");
    } finally {
      generatorCloseNode.execute(thisNode, generator);
    }
  }
}
