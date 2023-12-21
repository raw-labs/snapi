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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.util.ArrayList;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.list.*;
import raw.runtime.truffle.tryable_nullable.TryableNullable;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "List.Filter")
@NodeChild("list")
@NodeChild("function")
public abstract class ListFilterNode extends ExpressionNode {

  @Specialization(limit = "3")
  @CompilerDirectives.TruffleBoundary
  protected RawArrayList doByte(
      Object list,
      Object closure,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached ListNodes.ToIterableNode toIterableNode,
      @CachedLibrary("closure") InteropLibrary interops) {
    ArrayList<Object> llist = new ArrayList<>();
    Object iterable = toIterableNode.execute(list);
    Object generator = getGeneratorNode.execute(iterable);
    while (generatorHasNextNode.execute(generator)) {
      Object v = generatorNextNode.execute(generator);
      Boolean predicate = null;
      try {
        predicate = TryableNullable.handlePredicate(interops.execute(closure, v), false);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      if (predicate) {
        llist.add(v);
      }
    }
    return new RawArrayList(llist);
  }
}
