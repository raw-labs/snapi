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

package raw.runtime.truffle.runtime.option;

import com.oracle.truffle.api.library.GenerateLibrary;
import com.oracle.truffle.api.library.Library;
import com.oracle.truffle.api.library.LibraryFactory;
import raw.runtime.truffle.runtime.tryable.TryableLibrary;

@GenerateLibrary
public abstract class OptionLibrary extends Library {

    static final LibraryFactory<OptionLibrary> FACTORY =
            LibraryFactory.resolve(OptionLibrary.class);

    public static LibraryFactory<OptionLibrary> getFactory() {
        return FACTORY;
    }

    public static OptionLibrary getUncached() {
        return FACTORY.getUncached();
    }

    public boolean isOption(Object receiver) {
        return false;
    }

    public abstract Object get(Object receiver);

    public abstract void set(Object receiver, Object value);

    public abstract boolean isDefined(Object receiver);
}
