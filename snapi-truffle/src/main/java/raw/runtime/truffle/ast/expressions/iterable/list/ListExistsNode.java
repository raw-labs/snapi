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

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2Type;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.handlers.NullableTryableHandler;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.list.ListLibrary;

@NodeInfo(shortName = "List.Exists")
@NodeChild("list")
@NodeChild("function")
@NodeField(name = "predicateType", type = Rql2Type.class)
public abstract class ListExistsNode extends ExpressionNode {

  protected abstract Rql2Type getPredicateType();

  @Specialization(limit = "3")
  protected boolean doList(
      Object list,
      Object closure,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary("closure") InteropLibrary interops,
      @CachedLibrary(limit = "2") IterableLibrary iterables,
      @CachedLibrary(limit = "2") GeneratorLibrary generators) {
    Object iterable = lists.toIterable(list);
    Object generator = iterables.getGenerator(iterable);
    try {
      generators.init(generator);
      Object[] argumentValues = new Object[1];
      while (generators.hasNext(generator)) {
        argumentValues[0] = generators.next(generator);
        Boolean predicate =
            NullableTryableHandler.handleOptionTriablePredicate(
                interops.execute(closure, argumentValues), getPredicateType(), false);
        if (predicate) {
          return true;
        }
      }
      return false;
    } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
      throw new RawTruffleRuntimeException("failed to execute function");
    } finally {
      generators.close(generator);
    }
  }
}
