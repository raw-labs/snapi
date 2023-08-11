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

package raw.runtime.truffle.runtime.tryable;

import com.oracle.truffle.api.library.GenerateLibrary;
import com.oracle.truffle.api.library.Library;
import com.oracle.truffle.api.library.LibraryFactory;

@GenerateLibrary
public abstract class TryableLibrary extends Library {

    static final LibraryFactory<TryableLibrary> FACTORY = LibraryFactory.resolve(TryableLibrary.class);

    public boolean isTryable(Object receiver) {
        return false;
    }

    public static LibraryFactory<TryableLibrary> getFactory() {
        return FACTORY;
    }

    public static TryableLibrary getUncached() {
        return FACTORY.getUncached();
    }

    public abstract Object success(Object receiver);

    public abstract String failure(Object receiver);

    public abstract boolean isSuccess(Object receiver);

    public abstract boolean isFailure(Object receiver);


}
