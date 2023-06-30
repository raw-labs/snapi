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

package raw.runtime.truffle.runtime.generator;

import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.GenerateLibrary;
import com.oracle.truffle.api.library.Library;
import com.oracle.truffle.api.library.LibraryFactory;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

@GenerateLibrary
public abstract class GeneratorLibrary extends Library {

    static final LibraryFactory<GeneratorLibrary> FACTORY = LibraryFactory.resolve(GeneratorLibrary.class);

    public static LibraryFactory<GeneratorLibrary> getFactory() {
        return FACTORY;
    }

    public static GeneratorLibrary getUncached() {
        return FACTORY.getUncached();
    }

    public boolean isGenerator(Object receiver) {
        return false;
    }

    public abstract void init(Object receiver);

    public abstract void close(Object receiver);

    public abstract Object next(Object receiver);

    public abstract boolean hasNext(Object receiver);
}
