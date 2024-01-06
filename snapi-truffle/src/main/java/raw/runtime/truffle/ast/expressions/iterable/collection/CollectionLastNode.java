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

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.primitives.ErrorObject;
import raw.runtime.truffle.runtime.primitives.NullObject;

@NodeInfo(shortName = "Collection.Last")
@NodeChild("parent")
public abstract class CollectionLastNode extends ExpressionNode {
  @Specialization
  protected Object doObject(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode initNode,
      @Cached GeneratorNodes.GeneratorHasNextNode hasNextNode,
      @Cached GeneratorNodes.GeneratorNextNode nextNode) {
    try {
      Object generator = getGeneratorNode.execute(this, iterable);
      initNode.execute(this, generator);
      if (!hasNextNode.execute(this, generator)) {
        return NullObject.INSTANCE;
      }
      Object next = nextNode.execute(this, generator);
      while (hasNextNode.execute(this, generator)) {
        next = nextNode.execute(this, generator);
      }
      return next;
    } catch (RawTruffleRuntimeException e) {
      return new ErrorObject(e.getMessage());
    }
  }
}
