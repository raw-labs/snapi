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

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.generator.collection.compute_next.ComputeNextLibrary;
import raw.runtime.truffle.runtime.record.RecordObject;

@ExportLibrary(ComputeNextLibrary.class)
public class ZipComputeNext {

    final Object parent1;

    final Object parent2;

    final RawLanguage language;

    public ZipComputeNext(Object parent1, Object parent2, RawLanguage language) {
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.language = language;
    }

    @ExportMessage
    void init(@CachedLibrary("this.parent1") GeneratorLibrary generators1,
              @CachedLibrary("this.parent2") GeneratorLibrary generators2) {
        generators1.init(parent1);
        generators2.init(parent2);
    }

    @ExportMessage
    void close(@CachedLibrary("this.parent1") GeneratorLibrary generators1,
               @CachedLibrary("this.parent2") GeneratorLibrary generators2) {
        generators1.close(parent1);
        generators2.close(parent2);
    }

    @ExportMessage
    public boolean isComputeNext() {
        return true;
    }

    @ExportMessage
    Object computeNext(@CachedLibrary("this.parent1") GeneratorLibrary generators1,
                       @CachedLibrary("this.parent2") GeneratorLibrary generators2,
                       @CachedLibrary(limit = "5") InteropLibrary records) {
        try {
            if (generators1.hasNext(parent1) && generators2.hasNext(parent2)) {
                RecordObject record = language.createRecord();
                records.writeMember(record, "_1", generators1.next(parent1));
                records.writeMember(record, "_2", generators2.next(parent2));
                return record;
            }
            throw new BreakException();
        } catch (UnsupportedMessageException | UnknownIdentifierException | UnsupportedTypeException e) {
            throw new RawTruffleInternalErrorException(e.getCause());
        }
    }
}
