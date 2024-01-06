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

package raw.runtime.truffle.ast.expressions.iterable.collection;

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
import raw.runtime.truffle.runtime.primitives.ErrorObject;
import raw.runtime.truffle.tryable_nullable.TryableNullable;

@NodeInfo(shortName = "Collection.Exists")
@NodeChild("iterable")
@NodeChild("function")
public abstract class CollectionExistsNode extends ExpressionNode {

  @Specialization(limit = "3")
  protected static Object doIterable(
      Object iterable,
      Object closure,
      @Bind("this") Node thisNode,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) GeneratorNodes.GeneratorCloseNode generatorCloseNode,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      generatorInitNode.execute(thisNode, generator);
      Object[] argumentValues = new Object[1];
      while (generatorHasNextNode.execute(thisNode, generator)) {
        argumentValues[0] = generatorNextNode.execute(thisNode, generator);
        boolean predicate =
            TryableNullable.handlePredicate(interops.execute(closure, argumentValues), false);
        if (predicate) {
          return true;
        }
      }
      return false;
    } catch (RawTruffleRuntimeException
        | UnsupportedMessageException
        | UnsupportedTypeException
        | ArityException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      generatorCloseNode.execute(thisNode, generator);
    }
  }
}
