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

import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.generator.collection.compute_next.ComputeNextLibrary;
import raw.runtime.truffle.runtime.nullable_tryable.NullableTryableLibrary;
import raw.runtime.truffle.runtime.nullable_tryable.RuntimeNullableTryableHandler;

@ExportLibrary(ComputeNextLibrary.class)
public class FilterComputeNext {

    final Object parent;
    final Closure predicate;

    public FilterComputeNext(Object parent, Closure predicate) {
        this.parent = parent;
        this.predicate = predicate;
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
    Object computeNext(@CachedLibrary("this.parent") GeneratorLibrary generators,
                       @CachedLibrary(limit = "1") NullableTryableLibrary nullableTryables) {
        Object nullableTryable = new RuntimeNullableTryableHandler();
        Object[] argumentValues = new Object[1];

        while (generators.hasNext(parent)) {
            Object v = generators.next(parent);
            argumentValues[0] = v;
            Boolean isPredicateTrue = nullableTryables.handleOptionTriablePredicate(nullableTryable,
                predicate.call(argumentValues),
                false);
            if (isPredicateTrue) {
                return v;
            }
        }
        throw new BreakException();
    }
}
