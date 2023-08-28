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

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.handlers.NullableTryableHandler;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.tryable.BooleanTryable;

@NodeInfo(shortName = "Collection.Exists")
@NodeChild("iterable")
@NodeChild("function")
@NodeField(name = "predicateType", type = Rql2TypeWithProperties.class)
public abstract class CollectionExistsNode extends ExpressionNode {

    protected abstract Rql2TypeWithProperties getPredicateType();

    @Specialization(limit = "3")
    protected BooleanTryable doIterable(
            Object iterable,
            Closure function,
            @CachedLibrary("iterable") IterableLibrary iterables,
            @CachedLibrary(limit = "2") GeneratorLibrary generators) {
        Object generator = iterables.getGenerator(iterable);
        try {
            generators.init(generator);
            Object[] argumentValues = new Object[1];
            while (generators.hasNext(generator)) {
                argumentValues[0] = generators.next(generator);
                Boolean predicate =
                        NullableTryableHandler.handleOptionTriablePredicate(
                                function.call(argumentValues), getPredicateType(), false);
                if (predicate) {
                    return BooleanTryable.BuildSuccess(true);
                }
            }
            return BooleanTryable.BuildSuccess(false);
        } catch (RawTruffleRuntimeException ex) {
            return BooleanTryable.BuildFailure(ex.getMessage());
        } finally {
            generators.close(generator);
        }
    }
}
