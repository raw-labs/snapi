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
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.option.ObjectOption;
import raw.runtime.truffle.runtime.option.OptionLibrary;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;

@NodeInfo(shortName = "Collection.First")
@NodeChild("iterable")
public abstract class CollectionFirstNode extends ExpressionNode {

    @Specialization(limit = "3")
    protected ObjectTryable doObject(Object iterable,
                                     @CachedLibrary(limit = "1") GeneratorLibrary generators,
                                     @CachedLibrary("iterable") IterableLibrary iterables,
                                     @CachedLibrary(limit = "1") OptionLibrary options) {
        try {
            Object generator = iterables.getGenerator(iterable);
            if (!generators.hasNext(generator)) {
                return ObjectTryable.BuildSuccess(new ObjectOption());
            }
            Object next = generators.next(generator);
            if (options.isOption(next)) {
                return ObjectTryable.BuildSuccess(next);
            }
            return ObjectTryable.BuildSuccess(new ObjectOption(next));
        } catch (RawTruffleRuntimeException e) {
            return ObjectTryable.BuildFailure(e.getMessage());
        }
    }

}

