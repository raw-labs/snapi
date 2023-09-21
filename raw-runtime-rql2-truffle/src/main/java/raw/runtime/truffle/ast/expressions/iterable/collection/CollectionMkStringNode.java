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
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.operators.OperatorNodes;
import raw.runtime.truffle.runtime.tryable.StringTryable;

@NodeInfo(shortName = "Collection.MkString")
@NodeChild("iterable")
@NodeChild("start")
@NodeChild("sep")
@NodeChild("end")
public abstract class CollectionMkStringNode extends ExpressionNode {
  @Specialization(limit = "3")
  protected StringTryable doCollection(
      Object iterable,
      String start,
      String sep,
      String end,
      @Cached("create()") OperatorNodes.AddNode add,
      @CachedLibrary("iterable") IterableLibrary iterables,
      @CachedLibrary(limit = "1") GeneratorLibrary generators) {
    try {
      Object generator = iterables.getGenerator(iterable);
      String currentString = start;
      if (!generators.hasNext(generator)) {
        return StringTryable.BuildSuccess(start + end);
      } else {
        Object next = generators.next(generator);
        currentString = (String) add.execute(currentString, next);
      }
      while (generators.hasNext(generator)) {
        Object next = generators.next(generator);
        currentString = (String) add.execute(currentString + sep, next);
      }
      return StringTryable.BuildSuccess(currentString + end);
    } catch (RawTruffleRuntimeException ex) {
      return StringTryable.BuildFailure(ex.getMessage());
    }
  }
}
