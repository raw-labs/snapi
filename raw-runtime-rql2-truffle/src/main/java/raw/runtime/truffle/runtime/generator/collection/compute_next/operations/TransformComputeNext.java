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

package raw.runtime.truffle.runtime.generator.collection.compute_next.operations;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.generator.collection.compute_next.ComputeNextLibrary;

@ExportLibrary(ComputeNextLibrary.class)
public class TransformComputeNext {
    final Object parent;
    final Closure transform;

    public TransformComputeNext(Object parent, Closure transform) {
        this.parent = parent;
        this.transform = transform;
    }

    @ExportMessage
    void init(@CachedLibrary("this.parent") GeneratorLibrary generators) {
        generators.init(parent);
    }

    @ExportMessage
    void close(@CachedLibrary("this.parent") GeneratorLibrary generators) {
        generators.close(parent);
    }

    @ExportMessage
    public boolean isComputeNext() {
        return true;
    }

    @ExportMessage
    Object computeNext(@CachedLibrary("this.parent") GeneratorLibrary generators) {
        if (!generators.hasNext(parent)) {
            throw new BreakException();
        }
        Object[] argumentValues = new Object[1];
        argumentValues[0] = generators.next(parent);
        return transform.call(argumentValues);
    }
}
