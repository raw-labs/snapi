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

package raw.runtime.truffle.runtime.generator.collection.compute_next.sources;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.generator.collection.compute_next.ComputeNextLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;

@ExportLibrary(ComputeNextLibrary.class)
public final class UnionComputeNext {

    private final ExpressionNode[] inputs;
    private final VirtualFrame frame;
    private int index;
    private Object currentGenerator = null;

    public UnionComputeNext(ExpressionNode[] inputs, VirtualFrame frame) {
        this.frame = frame;
        this.inputs = inputs;
        this.index = 0;
    }

    @ExportMessage
    void init() {}

    @ExportMessage
    void close(
            @Cached.Shared("sharedGenerators") @CachedLibrary(limit = "3")
                    GeneratorLibrary generators) {
        if (currentGenerator != null) {
            generators.close(currentGenerator);
        }
    }

    @ExportMessage
    public boolean isComputeNext() {
        return true;
    }

    @ExportMessage
    Object computeNext(
            @CachedLibrary(limit = "3") IterableLibrary iterables,
            @Cached.Shared("sharedGenerators") @CachedLibrary(limit = "3")
                    GeneratorLibrary generators) {
        while (currentGenerator == null) {
            if (index >= inputs.length) {
                throw new BreakException();
            }
            Object iterable = inputs[index].executeGeneric(frame);
            currentGenerator = iterables.getGenerator(iterable);
            generators.init(currentGenerator);
            if (!generators.hasNext(currentGenerator)) {
                generators.close(currentGenerator);
                currentGenerator = null;
            }
            index++;
        }
        Object r = generators.next(currentGenerator);
        if (!generators.hasNext(currentGenerator)) {
            generators.close(currentGenerator);
            currentGenerator = null;
        }
        return r;
    }
}
