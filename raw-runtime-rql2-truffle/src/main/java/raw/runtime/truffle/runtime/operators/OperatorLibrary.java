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

package raw.runtime.truffle.runtime.operators;

import com.oracle.truffle.api.library.GenerateLibrary;
import com.oracle.truffle.api.library.Library;
import com.oracle.truffle.api.library.LibraryFactory;

@GenerateLibrary
public abstract class OperatorLibrary extends Library {
    static final LibraryFactory<OperatorLibrary> FACTORY = LibraryFactory.resolve(OperatorLibrary.class);

    public static LibraryFactory<OperatorLibrary> getFactory() {
        return FACTORY;
    }

    public static OperatorLibrary getUncached() {
        return FACTORY.getUncached();
    }

    public boolean isAggregator(Object receiver) {
        return false;
    }

    public abstract Object doOperation(Object receiver, Object left, Object right);

}
