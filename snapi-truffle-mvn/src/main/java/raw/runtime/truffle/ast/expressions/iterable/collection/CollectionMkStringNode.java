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
import raw.runtime.truffle.runtime.operators.OperatorNodes;
import raw.runtime.truffle.runtime.primitives.ErrorObject;

@NodeInfo(shortName = "Collection.MkString")
@NodeChild("iterable")
@NodeChild("start")
@NodeChild("sep")
@NodeChild("end")
public abstract class CollectionMkStringNode extends ExpressionNode {
  @Specialization
  protected Object doCollection(
      Object iterable,
      String start,
      String sep,
      String end,
      @Cached(inline = true) OperatorNodes.AddNode add,
      @Cached(inline = true) IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) GeneratorNodes.GeneratorHasNextNode hasNextNode,
      @Cached(inline = true) GeneratorNodes.GeneratorNextNode nextNode,
      @Cached(inline = true) GeneratorNodes.GeneratorInitNode initNode,
      @Cached(inline = true) GeneratorNodes.GeneratorCloseNode closeNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      initNode.execute(this, generator);
      String currentString = start;
      if (!hasNextNode.execute(this, generator)) {
        return start + end;
      } else {
        Object next = nextNode.execute(this, generator);
        currentString = (String) add.execute(this, currentString, next);
      }
      while (hasNextNode.execute(this, generator)) {
        Object next = nextNode.execute(this, generator);
        currentString = (String) add.execute(this, currentString + sep, next);
      }
      return currentString + end;
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      closeNode.execute(this, generator);
    }
  }
}
